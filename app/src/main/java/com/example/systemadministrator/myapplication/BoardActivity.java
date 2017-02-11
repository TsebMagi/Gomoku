package com.example.systemadministrator.myapplication;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;
import android.util.Log;
import android.os.Handler;

import com.example.systemadministrator.Gomoku.R;

public class BoardActivity extends AppCompatActivity {

    //Working Board
    private int dimension;
    private String player2Type;
    private char style;
    private Player[] players = new Player[2];
    private ImageView[][] boardArray;
    private Context context;
    private GameBoard piecesOnBoard; //0 is uninitialized, 1 or 2 to represent players pieces, 3 is empty
    private Drawable[] drawCell = new Drawable[3]; //0 is empty, 1 or 2 for different player pieces
    private int xPos, yPos; // x and y position of move
    private int playerTurn; // which players turn it is

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_board);

        Bundle extras =  getIntent().getExtras();
        dimension = extras.getInt("Size");
        player2Type = extras.getString("Opponent");

        players[0] = new humanPlayer();
        if(player2Type.equals("Human"))
            players[1] = new humanPlayer();
        else
            players[1] = new AIPlayer();

        piecesOnBoard = new GameBoard(dimension);
        boardArray = new ImageView[dimension][dimension];

        context = this;
        playerTurn = 1;

        loadDrawables();
        createBoard();

        players[playerTurn-1].setHasChosen(false);

    }

    private void loadDrawables() {
        drawCell[0] = context.getResources().getDrawable(R.drawable.plus, null);//empty cell
        drawCell[1] = context.getResources().getDrawable(R.drawable.red_piece, null);//drawable for player 1
        drawCell[2] = context.getResources().getDrawable(R.drawable.black_piece, null);//drawable for player 2
    }

    private void createBoard(){
        int sizeofCell = Math.round(ScreenWidth() / dimension);

        LinearLayout.LayoutParams singleCellDimensions = new LinearLayout.LayoutParams(sizeofCell, sizeofCell);
        LinearLayout.LayoutParams singleRowDimensions = new LinearLayout.LayoutParams(sizeofCell * dimension, sizeofCell);
        LinearLayout BoardGame = (LinearLayout) findViewById(R.id.Board);

        //create cells
        for (int i = 0; i < dimension; i++) {
            LinearLayout newRow = new LinearLayout(context); //make a row
            for (int j = 0; j < dimension; j++) { // loop through row and create individual cells
                boardArray[i][j] = new ImageView(context);
                boardArray[i][j].setBackground(drawCell[0]); //creates background + for all locations
                final int x = i; //each location knows it x,y coordinates
                final int y = j;
                boardArray[i][j].setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!players[playerTurn-1].hasChosen() && players[playerTurn-1] instanceof humanPlayer && piecesOnBoard.getPieceAtXY(x,y) == 0) { // make sure cell is empty
                            xPos = x;
                            yPos = y; //when a location is clicked it sets global x,y variables to be accessed in other functions
                            makeMove();
                        }
                    }
                });
                newRow.addView(boardArray[i][j], singleCellDimensions);
            }
            BoardGame.addView(newRow, singleRowDimensions); //add new row to board layout
        }
    }

    private void makeMove() {
        boardArray[xPos][yPos].setImageDrawable(drawCell[playerTurn]); //put players piece on space chosen
        piecesOnBoard.placePiece(playerTurn, xPos, yPos); //keep track of board in 2d array data structure
        if (playerTurn == 1 && players[0].hasChosen == false) {
            players[0].setHasChosen(true);
            players[1].setHasChosen(false);
            playerTurn = 2;
            if (players[1] instanceof networkPlayer)
                ((networkPlayer) players[1]).sendMove(xPos, yPos);
            else if (players[1] instanceof AIPlayer) {
                Coordinates move = ((AIPlayer) players[1]).generateMove(piecesOnBoard);
                while (piecesOnBoard.getPieceAtXY(move.x, move.y) != 0)
                    move = ((AIPlayer) players[1]).generateMove(piecesOnBoard);
                makeMove(move);
            }
        }
        else if(players[1].hasChosen == false){
            players[1].setHasChosen(true);
            players[0].setHasChosen(false);
            playerTurn = 1;
            if(players[0] instanceof networkPlayer)
                ((networkPlayer) players[1]).sendMove(xPos, yPos);
            else if(players[0] instanceof AIPlayer){
                Coordinates move = ((AIPlayer) players[1]).generateMove(piecesOnBoard);
                while(piecesOnBoard.getPieceAtXY(move.x, move.y) != 0)
                    move = ((AIPlayer) players[1]).generateMove(piecesOnBoard);
                makeMove(move);
            }
        }
        piecesOnBoard.gameState();
    }

    private void makeMove(Coordinates move) {
        boardArray[move.x][move.y].setImageDrawable(drawCell[playerTurn]); //put players piece on space chosen
        piecesOnBoard.placePiece(playerTurn, move.x, move.y); //keep track of board in 2d array data structure
        if (playerTurn == 1) {
            players[0].setHasChosen(true);
            players[1].setHasChosen(false);
            playerTurn = 2;
        }
        else{
            players[1].setHasChosen(true);
            players[0].setHasChosen(false);
            playerTurn = 1;
        }
    }

    private float ScreenWidth() {
        Resources resources = context.getResources();
        DisplayMetrics dm = resources.getDisplayMetrics();
        return dm.widthPixels-100; //50 pixels of padding on each side
    }


}
