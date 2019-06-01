package io.github.projectblackalert.coffeeclient.model;

import android.util.Log;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import io.github.projectblackalert.coffeeclient.Constants;

public class Order {

    public static class OrderComparater implements Comparator<Order> {

        public final static int SORT_BY_DATE_ASC = 0;

        public final static int SORT_BY_DATE_DESC = 1;

        int type;

        public OrderComparater(int type) {
            this.type = type;
        }

        @Override
        public int compare(Order o1, Order o2) {
            if (type == SORT_BY_DATE_ASC) {
                return o1.getDate().before(o2.getDate()) ? -1 : 1;
            } else if (type == SORT_BY_DATE_DESC) {
                return o1.getDate().before(o2.getDate()) ? 1 : -1;
            }
            return 0;
        }
    }

    String id;
    String userId;
    String userName;
    List<Product> products;
    String timestamp;
    Constants.State state;
    Double sumPrice;
    Long waitingTime;

    public Long getWaitingTime() {
        return waitingTime;
    }

    public void setWaitingTime(Long waitingTime) {
        this.waitingTime = waitingTime;
    }

    public Constants.State getState() {
        return state;
    }

    public void setState(Constants.State state) {
        this.state = state;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public List<Product> getProducts() {
        return products;
    }

    public void setProducts(List<Product> products) {
        this.products = new ArrayList<>(products);
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public Double getSumPrice() {
        return sumPrice;
    }

    public void setSumPrice(Double sumPrice) {
        this.sumPrice = sumPrice;
    }

    public Date getDate() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
        try {
            return dateFormat.parse(this.timestamp);
        } catch (ParseException e) {
            Log.e("Order", "Wrong number format in Order " + this.id);
            return null;
        }
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    @Override
    public String toString() {
        return "Order(id: " + id + "timestamp: " + timestamp + ")";
    }

    public String showOrder(){
        return this.timestamp + " " + this.products.get(0);
    }
}
