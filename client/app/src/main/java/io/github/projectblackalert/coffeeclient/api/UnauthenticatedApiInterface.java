package io.github.projectblackalert.coffeeclient.api;

import java.util.List;

import io.github.projectblackalert.coffeeclient.model.LoginDetails;
import io.github.projectblackalert.coffeeclient.model.Product;
import io.github.projectblackalert.coffeeclient.model.UserDetails;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface UnauthenticatedApiInterface {
    @GET("product")
    Call<List<Product>> getProducts();
}