package com.example.systemadministrator.myapplication;

import java.util.Timer;

/**
 * Created by Doug Whitley on 1/29/2017.
 */

abstract public class Player {
    protected boolean hasChosen;
    protected Timer turnTime;
    protected  boolean baseTimeUsed;

    public Player(){
        this.hasChosen = true; // by default cannot make move
    }

    public boolean hasChosen(){
        return hasChosen;
    }

    public void setHasChosen(boolean newBool){
        hasChosen = newBool;
    }
}
