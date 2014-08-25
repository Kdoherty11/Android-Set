package com.kdoherty.set.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
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

    Context context;

    private String gameId;

    private SetApi api;

    private SocketIO socket = null;

    int player1ScoreNum;
    int player2ScoreNum;

    private String player2NameStr;

    private List<TextView> playerViews;

    private ProgressBar spinner;
    private TextView findingGame;

    private int numPlayers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState, R.layout.activity_multiplayer, R.color.WHITE);
        context = getApplicationContext();
        if (Login.USERNAME == null) {
            Intent login = new Intent(getApplicationContext(), Login.class);
            startActivity(login);
        }
        initPlayerViews();
        initApi();
        initGameId();
        initSocket();
    }

    private void initPlayerViews() {
        playerViews = new ArrayList<>();
        playerViews.add((TextView) findViewById(R.id.player1));
        playerViews.add((TextView) findViewById(R.id.player2));
        playerViews.add((TextView) findViewById(R.id.player3));
        playerViews.add((TextView) findViewById(R.id.player4));
    }

    private void showProgressSpinner() {
        if (spinner == null || findingGame == null) {
            spinner = (ProgressBar) findViewById(R.id.progressBar);
            findingGame = (TextView) findViewById(R.id.findingGame);
        }
        spinner.setVisibility(View.VISIBLE);
        findingGame.setVisibility(View.VISIBLE);
    }

    private void removeSpinner() {
        if (spinner != null && findingGame != null) {
            spinner.setVisibility(View.GONE);
            findingGame.setVisibility(View.GONE);
        }
    }

    @Override
    protected void initGameView(Game game) {
        if (game.isOver()) {
            mGame = game;
            finishGame();
        }
        List<Player> players = game.getPlayers();
        numPlayers = players.size();
        if (numPlayers == 1) {
            showProgressSpinner();
        } else {
            removeSpinner();
            super.initGameView(game);
            //bringUserToFront(players);
            Collections.sort(players);
            LinearLayout playersLayout = ((LinearLayout) findViewById(R.id.playersLayout));
            playersLayout.setWeightSum((float) numPlayers);
            for (int i = 0; i < numPlayers; i++) {
                Player player = players.get(i);
                TextView playerView = playerViews.get(i);
                playerView.setText(player.getName() + '\n' + player.getSetCount());
                playerView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 22);
            }
        }
    }

    private void initGameId() {

        api.getGames(new Callback<Games>() {
            @Override
            public void success(Games games, Response response) {
                System.out.println("Success getting games " + games.getGames());
                if (games.getGames().isEmpty()) {
                    System.out.println("Adding mGame and setting id");
                    addGameAndPlayer();
                } else {
                    for (Game game : games.getGames()) {
                        if (game.isOpen() && !game.isStarted()) {
                            System.out.println("open and not started setting mGame id to " + game.getId());
                            gameId = game.getId();
                            break;
                        }
                    }
                    if (gameId == null) {
                        throw new IllegalStateException("GameId should never be null");
                    }
                    System.out.println("Adding player to mGame " + gameId);

                    api.addPlayer(gameId, Login.USERNAME, new Callback<Response>() {
                        @Override
                        public void success(Response response, Response response2) {
                            System.out.println("Success adding player");
                            socket.emit("update", gameId);
                        }

                        @Override
                        public void failure(RetrofitError error) {
                            System.out.println("Failure adding player " + error.getMessage());
                        }
                    });
                }
            }

            @Override
            public void failure(RetrofitError error) {
                System.out.println("failure getting games" + error.getMessage());
            }
        });

    }

    private void addGameAndPlayer() {
        api.addGame(new Callback<Response>() {
            @Override
            public void success(Response response, Response response2) {
                String resp = getResponseStr(response);
                gameId = resp.substring(1, resp.length() - 1);
                System.out.println("Adding player " + Login.USERNAME + " to mGame " + gameId);

                api.addPlayer(gameId, Login.USERNAME, new Callback<Response>() {
                    @Override
                    public void success(Response response, Response response2) {
                        System.out.println("Success adding player");
                        updateContentFromServer();
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        System.out.println("Failure adding player " + error.getMessage());
                    }
                });

            }

            @Override
            public void failure(RetrofitError error) {
                System.out.println("fail adding mGame here");
            }
        });
    }

    private void initSocket() {
        if (socket == null) {
            try {
                String hostedSocked = SetApi.ENDPOINT + "/";
                socket = new SocketIO(hostedSocked);
            } catch (MalformedURLException e) {
                throw new RuntimeException("Malformed URL in initializing socket");
            }
        }
        socket.connect(new IOCallback() {
            @Override
            public void onMessage(JsonElement json, IOAcknowledge ack) {
                System.out.println("Server said:" + json.toString());
            }

            @Override
            public void onMessage(String data, IOAcknowledge ack) {
                System.out.println("Server said: " + data);
            }

            @Override
            public void on(String event, IOAcknowledge ack, JsonElement... args) {
                System.out.println("Server triggered event '" + event + " with args " + args.toString());
                if (event.equalsIgnoreCase("update")) {
                    updateContentFromServer();
                }
            }

            @Override
            public void onError(SocketIOException socketIOException) {
                System.out.println("an Error occured");
                socketIOException.printStackTrace();
            }

            @Override
            public void onDisconnect() {
                System.out.println("Connection terminated.");
            }

            @Override
            public void onConnect() {
                System.out.println("Connection established");
            }
        });
    }

    private String getResponseStr(Response result) {
        BufferedReader reader = null;
        StringBuilder sb = new StringBuilder();
        try {

            reader = new BufferedReader(new InputStreamReader(result.getBody().in()));

            String line;

            try {
                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return sb.toString();
    }

    private void initApi() {
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(Card.class, new CardDeserializer())
                .create();

        RestAdapter adapter = new RestAdapter.Builder()
                .setEndpoint(SetApi.ENDPOINT)
                .setConverter(new GsonConverter(gson))
                .build();

        api = adapter.create(SetApi.class);
    }

    public void updateContentFromServer() {

        api.getGame(gameId, new Callback<Game>() {
            @Override
            public void success(Game game, Response response) {
                initGameView(game);
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
        api.sendSet(gameId, in, new Callback<Response>() {
            @Override
            public void success(Response result, Response response) {
                String str = getResponseStr(result);
                Toast.makeText(Multiplayer.this, str, Toast.LENGTH_SHORT).show();
                if (str.equalsIgnoreCase("\"Set!\"")) {
                    System.out.println("Trying to increment score and username is " + Login.USERNAME);
                    api.incrementScore(gameId, Login.USERNAME, new Callback<Response>() {
                        @Override
                        public void success(Response response, Response response2) {
                            System.out.println("Success incrementing score");
                            socket.emit("update", gameId);
                        }

                        @Override
                        public void failure(RetrofitError error) {
                            System.out.println("failure incrementing score " + error.getMessage());
                        }
                    });
                } else {
                    System.out.println("str is actually " + str);
                }
            }

            @Override
            public void failure(RetrofitError error) {
                System.out.println("Failure sending set: " + error.getMessage());
            }
        });

        unhighlightAll();
        mPosSet.clear();
        return true;
    }

    @Override
    public void onStop() {
        super.onStop();
        api.removePlayer(gameId, Login.USERNAME, new Callback<Response>() {
            @Override
            public void success(Response response, Response response2) {
                System.out.println("Success removing player");
                socket.emit("update", gameId);
            }

            @Override
            public void failure(RetrofitError error) {
                System.out.println("Failure removing player");
            }
        });
        socket.disconnect();
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
        api.removeGame(gameId, new Callback<Response>() {
                    @Override
                    public void success(Response response, Response response2) {
                        System.out.println("success removing game");
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        System.out.println("failure removing game");
                    }
                });
        Intent gameOver = new Intent(getApplicationContext(),
                MultiplayerOver.class);
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
