package com.example.systemadministrator.myapplication;

/**
 * Created by Tanner on 2/6/2017.
 */

public class StreakObj {
    public final Coordinates startPiece;
    public final Coordinates endPiece;
    public final int length;
    public final int playerNumber;

    public StreakObj(Coordinates startPiece, Coordinates endPiece, int length, int playerNumber) {
        this.startPiece = startPiece;
        this.endPiece = endPiece;
        this.length = length;
        this.playerNumber = playerNumber;
    }

    public boolean contains(StreakObj toCompare) {
        int x1 = this.startPiece.x;
        int y1 = this.startPiece.y;
        int x2 = this.endPiece.x;
        int y2 = this.endPiece.y;

        //case vertical lines
        if (x1 == x2 && toCompare.startPiece.x == toCompare.endPiece.x && x1 == toCompare.startPiece.x) {
            return (toCompare.startPiece.y >= y1 && toCompare.endPiece.y <= y2);
        }
        //case Diagonal and Horizontal Lines
        //y = y1 + ((y2 - y1) / (x2 - x1)) * (x - x1)
        if (x2 - x1 != 0 && toCompare.startPiece.y == (y1 + ((y2 - y1) / (x2 - x1)) * (toCompare.startPiece.x - x1))) {
            if (toCompare.endPiece.y == (y1 + ((y2 - y1) / (x2 - x1)) * (toCompare.endPiece.x - x1)))
                return ((toCompare.startPiece.y >= y1 && toCompare.endPiece.y <= y2) && (toCompare.startPiece.x >= x1 && toCompare.endPiece.x <= x2));
        }
        return false;
    }

}
