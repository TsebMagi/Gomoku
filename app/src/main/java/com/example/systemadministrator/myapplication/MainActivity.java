package com.example.systemadministrator.myapplication;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import com.example.systemadministrator.Gomoku.R;

public class MainActivity extends AppCompatActivity {
    /*
        Potential object version
    private GameBoard playField;
    private Player player1;
    private Player player2;
*/

    private int players;
    private int size;
    private char style;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        players = 2;
        size = 15;
        style = 'c';
        setContentView(R.layout.activity_main);
    }

    public void onPlayClick(View v) {
        //TODO update screen transition to package the info gathered here.
        Intent intent = new Intent(MainActivity.this, BoardActivity.class);
        Bundle boardVars = new Bundle();
        boardVars.putInt("Size", size);
        boardVars.putChar("Style", style);
        if(players == 3)
            boardVars.putString("Opponent", "Network");
        else if(players == 1)
            boardVars.putString("Opponent", "AI");
        else
            boardVars.putString("Opponent", "Human");
        intent.putExtras(boardVars);
        startActivity(intent);
    }

    public void onNetworkClick(View v) { players = 3; }

    public void on1PlayerClick(View v) {
        players = 1;
    }

    public void on2PlayerClick(View v) {
        players = 2;
    }

    public void on10XClick(View v) {
        size = 10;
    }

    public void on15XClick(View v){
        size = 15;
    }

    public void on20XClick(View v){
        size = 20;
    }

    public void onClassicClick(View v) {
        style = 'c';
    }

    public void onFreestyleClick(View v) {
        style = 'f';
    }

}