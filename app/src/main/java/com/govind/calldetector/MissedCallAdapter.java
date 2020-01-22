package com.govind.calldetector;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class MissedCallAdapter extends RecyclerView.Adapter<MissedCallAdapter.ViewHolder> {

    List<UserNumber> userNumberList;

    public MissedCallAdapter(List<UserNumber> userNumberList) {
        this.userNumberList = userNumberList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ViewHolder holder = new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list, parent, false));
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(userNumberList.get(position));
    }

    @Override
    public int getItemCount() {
        return userNumberList.size();
    }

    public void replaceItems(List<UserNumber> newItems) {
        userNumberList.clear();
        userNumberList.addAll(newItems);
        notifyDataSetChanged();
    }

    public void clearItems() {
        userNumberList.clear();
        notifyDataSetChanged();
    }

    public void addItems(List<UserNumber> newItems) {
        int previousCount = userNumberList.size();
        userNumberList.addAll(newItems);
        notifyItemRangeInserted(previousCount, newItems.size());
    }

    public void addItems(UserNumber newItem) {
        userNumberList.add(0, newItem);
        notifyDataSetChanged();
    }

    public void refresh() {
        for (UserNumber userNumber : userNumberList) {
            if (userNumber.isExpired()) {
                userNumberList.remove(userNumber);
                notifyDataSetChanged();
            }
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        View rootView;
        TextView txtNumber;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            rootView = itemView;
            txtNumber = itemView.findViewById(R.id.txt_number);
        }

        public void bind(final UserNumber number) {
            txtNumber.setText(number.getNumber());
        }
    }
}
