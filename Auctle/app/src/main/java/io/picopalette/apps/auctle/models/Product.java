package io.picopalette.apps.auctle.models;

import java.io.Serializable;

/**
 * Created by ramkumar on 21/10/17.
 */

public class Product implements Serializable{
    private String name;
    private Integer price;
    private String category;
    private String desc;

    public Product(String name, Integer price, String category, String desc) {
        this.name = name;
        this.price = price;
        this.category = category;
        this.desc = desc;
    }

    public String getName() {
        return name;
    }

    public Integer getPrice() {
        return price;
    }

    public String getCategory() {
        return category;
    }

    public String getDesc() {
        return desc;
    }
}
