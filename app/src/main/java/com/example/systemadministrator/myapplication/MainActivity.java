package com.example.systemadministrator.myapplication;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    private GameBoard playField;
    private Player player1;
    private Player player2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void onPlayClick(View v){
        //TODO update screen transition to appropriate grids
        setContentView(R.layout.test_activity);
    }
}
