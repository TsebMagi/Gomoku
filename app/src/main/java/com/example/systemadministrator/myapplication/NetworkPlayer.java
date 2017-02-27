package com.example.systemadministrator.myapplication;

/**
 * Created by Tanner on 2/6/2017.
 */

public class NetworkPlayer extends HumanPlayer {
    private Coordinates nextMove;
    private BoardActivity arr;

    public NetworkPlayer(){
        super();
        this.nextMove = null;
        setGoesFirst();
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

    public void setGoesFirst(){
        goesFirst = true;
    }
}
