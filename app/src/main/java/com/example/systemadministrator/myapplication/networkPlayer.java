package com.example.systemadministrator.myapplication;

/**
 * Created by Tanner on 2/6/2017.
 */

public class networkPlayer extends humanPlayer {
    private Coordinates nextMove;

    public networkPlayer(){
        super();
        this.nextMove = null;
    }

    public void sendMove(int xPos, int yPos){
        return;
    }

    public Coordinates getNextMove(){
        return nextMove;
    }

    public void setNextMoveNull(){
        nextMove = null;
    }
}
