package io.github.projectblackalert.model;

/**
 * Created by jakob on 15.06.18.
 */
public class Product {

    String name;

    Double price;

    public Product() {
        // empty constructor neccessary for deserialization
    }

    public Product(String name, Double price) {
        this.name = name;
        this.price = price;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }
}
