package io.github.projectblackalert.coffeeclient;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import io.github.projectblackalert.coffeeclient.api.ApiClient;
import io.github.projectblackalert.coffeeclient.model.Order;
import io.github.projectblackalert.coffeeclient.model.Product;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BuyFragment extends Fragment {

    LinearLayout orderlist;

    ArrayList<Product> bestellt = new ArrayList<>();

    ScrollView order_scroll;

    TextView price;

    double doubprice=0.0;

    int orange = Color.parseColor("#ffa22d");

    Button kaufen;

    private Context context;

    private List<Product> products = new ArrayList<>();

    private View view;

    private BroadcastReceiver orderSuccessReceiver;

    private BroadcastReceiver orderFailedReceiver;

    private ImageView coffeeView;

    private ImageButton selectRightButton;

    private ImageButton selectLeftButton;

    private TextView coffeeName;

    private TextView priceView;

    private ImageButton addCoffeeButton;

    private int currentImagePosition;

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
        currentImagePosition = 0;
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
        setSpinnerVisible(false);

        kaufen = view.findViewById(R.id.buybutten);
        kaufen.setBackgroundColor(Color.GRAY);

        final Button cancel = view.findViewById(R.id.cancelbutton);
        cancel.setBackgroundColor(Color.GRAY);

        selectRightButton = view.findViewById(R.id.nextButton);
        selectLeftButton = view.findViewById(R.id.previousButton);
        coffeeView = view.findViewById(R.id.coffeeView);
        coffeeName = view.findViewById(R.id.coffeeName);
        priceView = view.findViewById(R.id.priceView);
        addCoffeeButton = view.findViewById(R.id.addButton);

        orderlist = view.findViewById(R.id.orders);

        order_scroll = view.findViewById(R.id.order_scroll);

        price = view.findViewById(R.id.price);

        selectRightButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                if(currentImagePosition+1 < products.size()) currentImagePosition++;
                else currentImagePosition = 0;
                switch(products.get(currentImagePosition).getName()) {
                    case "Cappuccino": coffeeView.setImageResource(R.drawable.cappuccino);
                        break;
                    case "Cappuccino spezial": coffeeView.setImageResource(R.drawable.cappuccinospezial);
                        break;
                    case "Trinkschokolade": coffeeView.setImageResource(R.drawable.trinkschokolade);
                        break;
                    case "Kaffee Schwarz": coffeeView.setImageResource(R.drawable.kaffeeschwarz);
                        break;
                    case "Kaffee mit Milch": coffeeView.setImageResource(R.drawable.milchkaffee);
                        break;
                    case "Latte Macchiato": coffeeView.setImageResource(R.drawable.lattemacchiato);
                        break;
                    case "Espresso": coffeeView.setImageResource(R.drawable.espresso);
                        break;
                    case "Doppio": coffeeView.setImageResource(R.drawable.doppio);
                        break;
                    case "Espresso Macchiato": coffeeView.setImageResource(R.drawable.espressomacchiato);
                        break;
                    case "Heißes Wasser/Tee": coffeeView.setImageResource(R.drawable.tee);
                        break;
                }
                coffeeName.setText(products.get(currentImagePosition).getName());
                String priceString = String.format("%.2f",products.get(currentImagePosition).getPrice());
                priceString = priceString.replace(".", ",");
                priceView.setText(priceString+" €");
            }
        });

        selectLeftButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                if(currentImagePosition-1 >= 0) currentImagePosition--;
                else currentImagePosition = products.size()-1;
                switch(products.get(currentImagePosition).getName()) {
                    case "Cappuccino": coffeeView.setImageResource(R.drawable.cappuccino);
                        break;
                    case "Cappuccino spezial": coffeeView.setImageResource(R.drawable.cappuccinospezial);
                        break;
                    case "Trinkschokolade": coffeeView.setImageResource(R.drawable.trinkschokolade);
                        break;
                    case "Kaffee Schwarz": coffeeView.setImageResource(R.drawable.kaffeeschwarz);
                        break;
                    case "Kaffee mit Milch": coffeeView.setImageResource(R.drawable.milchkaffee);
                        break;
                    case "Latte Macchiato": coffeeView.setImageResource(R.drawable.lattemacchiato);
                        break;
                    case "Espresso": coffeeView.setImageResource(R.drawable.espresso);
                        break;
                    case "Doppio": coffeeView.setImageResource(R.drawable.doppio);
                        break;
                    case "Espresso Macchiato": coffeeView.setImageResource(R.drawable.espressomacchiato);
                        break;
                    case "Heißes Wasser/Tee": coffeeView.setImageResource(R.drawable.tee);
                        break;
                }
                coffeeName.setText(products.get(currentImagePosition).getName());
                String priceString = String.format("%.2f",products.get(currentImagePosition).getPrice());
                priceString = priceString.replace(".", ",");
                priceView.setText(priceString+" €");
            }
        });

        addCoffeeButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                TextView tv = new TextView(context);
                tv.setText(" "+products.get(currentImagePosition).getName());
                orderlist.addView(tv);
                bestellt.add(products.get(currentImagePosition));

                doubprice = doubprice+products.get(currentImagePosition).getPrice();
                String priceString = String.format("%.2f",doubprice);
                priceString = priceString.replace(".", ",");
                priceString = priceString+" €";
                price.setText(priceString);

                kaufen.setBackgroundColor(orange);

            }
        });

        kaufen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("Kaufen button wurde gedrückt.");
                if(bestellt.isEmpty()){
                    Toast.makeText(context,"Bitte ein Produkt wählen",Toast.LENGTH_SHORT).show();
                }
                else {
                    if (isSpinnerVisible()) {
                        return;
                    }
                    //here call send orders
                    sendOrders();
                    orderlist.removeAllViews();
                    kaufen.setBackgroundColor(Color.GRAY);
                }
            }
        });


        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("---------cancel");

                if(!bestellt.isEmpty()){
                    bestellt.clear();
                    orderlist.removeAllViews();
                    price.setText("0,00 €");
                    doubprice=0.0;
                    kaufen.setBackgroundColor(Color.GRAY);}


            }
        });
    }


    private void receivePrices() {
        Log.d("BuyFragment", "receiving prices");
        setSpinnerVisible(true);
        Call<List<Product>> products = ApiClient.getUnauthenticated().getProducts();
        products.enqueue(new Callback<List<Product>>() {
            @Override
            public void onResponse(Call<List<Product>> call, Response<List<Product>> response) {
                List<Product> newProducts = response.body();

                getProducts().clear();
                if(getProducts() == null) Log.d("GetPriceAsyncTask", "getProducts is null");
                if(newProducts == null) Log.d("GetPriceAsyncTask", "newProducts is null");

                getProducts().addAll(newProducts);
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
        order.setProducts(bestellt);
        order.setUserId(((MainActivity) getActivity()).getUid());
        order.setUserName(((MainActivity) getActivity()).getUserName());
        Call orderCall = ApiClient.getAuthenticated().addOrder(order);
        orderCall.enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) {


                String productName = bestellt.get(0).getName();
                for (int nr=1; nr<bestellt.size();nr++){
                    productName=productName+", "+bestellt.get(nr).getName();
                }

                bestellt.clear();
                Intent intent = new Intent("orderSuccessfullSentToServer");
                intent.putExtra("product", productName);
                LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
            }

            @Override
            public void onFailure(Call call, Throwable t) {
                Log.e("BuyFragment", "Bad http status code, message:" + t.getMessage());
                bestellt.clear();
                Intent intent = new Intent("orderFailed");
                LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
            }
        });
    }

    private void setSpinnerVisible(boolean visible) {
        ProgressBar spinner = view.findViewById(R.id.spinner);
        spinner.setVisibility(visible ? View.VISIBLE : View.GONE);
    }

    private boolean isSpinnerVisible() {
        ProgressBar spinner = view.findViewById(R.id.spinner);
        return spinner.getVisibility() == View.VISIBLE;
    }

    public List<Product> getProducts() {
        return products;
    }

    @Nullable
    @Override
    public Context getContext() {
        return context;
    }
}
