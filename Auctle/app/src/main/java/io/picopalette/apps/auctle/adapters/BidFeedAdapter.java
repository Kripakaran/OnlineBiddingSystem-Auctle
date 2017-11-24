package io.picopalette.apps.auctle.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import io.picopalette.apps.auctle.R;
import io.picopalette.apps.auctle.activities.SellDisplayActivity;
import io.picopalette.apps.auctle.models.Bidding;
import io.picopalette.apps.auctle.utilities.Constants;
import io.picopalette.apps.auctle.utilities.Helpers;

/**
 * Created by ramkumar on 21/10/17.
 */

public class BidFeedAdapter extends RecyclerView.Adapter<BidFeedAdapter.BidViewHolder> {

    private ArrayList<Bidding> biddings;
    private Context context;
    private View view;

    public BidFeedAdapter(ArrayList<Bidding> biddings, Context context) {
        this.biddings = biddings;
        this.context = context;
    }

    @Override
    public BidViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_product_details, parent, false);
        return new BidViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final BidViewHolder holder, int position) {
        Bidding bidding = biddings.get(position);
        holder.nameTextView.setText(bidding.getProduct().getName());
        holder.categoryTextView.setText(bidding.getProduct().getCategory());
        holder.bidPriceTextView.setText(String.format(Locale.ENGLISH, "Rs. %d", bidding.getBidSoldFor()));
        String seller;
        if(bidding.getSellerId().matches(context.getSharedPreferences(Constants.PRES_FILE, Context.MODE_PRIVATE).getString(Constants.USER_EMAIL, ""))) {
            seller = "you";
        } else {
            seller = bidding.getSellerId();
        }
        holder.sellerTextView.setText(String.format("sold by %s", seller));
        try {
            Date date = Helpers.getDateFormatter().parse(bidding.getStartTime());
            holder.dateTextView.setText(String.format(Locale.ENGLISH, "%02d/%02d/%d", date.getDate(), date.getMonth()+1, date.getYear()+1900));
            holder.timeTextView.setText(String.format(Locale.ENGLISH, "%02d:%02d", date.getHours(), date.getMinutes()));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, SellDisplayActivity.class);
                intent.putExtra("bidding", biddings.get(holder.getAdapterPosition()));
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return biddings.size();
    }

    class BidViewHolder extends RecyclerView.ViewHolder {

        TextView nameTextView, categoryTextView, dateTextView, timeTextView, bidPriceTextView, sellerTextView;

        BidViewHolder(View itemView) {
            super(itemView);
            nameTextView = (TextView) itemView.findViewById(R.id.productNameTextView);
            categoryTextView = (TextView) itemView.findViewById(R.id.productCategoryTextView);
            dateTextView = (TextView) itemView.findViewById(R.id.productDateTextView);
            timeTextView = (TextView) itemView.findViewById(R.id.productTimeTextView);
            bidPriceTextView = (TextView) itemView.findViewById(R.id.productPriceTextView);
            sellerTextView = (TextView) itemView.findViewById(R.id.productSellerTextView);
        }
    }
}
