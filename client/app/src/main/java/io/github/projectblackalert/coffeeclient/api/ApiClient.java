package io.github.projectblackalert.coffeeclient.api;

import io.github.projectblackalert.coffeeclient.Constants;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {

    private static OkHttpClient client;

    private static OkHttpClient interceptedClient;

    private static UnauthenticatedApiInterface unauthenticatedApiInterface;

    private static AuthenticatedApiInterface authenticatedApiInterface;

    private static HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();

    static {
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
    }


    public static UnauthenticatedApiInterface getUnauthenticated() {
        if (unauthenticatedApiInterface == null) {
            // Setup OkHttpClient
            client = new OkHttpClient.Builder()
                    .addInterceptor(loggingInterceptor)
                    .build();

            // Wire OkHttp to Retrofit
            Retrofit retrofit = new Retrofit.Builder()
                    .client(client)
                    .baseUrl(Constants.SERVER + Constants.REST + "/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            unauthenticatedApiInterface = retrofit.create(UnauthenticatedApiInterface.class);
        }

        return unauthenticatedApiInterface;
    }

    public static AuthenticatedApiInterface getAuthenticated() {
        if (authenticatedApiInterface == null) {
            // Setup OkHttpClient in order to use the custom Interceptor
            interceptedClient = new OkHttpClient.Builder()
                    .addInterceptor(new FirebaseUserIdTokenInterceptor())
                    .addInterceptor(loggingInterceptor)
                    .build();

            // Wire OkHttp to Retrofit
            Retrofit retrofit = new Retrofit.Builder()
                    .client(interceptedClient)
                    .baseUrl(Constants.SERVER + Constants.AUTH + "/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            authenticatedApiInterface = retrofit.create(AuthenticatedApiInterface.class);
        }

        return authenticatedApiInterface;
    }
}
