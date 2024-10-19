package com.bal.fourthproject.presentation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bal.fourthproject.R;

import java.util.List;
import java.util.Map;

public class QuotesAdapter extends RecyclerView.Adapter<QuotesAdapter.QuoteViewHolder> {

    private List<Map<String, String>> quotesList;
    private QuotesAdapter.OnDeleteClickListener onDeleteClickListener;

    public QuotesAdapter(List<Map<String, String>> quotesList, QuotesAdapter.OnDeleteClickListener onDeleteClickListener) {
        this.quotesList = quotesList;
        this.onDeleteClickListener = onDeleteClickListener;
    }

    @NonNull
    @Override
    public QuotesAdapter.QuoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_quote, parent, false);
        return new QuoteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull QuotesAdapter.QuoteViewHolder holder, int position) {
        Map<String, String> quoteData = quotesList.get(position);
        holder.quoteTextView.setText(quoteData.get("quote"));
        holder.authorTextView.setText(quoteData.get("author"));

        holder.deleteButton.setOnClickListener(v -> {
            if (onDeleteClickListener != null) {
                onDeleteClickListener.onDeleteClick(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return quotesList.size();
    }

    public class QuoteViewHolder extends RecyclerView.ViewHolder {

        private TextView quoteTextView;
        private TextView authorTextView;
        private Button deleteButton;

        public QuoteViewHolder(View view) {
            super(view);

            quoteTextView = view.findViewById(R.id.quoteTextView);
            authorTextView = view.findViewById(R.id.authorTextView);
            deleteButton = view.findViewById(R.id.deleteButton);
        }
    }

    public interface OnDeleteClickListener {
        void onDeleteClick(int position);
    }
}
