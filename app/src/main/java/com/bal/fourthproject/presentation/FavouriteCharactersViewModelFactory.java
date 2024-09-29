package com.bal.fourthproject.presentation;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.bal.fourthproject.domain.CharacterRepository;

public class FavouriteCharactersViewModelFactory implements ViewModelProvider.Factory {

    private final CharacterRepository characterRepository;

    public FavouriteCharactersViewModelFactory(CharacterRepository characterRepository) {
        this.characterRepository = characterRepository;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(FavouriteCharactersViewModel.class)) {
            return (T) new FavouriteCharactersViewModel(characterRepository);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}
