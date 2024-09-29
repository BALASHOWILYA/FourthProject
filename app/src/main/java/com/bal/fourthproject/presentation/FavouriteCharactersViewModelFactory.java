package com.bal.fourthproject.presentation;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.bal.fourthproject.domain.CharacterRepository;
import com.bal.fourthproject.domain.GetAllCharactersUseCase;

public class FavouriteCharactersViewModelFactory implements ViewModelProvider.Factory {

    private final GetAllCharactersUseCase getAllCharactersUseCase;

    public FavouriteCharactersViewModelFactory(GetAllCharactersUseCase characterRepository) {
        this.getAllCharactersUseCase = characterRepository;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(FavouriteCharactersViewModel.class)) {
            return (T) new FavouriteCharactersViewModel(getAllCharactersUseCase);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}
