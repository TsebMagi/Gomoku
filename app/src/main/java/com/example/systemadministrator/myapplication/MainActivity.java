package com.example.systemadministrator.myapplication;

import android.content.Intent;
import android.content.IntentSender;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import com.example.systemadministrator.myapplication.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.Api;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.games.Games;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks {
    /*
        Potential object version
    private GameBoard playField;
    private Player player1;
    private Player player2;
*/

    private int players;
    private int size;
    private char style;
    private GoogleApiClient mGoogleApiClient;
    private Games.GamesOptions apiOptions;
    private int REQUEST_CODE_RESOLVE_ERR = 12345;
    private ConnectionResult mConnectionResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        players = 2;
        size = 15;
        style = 'c';
        setContentView(R.layout.activity_main);

        apiOptions = Games.GamesOptions.builder().setShowConnectingPopup(true, Gravity.TOP).build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Games.API, apiOptions)
                .addScope(Games.SCOPE_GAMES)
                .addOnConnectionFailedListener(this)
                .addConnectionCallbacks(this)
                .build();
    }

    public void onPlayClick(View v) {
        //TODO update screen transition to package the info gathered here.
        Intent intent = new Intent(MainActivity.this, BoardActivity.class);
        Bundle boardVars = new Bundle();
        boardVars.putInt("Size", size);
        boardVars.putChar("Style", style);
        if(players == 0) {
            boardVars.putString("Opponent", "Network");
        }
        if(players == 2)
            boardVars.putString("Opponent", "Human");
        else
            boardVars.putString("Opponent", "AI");
        intent.putExtras(boardVars);
        startActivity(intent);
    }

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

    public void onSignInClick(View v) { signIn(); }

    public void onSignOutClick(View v) { signOut(); }

    public void onIsSignedInClick(View v) { isLoginWorking(); }



    @Override
    public void onConnectionFailed(ConnectionResult result) {
        if (result.hasResolution()) {
            try {
                result.startResolutionForResult(this, REQUEST_CODE_RESOLVE_ERR);
            } catch (IntentSender.SendIntentException e) {
                mGoogleApiClient.connect();
            }
        }
        // Save the result and resolve the connection failure upon a user click.
        mConnectionResult = result;
        System.out.println(mConnectionResult);
    }

    @Override
    protected void onActivityResult(int requestCode, int responseCode, Intent intent) {
        if (requestCode == REQUEST_CODE_RESOLVE_ERR && responseCode == RESULT_OK) {
            mConnectionResult = null;
            mGoogleApiClient.connect();
        }
    }

    @Override
    protected void onStart() {
        System.out.println("onStart");
        super.onStart();
    }

    @Override
    protected void onStop() {
        System.out.println("onStop");
        super.onStop();
    }

    @Override
    public void onConnected(Bundle bundle) {
        System.out.println("onConnected");
    }

    @Override
    public void onConnectionSuspended(int i) {
        System.out.println("onConnectionSuspended");
        System.out.println(i);
    }

    public void isLoginWorking() {
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            System.out.println("Yes, signed in.");
            System.out.println(apiOptions);
        } else {
            System.out.println("No, not signed in.");
        }
    }

    private void signIn() {
        if(mGoogleApiClient.isConnected())
            System.out.println("Already connected.");
        else
            mGoogleApiClient.connect();
    }

    private void signOut() {
        if(mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
            System.out.println("Logged out successfully.");
        }
        else {
            System.out.println("Not logged in when you pressed sign out.");
        }
    }


}