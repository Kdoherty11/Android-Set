package com.kdoherty.activities;

import android.content.Context;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.kdoherty.adapters.ImageAdapter;
import com.kdoherty.model.Card;
import com.kdoherty.model.Game;
import com.kdoherty.model.Games;
import com.kdoherty.model.Set;
import com.kdoherty.services.SetApi;
import com.kdoherty.set.R;

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
        System.out.println("game id is " + gameId);
        initSocket();

       // new MyClientTask("192.168.1.4", 8081).execute();
    }

    private void initGameId() {

        api.getGames(new Callback<Games>() {
            @Override
            public void success(Games games, Response response) {
                System.out.println("Success getting games " + games.getGames());
                if (games.getGames().isEmpty()) {
                    System.out.println("Adding game and setting id");
                    addGameAndPlayer();
                } else {
                    for (Game game : games.getGames()) {
                        if (game.isOpen() && !game.isStarted()) {
                            System.out.println("open and not started setting game id to " + game.getId());
                            gameId = game.getId();
                            break;
                        }
                    }
                    if (gameId == null) {
                        addGameAndPlayer();
                    } else {
                        System.out.println("Adding player to game " + gameId);
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
                System.out.println("Adding player " + Login.USERNAME + " to game " + gameId);
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
                System.out.println("fail adding game here");
            }
        });
    }

    private void initSocket() {
        try {
            String hostedSocked = SetApi.ENDPOINT + "/";
            socket = new SocketIO(hostedSocked);
        } catch (MalformedURLException e) {
            e.printStackTrace();
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
                System.out.println("Error adding player: " + error.getMessage());
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
                System.out.println("Success: " + game);
                initGameView(game);
            }

            @Override
            public void failure(RetrofitError error) {
                throw new RuntimeException("Could not get the game from the server " +
                        error.getMessage());
            }
        });
    }

    /**
     * Responds to a click on the solverButtonView. It finds a set as per game
     * rules if there is one and highlights the cards in the set. If there is
     * not one on the table, a Toast is shown saying there are no sets
     *
     * @param view
     */
    public void onSolverButtonClick(View view) {
        api.findSet(gameId, new Callback<Set>() {
            @Override
            public void success(Set set, Response response) {
                if (set == null) {
                    Toast.makeText(Multiplayer.this, "There are no sets!", Toast.LENGTH_SHORT).show();
                } else {
                    mPosSet.clear();
                    unhighlightAll();
                    for (Card card : set) {
                        mHighlight.add(mAdapter.getCardView(mGridView, ImageAdapter
                                .getCardImages().get(card)));
                    }
                    highlightAll(getResources().getColor(R.color.red_cherry));
                }
            }

            @Override
            public void failure(RetrofitError error) {
                System.out.println("Failure finding set");
            }
        });
    }

    /**
     * Responds to a click on the deal button. It deals a Set of cards from the
     * Deck and displays them
     *
     * @param arg0
     */
    public void onDealButtonClick(View arg0) {
        updateContentFromServer();
    }

    @Override
    protected void posSetFound(List<Card> cards) {
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

    void finishGame() {

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        return id == R.id.action_settings || super.onOptionsItemSelected(item);
    }


    // SOCKET CODE BELOW
}
