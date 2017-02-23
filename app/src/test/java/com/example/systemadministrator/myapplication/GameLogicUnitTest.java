package com.example.systemadministrator.myapplication;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Created by Noah Freed on 2/7/2017
 * Local unit test for the game's rules and systems, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class GameLogicUnitTest {
    @Test
    public void addition_isCorrect() throws Exception {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void emptyBoard () {
        //0 = game still going, 1 or 2 = player win, 3 = tie
        GameBoard board = new GameBoard(10);
        assertEquals(board.gameOver('c'), 0);
    }

    @Test
    public void fiveMoves () {
        GameBoard board = new GameBoard(10);
        board.placePiece(1,1,1);
        board.placePiece(2,2,2);
        board.placePiece(1,3,3);
        board.placePiece(2,4,4);
        board.placePiece(1,5,5);
        assertEquals(board.gameOver('c'), 0);
    }

    @Test
    public void quickWin () {
        GameBoard board = new GameBoard(10);
        board.placePiece(1,1,1);
        board.placePiece(1,2,2);
        board.placePiece(1,3,3);
        board.placePiece(1,4,4);
        board.placePiece(1,5,5);
        assertEquals(board.gameOver('c'), 1);
    }

    @Test
    public void blocked () {
        GameBoard board = new GameBoard(10);
        board.placePiece(2,1,1);
        board.placePiece(1,2,2);
        board.placePiece(2,7,7);
        board.placePiece(1,3,3);
        board.placePiece(1,4,4);
        board.placePiece(1,5,5);
        board.placePiece(1,6,6);
        assertEquals(board.gameOver('c'), 0);
    }

    @Test
    public void cross () {
        GameBoard board = new GameBoard(10);
        board.placePiece(1,1,5);
        board.placePiece(2,5,1);
        board.placePiece(1,2,5);
        board.placePiece(2,5,2);
        board.placePiece(1,3,5);
        board.placePiece(2,5,3);
        board.placePiece(1,4,5);
        board.placePiece(2,5,4);
        board.placePiece(1,6,5);
        board.placePiece(2,5,5);
        assertEquals(board.gameOver('c'), 2);
    }

    @Test
    public void tie () {
        GameBoard board = new GameBoard(10);
        for(int i = 0; i < 10; i++){
            for(int j = 0; j < 10; j++){
                board.placePiece((i/2+j)%2+1,i,j);
            }
        }
        assertEquals((7/2)%2,1);
        assertEquals(board.gameOver('c'), 3);
    }

    @Test
    public void blockedByEdge () {
        GameBoard board = new GameBoard(15);
        board.placePiece(2, 5, 0);
        for(int i = 0; i < 5; i++){
            board.placePiece(1, i, 0);
        }
        assertEquals(board.gameOver('c'), 0);
    }

    @Test
    public void blockedByEdge2 () {
        GameBoard board = new GameBoard(15);
        board.placePiece(1, 9, 9);
        for(int i = 10; i < 15; i++){
            board.placePiece(2, i, i);
        }
        assertEquals(board.gameOver('c'), 0);
    }

    @Test
    public void twoHorizontalStreaks () {
        //name based on assumption that x-coordinate applies to horizontal position,
        //this assumption may not be consistent with the underlying implementation
        GameBoard board = new GameBoard(15);
        board.placePiece(1, 1, 1);
        board.placePiece(1, 2, 1);
        for(int i = 4; i < 9; i++){
            board.placePiece(1, i, 1);
        }
        assertEquals(board.gameOver('c'), 1);
    }

    @Test
    public void twoVerticalStreaks () {
        GameBoard board = new GameBoard(15);
        board.placePiece(1, 1, 1);
        board.placePiece(1, 1, 2);
        for(int i = 4; i < 9; i++){
            board.placePiece(1, 1, i);
        }
        assertEquals(board.gameOver('c'), 1);
    }

    @Test
    public void twoDiagonalStreaks () {
        GameBoard board = new GameBoard(15);
        board.placePiece(1, 1, 1);
        board.placePiece(1, 2, 2);
        for(int i = 4; i < 9; i++){
            board.placePiece(1, i, i);
        }
        assertEquals(board.gameOver('c'), 1);
    }

    @Test
    public void twoUpwardDiagonalStreaks () {
        GameBoard board = new GameBoard(15);
        board.placePiece(1, 1, 9);
        board.placePiece(1, 2, 8);
        for(int i = 4; i < 9; i++){
            board.placePiece(1, i, 9-i);
        }
        assertEquals(board.gameOver('c'), 1);
    }

    @Test
    public void compareStylesVertical () {
        GameBoard board = new GameBoard(10);
        board.placePiece(1,1,6);
        for(int i = 1; i < 6; i++){
            board.placePiece(1, 1, i);
        }
        assertEquals(board.gameOver('c'), 0);
        assertEquals(board.gameOver('f'), 1);
    }

    public void compareStylesHorizontal () {
        GameBoard board = new GameBoard(10);
        board.placePiece(1,6,1);
        for(int i = 1; i < 6; i++){
            board.placePiece(1, i, 1);
        }
        assertEquals(board.gameOver('c'), 0);
        assertEquals(board.gameOver('f'), 1);
    }

    public void compareStylesDownwardDiagonal () {
        GameBoard board = new GameBoard(10);
        board.placePiece(1,6,6);
        for(int i = 1; i < 6; i++){
            board.placePiece(1, i, i);
        }
        assertEquals(board.gameOver('c'), 0);
        assertEquals(board.gameOver('f'), 1);
    }

    public void compareStylesUpwardDiagonal () {
        GameBoard board = new GameBoard(10);
        board.placePiece(1,1,6);
        for(int i = 1; i < 6; i++){
            board.placePiece(1, 7-i, i);
        }
        assertEquals(board.gameOver('c'), 0);
        assertEquals(board.gameOver('f'), 1);
    }
}