package com.votingapp.votingapp.server;

import com.votingapp.votingapp.model.Poll;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

/**
 *  An interface that deals with all server calls done in
 *  the app
 */

public interface ServerProxy {
    @GET("polls")
    Call<List<Poll>> getPolls();

}
