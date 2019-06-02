package io.github.projectblackalert.coffeeclient;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import io.github.projectblackalert.coffeeclient.adapter.AvailableProductsAdapter;
import io.github.projectblackalert.coffeeclient.api.ApiClient;
import io.github.projectblackalert.coffeeclient.model.Order;
import io.github.projectblackalert.coffeeclient.model.Product;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BuyFragment extends Fragment {
    private View view;

    private ListView listView;

    private AvailableProductsAdapter availableProductsAdapter;

    private Context context;

    private List<Product> availableProducts = new ArrayList<>();

    ArrayList<Product> orderedProducts = new ArrayList<>();

    private BroadcastReceiver orderSuccessReceiver;

    private BroadcastReceiver orderFailedReceiver;

    private Button buyButton;

    public BuyFragment() {
        //Product was successfully sent to server
        orderSuccessReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.d("BuyFragment", "Intent received");
                setSpinnerVisible(false);
                if(intent.hasExtra("product")){
                    String orderedProduct = intent.getExtras().getString("product");
                    System.out.println(orderedProduct);
                    Toast.makeText(context, String.format("%s erfolgreich bestellt", orderedProduct), Toast.LENGTH_LONG).show();
                }
            }
        };

        //Product was not successfully sent to server
        orderFailedReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                setSpinnerVisible(false);
                Toast.makeText(context, String.format("Bestellung fehlgeschlagen"), Toast.LENGTH_LONG).show();
            }
        };
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_buy, container, false);
        buyButton = view.findViewById(R.id.buybutten);
        buyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("Kaufen button wurde gedrückt.");
                if(orderedProducts.isEmpty()){
                    Toast.makeText(context,"Bitte ein Produkt wählen",Toast.LENGTH_SHORT).show();
                    return;
                }

                sendOrders();
            }
        });
        updateView();
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
        Log.w("BuyFragment", "onAttach");
    }

    @Override
    public void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(context)
                .registerReceiver(orderSuccessReceiver,
                        new IntentFilter("orderSuccessfullSentToServer"));
        LocalBroadcastManager.getInstance(context)
                .registerReceiver(orderFailedReceiver,
                        new IntentFilter("orderFailed"));
        receivePrices();
    }

    @Override
    public void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(context)
                .unregisterReceiver(orderSuccessReceiver);
        LocalBroadcastManager.getInstance(context)
                .unregisterReceiver(orderFailedReceiver);
    }



    public void updateView() {
        listView = view.findViewById(R.id.availableProductsList);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Product orderedProduct = availableProductsAdapter.getItem(i);
                View productView = listView.getChildAt(i -
                        listView.getFirstVisiblePosition());

                if (orderedProducts.contains(orderedProduct)) {
                    orderedProducts.remove(orderedProduct);
                    productView.setBackgroundColor(Color.WHITE);
                } else {
                    orderedProducts.add(orderedProduct);
                    productView.setBackgroundColor(Color.parseColor("#ffa22d"));
                }
            }
        });
        availableProductsAdapter = new AvailableProductsAdapter(getActivity(), this.availableProducts);
        listView.setAdapter(availableProductsAdapter);
    }

    private void receivePrices() {
        Log.d("BuyFragment", "receiving prices");
        setSpinnerVisible(true);
        Call<List<Product>> products = ApiClient.getUnauthenticated().getProducts();
        products.enqueue(new Callback<List<Product>>() {
            @Override
            public void onResponse(Call<List<Product>> call, Response<List<Product>> response) {
                List<Product> newProducts = response.body();

                availableProducts.clear();
                if(availableProducts == null) Log.d("GetPriceAsyncTask", "getProducts is null");
                if(newProducts == null) Log.d("GetPriceAsyncTask", "newProducts is null");

                availableProducts.addAll(newProducts);

                setSpinnerVisible(false);
                updateView();
            }

            @Override
            public void onFailure(Call<List<Product>> call, Throwable t) {
                Log.e("BuyFragment", "Api call to get Products failed");
            }
        });
    }


    private void sendOrders() {
        if (!((MainActivity) getActivity()).isUserLoggedIn()) {
            Toast.makeText(context, "Du bist nicht eingelogggt. Bitte logge dich ein, um Kaffee zu bestellen.",
                    Toast.LENGTH_LONG).show();
            return;
        }

        setSpinnerVisible(true);

        Order order = new Order();
        order.setProducts(orderedProducts);
        order.setUserId(((MainActivity) getActivity()).getUid());
        order.setUserName(((MainActivity) getActivity()).getUserName());
        Call orderCall = ApiClient.getAuthenticated().addOrder(order);
        orderCall.enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) {


                String productName = "";
                for (Product product : orderedProducts){
                    productName += ", " + product.getName();
                }

                orderedProducts.clear();
                Intent intent = new Intent("orderSuccessfullSentToServer");
                intent.putExtra("product", productName);
                LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
            }

            @Override
            public void onFailure(Call call, Throwable t) {
                Log.e("BuyFragment", "Bad http status code, message:" + t.getMessage());
                orderedProducts.clear();
                Intent intent = new Intent("orderFailed");
                LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
            }
        });
    }

    private void setSpinnerVisible(boolean visible) {
        ProgressBar spinner = view.findViewById(R.id.spinner);
        spinner.setVisibility(visible ? View.VISIBLE : View.GONE);
    }
}