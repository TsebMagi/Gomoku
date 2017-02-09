package com.example.systemadministrator.myapplication;

/**
 * Created by Tanner on 2/6/2017.
 */

public class AIPlayer extends Player {

    private int coor;

    public AIPlayer(){
        super();
        coor = 0;
    }

    public Coordinates generateMove(GameBoard board){
        return new Coordinates(coor, coor++);
    }
}
