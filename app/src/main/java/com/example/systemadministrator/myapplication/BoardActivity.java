package com.example.systemadministrator.myapplication;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.TextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

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
    private boolean gameOverFlag;
    private int player1Wins;
    private int player2Wins;
    private int tieGames;
    TextView p1Status;
    TextView p2Status;
    TextView tieStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_board);

        Bundle extras =  getIntent().getExtras();
        dimension = extras.getInt("Size");
        player2Type = extras.getString("Opponent");

        players[0] = new HumanPlayer();
        if(player2Type.equals("Human"))
            players[1] = new HumanPlayer();
        else
            players[1] = new AIPlayer();

        piecesOnBoard = new GameBoard(dimension);
        boardArray = new ImageView[dimension][dimension];
        gameOverFlag = false;

        context = this;
        playerTurn = 1;

        loadDrawables();
        createBoard();

        player1Wins = 0;
        player2Wins = 0;
        tieGames = 0;
        p1Status = (TextView) findViewById(R.id.p1Status);
        p2Status = (TextView) findViewById(R.id.p2Status);
        tieStatus = (TextView) findViewById(R.id.tieStatus);
        updateGameStatus();

        players[0].setHasChosen(false);
        startTimers();

        Timer timer = new Timer();
        timer.schedule(new checkGameOver(), 0, 1000);

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
                        if (!gameOverFlag && !players[playerTurn-1].hasChosen() && players[playerTurn-1] instanceof HumanPlayer && piecesOnBoard.getPieceAtXY(x,y) == 0) { // make sure cell is empty
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
        checkGameOver();
        players[0].checkExpired();
        players[1].checkExpired();

        if (playerTurn == 1 && players[0].hasChosen == false) {
            players[0].setHasChosen(true);
            players[1].setHasChosen(false);
            playerTurn = 2;
            switchTimers(1);
            if (players[1] instanceof NetworkPlayer)
                ((NetworkPlayer) players[1]).sendMove(xPos, yPos);
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
            switchTimers(0);
            if(players[0] instanceof NetworkPlayer)
                ((NetworkPlayer) players[1]).sendMove(xPos, yPos);
            else if(players[0] instanceof AIPlayer){
                Coordinates move = ((AIPlayer) players[1]).generateMove(piecesOnBoard);
                while(piecesOnBoard.getPieceAtXY(move.x, move.y) != 0)
                    move = ((AIPlayer) players[1]).generateMove(piecesOnBoard);
                makeMove(move);
            }
        }
    }

    private void switchTimers(int nextPlayer){
        if(!gameOverFlag && !(players[1] instanceof AIPlayer)){
            if(nextPlayer == 0){
                players[1].stopTimer();
                players[0].startTimer(p1Status, false);
            }
            else{
                players[0].stopTimer();
                players[1].startTimer(p2Status, false);
            }
        }
    }

    private void startTimers(){

        players[0].startTimer(p1Status, true);
        if(!(players[1] instanceof AIPlayer)) {
            players[1].startTimer(p2Status, true);
            players[1].stopTimer();
        }
    }

    private void makeMove(Coordinates move) {
        boardArray[move.x][move.y].setImageDrawable(drawCell[playerTurn]); //put players piece on space chosen
        piecesOnBoard.placePiece(playerTurn, move.x, move.y); //keep track of board in 2d array data structure
        checkGameOver();
        
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

    class checkGameOver extends TimerTask {
        public void run() {
            runOnUiThread(new Runnable() {
                public void run () {
                    if(! gameOverFlag) {
                        gameOverFlag = players[0].checkExpired() || players[1].checkExpired();
                        checkGameOver();
                    }
                }
            });
        }
    }

    private void checkGameOver(){
        int result = piecesOnBoard.gameOver();
        CharSequence text;
        if(result != 0){
            gameOverFlag = true;
            players[0].stopTimer();
            players[1].stopTimer();
            if(result == 1) {
                text = "Player 1 wins!";
                player1Wins++;
            }
            else if(result == 2) {
                text = "Player 2 wins!";
                player2Wins++;
            }
            else {
                text = "Tie Game! Nobody wins";
                tieGames++;
            }
            updateGameStatus();
            Context context = getApplicationContext();
            int duration = Toast.LENGTH_LONG;
            Toast toast = Toast.makeText(context, text, duration);
            toast.show();
            afterGame();
        }
        else if(gameOverFlag){
            if(players[0].checkExpired()) {
                players[1].stopTimer();
                text = "Player 2 wins!";
                player2Wins++;
            }
            else {
                players[0].stopTimer();
                text = "Player 1 wins!";
                player1Wins++;
            }
            updateGameStatus();
            Context context = getApplicationContext();
            int duration = Toast.LENGTH_LONG;
            Toast toast = Toast.makeText(context, text, duration);
            toast.show();
            afterGame();
        }
    }

    private float ScreenWidth() {
        Resources resources = context.getResources();
        DisplayMetrics dm = resources.getDisplayMetrics();
        return dm.widthPixels-100; //50 pixels of padding on each side
    }

    //after the game is over, make the buttons appear.
    private void afterGame() {
        Button regameButton =(Button)findViewById(R.id.regameButton);
        Button menuButton =(Button)findViewById(R.id.menuButton);
        regameButton.setVisibility(View.VISIBLE);
        menuButton.setVisibility(View.VISIBLE);

    }

    //sends a the user back to the main menu when clicking menuButton
    public void onMenuClick(View v) {
        Intent intent = new Intent(BoardActivity.this, MainActivity.class);
        startActivity(intent);

    }

    //regame sets the array to 0, the loop clears the board ui.
    public void onReGameClick(View v) {
        piecesOnBoard.reGame();
        for(int i = 0; i < dimension; i++) {
            for(int j = 0; j < dimension; j++) {
                boardArray[i][j].setImageDrawable(drawCell[0]);
            }
        }
        playerTurn = 1;
        players[playerTurn-1].setHasChosen(false);
        gameOverFlag = false;
        Button regameButton =(Button)findViewById(R.id.regameButton);
        Button menuButton =(Button)findViewById(R.id.menuButton);
        regameButton.setVisibility(View.INVISIBLE);
        menuButton.setVisibility(View.INVISIBLE);
        players[0].resetTimers();
        players[1].resetTimers();
        startTimers();

    }

    private void updateGameStatus(){
        String p1Text = String.format("P1\n %d", player1Wins);
        String p2Text = String.format("P2\n%d", player2Wins);
        String tieText = String.format("Tie\n%d", tieGames);
        p1Status.setText(p1Text);
        players[0].updateText(p1Text);
        p2Status.setText(p2Text);
        players[1].updateText(p2Text);
        tieStatus.setText(tieText);
    }

}
