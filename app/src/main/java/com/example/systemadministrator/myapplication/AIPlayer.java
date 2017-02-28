package com.example.systemadministrator.myapplication;

import java.util.ArrayList;

/**
 * Created by Tanner on 2/6/2017.
 */

public class AIPlayer extends Player {

    private int coor;
    public int playerNumber;

    public AIPlayer(){
        super();
        coor = 0;
        playerNumber = 2;
    }

    public Coordinates generateMove(GameBoard board){
        ArrayList<Coordinates> toExamine = new ArrayList<>(board.OpenSpaces());
        StreakObj potentialWinStreak = board.getLongestWinnableStreak(playerNumber);
        StreakObj potentialDefenseStreak = board.getLongestStreak((playerNumber+1)%2);
        return new Coordinates(coor, coor++);
    }

    public Coordinates generateFreestyleMove(GameBoard board){return new Coordinates(coor, coor++); }
}
