package com.bal.fourthproject;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.bumptech.glide.Glide;

import java.util.List;

public class CharacterAdapter extends RecyclerView.Adapter<CharacterAdapter.CharacterViewHolder> {

    private List<Character> characterList;

    public CharacterAdapter(List<Character> characterList){
        this.characterList = characterList;
    }

    @NonNull
    @Override
    public CharacterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.character_item, parent, false);

        return new CharacterViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull CharacterViewHolder holder, int position) {
        Character character = characterList.get(position);
        holder.characterIdTextView.setText("Id: " + character.getId());
        holder.characterNameTextView.setText("Name: " + character.getName());
        holder.characterNameTextView.setText("Status: " + character.getStatus());
        holder.characterNameTextView.setText("Species: " + character.getSpecies());

        Glide.with(holder.itemView.getContext())
                .load(character.getImageUrl())
                .into(holder.characterImageView);


    }

    @Override
    public int getItemCount() {
        return characterList.size();
    }

    public  void setCharacters(List<Character> characters){
        this.characterList = characters;
        notifyDataSetChanged();
    }


    public static class CharacterViewHolder extends  RecyclerView.ViewHolder {

        public ImageView characterImageView;
        public TextView characterIdTextView;
        public TextView characterNameTextView;
        public TextView characterStatusTextView;
        public TextView characterSpeciesTextView;

        public CharacterViewHolder(@NonNull View itemView) {
            super(itemView);


            characterImageView = itemView.findViewById(R.id.characterImageView);
            characterIdTextView = itemView.findViewById(R.id.characterIdTextView);
            characterNameTextView = itemView.findViewById(R.id.characterNameTextView);
            characterStatusTextView = itemView.findViewById(R.id.characterStatusTextView);
            characterSpeciesTextView = itemView.findViewById(R.id.characterSpeciesTextView);
        }






    }
}
