package io.picopalette.apps.auctle.models;

import java.io.Serializable;

/**
 * Created by ramkumar on 21/10/17.
 */

public class Bid implements Serializable{
    private String _id;
    private String bidder;
    private Integer price;

    public Bid(String _id, String bidder, Integer price) {
        this._id = _id;
        this.bidder = bidder;
        this.price = price;
    }

    public String getId() {
        return _id;
    }

    public String getBidder() {
        return bidder;
    }

    public Integer getPrice() {
        return price;
    }

}
