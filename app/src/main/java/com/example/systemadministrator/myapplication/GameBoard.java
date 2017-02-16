package com.example.systemadministrator.myapplication;

import java.util.ArrayList;

/**
 * Created by Doug Whitley on 1/29/2017.
 */

public class GameBoard {

    //the game board
    private int[][] board;
    private int boardSize;

    //used for the game state algorithms
    private ArrayList<StreakObj>[] playerStreaks;

    //constructors for the game board
    //dafault constructor that sets game board size to 10x10
    public GameBoard(){
        board = new int[10][10];
        boardInitialize(10);
        playerStreaks = new ArrayList[2];
        playerStreaks[0] = new ArrayList<>();
        playerStreaks[1] = new ArrayList<>();
    }

    //non default constructor that sets game board to arg x arg
    public GameBoard(int boardSize){
        board = new int[boardSize][boardSize];
        boardInitialize(boardSize);
        this.boardSize = boardSize;
        playerStreaks = new ArrayList[2];
        playerStreaks[0] = new ArrayList<>();
        playerStreaks[1] = new ArrayList<>();
    }

    public  int getPieceAtXY(int xPos, int yPos){
        return board[xPos][yPos];
    }

    //used to place a piece on the board return true for valid placement and false for invalid placement
    public Boolean placePiece(int playerNumber, int xCoordinate, int yCoordinate){
        if(board[xCoordinate][yCoordinate] != 0)
            return false;
        board[xCoordinate][yCoordinate] = playerNumber;
        playerStreaks = gameState();
        return true;
    }

    //TODO FIXME write algorithm for gameOver
    public int gameOver(){ // return 0 for still going, 1 or 2 for winning player number, or 3 for tie
        for(StreakObj i : playerStreaks[0])
            if (i.length == 5)
                return 1;
        for(StreakObj j : playerStreaks[1])
            if (j.length == 5)
                return 2;
        if(tieGame())
            return 3;
        return 0;
    }

