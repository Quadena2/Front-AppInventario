package com.laboratorio.inventarioapp.network;

import com.google.gson.JsonObject;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import java.io.IOException;

public class GraphQLClient {

    private static final String GRAPHQL_URL = "https://appinventario-production-9286.up.railway.app/graphql";
    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");

    private static GraphQLClient instance;
    private final OkHttpClient client;

    private GraphQLClient() {
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        client = new OkHttpClient.Builder()
                .addInterceptor(logging)
                .build();
    }

    public static GraphQLClient getInstance() {
        if (instance == null) {
            instance = new GraphQLClient();
        }
        return instance;
    }

    // Query simple sin variables
    public String ejecutar(String query) throws IOException {
        JsonObject bodyJson = new JsonObject();
        bodyJson.addProperty("query", query);
        return enviar(bodyJson);
    }

    // Query con variables
    public String ejecutarConVariables(String query,
                                       JsonObject variables) throws IOException {
        JsonObject bodyJson = new JsonObject();
        bodyJson.addProperty("query", query);
        bodyJson.add("variables", variables);
        return enviar(bodyJson);
    }

    private String enviar(JsonObject bodyJson) throws IOException {
        RequestBody body = RequestBody.create(bodyJson.toString(), JSON);
        Request request = new Request.Builder()
                .url(GRAPHQL_URL)
                .post(body)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (response.body() != null) {
                return response.body().string();
            }
            return "{}";
        }
    }
}