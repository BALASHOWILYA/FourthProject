package com.bal.fourthproject.presentation;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bal.fourthproject.R;
import com.bal.fourthproject.domain.CharacterModel;
import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

public class FavouriteCharactersAdapter extends RecyclerView.Adapter<FavouriteCharactersAdapter.CharacterViewHolder> {

    private List<CharacterModel> characters = new ArrayList<>();

    @NonNull
    @Override
    public CharacterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.favourite_character_item, parent, false);
        return new CharacterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CharacterViewHolder holder, int position) {
        CharacterModel character = characters.get(position);
        holder.nameTextView.setText(character.getName());
        holder.statusTextView.setText(character.getStatus());
        holder.speciesTextView.setText(character.getSpecies());

        // Загрузка изображения с помощью Glide
        Glide.with(holder.itemView.getContext())
                .load(character.getImageUrl())
                .into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return characters.size();
    }

    public void setCharacters(List<CharacterModel> characters) {
        this.characters = characters;
        notifyDataSetChanged();
    }

    static class CharacterViewHolder extends RecyclerView.ViewHolder {

        TextView nameTextView;
        TextView statusTextView;
        TextView speciesTextView;
        ImageView imageView;

        public CharacterViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.characterNameTextView);
            statusTextView = itemView.findViewById(R.id.characterStatusTextView);
            speciesTextView = itemView.findViewById(R.id.characterSpeciesTextView);
            imageView = itemView.findViewById(R.id.characterImageView);
        }
    }
}
