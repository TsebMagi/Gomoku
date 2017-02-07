package com.example.systemadministrator.myapplication;

/**
 * Created by Tanner on 2/6/2017.
 */

public class AIPlayer extends Player {

    public AIPlayer(){
        super();
    }

    public Coordinates generateMove(GameBoard board){
        return new Coordinates(0, 0);
    }
}
