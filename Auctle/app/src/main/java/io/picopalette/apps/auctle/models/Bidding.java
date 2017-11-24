package io.picopalette.apps.auctle.models;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by ramkumar on 21/10/17.
 */

public class Bidding implements Serializable{
    private String _id;
    private String sellerId;
    private Integer startPrice;
    private Integer bidSoldFor;
    private String bidSoldTo;
    private String startTime;
    private String status;
    private Product product;
    private ArrayList<Bid> bids;

    public Bidding(Integer startPrice, String startTime, Product product) {
        this.startPrice = startPrice;
        this.startTime = startTime;
        this.product = product;
    }

    public Bidding(String _id, String sellerId, Integer startPrice, Integer bidSoldFor, String bidSoldTo, String startTime, String status, Product product, ArrayList<Bid> bids) {
        this._id = _id;
        this.sellerId = sellerId;
        this.startPrice = startPrice;
        this.bidSoldFor = bidSoldFor;
        this.bidSoldTo = bidSoldTo;
        this.startTime = startTime;
        this.status = status;
        this.product = product;
        this.bids = bids;
    }

    public String getId() {
        return _id;
    }

    public String getSellerId() {
        return sellerId;
    }

    public Integer getStartPrice() {
        return startPrice;
    }

    public Integer getBidSoldFor() {
        return bidSoldFor;
    }

    public String getBidSoldTo() {
        return bidSoldTo;
    }

    public String getStartTime() {
        return startTime;
    }

    public String getStatus() {
        return status;
    }

    public ArrayList<Bid> getBids() {
        return bids;
    }

    public Product getProduct() {
        return product;
    }
}
