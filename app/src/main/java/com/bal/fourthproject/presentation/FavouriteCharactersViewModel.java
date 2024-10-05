package com.bal.fourthproject.presentation;


import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.bal.fourthproject.domain.CharacterModel;
import com.bal.fourthproject.domain.CharacterRepository;
import com.bal.fourthproject.domain.GetAllCharactersUseCase;

import java.util.List;

public class FavouriteCharactersViewModel extends ViewModel {

    private final GetAllCharactersUseCase getAllCharactersUseCase;
    private final MutableLiveData<List<CharacterModel>> characters = new MutableLiveData<>();

    public FavouriteCharactersViewModel(GetAllCharactersUseCase characterRepository) {
        this.getAllCharactersUseCase = characterRepository;
    }

    public LiveData<List<CharacterModel>> getCharacters() {
        return characters;
    }

    public void loadCharacters() {

        new Thread(() -> {
            List<CharacterModel> characterList = getAllCharactersUseCase.execute();
            characters.postValue(characterList);
        }).start();
    }
}
