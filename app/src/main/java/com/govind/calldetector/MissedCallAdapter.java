package com.govind.calldetector;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class MissedCallAdapter extends RecyclerView.Adapter<MissedCallAdapter.ViewHolder> {

    List<String> missedCallList;

    public MissedCallAdapter(List<String> missedCallList) {
        this.missedCallList = missedCallList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ViewHolder holder = new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list, parent, false));
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(missedCallList.get(position));
    }

    @Override
    public int getItemCount() {
        return missedCallList.size();
    }

    public void replaceItems(List<String> newItems) {
        missedCallList.clear();
        missedCallList.addAll(newItems);
        notifyDataSetChanged();
    }

    public void clearItems() {
        missedCallList.clear();
        notifyDataSetChanged();
    }

    public void addItems(List<String> newItems) {
        int previousCount = missedCallList.size();
        missedCallList.addAll(newItems);
        notifyItemRangeInserted(previousCount, newItems.size());
    }

    public void addItems(String newItem) {
        missedCallList.add(newItem);
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        View rootView;
        TextView txtNumber;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            rootView = itemView;
            txtNumber = itemView.findViewById(R.id.txt_number);
        }

        public void bind(final String missedCall) {
            txtNumber.setText(missedCall);
        }
    }
}
