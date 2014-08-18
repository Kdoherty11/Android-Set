package com.kdoherty.set.activities;

import android.content.Context;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.devsmart.android.ui.HorizontalListView;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.kdoherty.set.R;
import com.kdoherty.set.adapters.PlayerAdapter;
import com.kdoherty.set.model.Card;
import com.kdoherty.set.model.Game;
import com.kdoherty.set.model.Games;
import com.kdoherty.set.services.SetApi;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.net.MalformedURLException;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multiplayer);
        context = getApplicationContext();
        initApi();
        initGameId();
        initSocket();
    }

    @Override
    protected void initGameView(Game game) {
        super.initGameView(game);
        PlayerAdapter adapter = new PlayerAdapter(getApplicationContext(), game.getPlayers());
        HorizontalListView playersView = (HorizontalListView) findViewById(R.id.player_list);
        playersView.setAdapter(adapter);
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
                                socket.emit("update");
//                                updateContentFromServer();
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
                System.out.println("Server triggered event '" + event + "'");
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


    private void addPlayer(String id, String name) {
        api.addPlayer(id, name, new Callback<Response>() {
            @Override
            public void success(Response response, Response response2) {
                System.out.println("Adding player Response: " + getResponseStr(response));
            }

            @Override
            public void failure(RetrofitError error) {
                throw new RuntimeException("Failure adding player " + error.getMessage());
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
                    System.out.println("Attempting to send update to socket");
                    api.incrementScore(gameId, Login.USERNAME, new Callback<Response>() {
                        @Override
                        public void success(Response response, Response response2) {
                            System.out.println("Success incrementing score");
                        }

                        @Override
                        public void failure(RetrofitError error) {
                            System.out.println("failure incrementing score " + error.getMessage());
                        }
                    });
                    socket.emit("update");
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
