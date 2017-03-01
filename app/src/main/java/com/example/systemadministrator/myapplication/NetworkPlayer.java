package com.example.systemadministrator.myapplication;

/**
 * Created by Tanner on 2/6/2017.
 */

 public class NetworkPlayer extends HumanPlayer {
    private Coordinates nextMove;
    private boolean madeMove;

    public NetworkPlayer(){
        super();
        this.nextMove = null;
        setGoesFirst();
        madeMove = false;
    }

    public void sendMove(int xPos, int yPos){
        return;
    }

    public Coordinates getNextMove(){
        return nextMove;
    }

    public void setNextMove(Coordinates nextMove){
        this.nextMove = nextMove;
    }

    public void setNextMoveNull(){
        nextMove = null;
    }

    public void setGoesFirst(){
        goesFirst = true;
    }

    public void setMadeMove(boolean madeMove){
        this.madeMove = madeMove;
    }

    public boolean getMadeMove(){
        return madeMove;
    }
}
