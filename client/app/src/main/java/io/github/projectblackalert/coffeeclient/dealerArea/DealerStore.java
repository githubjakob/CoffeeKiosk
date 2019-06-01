package io.github.projectblackalert.coffeeclient.dealerArea;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import io.github.projectblackalert.coffeeclient.Constants;
import io.github.projectblackalert.coffeeclient.api.ApiClient;
import io.github.projectblackalert.coffeeclient.model.Order;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Keeps a state of all Orders, displayed in the DealerActivity
 * Listens for Actions from the DealerActivity
 * Notifies DealerActivity to update the view
 */
public class DealerStore {

    private List<Order> orders = new ArrayList<>();

    private static final int SHOW_ALL = 1;

    private static final int SHOW_ONLY_CONFIRMED_AND_ORDERED = 2;

    private static int show = SHOW_ALL;

    private final LocalBroadcastManager localBroadcastManager;

    private BroadcastReceiver showAllOrdersAction = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            show = SHOW_ALL;
            notifyViewToUpdate();
        }
    };

    private BroadcastReceiver showOnlyConfirmedAndOrderedAction = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            show = SHOW_ONLY_CONFIRMED_AND_ORDERED;
            notifyViewToUpdate();
        }
    };

    private BroadcastReceiver orderStateChanged = new BroadcastReceiver() {
        @Override
        public void onReceive(final Context context, final Intent intent) {
            final Bundle extras = intent.getExtras();
            final String orderId = extras.getString("orderId");
            final Constants.State newState = (Constants.State) extras.get("newState");

            updateState(orderId, newState);
        }
    };

    private BroadcastReceiver newOrderPushMessageReceived = new BroadcastReceiver() {
        @Override
        public void onReceive(final Context context, final Intent intent) {
            receiveOrdersFromServer();
        }
    };

    private void updateState(String orderId, Constants.State newState) {
        Order order = getOrderWith(orderId);
        if (order != null) {
            order.setState(newState);
        }
        notifyViewToUpdate();
    }

    @Nullable
    private Order getOrderWith(final String orderId) {
        Order result = null;
        for (Order order : orders) {
            if (order.getId().equals(orderId)) result = order;
        }
        return result;
    }

    public DealerStore(Context context) {
        receiveOrdersFromServer();
        this.localBroadcastManager = LocalBroadcastManager.getInstance(context);

        this.localBroadcastManager.registerReceiver(showAllOrdersAction,
                        new IntentFilter(Constants.SHOW_ALL_ORDER_ACTION));

        this.localBroadcastManager.registerReceiver(showOnlyConfirmedAndOrderedAction,
                        new IntentFilter(Constants.SHOW_ONLY_CONFIMED_AND_SOLD_ACTION));

        this.localBroadcastManager.registerReceiver(orderStateChanged,
                        new IntentFilter(Constants.ORDER_STATE_CHANGED));

        this.localBroadcastManager.registerReceiver(newOrderPushMessageReceived,
                        new IntentFilter("newOrderPushMessageReceived"));
    }

    public void delete() {
        this.localBroadcastManager.unregisterReceiver(showAllOrdersAction);
        this.localBroadcastManager.unregisterReceiver(showOnlyConfirmedAndOrderedAction);
        this.localBroadcastManager.unregisterReceiver(orderStateChanged);
        this.localBroadcastManager.unregisterReceiver(newOrderPushMessageReceived);
    }

    public List<Order> getOrders() {
        if (show == SHOW_ONLY_CONFIRMED_AND_ORDERED) {
            return excludeSold(orders);
        }
        return orders;
    }

    private List<Order> excludeSold(List<Order> orders) {
        List<Order> filteredOrders = new ArrayList<>();
        for (Order order : orders) {
            if (order.getState().equals(Constants.State.SOLD)) {
                continue;
            }
            filteredOrders.add(order);
        }
        return filteredOrders;
    }

    public void setOrders(List<Order> orders) {
        this.orders = orders;
    }

    private void notifyViewToUpdate() {
        Intent intent = new Intent(Constants.UPDATE_DEALER_VIEW);
        this.localBroadcastManager.sendBroadcast(intent);
    }

    private void receiveOrdersFromServer() {
        Call<List<Order>> receiveOrdersCall = ApiClient.getAuthenticated().getOrders(null, null, null);
        receiveOrdersCall.enqueue(new Callback<List<Order>>() {
            @Override
            public void onResponse(Call<List<Order>> call, Response<List<Order>> response) {
                List<Order> newOrders = response.body();
                setOrders(newOrders);
                notifyViewToUpdate();
            }

            @Override
            public void onFailure(Call<List<Order>> call, Throwable t) {
                Log.e("DealerActivity", "call failed");
            }
        });
    }
}