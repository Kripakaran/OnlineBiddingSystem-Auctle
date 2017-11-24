package io.picopalette.apps.auctle.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import io.picopalette.apps.auctle.R;
import io.picopalette.apps.auctle.models.Bid;

/**
 * Created by ramkumar on 22/10/17.
 */

public class LiveBidderAdapter extends RecyclerView.Adapter<LiveBidderAdapter.BidderViewHolder>{

    private ArrayList<Bid> bids;

    public LiveBidderAdapter(ArrayList<Bid> bids) {
        this.bids = bids;
    }

    public void addBid(Bid bid) {
        this.bids.add(0, bid);
        this.notifyDataSetChanged();
    }

    @Override
    public BidderViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_auction_participants, parent, false);
        return new BidderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(BidderViewHolder holder, int position) {
        Bid bid = bids.get(position);
        holder.serialNumberTextView.setText(String.valueOf(getItemCount() - position));
        holder.participantTextView.setText(bid.getBidder());
        holder.statedPriceTextView.setText(String.valueOf(bid.getPrice()));
    }

    @Override
    public int getItemCount() {
        return bids.size();
    }

    class BidderViewHolder extends RecyclerView.ViewHolder {

        private TextView serialNumberTextView, participantTextView, statedPriceTextView;

        BidderViewHolder(View itemView) {
            super(itemView);
            serialNumberTextView = (TextView) itemView.findViewById(R.id.serialNumberTV);
            participantTextView = (TextView) itemView.findViewById(R.id.participantTV);
            statedPriceTextView = (TextView) itemView.findViewById(R.id.statedPriceTV);
        }
    }
}
