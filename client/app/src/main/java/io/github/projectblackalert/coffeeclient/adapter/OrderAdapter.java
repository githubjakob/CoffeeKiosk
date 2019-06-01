package io.github.projectblackalert.coffeeclient.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.apache.commons.text.WordUtils;

import java.util.Date;
import java.util.List;
import java.text.SimpleDateFormat;

import io.github.projectblackalert.coffeeclient.Constants;
import io.github.projectblackalert.coffeeclient.R;
import io.github.projectblackalert.coffeeclient.api.ApiClient;
import io.github.projectblackalert.coffeeclient.model.Order;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderAdapter extends ArrayAdapter<Order> {

    private final boolean showCheckbox;

    private final boolean showCustomerName;

    private LinearLayout productList;

    private ProductAdapter productAdapter;

    private List<Order> orders;

    private Context context;

    CompoundButton.OnCheckedChangeListener checkBoxOnCheckedChangeListener = new CompoundButton.OnCheckedChangeListener() {

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (!isChecked) {
                return;
            }

            final Order orderToUpdate = (Order) buttonView.getTag();

            final Constants.State newState = Constants.State.SOLD;
            updateOrderAtServer(orderToUpdate, newState);
            buttonView.setChecked(false);
        }
    };

    public OrderAdapter(@NonNull Context context, List<Order> orders, boolean showCheckbox, boolean showCustomerName) {
        super(context, R.layout.dealer_order_tile, orders);
        this.orders = orders;
        this.context = context;
        this.showCheckbox = showCheckbox;
        this.showCustomerName = showCustomerName;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        final View listItem = convertView != null ? convertView :
                LayoutInflater.from(context).inflate(R.layout.dealer_order_tile,parent,false);
        final Order currentOrder = orders.get(position);

        setUserName(listItem, currentOrder, showCustomerName);
        setStatus(listItem, currentOrder);
        setSumPrice(listItem, currentOrder);
        setTimeInformation(listItem, currentOrder);

        setUpProductAdapter(listItem, currentOrder);
        setUpCheckbox(listItem, currentOrder, showCheckbox);
        return listItem;
    }

    private void updateOrderAtServer(final Order orderToUpdate, final Constants.State newState) {
        Call<ResponseBody> updateOrderCall = ApiClient
                .getAuthenticated()
                .updateOrderState(orderToUpdate.getId(), newState);

        updateOrderCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Intent intent = new Intent(Constants.ORDER_STATE_CHANGED);
                intent.putExtra("orderId", orderToUpdate.getId());
                intent.putExtra("newState", newState);
                LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("SellCoffeeActivy", "Call failed");
            }
        });
    }

    private void setUpProductAdapter(View listItem, Order currentOrder) {
        productList = listItem.findViewById(R.id.orderedProductsList);
        productList.removeAllViews();
        productAdapter = new ProductAdapter(context, currentOrder.getProducts());

        int productsInOrder = currentOrder.getProducts().size();

        for (int i = 0; i < productsInOrder; i++) {
            View item = productAdapter.getView(i, null, null);
            productList.addView(item);
        }
    }

    private void setUpCheckbox(View listItem, Order currentOrder, boolean showCheckbox) {
        final CheckBox checkBox = listItem.findViewById(R.id.markAsSoldCheckbox);

        if (!showCheckbox) {
            View checkboxLabel = listItem.findViewById(R.id.checkboxLabel);
            checkboxLabel.setVisibility(View.GONE);
            checkBox.setVisibility(View.GONE);
            return;
        }
        checkBox.setTag(currentOrder);

        if(currentOrder.getState().equals(Constants.State.SOLD)){
            checkBox.setOnCheckedChangeListener(null);
            checkBox.setChecked(true);
            checkBox.setEnabled(false);
        } else {
            checkBox.setChecked(false);
            checkBox.setEnabled(true);
            checkBox.setOnCheckedChangeListener(checkBoxOnCheckedChangeListener);
        }
    }

    private void setTimeInformation(View listItem, Order currentOrder) {
        TextView timeOfOrder = listItem.findViewById(R.id.timestamp);
        Date date = currentOrder.getDate();
        String formatedDate = "";
        if (date != null) {
            SimpleDateFormat formatter = new SimpleDateFormat();
            formatedDate = formatter.format(date);
        }

        if (currentOrder.getState().equals(Constants.State.SOLD)) {
            formatedDate = formatedDate + System.getProperty ("line.separator") +
                    String.format("Verkauft %ds nach Bestellung", currentOrder.getWaitingTime());
        }
        timeOfOrder.setText(formatedDate);
    }

    private void setSumPrice(View listItem, Order currentOrder) {
        TextView sumPriceTextView = listItem.findViewById(R.id.sumPrice);
        sumPriceTextView.setText(String.format("Gesamtpreis: %.2f €", currentOrder.getSumPrice()));
    }

    private void setStatus(View listItem, Order currentOrder) {
        TextView statusTextView = listItem.findViewById(R.id.statusTextView);
        statusTextView.setText("Status: " + currentOrder.getState().getText());
    }

    private void setUserName(View listItem, Order currentOrder, boolean showCustomerName) {
        TextView userNameTextView = listItem.findViewById(R.id.userNameTextView);

        if (!showCustomerName) {
            userNameTextView.setVisibility(View.GONE);
            return;
        }

        boolean hasNoUserName = currentOrder.getUserName() == null || currentOrder.getUserName().isEmpty();
        userNameTextView.setText("Bestellung von: " + (hasNoUserName ? "Kein Name" :
                WordUtils.capitalizeFully(currentOrder.getUserName())));
    }

    public List<Order> getOrders() {
        return orders;
    }
}