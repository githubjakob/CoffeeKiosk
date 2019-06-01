package io.github.projectblackalert.coffeeclient.dealerArea;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.Switch;

import java.util.List;

import io.github.projectblackalert.coffeeclient.Constants;
import io.github.projectblackalert.coffeeclient.R;
import io.github.projectblackalert.coffeeclient.adapter.OrderAdapter;
import io.github.projectblackalert.coffeeclient.api.ApiClient;
import io.github.projectblackalert.coffeeclient.model.Order;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DealerActivity extends AppCompatActivity {

    private DealerStore dealerStore;

    private LocalBroadcastManager localBroadcastManager;

    List<Order> orders;

    private ListView listView;

    private static OrderAdapter orderAdapter;

    private BroadcastReceiver updateOrderReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            updateAdapterWithNewItems();
            notifyOrdersConfirmed();
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dealer);

        this.localBroadcastManager = LocalBroadcastManager.getInstance(getApplicationContext());

        final Switch showAllOrdersSwitch = findViewById(R.id.showAllOrdersSwitch);
        showAllOrdersSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                   sendShowAllOrdersEvent();
                } else {
                    sendShowOnlyConfirmedAndOrderedEvent();
                }
            }
        });
    }

    private void sendShowOnlyConfirmedAndOrderedEvent() {
        Intent intent = new Intent(Constants.SHOW_ONLY_CONFIMED_AND_SOLD_ACTION);
        localBroadcastManager.sendBroadcast(intent);
    }

    private void sendShowAllOrdersEvent() {
        Intent intent = new Intent(Constants.SHOW_ALL_ORDER_ACTION);
        localBroadcastManager.sendBroadcast(intent);
    }

    @Override
    public void onResume() {
        super.onResume();

        this.dealerStore = new DealerStore(getApplicationContext());

        this.orders = dealerStore.getOrders();
        sendShowOnlyConfirmedAndOrderedEvent();

        listView = findViewById(R.id.orderedProductsList);
        orderAdapter = new OrderAdapter(this, orders, true, true);
        listView.setAdapter(orderAdapter);

        localBroadcastManager.registerReceiver(updateOrderReceiver,
                        new IntentFilter(Constants.UPDATE_DEALER_VIEW));
    }

    private void notifyOrdersConfirmed() {
        for(Order order: orders){
            if (order.getState().equals(Constants.State.ORDERED)) {
                updateState(order, Constants.State.CONFIRMED);
            }
        }
    }

    private void updateState(final Order order, final Constants.State newState) {
        Call<ResponseBody> updateOrderCall = ApiClient
                .getAuthenticated()
                .updateOrderState(order.getId(), newState);

        updateOrderCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Intent intent = new Intent(Constants.ORDER_STATE_CHANGED);
                intent.putExtra("orderId", order.getId());
                intent.putExtra("newState", newState);
                localBroadcastManager.sendBroadcast(intent);
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("SellCoffeeActivy", "Call failed");
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();

        localBroadcastManager.unregisterReceiver(updateOrderReceiver);
        this.dealerStore.delete();
        this.dealerStore = null; // JVM Garbage Collector will remove object
    }

    private void updateAdapterWithNewItems() {
        orders = dealerStore.getOrders();

        orderAdapter.clear();
        if (orders != null){
            for (Order order : orders) {
                orderAdapter.insert(order, orderAdapter.getCount());
            }
        }

        orderAdapter.notifyDataSetChanged();
    }
}
