package com.example.systemadministrator.myapplication;

/**
 * Created by Doug Whitley on 1/29/2017.
 */

public class GameBoard {

    //the game board
    private int[][] board;
    private int boardSize;

    //constructors for the game board
    //dafault constructor that sets game board size to 10x10
    public GameBoard(){
        board = new int[10][10];
        boardInitialize(10);
    }

    //non default constructor that sets game board to arg x arg
    public GameBoard(int boardSize){
        board = new int[boardSize][boardSize];
        boardInitialize(boardSize);
        this.boardSize = boardSize;
    }

    public  int getPieceAtXY(int xPos, int yPos){
        return board[xPos][yPos];
    }

    //used to place a piece on the board return true for valid placement and false for invalid placement
    public Boolean placePiece(int playerNumber, int xCoordinate, int yCoordinate){
        if(board[xCoordinate][yCoordinate] != 0)
            return false;
        board[xCoordinate][yCoordinate] = playerNumber;
        return true;
    }

    //TODO FIXME write algorithm for gameOver
    public int gameOver(){
        if(board[0][0] == 1 || board[0][0] == 2 && board[0][0] == board[0][1] && board[0][1] == board[0][2] && board[0][2] == board[0][3] && board[0][3] == board[0][4])
            return 1;
        else
            return 0;
    }

    //TODO FIXME write algorithm for longest streak
    public streakObj getLongestStreak(){
        return new streakObj(new Coordinates(0,0), new Coordinates(0,0), 0, 0);
    }

    public boolean tieGame (){
        for(int i = 0; i< boardSize; i++){
            for(int j = 0; j< boardSize; j++){
                if(board[i][j] == 0)
                    return false;
            }
        }
        return true;
    }

    private void boardInitialize(int boardSize){
        for (int i = 0; i < boardSize; ++i)
            for (int j=0; j < boardSize; ++j)
                board[i][j] = 0;
    }

    //TODO implement game state code
    public streakObj[] gameState () {

        return null;
    }
}
