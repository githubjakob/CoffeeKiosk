package io.github.projectblackalert.coffeeclient;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import io.github.projectblackalert.coffeeclient.adapter.OrderAdapter;
import io.github.projectblackalert.coffeeclient.api.ApiClient;
import io.github.projectblackalert.coffeeclient.model.Order;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileFragment extends Fragment {

    private View view;

    ListView listView;

    OrderAdapter orderAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_profile, container, false);

        listView = view.findViewById(R.id.orderedProductsList);
        orderAdapter = new OrderAdapter(getActivity(), new ArrayList<Order>(), false, false);
        listView.setAdapter(orderAdapter);

        Call<List<Order>> ordersCall = ApiClient.getAuthenticated().getOrders(getUid(), null, null);
        ordersCall.enqueue(new Callback<List<Order>>() {
            @Override
            public void onResponse(Call<List<Order>> call, Response<List<Order>> response) {
                List<Order> orders = response.body();
                updateOrderAdapter(orders);
            }

            @Override
            public void onFailure(Call<List<Order>> call, Throwable t) {
                Log.e("ProfileFragment", "Call failed");
            }
        });

        return view;
    }

    private String getUid() {
        SharedPreferences sharedPrefs = getActivity().getSharedPreferences(Constants.SHARED_PREFERENCES, 0);
        return sharedPrefs.getString(Constants.UID, "");
    }

    public void updateOrderAdapter(List<Order> orders){
        orderAdapter.clear();
        Collections.sort(orders, new Order.OrderComparater(Order.OrderComparater.SORT_BY_DATE_DESC));
        orderAdapter.addAll(orders);
        orderAdapter.notifyDataSetChanged();
    }
}
