package io.github.projectblackalert.coffeeclient.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import io.github.projectblackalert.coffeeclient.R;
import io.github.projectblackalert.coffeeclient.model.Product;

public class AvailableProductsAdapter extends ArrayAdapter<Product> {

    List<Product> products;

    Context context;

    public AvailableProductsAdapter(@NonNull Context context, List<Product> products) {
        super(context, R.layout.product_tile, products);
        this.products = products;
        this.context = context;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItem = convertView;
        if (listItem == null)
            listItem = LayoutInflater.from(context).inflate(R.layout.fragment_buy_product_tile,parent,false);

        final Product currentProduct = products.get(position);

        TextView productName = listItem.findViewById(R.id.productName);
        productName.setText(currentProduct.getName() + " (" + String.format("%.2f", currentProduct.getPrice()) + " €)");

        return listItem;
    }
}