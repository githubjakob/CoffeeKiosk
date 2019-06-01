package io.github.projectblackalert.coffeeclient.api;

import java.util.List;

import io.github.projectblackalert.coffeeclient.Constants;
import io.github.projectblackalert.coffeeclient.model.LoginDetails;
import io.github.projectblackalert.coffeeclient.model.Order;
import io.github.projectblackalert.coffeeclient.model.UserDetails;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * REST calls using {@link FirebaseUserIdTokenInterceptor}
 */
public interface AuthenticatedApiInterface {
    @POST("login")
    Call<UserDetails> loginUser(@Body LoginDetails loginDetails);

    @GET("order")
    Call<List<Order>> getOrders(@Query("userId") String userId,
                               @Query("orderId") String oderId,
                               @Query("state") Constants.State state);

    @POST("order")
    Call<ResponseBody> addOrder(@Body Order order);

    @PATCH("order/{orderId}/{newState}")
    Call<ResponseBody> updateOrderState(@Path("orderId") String orderId,
                                        @Path("newState") Constants.State newState);

    @GET("position")
    Call<Integer> getPosition(@Query("userId") String userId);
}
