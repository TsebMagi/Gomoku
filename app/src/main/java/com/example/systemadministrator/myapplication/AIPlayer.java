package com.example.systemadministrator.myapplication;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by Tanner on 2/6/2017.
 */

public class AIPlayer extends Player {

    private int coor;
    public int playerNumber;

    public AIPlayer(){
        super();
        coor = 0;
        goesFirst = false;
        playerNumber = 2;
    }

    public Coordinates generateMove(GameBoard board){
        ArrayList<Coordinates> toExamine = board.OpenSpaces();
        StreakObj potentialWinStreak = board.getLongestWinnableStreak(playerNumber);
        StreakObj potentialDefenseStreak = board.getLongestWinnableStreak(playerNumber-1);

        //win
        if(potentialWinStreak != null && potentialWinStreak.length >= 3) {

            for (Coordinates i : toExamine) {
                if (potentialWinStreak.adjacentPoint(i))
                    return i;
            }
        }

        //defend
        if(potentialDefenseStreak != null && potentialDefenseStreak.length >= 3){
            for(Coordinates i : toExamine)
                if(potentialDefenseStreak.adjacentPoint(i))
                    return i;
        }

        //add to longest streak
        if(potentialWinStreak!=null){
            for (Coordinates i : toExamine)
                if (potentialWinStreak.adjacentPoint(i))
                    return i;
        }

        //pick something
        return toExamine.get(0);
    }

    public Coordinates generateFreestyleMove(GameBoard board){return new Coordinates(coor, coor++); }
}
