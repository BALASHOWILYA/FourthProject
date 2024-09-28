package com.bal.fourthproject.presentation;

import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bal.fourthproject.R;
import com.bal.fourthproject.data.Character;
import com.bal.fourthproject.domain.CharacterModel;
import com.bal.fourthproject.domain.CharacterRepository;

import java.util.List;

public class FavouriteCharactersAdapter extends RecyclerView.Adapter<CharacterAdapter.CharacterViewHolder>{

    private List<CharacterModel> characterList;

    @NonNull
    @Override
    public CharacterAdapter.CharacterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull CharacterAdapter.CharacterViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return characterList.size();
    }

    public static class CharacterViewHolder extends RecyclerView.ViewHolder {

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
