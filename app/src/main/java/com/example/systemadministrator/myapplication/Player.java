package com.example.systemadministrator.myapplication;

import android.os.CountDownTimer;
import android.widget.TextView;


/**
 * Created by Doug Whitley on 1/29/2017.
 */

abstract public class Player {
    protected boolean hasChosen;
    protected CountDownTimer turnTime;
    protected int timeRemaining;
    protected  boolean longTimeExpired;
    protected boolean shortTimeExpired;
    protected String originalText;
    final int LONG_TIME = 600;
    final int SHORT_TIME = 60;
    protected boolean goesFirst;

    public Player(){
        longTimeExpired = false;
        shortTimeExpired = false;
        this.hasChosen = true; // by default cannot make move
        this.originalText = "";
        goesFirst = false;
    }

    public boolean hasChosen(){
        return hasChosen;
    }

    public boolean checkExpired(){
        return shortTimeExpired;
    }

    public void setHasChosen(boolean newBool){
        hasChosen = newBool;
    }

    public void updateText(String str){
        this.originalText = str;
    }

    public void setTimeRemaining(int timeRemaining){
        this.timeRemaining = timeRemaining*1000;
    }

    public void startTimer(final TextView text, boolean newTime){
        if(newTime) {
            String secString = (LONG_TIME % 60 != 0) ? Integer.toString(LONG_TIME % 60) : "00";
            String originalTime = originalText + "\n" + Integer.toString(LONG_TIME/60) + ":" + secString;
            text.setText(originalTime);
            timeRemaining = LONG_TIME*1000;
        }
        if(longTimeExpired)
            timeRemaining = SHORT_TIME*1000;

        turnTime = new CountDownTimer(timeRemaining, 1000) { // adjust the milli seconds here

            public void onTick(long millisUntilFinished) {
                int allSeconds = (int) millisUntilFinished / 1000;
                timeRemaining = allSeconds*1000;
                int min = allSeconds /60;
                int sec = allSeconds % 60;
                String secString = (sec > 9) ? Integer.toString(sec) : "0" + Integer.toString(sec);
                String newTime = originalText + "\n" + Integer.toString(min) + ":" + secString;
                text.setText(newTime);
            }

            public void onFinish() {
                String newTime = originalText + "\n" + "0:00";
                text.setText(newTime);
                if(!longTimeExpired) {
                    longTimeExpired = true;
                    startTimer(text, true);
                }
                else
                    shortTimeExpired = true;
            }
        }.start();
    }

    public void stopTimer(){
        turnTime.cancel();
    }

    public void resetTimers(){
        longTimeExpired = false;
        shortTimeExpired = false;
    }

    public boolean getGoesFirst(){
        return goesFirst;
    }
}
