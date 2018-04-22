package com.votingapp.votingapp.server;


import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;

import javax.net.ssl.HttpsURLConnection;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

/**
 * Server call code borrowed from Brian Fraser, a professor based in Simon Fraser University
 */

public class ProxyBuilder {
    private static final String SERVER_URL = "http://a1hella-voting.herokuapp.com/api/";

    private static SimpleCallback<String> receivedTokenCallback;
    public static void setOnTokenReceiveCallback(SimpleCallback<String> callback) {
        receivedTokenCallback = callback;
    }

    /**
     * Return the proxy that client code can use to call server.
     * @return proxy object to call the server.
     */
    public static ServerProxy getProxy() {
        return getProxy(null, null);
    }

    /**
     * Return the proxy that client code can use to call server.
     * @param apiKey   Your group's API key to communicate with the server.
     * @return proxy object to call the server.
     */
    public static ServerProxy getProxy(String apiKey) {
        return getProxy(apiKey, null);
    }

    /**
     * Return the proxy that client code can use to call server.
     * @param token    The token you have been issued
     * @return proxy object to call the server.
     */
    public static ServerProxy getProxy(String apiKey, String token) {
        // Enable Logging
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);


        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(logging)
                .build();

        // Build Retrofit proxy object for server
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(SERVER_URL)
                .addConverterFactory(JacksonConverterFactory.create())
                .client(client)
                .build();

        return retrofit.create(ServerProxy.class);
    }

    /**
     * Simplify the calling of the "Call"
     * - Handle error checking in one place and put up toast & log on failure.
     * - Callback to simplified interface on success.
     * @param context   Current activity for showing toast if there's an error.
     * @param caller    Call object returned by the proxy
     * @param callback  Client-code to execute when we have a good answer for them.
     * @param <T>       The type of data that Call object is expected to fetch
     */
    public static <T extends Object> void callProxy(
            final Context context, Call<T> caller, final SimpleCallback<T> callback) {
        caller.enqueue(new Callback<T>() {
            @Override
            public void onResponse(Call<T> call, retrofit2.Response<T> response) {

                // Process the response
                if (response.errorBody() == null) {
                    // Check for authentication token:
                    String tokenInHeader = response.headers().get("Authorization");
                    if (tokenInHeader != null) {
                        if (receivedTokenCallback != null) {
                            receivedTokenCallback.callback(tokenInHeader);
                        } else {
                            // We got the token, but nobody wanted it!
                            Log.w("ProxyBuilder", "WARNING: Received token but no callback registered for it!");
                        }
                    }

                    if (callback != null) {
                        T body = response.body();
                        callback.callback(body);
                    }
                } else {
                    String message;
                    try {
                        message = "CALL TO SERVER FAILED:\n" + response.errorBody().string();
                    } catch (IOException e) {
                        e.printStackTrace();
                        message = "Unable to decode response (body or error's body).";
                    }
                    showFailure(message);
                }
            }

            @Override
            public void onFailure(Call<T> call, Throwable t) {
                String message = "Server Error: " + t.getMessage();
                showFailure(message);
            }
            private void showFailure(String message) {
                Log.e("ProxyBuilder", message);
                if (context != null) {
                    Toast.makeText(context, message, Toast.LENGTH_LONG).show();
                }
            }
        });
    }
    /**
     * Interface for simplifying the callbacks from the server.
     */
    public interface SimpleCallback<T> {
        void callback(T ans);
    };
}
