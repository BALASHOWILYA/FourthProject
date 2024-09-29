package com.bal.fourthproject.presentation;


import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.bal.fourthproject.domain.CharacterModel;
import com.bal.fourthproject.domain.CharacterRepository;

import java.util.List;

public class FavouriteCharactersViewModel extends ViewModel {

    private final CharacterRepository characterRepository;
    private final MutableLiveData<List<CharacterModel>> characters = new MutableLiveData<>();

    public FavouriteCharactersViewModel(CharacterRepository characterRepository) {
        this.characterRepository = characterRepository;
    }

    public LiveData<List<CharacterModel>> getCharacters() {
        return characters;
    }

    public void loadCharacters() {
        // Получаем персонажей из репозитория в отдельном потоке
        new Thread(() -> {
            List<CharacterModel> characterList = characterRepository.getAllCharacters();
            characters.postValue(characterList);
        }).start();
    }
}
