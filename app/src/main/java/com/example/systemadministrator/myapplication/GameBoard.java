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

    //Might be too brute force, but from what I've seen, it works. Don't cover the edge cases where both ends aren't covered. Basic game over check.
    public int gameOver(){
        int counter1 = 0;
        int counter2 = 0;
        for(int i = 0; i< boardSize; i++){
            for(int j = 0; j< boardSize; j++){
                if(board[i][j] == 1) {
                    if(board[i][j] == board[i+1][j] && board[i][j] == board[i+2][j] && board[i][j] == board[i+3][j] && board[i][j] == board[i+4][j]) {
                        return 1;
                    }
                    if(board[i][j] == board[i][j+1] && board[i][j] == board[i][j+2] && board[i][j] == board[i][j+3] && board[i][j] == board[i][j+4]) {
                        return 1;
                    }
                    if(board[i][j] == board[i+1][j+1] && board[i][j] == board[i+2][j+2] && board[i][j] == board[i+3][j+3] && board[i][j] == board[i+4][j+4]) {
                        return 1;
                    }
                    if(board[i][j] == board[i+1][j-1] && board[i][j] == board[i+2][j-2] && board[i][j] == board[i+3][j-3] && board[i][j] == board[i+4][j-4]) {
                        return 1;
                    }
                }
                else if(board[i][j] == 2) {
                    if(board[i][j] == board[i+1][j] && board[i][j] == board[i+2][j] && board[i][j] == board[i+3][j] && board[i][j] == board[i+4][j]) {
                        return 2;
                    }
                    if(board[i][j] == board[i][j+1] && board[i][j] == board[i][j+2] && board[i][j] == board[i][j+3] && board[i][j] == board[i][j+4]) {
                        return 2;
                    }
                    if(board[i][j] == board[i+1][j+1] && board[i][j] == board[i+2][j+2] && board[i][j] == board[i+3][j+3] && board[i][j] == board[i+4][j+4]) {
                        return 2;
                    }
                    if(board[i][j] == board[i+1][j-1] && board[i][j] == board[i+2][j-2] && board[i][j] == board[i+3][j-3] && board[i][j] == board[i+4][j-4]) {
                        return 2;
                    }
                }
            }
        }
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

    //This just prints out to console. This is for my testing purposes.
    public void gameState () {
        int gameOver;
        for(int i = 0; i < boardSize; i++) {
            for (int j = 0; j < boardSize; j++) {
                System.out.print(board[i][j] + ",");
            }
            System.out.println("");
        }
        gameOver = gameOver();
        if(gameOver != 0) {
            System.out.println("Game Over. Player "+gameOver+" wins!");
        }

    }
}
