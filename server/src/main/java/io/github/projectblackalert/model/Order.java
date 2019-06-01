package io.github.projectblackalert.model;

import io.github.projectblackalert.dto.OrderDto;
import io.github.projectblackalert.enums.State;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Document(collection = "Order")
public class Order {

    @Id
    String id;

    String userId;

    String userName;

    List<Product> products;

    Instant timestamp;

    State state;

    Double sumPrice = 0d;

    Long waitingTimeSeconds = 0l;

    public Order(OrderDto orderDto) {
        this.userId = orderDto.getUserId();
        this.products = orderDto.getProducts();
        this.timestamp = Instant.now();
        this.state = State.ORDERED;
        this.userName = orderDto.getUserName();
        for (Product product : orderDto.getProducts()) {
            this.sumPrice += product.getPrice();
        }
    }

    public Order(String userId, String userName, Product... products) {
        this.userId = userId;
        this.products = new ArrayList<>(Arrays.asList(products));
        this.timestamp = Instant.now();
        this.state = State.ORDERED;
        for (Product product : new ArrayList<>(Arrays.asList(products))) {
            this.sumPrice += product.getPrice();
        }
    }

    public Order() {

    }

    @Override
    public String toString() {
        return String.format("Order[id=%s, userId=%s, timestamp=%s]",
            id, userId, timestamp);
    }

    public Double getSumPrice() {
        return sumPrice;
    }

    public void setSumPrice(Double sumPrice) {
        this.sumPrice = sumPrice;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
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

    @Override
    public boolean equals(Object obj) {
        Order orderToCompairWith = (Order)obj;
        System.out.println(this.id);
        System.out.println(orderToCompairWith.id);
        return this.id.equals(orderToCompairWith.id);
    }

    public void setWaitingTimeWhenSold(Instant soldTimestamp) {
        this.waitingTimeSeconds = soldTimestamp.getEpochSecond() - this.timestamp.getEpochSecond();
    }

    public Long getWaitingTimeSeconds() {
        return waitingTimeSeconds;
    }

    public void setWaitingTimeSeconds(Long waitingTimeSeconds) {
        this.waitingTimeSeconds = waitingTimeSeconds;
    }
}
