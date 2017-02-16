package com.example.systemadministrator.myapplication;

import android.os.CountDownTimer;
import android.widget.TextView;
import android.util.Log;

/**
 * Created by Doug Whitley on 1/29/2017.
 */

abstract public class Player {
    protected boolean hasChosen;
    protected CountDownTimer turnTime;
    protected int timeRemaining;
    protected  boolean timeExpired;
    protected String originalText;

    public Player(){
        timeExpired = false;
        this.hasChosen = true; // by default cannot make move
        this.originalText = "";
    }

    public Player(TextView text){
        timeExpired = false;
        this.hasChosen = true; // by default cannot make move
        this.originalText = text.getText().toString();
    }

    public boolean hasChosen(){
        return hasChosen;
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

    public void startNewTimer(final TextView text){
        String originalTime = originalText + "\n" + "10:00";
        text.setText(originalTime);
        timeRemaining = 600000;

        turnTime = new CountDownTimer(timeRemaining, 1000) { // adjust the milli seconds here

            public void onTick(long millisUntilFinished) {
                int allSeconds = (int) millisUntilFinished / 1000;
                timeRemaining = allSeconds*1000;
                int min = allSeconds /60;
                int sec = allSeconds % 60;
                String newTime = originalText + "\n" + Integer.toString(min) + ":" + Integer.toString(sec);
                text.setText(newTime);
            }

            public void onFinish() {
                timeExpired = true;
            }
        }.start();
    }

    public void stopTimer(){
        turnTime.cancel();
    }

    public void restartTimer(final TextView text){
        turnTime = new CountDownTimer(timeRemaining, 1000) { // adjust the milli seconds here

            public void onTick(long millisUntilFinished) {
                int allSeconds = (int) millisUntilFinished / 1000;
                timeRemaining = allSeconds*1000;
                int min = allSeconds /60;
                int sec = allSeconds % 60;
                String newTime = originalText + "\n" + Integer.toString(min) + ":" + Integer.toString(sec);
                text.setText(newTime);
            }

            public void onFinish() {
                timeExpired = true;
            }
        }.start();
    }
}
