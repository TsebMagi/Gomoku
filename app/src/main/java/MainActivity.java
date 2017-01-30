package com.example.tanner.gomoku2;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.util.DisplayMetrics;
import android.content.res.Resources;
import android.widget.LinearLayout;
import android.content.Context;
import android.view.View;
import android.graphics.drawable.Drawable;

public class MainActivity extends AppCompatActivity {

    int dimension = 15;
    private ImageView[][] boardArray = new ImageView[dimension][dimension];
    private Context context;
    private int[][] piecesOnBoard = new int [dimension][dimension]; //0 is uninitialized, 1 or 2 to represent players pieces, 3 is empty
    private Drawable[] drawCell = new Drawable[3];//0 is empty, 1 or 2 for different player pieces
    private int xPos, yPos; // x and y position of move
    private int playerTurn; // which players turn it is

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = this;
        playerTurn = 1;
        loadResources();
        createBoard();
        init_board();
    }

    private void init_board(){
        for (int i = 0; i < dimension; i++) {
            for (int j = 0; j < dimension; j++) {
                boardArray[i][j].setImageDrawable(null);//clears content in all locations
                piecesOnBoard[i][j] = 0;
            }
        }
    }

    private void loadResources() {
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
                        if (piecesOnBoard[x][y] == 0) { // make sure cell is empty
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
        piecesOnBoard[xPos][yPos] = playerTurn; //keep track of board in 2d array data structure

        if (playerTurn == 1) {
            playerTurn = 2;
        } else {
            playerTurn = 1;
        }
    }

    private float ScreenWidth() {
        Resources resources = context.getResources();
        DisplayMetrics dm = resources.getDisplayMetrics();
        return dm.widthPixels-100; //50 pixels of padding on each side
    }
}
