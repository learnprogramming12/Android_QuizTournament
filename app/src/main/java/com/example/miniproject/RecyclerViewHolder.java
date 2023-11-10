package com.example.miniproject;

import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class RecyclerViewHolder extends RecyclerView.ViewHolder {
    ImageView imgTournament;
    TextView tvName;
    TextView tvCategory;
    TextView tvDifficulty;
    TextView tvStartDate;
    TextView tvEndDate;
    TextView tvState;
    Button btnMore;

    public RecyclerViewHolder(@NonNull View itemView){
        super(itemView);

        imgTournament = itemView.findViewById(R.id.imageView);
        tvName = itemView.findViewById(R.id.name);
        tvCategory = itemView.findViewById(R.id.category);
        tvDifficulty = itemView.findViewById(R.id.difficulty);
        tvState = itemView.findViewById(R.id.state);
        tvStartDate = itemView.findViewById(R.id.start_date);
        tvEndDate = itemView.findViewById(R.id.end_date);
        btnMore = itemView.findViewById(R.id.more);
    }
}