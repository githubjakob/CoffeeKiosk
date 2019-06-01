package io.github.projectblackalert.dto;

import io.github.projectblackalert.enums.State;
import io.github.projectblackalert.model.Order;
import io.github.projectblackalert.model.Product;

import java.time.Instant;
import java.util.List;

public class OrderDto {

    String id;

    String userId;

    String userName;

    List<Product> products;

    Instant timestamp;

    State state;

    Double sumPrice;

    Long waitingTime;

    public OrderDto(Order order) {
        this.id = order.getId();
        this.state = order.getState();
        this.userId = order.getUserId();
        this.products = order.getProducts();
        this.userName = order.getUserName();
        this.timestamp = order.getTimestamp();
        this.sumPrice = order.getSumPrice();
        this.waitingTime = order.getWaitingTimeSeconds();
    }

    public OrderDto() {
        // empty constructor neccessary for deserialization
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
        this.products = products;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Double getSumPrice() {
        return sumPrice;
    }

    public void setSumPrice(Double sumPrice) {
        this.sumPrice = sumPrice;
    }

    public Long getWaitingTime() {
        return waitingTime;
    }

    public void setWaitingTime(Long waitingTime) {
        this.waitingTime = waitingTime;
    }
}
