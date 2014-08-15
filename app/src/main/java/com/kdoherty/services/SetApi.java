package com.kdoherty.services;

import com.kdoherty.model.Card;
import com.kdoherty.model.Game;
import com.kdoherty.model.Games;
import com.kdoherty.model.Set;

import java.util.List;

import retrofit.Callback;
import retrofit.client.Response;
import retrofit.http.Body;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.GET;
import retrofit.http.Headers;
import retrofit.http.POST;
import retrofit.http.Path;
import retrofit.mime.TypedInput;

/**
 * Created by kdoherty on 8/6/14.
 */
public interface SetApi {

//    public static final String ENDPOINT = "http://nodejs-setserver.rhcloud.com";
    public static final String ENDPOINT = "http://10.0.2.2:5000";

    @GET("/games")
    public void getGames(Callback<Games> response);

    @GET("/games/{id}/cards")
    public void getActiveCards(@Path("id") String number, Callback<List<Card>> response);

    @GET("/games/{id}")
    public void getGame(@Path("id") String id, Callback<Game> response);

    @GET("/games/{id}/findset")
    public void findSet(@Path("id") String id, Callback<Set> response);

    @POST("/games")
    public void addGame(Callback<Response> response);

    @FormUrlEncoded
    @POST("/games/{id}/addplayer")
    public void addPlayer(@Path("id") String id, @Field("name") String name,
                          Callback<Response> response);

    //@Headers("Content-Type: application/json")
    @POST("/games/{id}/receiveset")
    public void sendSet(@Path("id") String id, @Body TypedInput cards, Callback<Response> response);

    @POST("/games/{id}/removeall")
    public void removeSet(@Path("id") String id, @Body TypedInput cards, Callback<Response> response);
}
