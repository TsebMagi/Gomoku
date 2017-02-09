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
        assertEquals(board.gameOver(), 0);
    }
}