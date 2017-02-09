package com.example.systemadministrator.myapplication;

/**
 * Created by Tanner on 2/6/2017.
 */

public class streakObj {
    public final Coordinates startPiece;
    public final Coordinates endPiece;
    public final int length;
    public final int playerNumber;

    public streakObj(Coordinates startPiece, Coordinates endPiece, int length, int playerNumber) {
        this.startPiece = startPiece;
        this.endPiece = endPiece;
        this.length = length;
        this.playerNumber = playerNumber;
    }

}
