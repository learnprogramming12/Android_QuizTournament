package com.example.miniproject;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;


public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewHolder> {
    List<Tournament> tournamentList;

    public RecyclerViewAdapter(List<Tournament> tournamentList){
        this.tournamentList = tournamentList;
    }
    @NonNull
    @Override
    public RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.laytout_view_holder, parent, false);

        return new RecyclerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewHolder holder, int position) {
        Tournament tournament = tournamentList.get(holder.getAdapterPosition());

        holder.imgTournament.setImageResource(R.drawable.tournament);
        holder.tvName.setText(tournament.name);
        holder.tvCategory.setText(tournament.category);
        holder.tvDifficulty.setText(tournament.difficulty);
        //holder.tvState.setText();
        holder.tvStartDate.setText(tournament.startDate);
        holder.tvEndDate.setText(tournament.endDate);
        holder.tvState.setText(tournament.status);

        holder.btnMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //start a new activity for tournament based on role
                Context context = view.getContext();
                Intent intent;
                if(Account.getInstance().getRole() == Role.Administrator){
                    intent = new Intent(context, TournamentDetailsForAdminActivity.class);
                }
                else{
                    intent = new Intent(context, TournamentDetailsForPlayerActivity.class);
                }
                intent.putExtra("tournamentName", tournament.name);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return tournamentList.size();
    }
    public void updateData(){
        //the data maintained by a singleton outside, and they share the same underlying memory
        notifyDataSetChanged();
    }
}