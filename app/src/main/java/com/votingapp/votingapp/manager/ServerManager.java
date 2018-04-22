package com.votingapp.votingapp.manager;

import android.content.Context;

import com.votingapp.votingapp.model.Poll;
import com.votingapp.votingapp.server.ProxyBuilder;
import com.votingapp.votingapp.server.ServerProxy;

import java.util.List;

import retrofit2.Call;

public class ServerManager {
    private static ServerProxy proxy;

    public static void setProxy(ServerProxy newProxy) {
        proxy = newProxy;
    }

    public static void getAllPolls(Context context,
                                   ProxyBuilder.SimpleCallback<List<Poll>> callback
    ) {

        Call<List<Poll>> caller = proxy.getPolls();
        ProxyBuilder.callProxy(context, caller, callback);
    }
}
