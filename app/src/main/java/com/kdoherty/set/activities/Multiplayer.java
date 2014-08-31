package com.kdoherty.set.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.common.base.Strings;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.kdoherty.set.Constants;
import com.kdoherty.set.R;
import com.kdoherty.set.model.Card;
import com.kdoherty.set.model.Game;
import com.kdoherty.set.model.Games;
import com.kdoherty.set.model.Player;
import com.kdoherty.set.services.SetApi;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import io.socket.IOAcknowledge;
import io.socket.IOCallback;
import io.socket.SocketIO;
import io.socket.SocketIOException;
import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.converter.GsonConverter;
import retrofit.mime.TypedByteArray;
import retrofit.mime.TypedInput;

public class Multiplayer extends AbstractSetActivity {

    private SetApi mApi;
    private SocketIO mSocket = null;
    private String mGameId;

    private LinearLayout mPlayersLayout;
    private List<TextView> mPlayerViews;
    private int mPlayerCount;

    private ProgressBar mProgressSpinner;
    private TextView mFindingGameTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState, R.layout.activity_multiplayer, R.color.WHITE);
        initPlayerViews();
        initApi();
        initGameId();
        initSocket();
    }

    private void initPlayerViews() {
        mPlayersLayout = ((LinearLayout) findViewById(R.id.playersLayout));
        mPlayerViews = new ArrayList<>();
        mPlayerViews.add((TextView) findViewById(R.id.player1));
        mPlayerViews.add((TextView) findViewById(R.id.player2));
        mPlayerViews.add((TextView) findViewById(R.id.player3));
        mPlayerViews.add((TextView) findViewById(R.id.player4));
    }

    private void showProgressSpinner() {
        if (mProgressSpinner == null || mFindingGameTv == null) {
            mProgressSpinner = (ProgressBar) findViewById(R.id.progressBar);
            mFindingGameTv = (TextView) findViewById(R.id.findingGame);
        }
        mCardsGv.setVisibility(View.GONE);
        mPlayersLayout.setVisibility(View.GONE);
        mProgressSpinner.setVisibility(View.VISIBLE);
        mFindingGameTv.setVisibility(View.VISIBLE);
    }

    private void removeSpinner() {
        if (mProgressSpinner != null && mFindingGameTv != null) {
            mProgressSpinner.setVisibility(View.GONE);
            mFindingGameTv.setVisibility(View.GONE);
        }
        mCardsGv.setVisibility(View.VISIBLE);
        mPlayersLayout.setVisibility(View.VISIBLE);
    }

    @Override
    protected void initGameView(Game game) {
        if (game.isOver()) {
            mGame = game;
            finishGame();
        }
        List<Player> players = game.getPlayers();
        mPlayerCount = players.size();
        if (mPlayerCount == 1) {
            if (game.isStarted()) {
                mGame = game;
                // TODO: Wait 10 seconds for reconnect?
                finishGame();
            } else {
                showProgressSpinner();
            }
        } else {
            removeSpinner();
            super.initGameView(game);
            Collections.sort(players);
            mPlayersLayout.setWeightSum((float) mPlayerCount);
            for (int i = 0; i < mPlayerCount; i++) {
                Player player = players.get(i);
                TextView playerView = mPlayerViews.get(i);
                playerView.setText(player.getName() + '\n' + player.getSetCount());
                playerView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 22);
            }
        }
    }

    private void initGameId() {

        mApi.getGames(new Callback<Games>() {
            @Override
            public void success(Games games, Response response) {
                if (games.getGames().isEmpty()) {
                    addGameAndPlayer();
                } else {
                    for (Game game : games.getGames()) {
                        if (game.isOpen() && !game.isStarted()) {
                            mGameId = game.getId();
                            break;
                        }
                    }

                    if (Strings.isNullOrEmpty(mGameId)) {
                        addGameAndPlayer();
                        return;
                    }

                    mApi.addPlayer(mGameId, ActivityUtils.getUsername(Multiplayer.this), new Callback<Response>() {
                        @Override
                        public void success(Response response, Response response2) {
                            mSocket.emit("update", mGameId);
                        }

                        @Override
                        public void failure(RetrofitError error) {
                            Log.e(Constants.TAG, "Failure adding player to game: " + error.getMessage());
                        }
                    });
                }
            }

            @Override
            public void failure(RetrofitError error) {
                Log.e(Constants.TAG, "Failure getting games: " + error.getMessage());
            }
        });

    }

    private void addGameAndPlayer() {
        mApi.addGame(new Callback<Response>() {
            @Override
            public void success(Response response, Response response2) {
                String resp = ActivityUtils.getResponseStr(response);
                mGameId = resp.substring(1, resp.length() - 1);

                mApi.addPlayer(mGameId, ActivityUtils.getUsername(Multiplayer.this), new Callback<Response>() {
                    @Override
                    public void success(Response response, Response response2) {
                        updateContentFromServer();
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        Log.e(Constants.TAG, "Failure adding player to game: " + error.getMessage());
                    }
                });

            }

            @Override
            public void failure(RetrofitError error) {
                Log.e(Constants.TAG, "Failure adding game: " + error.getMessage());
            }
        });
    }

    private void initSocket() {
        if (mSocket == null) {
            try {
                String hostedSocked = SetApi.ENDPOINT + "/";
                mSocket = new SocketIO(hostedSocked);
            } catch (MalformedURLException e) {
                throw new RuntimeException("Malformed URL in initializing mSocket");
            }
        }
        mSocket.connect(new IOCallback() {
            @Override
            public void onMessage(JsonElement json, IOAcknowledge ack) {
                // Nothing
            }

            @Override
            public void onMessage(String data, IOAcknowledge ack) {
                // Nothing
            }

            @Override
            public void on(String event, IOAcknowledge ack, JsonElement... args) {
                if (event.equalsIgnoreCase("update")) {
                    updateContentFromServer();
                }
            }

            @Override
            public void onError(SocketIOException socketIOException) {
                Log.e(Constants.TAG, "Received error from socket: " + socketIOException.getMessage());
            }

            @Override
            public void onDisconnect() {
                Log.v(Constants.TAG, "Socket connection terminated.");
            }

            @Override
            public void onConnect() {
                Log.v(Constants.TAG, "Socket connection established.");
            }
        });
    }

    private void initApi() {
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(Card.class, new CardDeserializer())
                .create();

        RestAdapter adapter = new RestAdapter.Builder()
                .setEndpoint(SetApi.ENDPOINT)
                .setConverter(new GsonConverter(gson))
                .build();

        mApi = adapter.create(SetApi.class);
    }

    public void updateContentFromServer() {

        mApi.getGame(mGameId, new Callback<Game>() {
            @Override
            public void success(Game game, Response response) {
                if (game != null) {
                    initGameView(game);
                } else {
                    Toast.makeText(Multiplayer.this, "Server error. Please try again", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void failure(RetrofitError error) {
                throw new RuntimeException("Could not get the mGame from the server " +
                        error.getMessage());
            }
        });
    }

    @Override
    protected boolean posSetFound(List<Card> cards) {
        TypedInput in = null;
        try {
            String json = new Gson().toJson(cards).toLowerCase();
            in = new TypedByteArray("application/json", json.getBytes("UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            throw new RuntimeException("Problem creating TypedInput");
        }
        mApi.sendSet(mGameId, in, new Callback<Response>() {
            @Override
            public void success(Response result, Response response) {
                String str = ActivityUtils.getResponseStr(result);
                Toast.makeText(Multiplayer.this, str, Toast.LENGTH_SHORT).show();
                if (str.equalsIgnoreCase("\"Set!\"")) {
                    mApi.incrementScore(mGameId, ActivityUtils.getUsername(Multiplayer.this), new Callback<Response>() {
                        @Override
                        public void success(Response response, Response response2) {
                            mSocket.emit("update", mGameId);
                        }

                        @Override
                        public void failure(RetrofitError error) {
                            Log.e(Constants.TAG, "Failure incrementing score " + error.getMessage());
                        }
                    });
                }
            }

            @Override
            public void failure(RetrofitError error) {
                Log.e(Constants.TAG, "Failure sending set: " + error.getMessage());
            }
        });

        unhighlightAll();
        mPosSet.clear();
        return true;
    }

    @Override
    public void onStop() {
        super.onStop();
        mApi.removePlayer(mGameId, ActivityUtils.getUsername(this), new Callback<Response>() {
            @Override
            public void success(Response response, Response response2) {
                mSocket.emit("update", mGameId);
                mSocket.disconnect();
            }

            @Override
            public void failure(RetrofitError error) {
                Log.e(Constants.TAG, "Failure removing player");
                mSocket.disconnect();
            }
        });

    }

    @Override
    public void onRestart() {
        initSocket();
    }

    private static class CardDeserializer implements JsonDeserializer<Card> {
        @Override
        public Card deserialize(JsonElement jsonElement, Type typeOF,
                                JsonDeserializationContext context) throws JsonParseException {
            JsonObject jsonCard = jsonElement.getAsJsonObject();

            String shape = jsonCard.get("shape").getAsString();
            int num = jsonCard.get("num").getAsInt();
            String color = jsonCard.get("color").getAsString();
            String fill = jsonCard.get("fill").getAsString();
            return new Card(shape, num, color, fill);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.multiplayer, menu);
        return true;
    }

    protected void finishGame() {
        mApi.removeGame(mGameId, new Callback<Response>() {
            @Override
            public void success(Response response, Response response2) {
                Log.v(Constants.TAG, "Successfully removed game from server");
            }

            @Override
            public void failure(RetrofitError error) {
                Log.e(Constants.TAG, "Failure removing game from server: " + error.getMessage());
            }
        });
        Intent gameOver = new Intent(getApplicationContext(),
                MultiplayerOver.class);
        gameOver.putParcelableArrayListExtra(Constants.Keys.PLAYERS, (ArrayList) mGame.getPlayers());
        gameOver.putParcelableArrayListExtra(Constants.Keys.PLAYERS, (ArrayList) mGame.getPlayers());
        startActivity(gameOver);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        return id == R.id.action_settings || super.onOptionsItemSelected(item);
    }
}
