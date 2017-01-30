package com.example.systemadministrator.myapplication;

/**
 * Created by Doug Whitley on 1/29/2017.
 */

public class GameBoard {

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
    }

    //TODO write algorithm for win check
    public int winCheck(){return 0;}

    //used to place a piece on the board return true for valid placement and false for invalid placement
    public Boolean placePiece(int playerNumber, int xCoordinate, int yCoordinate){
        if(board[xCoordinate][yCoordinate] != 0)
            return false;
        board[xCoordinate][yCoordinate] = playerNumber;
        return true;
    }


    private void boardInitialize(int boardSize){
        for (int i = 0; i < boardSize; ++i)
            for (int j=0; j < boardSize; ++j)
                board[i][j] = 0;
    }

    //the game board
    private int[][] board;
}