    //TODO FIXME write algorithm for longest streak
    public StreakObj getLongestStreak(){
        return new StreakObj(new Coordinates(0,0), new Coordinates(0,0), 0, 0);
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

    public ArrayList<StreakObj>[] gameState () {

        //visited flags setup
        int[][] visitedBoard = new int[boardSize][boardSize];
        for (int i = 0; i < boardSize; ++i)
            for (int j = 0; j < boardSize; ++j)
                visitedBoard[i][j] = 0;

        Coordinates start = new Coordinates(0,0);

        return gameStateHelper(visitedBoard,start);
    }

    private ArrayList<StreakObj>[] gameStateHelper(int [][] visitedBoard, Coordinates currentLocation) {
         //Base Case: Off the Board.
        if (currentLocation.y >= boardSize || currentLocation.x >= boardSize) return null;

        //Base Case: Already visited.
        if (visitedBoard[currentLocation.x][currentLocation.y] == 1) return null;
        else
            visitedBoard[currentLocation.x][currentLocation.y] = 1;

        //Initialize Return.
        ArrayList<StreakObj>[] localStreaks = new ArrayList[2];
        localStreaks[0] = new ArrayList<StreakObj>();
        localStreaks[1] = new ArrayList<StreakObj>();

        //Setup Comparison.
        int localPlayer = board[currentLocation.x][currentLocation.y];

        //Horizontal streak case and degenerate singleton case
        StreakObj horStreak = horizontalStreak(localPlayer,currentLocation);

        //Check for capping.
        if (horStreak!= null && horizontalUncapped(horStreak))
            localStreaks[localPlayer - 1].add(horStreak);

        //Vertical streak case
        StreakObj verStreak = verticalStreak(localPlayer,currentLocation);
        //Check for capping
        if (verStreak != null && verticalUncapped(verStreak))
            localStreaks[localPlayer - 1].add(verStreak);

        //Diagonal streak case
        StreakObj diaDownStreak = diagonalDownStreak(localPlayer,currentLocation);
        if (diaDownStreak != null && diagonalDownUncapped(diaDownStreak))
            localStreaks[localPlayer - 1].add(diaDownStreak);

        StreakObj diaUpStreak = diagonalUpStreak(localPlayer,currentLocation);
        if (diaUpStreak != null && diagonalUpUncapped(diaUpStreak))
            localStreaks[localPlayer-1].add(diaUpStreak);

        ArrayList<StreakObj>[] ret = new ArrayList[2];
        ret[0] = new ArrayList<StreakObj>();
        ret[1] = new ArrayList<StreakObj>();

        ArrayList<StreakObj>[] recursiveCallRight = gameStateHelper(visitedBoard,new Coordinates(currentLocation.x+1,currentLocation.y));
        ArrayList<StreakObj>[] recursiveCallDown = gameStateHelper(visitedBoard,new Coordinates(currentLocation.x,currentLocation.y+1));
        ArrayList<StreakObj>[] recursiveCallDiagonal = gameStateHelper(visitedBoard,new Coordinates(currentLocation.x+1,currentLocation.y+1));

        ArrayList<StreakObj>[] comparisons = new ArrayList[2];
        comparisons[0] = new ArrayList<>();
        comparisons[1] = new ArrayList<>();

        for(int i = 0; i < 2; ++i) {
            if(recursiveCallRight!= null)
                comparisons[i].addAll(recursiveCallRight[i]);
            if (recursiveCallDown!=null)
                comparisons[i].addAll(recursiveCallDown[i]);
            if (recursiveCallDiagonal != null)
                comparisons[i].addAll(recursiveCallDiagonal[i]);
            ret[i].addAll(pruneRepeats(localStreaks[i],comparisons[i]));
        }


        return ret;
    }

    //builds horizontal streak from given starting point for player num
    private StreakObj horizontalStreak(int playerNum,Coordinates start){
        //called on blank board space
        if (playerNum == 0)
            return null;

        //setup end point
        Coordinates horEnd = new Coordinates(start.x, start.y);
        int horLength = 1;

        //find end of chain
        while (horEnd.x+1 < boardSize && board[horEnd.x + 1][horEnd.y] == playerNum) {
            ++horLength;
            ++horEnd.x;
        }
        return new StreakObj(start,horEnd,horLength,playerNum);
    }

    //builds vertical streak, ignores case of single piece.
    private StreakObj verticalStreak(int playerNum, Coordinates start) {
        if (playerNum == 0)
            return null;

        if (start.y+1 < boardSize && board[start.x][start.y + 1] == playerNum) {
            Coordinates verEnd = new Coordinates(start.x, start.y+1);
            int verLength = 2;
            //find end of chain
            while (verEnd.y+1 < boardSize && board[verEnd.x][verEnd.y + 1] == playerNum) {
                ++verLength;
                ++verEnd.y;
            }
            return new StreakObj(start,verEnd,verLength,playerNum);
        }
        return null;
    }

    private StreakObj diagonalDownStreak(int playerNum, Coordinates start) {
        if (playerNum == 0)
            return null;

        if (start.x+1 < boardSize && start.y+1 < boardSize && board[start.x + 1][start.y + 1] == playerNum) {
            Coordinates diaEnd = new Coordinates(start.x+1, start.y+1);
            int diaLength = 2;
            //find end of chain
            while (diaEnd.x+1 < boardSize && diaEnd.y+1 < boardSize && board[diaEnd.x + 1][diaEnd.y + 1] == playerNum) {
                ++diaLength;
                ++diaEnd.x;
                ++diaEnd.y;
            }
            return new StreakObj(start,diaEnd,diaLength,playerNum);
        }
        return null;
    }

    private StreakObj diagonalUpStreak(int playerNum, Coordinates start) {
        if (playerNum == 0)
            return null;

        if (start.x-1 > -1 && start.y+1 < boardSize && board[start.x - 1][start.y + 1] == playerNum) {
            Coordinates diaEnd = new Coordinates(start.x-1, start.y+1);
            int diaLength = 2;
            //find end of chain
            while (diaEnd.x-1 > -1 && diaEnd.y+1 < boardSize && board[diaEnd.x - 1][diaEnd.y + 1] == playerNum) {
                ++diaLength;
                --diaEnd.x;
                ++diaEnd.y;
            }
            return new StreakObj(start,diaEnd,diaLength,playerNum);
        }
        return null;
    }

    private boolean horizontalUncapped(StreakObj toCheck) {

        //***********edge of board cases***********
        //Case: runs length of board
        if (toCheck.startPiece.y == toCheck.endPiece.y && toCheck.startPiece.x == 0 && toCheck.endPiece.x == boardSize - 1) return true;
        //Case: starts at edge of board
        if (toCheck.startPiece.x == 0 && board[toCheck.endPiece.x + 1][toCheck.endPiece.y] == 0)
            return true;
        //Case: ends on edge of Board
        if (toCheck.endPiece.x == boardSize - 1 && board[toCheck.startPiece.x - 1][toCheck.startPiece.y] == 0)
            return true;
        //*********internal to board case**********
        if (toCheck.endPiece.y == toCheck.startPiece.y && toCheck.startPiece.x > 0 && toCheck.endPiece.x < boardSize-1 && (board[toCheck.startPiece.x - 1][toCheck.startPiece.y] == 0 || board[toCheck.endPiece.x + 1][toCheck.endPiece.y] == 0)) return true;
        //Case: capped
        return false;
    }

    private boolean verticalUncapped(StreakObj toCheck){
        //***********edge of board cases***********
        //Case: runs length of board
        if(toCheck.startPiece.y == 0 && toCheck.endPiece.y == boardSize-1 && toCheck.startPiece.x == toCheck.endPiece.x) return true;
        //Case: starts at edge of board
        if(toCheck.startPiece.y == 0 && board[toCheck.endPiece.x][toCheck.endPiece.y+1] == 0 && toCheck.startPiece.x == toCheck.endPiece.x) return true;
        //Case: ends on edge of Board
        if(toCheck.endPiece.y == boardSize-1 && board[toCheck.startPiece.x][toCheck.startPiece.y-1] == 0 && toCheck.startPiece.x==toCheck.endPiece.x) return true;
        //*********internal to board case**********
        if(toCheck.startPiece.x == toCheck.endPiece.x && toCheck.startPiece.y>0 && toCheck.endPiece.y < boardSize -1 &&  (board[toCheck.startPiece.x][toCheck.startPiece.y-1] == 0 || board[toCheck.endPiece.x][toCheck.endPiece.y+1] == 0)) return true;
        //Case: capped
        return false;
    }

    private boolean diagonalDownUncapped(StreakObj toCheck) {
        //***********edge of board cases***********
        //Case: runs length of board
        if ((toCheck.startPiece.x == 0 && toCheck.endPiece.y == boardSize-1) || (toCheck.endPiece.x == boardSize - 1 && toCheck.startPiece.y == 0)) return true;
        //Case: starts at edge of board
        if (toCheck.startPiece.y == 0 && toCheck.endPiece.x < boardSize-1 && toCheck.endPiece.y < boardSize-1 && board[toCheck.endPiece.x + 1][toCheck.endPiece.y + 1] == 0) return true;
        if (toCheck.startPiece.y == 0 && toCheck.endPiece.x < boardSize-1 && toCheck.endPiece.y < boardSize-1 && board[toCheck.endPiece.x + 1][toCheck.endPiece.y + 1] == 0) return true;
        //Case: ends on edge of Board
        if (toCheck.endPiece.x == boardSize - 1 && toCheck.startPiece.x > 1&& toCheck.startPiece.y >1 && board[toCheck.startPiece.x - 1][toCheck.startPiece.y - 1] == 0) return true;
        if (toCheck.endPiece.y == boardSize - 1 && toCheck.startPiece.x > 1&& toCheck.startPiece.y >1 && board[toCheck.startPiece.x - 1][toCheck.startPiece.y - 1] == 0) return true;
        //*********internal to board case**********
        if (toCheck.startPiece.x >0 && toCheck.startPiece.y > 0 && toCheck.endPiece.x < boardSize -1 && toCheck.endPiece.y < boardSize-1 && (board[toCheck.startPiece.x - 1][toCheck.startPiece.y - 1] == 0 || board[toCheck.endPiece.x + 1][toCheck.endPiece.y + 1] == 0)) return true;
        //Case: capped
        return false;
    }

    private boolean diagonalUpUncapped(StreakObj toCheck) {
        //***********edge of board cases***********
        //Case: runs length of board
        if ((toCheck.endPiece.x == 0 && toCheck.startPiece.y == 0) || (toCheck.startPiece.x == boardSize-1 && toCheck.endPiece.y == boardSize-1)) return true;
        //Case: starts at edge of board
        if (toCheck.startPiece.y == 0 && toCheck.endPiece.x < boardSize-1 && toCheck.endPiece.x > 0 &&board [toCheck.endPiece.x - 1][toCheck.endPiece.y + 1] == 0) return true;
        if (toCheck.startPiece.x == boardSize-1 && toCheck.endPiece.y > 0 && toCheck.endPiece.y<boardSize-1 && toCheck.endPiece.x > 0 &&board [toCheck.endPiece.x - 1][toCheck.endPiece.y + 1] == 0) return true;
        //Case: ends on edge of Board
        if (toCheck.endPiece.x == 0 && toCheck.startPiece.y>0 && toCheck.startPiece.x < boardSize-1 && board[toCheck.startPiece.x +1 ][toCheck.startPiece.y - 1] == 0) return true;
        if (toCheck.endPiece.y == boardSize-1 && toCheck.startPiece.x < boardSize-1 && toCheck.startPiece.y > 0 && board[toCheck.startPiece.x +1 ][toCheck.startPiece.y - 1] == 0) return true;
        //*********internal to board case**********
        if (toCheck.startPiece.x < boardSize-1 && toCheck.startPiece.y > 0 && toCheck.endPiece.x > 0 && toCheck.endPiece.y < boardSize-1 && (board[toCheck.startPiece.x - 1][toCheck.startPiece.y + 1] == 0 || board[toCheck.endPiece.x + 1][toCheck.endPiece.y + 1] == 0)) return true;
        //Case: capped
        return false;
    }


    private ArrayList<StreakObj> pruneRepeats(ArrayList<StreakObj> master, ArrayList<StreakObj> toCompare){
        //comparisons
        int masterLength = master.size();

        //setup return
        ArrayList<StreakObj> ret = new ArrayList<>();

        //master contains at least 1 element
        if(masterLength > 0) {
            //add all of the base to the return
            ret.addAll(master);
            //setup loop structure
            boolean add = true;
            //compare each comparison to the master and add if it's not contained by one already there
            for (StreakObj j : toCompare) {
                for (StreakObj i : master) {
                    if (i.contains(j)){
                        add = false;
                    }
                }
                if (add){
                    ret.add(j);
                    add = true;
                }
            }
        }
        //if there's nothing in the master just add the comparison list
        else{
            ret.addAll(toCompare);
        }
        return ret;
    }

}
