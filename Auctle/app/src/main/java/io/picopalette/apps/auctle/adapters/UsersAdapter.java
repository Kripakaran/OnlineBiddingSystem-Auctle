package io.picopalette.apps.auctle.adapters;

import android.support.v4.util.Pair;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import io.picopalette.apps.auctle.R;

/**
 * Created by ramkumar on 22/10/17.
 */

public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.UserViewHolder> {

    ArrayList<Pair<String, String>> users;

    public UsersAdapter(ArrayList<Pair<String, String>> users) {
        this.users = users;
    }

    @Override
    public UsersAdapter.UserViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_user_mini, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(UsersAdapter.UserViewHolder holder, int position) {
        holder.nameTextView.setText(users.get(position).first);
        holder.emailTextView.setText(users.get(position).second);
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    class UserViewHolder extends RecyclerView.ViewHolder {

        TextView nameTextView, emailTextView;

        UserViewHolder(View itemView) {
            super(itemView);
            nameTextView = (TextView) itemView.findViewById(R.id.userCardNameTextView);
            emailTextView = (TextView) itemView.findViewById(R.id.userCardEmailTextView);
        }
    }
}
