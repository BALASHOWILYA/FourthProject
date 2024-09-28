package com.bal.fourthproject.presentation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.bal.fourthproject.data.Character;
import com.bal.fourthproject.R;
import com.bal.fourthproject.domain.CharacterModel;
import com.bal.fourthproject.domain.CharacterRepository;
import com.bumptech.glide.Glide;

import java.util.List;

public class CharacterAdapter extends RecyclerView.Adapter<CharacterAdapter.CharacterViewHolder> {

    private List<Character> characterList;
    private CharacterRepository characterRepository;

    public CharacterAdapter(List<Character> characterList, CharacterRepository characterRepository) {
        this.characterList = characterList;
        this.characterRepository = characterRepository;
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

        // Устанавливаем данные персонажа
        holder.characterIdTextView.setText("Id: " + character.getId());
        holder.characterNameTextView.setText("Name: " + character.getName());
        holder.characterStatusTextView.setText("Status: " + character.getStatus());
        holder.characterSpeciesTextView.setText("Species: " + character.getSpecies());

        // Загрузка изображения персонажа
        Glide.with(holder.itemView.getContext())
                .load(character.getImageUrl())
                .into(holder.characterImageView);

        // Обработка нажатия на кнопку "add character"
        holder.addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Преобразуем объект Character в CharacterModel для domain слоя
                CharacterModel characterModel = new CharacterModel(
                        character.getId(),
                        character.getName(),
                        character.getStatus(),
                        character.getSpecies(),
                        character.getImageUrl()
                );

                // Добавляем персонажа через репозиторий
                characterRepository.addCharacter(characterModel);
            }
        });
    }
    // Метод для обновления списка персонажей
    public void setCharacters(List<Character> newCharacterList) {
        this.characterList = newCharacterList;
        notifyDataSetChanged();  // Обновляем данные в адаптере
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
        public Button addButton;

        public CharacterViewHolder(@NonNull View itemView) {
            super(itemView);
            characterImageView = itemView.findViewById(R.id.characterImageView);
            characterIdTextView = itemView.findViewById(R.id.characterIdTextView);
            characterNameTextView = itemView.findViewById(R.id.characterNameTextView);
            characterStatusTextView = itemView.findViewById(R.id.characterStatusTextView);
            characterSpeciesTextView = itemView.findViewById(R.id.characterSpeciesTextView);
            addButton = itemView.findViewById(R.id.add_character_id);
        }
    }
}
