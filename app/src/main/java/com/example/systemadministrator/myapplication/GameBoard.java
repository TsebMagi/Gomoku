package com.example.systemadministrator.myapplication;

import java.util.ArrayList;

import javax.sql.StatementEvent;

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
        playerStreaks[playerNumber-1].addAll(gameState(playerNumber,xCoordinate,yCoordinate));
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

    public StreakObj getLongestStreak(int playerNumber){
        StreakObj ret = playerStreaks[playerNumber-1].get(0);
        int length = 0;
        for (StreakObj i : playerStreaks[playerNumber-1]){
            if (i.length > length)
                ret = i;
        }
        return ret;
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

    //takes a placement position and builds a list of streaks that that placement creates.
    public ArrayList<StreakObj> gameState(int playerNumber, int xCoordinate, int yCoordinate){

        //Return Array
        ArrayList<StreakObj> ret = new ArrayList<>();

        //setup single streak and add to ret
        Coordinates start = new Coordinates(xCoordinate,yCoordinate);
        Coordinates end = new Coordinates(xCoordinate,yCoordinate);
        StreakObj single = new StreakObj(start,end,1,playerNumber,StreakType.HORIZONTAL);
        ret.add(single);

        //build other streaks
        StreakObj horizon = buildHorizontalStreak(playerNumber,xCoordinate,yCoordinate);
        if (horizon != null && horizon.length>1)
            if (horizontalUncapped(horizon)) {

                ret.add(horizon);
            }

        StreakObj vertical = buildVerticalStreak(playerNumber,xCoordinate,yCoordinate);
        if (vertical != null && vertical.length>1)
            if (verticalUncapped(vertical)) {
                ret.add(vertical);
            }

        StreakObj diagonalUp = buildDiagonalUpStreak(playerNumber,xCoordinate,yCoordinate);
        if(diagonalUp != null && diagonalUp.length>1)
            if (diagonalUpUncapped(diagonalUp)) {
                ret.add(diagonalUp);
            }

        StreakObj diagonalDown = buildDiagonalDownStreak(playerNumber,xCoordinate,yCoordinate);
        if(diagonalDown != null && diagonalDown.length>1)
            if (diagonalDownUncapped(diagonalDown)) {
                ret.add(diagonalDown);
            }

        //return list
        return ret;
    }
    private StreakObj buildHorizontalStreak(int playerNumber, int xCoordinate, int yCoordinate){
        int xStart = xCoordinate;
        int xEnd = xCoordinate;
        int length = 1;
        //flesh out left hand side
        while(xStart-1 > -1 && board[xStart-1][yCoordinate] == playerNumber){
            --xStart;
            ++length;
        }
        Coordinates start = new Coordinates(xStart,yCoordinate);
        while(xEnd+1 < boardSize && board[xEnd+1][yCoordinate] == playerNumber){
            ++xEnd;
            ++length;
        }
        Coordinates end = new Coordinates(xEnd,yCoordinate);

        return new StreakObj(start,end,length,playerNumber, StreakType.HORIZONTAL);
    }

    private StreakObj buildVerticalStreak(int playerNumber, int xCoordinate, int yCoordinate){
        int yStart = yCoordinate;
        int yEnd = yCoordinate;
        int length = 1;
        //flesh out left hand side
        while(yStart-1 > -1 && board[xCoordinate][yStart-1] == playerNumber){
            --yStart;
            ++length;
        }
        Coordinates start = new Coordinates(xCoordinate,yStart);
        while(yEnd+1 < boardSize && board[xCoordinate][yEnd+1] == playerNumber){
            ++yEnd;
            ++length;
        }
        Coordinates end = new Coordinates(xCoordinate,yEnd);

        return new StreakObj(start,end,length,playerNumber,StreakType.VERTICAL);
    }

    private StreakObj buildDiagonalDownStreak(int playerNumber, int xCoordinate, int yCoordinate){
        int xStart = xCoordinate;
        int xEnd = xCoordinate;
        int yStart = yCoordinate;
        int yEnd = yCoordinate;
        int length = 1;
        //flesh out left hand side
        while(xStart-1 > -1 && yStart-1 > -1 && board[xStart-1][yStart-1] == playerNumber){
            --xStart;
            --yStart;
            ++length;
        }
        Coordinates start = new Coordinates(xStart,yStart);
        while(xEnd+1 < boardSize && yEnd+ 1< boardSize && board[xEnd+1][yEnd+1] == playerNumber){
            ++xEnd;
            ++yEnd;
            ++length;
        }
        Coordinates end = new Coordinates(xEnd,yCoordinate);

        return new StreakObj(start,end,length,playerNumber, StreakType.DIAGONALDOWN);
    }

    private StreakObj buildDiagonalUpStreak(int playerNumber, int xCoordinate, int yCoordinate){
        int xStart = xCoordinate;
        int xEnd = xCoordinate;
        int yStart = yCoordinate;
        int yEnd = yCoordinate;
        int length = 1;
        //flesh out left hand side
        while(xStart+1 < boardSize && yStart-1 > -1 && board[xStart+1][yStart-1] == playerNumber){
            ++xStart;
            --yStart;
            ++length;
        }
        Coordinates start = new Coordinates(xStart,yStart);
        while(xEnd-1 > -1 && yEnd+ 1< boardSize && board[xEnd-1][yEnd+1] == playerNumber){
            --xEnd;
            ++yEnd;
            ++length;
        }
        Coordinates end = new Coordinates(xEnd,yCoordinate);

        return new StreakObj(start,end,length,playerNumber, StreakType.DIAGONALUP);
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
        if (toCheck.startPiece.x < boardSize-1 && toCheck.startPiece.y > 0 && toCheck.endPiece.x > 0 && toCheck.endPiece.y < boardSize-1 && (board[toCheck.startPiece.x + 1][toCheck.startPiece.y - 1] == 0 || board[toCheck.endPiece.x - 1][toCheck.endPiece.y + 1] == 0)) return true;
        //Case: capped
        return false;
    }


    //just resets the board.
    public void reGame() {
        for(int i = 0; i < boardSize; i++)
            for(int j = 0; j < boardSize; j++)
                board[i][j] = 0;
        playerStreaks[0] = new ArrayList<>();
        playerStreaks[1] = new ArrayList<>();
    }

}
