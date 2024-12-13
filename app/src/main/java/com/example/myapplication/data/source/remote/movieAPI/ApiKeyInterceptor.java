package com.example.myapplication.data.source.remote.movieAPI;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class ApiKeyInterceptor implements Interceptor {

    private String apiKey;

    public ApiKeyInterceptor(String apiKey) {
        this.apiKey = apiKey;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request originalRequest = chain.request();
        Request newRequest = originalRequest.newBuilder()
                .url(originalRequest.url().newBuilder()
                        .addQueryParameter("api_key", apiKey)
                        .build())
                .build();
        return chain.proceed(newRequest);
    }
}