package com.kdoherty.set.services;

import com.kdoherty.set.model.Card;
import com.kdoherty.set.model.Game;
import com.kdoherty.set.model.Games;
import com.kdoherty.set.model.LeaderboardEntry;

import java.util.List;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.client.Response;
import retrofit.http.Body;
import retrofit.http.DELETE;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Path;
import retrofit.mime.TypedInput;

/**
 * Created by kdoherty on 8/6/14.
 */
public interface SetApi {

    public static final String ENDPOINT = "http://calm-caverns-3319.herokuapp.com";

    public static final RestAdapter adapter = new RestAdapter.Builder()
            .setEndpoint(ENDPOINT)
            .build();

    public static final SetApi INSTANCE = adapter.create(SetApi.class);

    @GET("/games")
    public void getGames(Callback<Games> response);

    @GET("/games/{id}/cards")
    public void getActiveCards(@Path("id") String number, Callback<List<Card>> response);

    @GET("/games/{id}")
    public void getGame(@Path("id") String id, Callback<Game> response);

    @DELETE("/games/{id}")
    public void removeGame(@Path("id") String id, Callback<Response> response);

    @POST("/games")
    public void addGame(Callback<Response> response);

    @FormUrlEncoded
    @POST("/games/{id}/addplayer")
    public void addPlayer(@Path("id") String id, @Field("name") String name,
                          Callback<Response> response);

    @POST("/games/{id}/receiveset")
    public void sendSet(@Path("id") String id, @Body TypedInput cards, Callback<Response> response);

    @POST("/games/{id}/removeall")
    public void removeSet(@Path("id") String id, @Body TypedInput cards, Callback<Response> response);

    @FormUrlEncoded
    @POST("/games/{id}/incrementscore")
    public void incrementScore(@Path("id") String id, @Field("name") String name, Callback<Response> response);

    @FormUrlEncoded
    @POST("/games/{id}/removeplayer")
    public void removePlayer(@Path("id") String id, @Field("name") String name, Callback<Response> response);

    @GET("/leaderboards/practice/{key}")
    public void getPracticeLeaderboard(@Path("key") long time, Callback<List<LeaderboardEntry>> response);

    @GET("/leaderboards/race/{key}")
    public void getRaceLeaderboard(@Path("key") int target, Callback<List<LeaderboardEntry>> response);

    @FormUrlEncoded
    @POST("/leaderboards/practice/{key}")
    public void insertPracticeEntry(@Path("key") long time, @Field("name") String name, @Field("score") int score, Callback<Response> response);

    @FormUrlEncoded
    @POST("/leaderboards/race/{key}")
    public void insertRaceEntry(@Path("key") int target, @Field("name") String name, @Field("score") long time, Callback<Response> response);
}
