package com.example.systemadministrator.myapplication;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.TextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import android.util.Log;

import com.example.systemadministrator.Gomoku.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.games.Games;
import com.google.android.gms.games.GamesActivityResultCodes;
import com.google.android.gms.games.GamesStatusCodes;
import com.google.android.gms.games.multiplayer.Invitation;
import com.google.android.gms.games.multiplayer.Multiplayer;
import com.google.android.gms.games.multiplayer.OnInvitationReceivedListener;
import com.google.android.gms.games.multiplayer.Participant;
import com.google.android.gms.games.multiplayer.realtime.RealTimeMessage;
import com.google.android.gms.games.multiplayer.realtime.RealTimeMessageReceivedListener;
import com.google.android.gms.games.multiplayer.realtime.Room;
import com.google.android.gms.games.multiplayer.realtime.RoomConfig;
import com.google.android.gms.games.multiplayer.realtime.RoomStatusUpdateListener;
import com.google.android.gms.games.multiplayer.realtime.RoomUpdateListener;

public class BoardActivity extends AppCompatActivity
        implements GoogleApiClient.OnConnectionFailedListener,
        GoogleApiClient.ConnectionCallbacks,
        RoomUpdateListener,
        RealTimeMessageReceivedListener,
        RoomStatusUpdateListener,
        OnInvitationReceivedListener {

    //Working Board
    private int dimension;
    private String player2Type;
    private char style;
    private Player[] players = new Player[2];
    private ImageView[][] boardArray;
    private Context context;
    private GameBoard piecesOnBoard; //0 is uninitialized, 1 or 2 to represent players pieces, 3 is empty
    private Drawable[] drawCell = new Drawable[3]; //0 is empty, 1 or 2 for different player pieces
    private int xPos, yPos; // x and y position of move
    private int playerTurn; // which players turn it is
    private boolean gameOverFlag;
    private int player1Wins;
    private int player2Wins;
    private int tieGames;
    private int playerTurnLastRound;
    TextView p1Status;
    TextView p2Status;
    TextView tieStatus;
    boolean playerWaiting;

    //Client used to interact with the Google API
    private GoogleApiClient mGoogleApiClient;
    String mRoomId = null;
    //Are we playing online
    boolean mMultiplayer = false;
    //Are we logged in
    boolean loggedin = false;
    //Participants
    ArrayList<Participant> mParticipants = null;
    //My participant ID
    String mMyId = null;
    //My invitation ID
    String mIncomingInvitationID = null;
    // Message buffer for sending messages
    byte[] mMsgBuf = new byte[2];
    //resolving connection
    private boolean mResolvingConnectionFailure = false;



    private static final int RC_SIGN_IN = 9001;
    final static int REQUEST_CODE_RESOLVE_ERR = 1234;
    final static int RC_SELECT_PLAYERS = 4321;
    final static int RC_WAITING_ROOM = 2345;
    final static int MIN_PLAYERS = 2;
    final static int RC_INVITATION_INBOX = 3821;
    private static final String TAG = "BoardActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG + " onCreate", "Created a new board");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_board);
        System.out.println("hi");
        Log.w("onCreate", "starting onCreate");

        Bundle extras =  getIntent().getExtras();
        dimension = extras.getInt("Size");
        player2Type = extras.getString("Opponent");
        style = extras.getChar("Style");

        players[0] = new HumanPlayer();
        if(player2Type.equals("Human")) {
            Log.d("onCreate", "player2type = human");
            players[1] = new HumanPlayer();
            playerWaiting = false;
            localView();
        }
        else if(player2Type.equals("Network")) {
            Log.d(TAG + " onCreate", "Setting up a network game");
            players[1] = new NetworkPlayer();
            playerWaiting = true;

            //Website used for passing messages between clients
            WebView myWebView = (WebView) findViewById(R.id.webview);
            WebSettings webSettings = myWebView.getSettings();
            webSettings.setJavaScriptEnabled(true);
            myWebView.loadUrl("http://www.noahfreed.com/gomoku.html");
            localView();

            //Create API client
            /*mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addApi(Games.API)
                    .addScope(Games.SCOPE_GAMES)
                    .addOnConnectionFailedListener(this)
                    .addConnectionCallbacks(this)
                    .build();
            onlineView();*/
        }
        else {
            players[1] = new AIPlayer();
            localView();
        }

        if(!playerWaiting)
            initBoard();

    }

    public void initBoard(){

        piecesOnBoard = new GameBoard(dimension);
        boardArray = new ImageView[dimension][dimension];
        gameOverFlag = false;

        context = this;

        if(players[1].getGoesFirst())
            playerTurn = 2;
        else
            playerTurn = 1;

        playerTurnLastRound = playerTurn;

        players[playerTurn-1].setHasChosen(false);

        loadDrawables();
        createBoard();

        player1Wins = 0;
        player2Wins = 0;
        tieGames = 0;
        p1Status = (TextView) findViewById(R.id.p1Status);
        p2Status = (TextView) findViewById(R.id.p2Status);
        tieStatus = (TextView) findViewById(R.id.tieStatus);
        updateGameStatus();

        startTimers(playerTurn);

        Timer timer = new Timer();
        timer.schedule(new checkGameOver(), 0, 1000);

        if(playerTurn == 2 && players[1] instanceof AIPlayer)
            doAIMove();
    }

    private void loadDrawables() {
        drawCell[0] = context.getResources().getDrawable(R.drawable.plus, null);//empty cell
        drawCell[1] = context.getResources().getDrawable(R.drawable.red_piece, null);//drawable for player 1
        drawCell[2] = context.getResources().getDrawable(R.drawable.black_piece, null);//drawable for player 2
    }

    private void createBoard(){
        int sizeofCell = Math.round(ScreenWidth() / dimension);

        LinearLayout.LayoutParams singleCellDimensions = new LinearLayout.LayoutParams(sizeofCell, sizeofCell);
        LinearLayout.LayoutParams singleRowDimensions = new LinearLayout.LayoutParams(sizeofCell * dimension, sizeofCell);
        LinearLayout BoardGame = (LinearLayout) findViewById(R.id.Board);

        //create cells
        for (int i = 0; i < dimension; i++) {
            LinearLayout newRow = new LinearLayout(context); //make a row
            for (int j = 0; j < dimension; j++) { // loop through row and create individual cells
                boardArray[i][j] = new ImageView(context);
                boardArray[i][j].setBackground(drawCell[0]); //creates background + for all locations
                final int x = i; //each location knows it x,y coordinates
                final int y = j;
                boardArray[i][j].setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!gameOverFlag && !players[playerTurn-1].hasChosen() && players[playerTurn-1] instanceof HumanPlayer && piecesOnBoard.getPieceAtXY(x,y) == 0) { // make sure cell is empty
                            xPos = x;
                            yPos = y; //when a location is clicked it sets global x,y variables to be accessed in other functions
                            makeMove();
                        }
                    }
                });
                newRow.addView(boardArray[i][j], singleCellDimensions);
            }
            BoardGame.addView(newRow, singleRowDimensions); //add new row to board layout
        }
    }

    private void makeMove() {
        boardArray[xPos][yPos].setImageDrawable(drawCell[playerTurn]); //put players piece on space chosen
        piecesOnBoard.placePiece(playerTurn, xPos, yPos); //keep track of board in 2d array data structure
        checkGameOver();
        if (playerTurn == 1 && players[0].hasChosen == false) {
            players[0].setHasChosen(true);
            players[1].setHasChosen(false);
            playerTurn = 2;
            switchTimers(1);
            if (players[1] instanceof NetworkPlayer)
                ((NetworkPlayer) players[1]).sendMove(xPos, yPos);
            else if (players[1] instanceof AIPlayer && !gameOverFlag) {
                doAIMove();
            }
        }
        else if(playerTurn == 2 && players[1].hasChosen == false){
            players[1].setHasChosen(true);
            players[0].setHasChosen(false);
            playerTurn = 1;
            switchTimers(0);
            if(players[0] instanceof NetworkPlayer)
                ((NetworkPlayer) players[1]).sendMove(xPos, yPos);
            else if(players[0] instanceof AIPlayer && !gameOverFlag){
                doAIMove();
            }
        }
    }

    private void doAIMove(){
        Coordinates move = ((AIPlayer) players[1]).generateMove(piecesOnBoard);
        while(piecesOnBoard.getPieceAtXY(move.x, move.y) != 0)
            move = ((AIPlayer) players[1]).generateMove(piecesOnBoard);
        makeMove(move);
        switchTimers(0);
    }

    private void switchTimers(int nextPlayer){
        if(!gameOverFlag){
            if(nextPlayer == 0){
                if(!(players[1] instanceof AIPlayer))
                    players[1].stopTimer();
                players[0].startTimer(p1Status, false);
            }
            else{
                players[0].stopTimer();
                if(!(players[1] instanceof AIPlayer))
                    players[1].startTimer(p2Status, false);
            }
        }
    }

    private void startTimers(int firstPlayer){
        if(firstPlayer == 1){
            players[0].startTimer(p1Status, true);
            if(!(players[1] instanceof AIPlayer)) {
                players[1].startTimer(p2Status, true);
                players[1].stopTimer();
            }
        }
        else{
            players[0].startTimer(p1Status, true);
            players[0].stopTimer();
            if(!(players[1] instanceof AIPlayer))
                players[1].startTimer(p2Status, true);
        }
    }

    private void makeMove(Coordinates move) {
        boardArray[move.x][move.y].setImageDrawable(drawCell[playerTurn]); //put players piece on space chosen
        piecesOnBoard.placePiece(playerTurn, move.x, move.y); //keep track of board in 2d array data structure
        checkGameOver();
        
        if (playerTurn == 1) {
            players[0].setHasChosen(true);
            players[1].setHasChosen(false);
            playerTurn = 2;
        }
        else{
            players[1].setHasChosen(true);
            players[0].setHasChosen(false);
            playerTurn = 1;
        }
    }

    class checkGameOver extends TimerTask {
        public void run() {
            runOnUiThread(new Runnable() {
                public void run () {
                    if( players[1] instanceof NetworkPlayer)
                        checkNetworkPlayer();
                    if(! gameOverFlag) {
                        gameOverFlag = players[0].checkExpired() || players[1].checkExpired();
                        checkGameOver();
                    }
                }
            });
        }
    }

    private void checkNetworkPlayer(){
        if(playerTurn == 2 && ((NetworkPlayer) players[1]).getMadeMove()) {
            Coordinates move = ((NetworkPlayer) players[1]).getNextMove();
            if(piecesOnBoard.moveIsValid(move)) {
                makeMove(move);
                ((NetworkPlayer) players[1]).setMadeMove(false);
            }
        }
    }

    private void checkGameOver(){
        int result = piecesOnBoard.gameOver(style);
        CharSequence text;
        if(result != 0 && !gameOverFlag){
            gameOverFlag = true;
            players[0].stopTimer();
            if(!(players[1] instanceof AIPlayer))
                players[1].stopTimer();
            if(result == 1) {
                text = "Player 1 wins!";
                player1Wins++;
            }
            else if(result == 2) {
                text = "Player 2 wins!";
                player2Wins++;
            }
            else {
                text = "Tie Game! Nobody wins";
                tieGames++;
            }
            updateGameStatus();
            Context context = getApplicationContext();
            int duration = Toast.LENGTH_LONG;
            Toast toast = Toast.makeText(context, text, duration);
            toast.show();
            afterGame();
        }
        else if(result == 0 && gameOverFlag){
            if(players[0].checkExpired()) {
                if(!(players[1] instanceof AIPlayer))
                    players[1].stopTimer();
                text = "Player 2 wins!";
                player2Wins++;
            }
            else {
                players[0].stopTimer();
                text = "Player 1 wins!";
                player1Wins++;
            }
            updateGameStatus();
            Context context = getApplicationContext();
            int duration = Toast.LENGTH_LONG;
            Toast toast = Toast.makeText(context, text, duration);
            toast.show();
            afterGame();
        }
    }

    private float ScreenWidth() {
        Resources resources = context.getResources();
        DisplayMetrics dm = resources.getDisplayMetrics();
        return dm.widthPixels-100; //50 pixels of padding on each side
    }

    //after the game is over, make the buttons appear.
    private void afterGame() {
        Button regameButton =(Button)findViewById(R.id.regameButton);
        Button menuButton =(Button)findViewById(R.id.menuButton);
        regameButton.setVisibility(View.VISIBLE);
        menuButton.setVisibility(View.VISIBLE);

    }

    //sends a the user back to the main menu when clicking menuButton
    public void onMenuClick(View v) {
        Intent intent = new Intent(BoardActivity.this, MainActivity.class);
        startActivity(intent);

    }

    //regame sets the array to 0, the loop clears the board ui.
    public void onReGameClick(View v) {
        piecesOnBoard.reGame();
        for(int i = 0; i < dimension; i++) {
            for(int j = 0; j < dimension; j++) {
                boardArray[i][j].setImageDrawable(drawCell[0]);
            }
        }
        if(playerTurnLastRound == 1)
            playerTurn = 2;
        else
            playerTurn = 1;

        playerTurnLastRound = playerTurn;

        players[playerTurn-1].setHasChosen(false);
        gameOverFlag = false;
        Button regameButton =(Button)findViewById(R.id.regameButton);
        Button menuButton =(Button)findViewById(R.id.menuButton);
        regameButton.setVisibility(View.INVISIBLE);
        menuButton.setVisibility(View.INVISIBLE);
        players[0].resetTimers();
        players[1].resetTimers();
        startTimers(playerTurn);

        if(playerTurn == 2 && players[1] instanceof AIPlayer)
            doAIMove();
    }

    public void onQuickGameClick(View v) {
        startQuickGame();
    }

    public void onOnlineGameClick(View v) {
        startGame();
    }

    private void updateGameStatus(){
        String p1Text = String.format("P1\n %d", player1Wins);
        String p2Text = String.format("P2\n%d", player2Wins);
        String tieText = String.format("Tie\n%d", tieGames);
        p1Status.setText(p1Text);
        players[0].updateText(p1Text);
        p2Status.setText(p2Text);
        players[1].updateText(p2Text);
        tieStatus.setText(tieText);
    }







    private void onlineView() {
        //show
        Button button = (Button) findViewById(R.id.loginButton);
        button.setVisibility(View.VISIBLE);

        //hide
        LinearLayout linearLayout = (LinearLayout)findViewById(R.id.Board);
        linearLayout.setVisibility(View.GONE);
        TextView textView = (TextView) findViewById(R.id.p1Status);
        textView.setVisibility(View.GONE);
        textView = (TextView) findViewById(R.id.p2Status);
        textView.setVisibility(View.GONE);
        textView = (TextView) findViewById(R.id.tieStatus);
        textView.setVisibility(View.GONE);
        button = (Button) findViewById(R.id.onlineGameButton);
        button.setVisibility(View.GONE);
        button = (Button) findViewById(R.id.quickGameButton);
        button.setVisibility(View.GONE);
        button = (Button) findViewById(R.id.logOutButton);
        button.setVisibility(View.GONE);
        button = (Button) findViewById(R.id.lookInviteButton);
        button.setVisibility(View.GONE);
        boolean showInvPopup;
        if (mIncomingInvitationID == null) {
            // no invitation, so no popup
            showInvPopup = false;
        } else {
            showInvPopup = true;
        }
        findViewById(R.id.invitation_popup).setVisibility(showInvPopup ? View.VISIBLE : View.GONE);
    }

    private void localView() {
        //show
        LinearLayout linearLayout = (LinearLayout)findViewById(R.id.Board);
        linearLayout.setVisibility(View.VISIBLE);
        TextView textView = (TextView) findViewById(R.id.p1Status);
        textView.setVisibility(View.VISIBLE);
        textView = (TextView) findViewById(R.id.p2Status);
        textView.setVisibility(View.VISIBLE);
        textView = (TextView) findViewById(R.id.tieStatus);
        textView.setVisibility(View.VISIBLE);

        //hide
        Button button = (Button) findViewById(R.id.onlineGameButton);
        button.setVisibility(View.GONE);
        button = (Button) findViewById(R.id.quickGameButton);
        button.setVisibility(View.GONE);
        button = (Button) findViewById(R.id.loginButton);
        button.setVisibility(View.GONE);
        button = (Button) findViewById(R.id.logOutButton);
        button.setVisibility(View.GONE);
        findViewById(R.id.invitation_popup).setVisibility(View.GONE);
    }

    public void onInviteClick(View v) {
        System.out.println("Check1");
        Intent intent = Games.Invitations.getInvitationInboxIntent(mGoogleApiClient);
        System.out.println("Check2");
        startActivityForResult(intent, RC_INVITATION_INBOX);
    }

    public void onLoginClick(View v) {
        loggedin = false;
        toggleLogin();
        mGoogleApiClient.connect();
    }

    public void onLogOutClick(View v) {
        loggedin = true;
        toggleLogin();
        mGoogleApiClient.disconnect();
    }

    private void toggleLogin() {
        if(loggedin == false) {
            mGoogleApiClient.connect();
            Button button = (Button) findViewById(R.id.loginButton);
            button.setVisibility(View.GONE);
            button = (Button) findViewById(R.id.logOutButton);
            button.setVisibility(View.VISIBLE);
            button = (Button) findViewById(R.id.quickGameButton);
            button.setVisibility(View.VISIBLE);
            button = (Button) findViewById(R.id.onlineGameButton);
            button.setVisibility(View.VISIBLE);
            button = (Button) findViewById(R.id.lookInviteButton);
            button.setVisibility(View.VISIBLE);
        }
        else {
            mGoogleApiClient.disconnect();
            Button button = (Button) findViewById(R.id.loginButton);
            button.setVisibility(View.VISIBLE);
            button = (Button) findViewById(R.id.logOutButton);
            button.setVisibility(View.GONE);
            button = (Button) findViewById(R.id.quickGameButton);
            button.setVisibility(View.GONE);
            button = (Button) findViewById(R.id.onlineGameButton);
            button.setVisibility(View.GONE);
            button = (Button) findViewById(R.id.lookInviteButton);
            button.setVisibility(View.GONE);

        }

    }

    private void startGame() {
        Log.d(TAG + " startGame", "Started multiplayer activity");
        Intent intent = Games.RealTimeMultiplayer.getSelectOpponentsIntent(mGoogleApiClient, 1, 1);
        startActivityForResult(intent, RC_SELECT_PLAYERS);
    }

    private void startQuickGame() {
        final int MIN_OPPONENTS = 1, MAX_OPPONENTS = 1;
        Bundle autoMatchCriteria = RoomConfig.createAutoMatchCriteria(MIN_OPPONENTS, MAX_OPPONENTS, 0);
        RoomConfig.Builder roomConfigBuilder = RoomConfig.builder(this)
                .setMessageReceivedListener(this)
                .setRoomStatusUpdateListener(this)
                .setAutoMatchCriteria(autoMatchCriteria);
        //prevent screen from sleeping
        keepScreenOn();
        //create room
        Games.RealTimeMultiplayer.create(mGoogleApiClient, roomConfigBuilder.build());
        //go to game screen
        //but should probably go to some waiting screen
        localView();
        //should probably reset timers here.
    }

    void keepScreenOn() {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    void stopKeepingScreenOn() {
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    //need to work on this one. Find out where I need to go with this one.
    void leaveRoom() {
        stopKeepingScreenOn();
        if (mRoomId != null) {
            Games.RealTimeMultiplayer.leave(mGoogleApiClient, this, mRoomId);
            mRoomId = null;
            //switchToScreen(R.id.screen_wait);
        } else {
            //switchToMainScreen();
        }
        onlineView();
    }

    void showWaitingRoom(Room room) {
        Log.d("showWaitingRoom","Displaying the waiting room");
        final int MIN_PLAYERS = Integer.MAX_VALUE;
        Intent i = Games.RealTimeMultiplayer.getWaitingRoomIntent(mGoogleApiClient, room, MIN_PLAYERS);
        startActivityForResult(i, RC_WAITING_ROOM);
    }

    void acceptInviteToRoom(String s) {
        // accept the invitation
        RoomConfig.Builder roomConfigBuilder = RoomConfig.builder(this)
                .setInvitationIdToAccept(s)
                .setMessageReceivedListener(this)
                .setRoomStatusUpdateListener(this);
        keepScreenOn();
        Games.RealTimeMultiplayer.join(mGoogleApiClient, roomConfigBuilder.build());
    }

    void updateRoom(Room room) {
        if (room != null) {
            mParticipants = room.getParticipants();
        }
        /*
        if (mParticipants != null) {
            updatePeerScoresDisplay();
        }
        */
    }

    @Override
    public void onInvitationReceived(Invitation invitation) {
        Log.d("onInvitationReceived", "Got a game invite" + invitation);
        mIncomingInvitationID = invitation.getInvitationId();
        ((TextView) findViewById(R.id.incoming_invitation_text)).setText(
                invitation.getInviter().getDisplayName() + " " +
                        getString(R.string.is_inviting_you));
        System.out.println(mIncomingInvitationID);
    }
    @Override
    public void onInvitationRemoved(String s) {
        Log.d("onInvitationRemoved", "Invitation went away");
        if (mIncomingInvitationID.equals(s) && mIncomingInvitationID != null) {
            mIncomingInvitationID = null;
            System.out.println("invitation removed");
        }
    }
    @Override
    public void onConnectedToRoom(Room room) {
        Log.d("onConnectedToRoom", "Connected to room" + room);
        //get participants and my ID:
        mParticipants = room.getParticipants();
        mMyId = room.getParticipantId(Games.Players.getCurrentPlayerId(mGoogleApiClient));
        // save room ID if its not initialized in onRoomCreated() so we can leave cleanly before the game starts.
        if(mRoomId==null)
            mRoomId = room.getRoomId();
        initBoard();
    }
    @Override
    public void onActivityResult(int request, int response, Intent data) {
        Log.d("onActivityResult", "Activity " + request + " produced result " + response);
        super.onActivityResult(request, response, data);
        if (request == RC_SELECT_PLAYERS) {
            Log.d(TAG, "RC_SELECT_PLAYERS");
            if (response != Activity.RESULT_OK) {
                Log.w(TAG, "*** select players UI cancelled, " + response);
                // user canceled
                return;
            }
            Log.d(TAG, "Select players UI succeeded.");
            // get the invitee list
            Bundle extras = data.getExtras();
            final ArrayList<String> invitees = data.getStringArrayListExtra(Games.EXTRA_PLAYER_IDS);
            Log.d(TAG, "Invitee count: " + invitees.size());
            // get auto-match criteria
            Bundle autoMatchCriteria = null;
            int minAutoMatchPlayers = data.getIntExtra(Multiplayer.EXTRA_MIN_AUTOMATCH_PLAYERS, 0);
            int maxAutoMatchPlayers = data.getIntExtra(Multiplayer.EXTRA_MAX_AUTOMATCH_PLAYERS, 0);
            if (minAutoMatchPlayers > 0 || maxAutoMatchPlayers > 0) {
                autoMatchCriteria = RoomConfig.createAutoMatchCriteria(
                        minAutoMatchPlayers, maxAutoMatchPlayers, 0);
                Log.d(TAG, "Automatch criteria: " + autoMatchCriteria);
            } else {
                autoMatchCriteria = null;
            }
            // create the room and specify a variant if appropriate
            Log.d(TAG, "Creating room...");
            RoomConfig.Builder roomConfigBuilder = makeBasicRoomConfigBuilder();
            roomConfigBuilder.addPlayersToInvite(invitees);
            if (autoMatchCriteria != null)
                roomConfigBuilder.setAutoMatchCriteria(autoMatchCriteria);
            // prevent screen from sleeping
            keepScreenOn();
            RoomConfig roomConfig = roomConfigBuilder.build();
            Games.RealTimeMultiplayer.create(mGoogleApiClient, roomConfig);
            Log.d(TAG, "Room created, waiting for it to be ready...");
        }
        if (request == RC_WAITING_ROOM) {
            Log.d(TAG, "RC_WAITING_ROOM");
            if (response == Activity.RESULT_OK) {
                Log.d(TAG, "RC_WAITING_ROOM, Activity.RESULT_OK");
                //start the game
                localView();
            }
            else if (response == Activity.RESULT_CANCELED) {
                Log.d(TAG, "RC_WAITING_ROOM, Activity.RESULT_CANCELED");
                // in this example, we take the simple approach and just leave the room:
                leaveRoom();
            }
            else if (response == GamesActivityResultCodes.RESULT_LEFT_ROOM) {
                Log.d(TAG, "RC_WAITING_ROOM, Activity.RESULT_LEFT_ROOM");
                // player wants to leave the room.
                leaveRoom();
            }
        }
        if(request == RC_INVITATION_INBOX) {
            Log.w("HI", "*** CANT EVEN START " + response);
            if(response != Activity.RESULT_OK) {
                Log.w("HI", "*** invitation inbox UI cancelled, " + response);
                return;
            }
            Log.w("HI", "*** invitation inbox UI succeeded, " + response);
            // get the selected invitation
            Bundle extras = data.getExtras();
            Invitation invitation =
                    extras.getParcelable(Multiplayer.EXTRA_INVITATION);
            // accept it!
            RoomConfig roomConfig = makeBasicRoomConfigBuilder()
                    .setInvitationIdToAccept(invitation.getInvitationId())
                    .build();
            Games.RealTimeMultiplayer.join(mGoogleApiClient, roomConfig);

            keepScreenOn();
        }
        /*
        if (request == RC_SIGN_IN) {
            loggedin = false;
            mResolvingConnectionFailure = false;
            if (request == RESULT_OK) {
                mGoogleApiClient.connect();
            }
        }
        */
    }

    // create a RoomConfigBuilder that's appropriate for your implementation
    private RoomConfig.Builder makeBasicRoomConfigBuilder() {
        return RoomConfig.builder(this)
                .setMessageReceivedListener(this)
                .setRoomStatusUpdateListener(this);
    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.d("onConnected", "Connected");
        if (bundle != null) {
            Invitation inv =
                    bundle.getParcelable(Multiplayer.EXTRA_INVITATION);

            if (inv != null && inv.getInvitationId() != null) {
                acceptInviteToRoom(inv.getInvitationId());
                return;
            }
        }
        //switch to main screen?
    }
    @Override
    public void onConnectionSuspended(int i) {
        Log.d("onConnectionSuspended", "Connection suspended, attempting to reconnect");
        //attempt to reconnect
        mGoogleApiClient.connect();
    }
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d("onConnectionFailed", "Connection failed");
        if (connectionResult.hasResolution()) {
            try {
                connectionResult.startResolutionForResult(this, REQUEST_CODE_RESOLVE_ERR);
            } catch (IntentSender.SendIntentException e) {
                mGoogleApiClient.connect();
            }
        }
    }
    @Override
    public void onJoinedRoom(int statusCode, Room room) {
        Log.d("onJoinedRoom", "Joined room");
        if (statusCode != GamesStatusCodes.STATUS_OK) {
            stopKeepingScreenOn();
            return;
        }
        showWaitingRoom(room);
    }
    @Override
    public void onLeftRoom(int i, String s) {
        Log.d("onLeftRoom", "Left room");
        //go to main screen
    }
    @Override
    public void onDisconnectedFromRoom(Room room) {
        Log.d("onDisconnectedFromRoom","Disconnected from room");
        // leave the room
        Games.RealTimeMultiplayer.leave(mGoogleApiClient, null, mRoomId);
        // clear the flag that keeps the screen on
        stopKeepingScreenOn();
        // show error message and return to main screen
        onlineView();
    }
    @Override
    public void onRoomCreated(int statusCode, Room room) {
        Log.d("onRoomCreated", "Created room " + room + " with status code " + statusCode);
        if (statusCode != GamesStatusCodes.STATUS_OK) {
            stopKeepingScreenOn();
            Log.e("Problem...", "*** Error: onRoomCreated, status " + statusCode);
            return;
        }
        mRoomId = room.getRoomId();
        showWaitingRoom(room);
    }
    @Override
    public void onRoomConnected(int i, Room room) {
        Log.d("onRoomConnected", "Connected to a room");
        if(i != GamesStatusCodes.STATUS_OK) {
            stopKeepingScreenOn();
        }
    }
    @Override
    public void onPeerDeclined(Room room, List<String> list) {
        Log.d("onPeerDeclined","");
        updateRoom(room);
    }
    @Override
    public void onPeerInvitedToRoom(Room room, List<String> list) {
        Log.d("onPeerInvitedToRoom", "");
        updateRoom(room);
    }
    @Override
    public void onPeerLeft(Room room, List<String> list) {
        Log.d("onPeerLeft","");
        updateRoom(room);
    }
    @Override
    public void onPeerJoined(Room room, List<String> list) {
        Log.d("onPeerJoined", "");
        updateRoom(room);
        initBoard();
    }
    @Override
    public void onRoomAutoMatching(Room room) {
        Log.d("onRoomAutoMatching", "");
        updateRoom(room);
    }
    @Override
    public void onRoomConnecting(Room room) {
        Log.d("onRoomConnecting","");
        updateRoom(room);
    }
    @Override
    public void onPeersConnected(Room room, List<String> list) {
        Log.d("onPeersConnected","");
        updateRoom(room);
        initBoard();
    }
    @Override
    public void onPeersDisconnected(Room room, List<String> list) {
        Log.d("onPeersDisconnected", "");
        updateRoom(room);
    }
    @Override
    public void onRealTimeMessageReceived(RealTimeMessage realTimeMessage) {
        Log.d("onRealTimeMessageReceiv", "Got a realtime message");
        // get real-time message
        final byte[] b = realTimeMessage.getMessageData();
        final String sender = realTimeMessage.getSenderParticipantId();
        Log.d("HI", "Message received from: " + sender);
        // process message
    }



    @Override
    public void onP2PConnected(String s) {
        Log.d("onP2PConnected","");
    }
    @Override
    public void onP2PDisconnected(String s) {
        Log.d("onP2PDisconnected","");
    }

}
