package com.bal.fourthproject.presentation;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.bal.fourthproject.domain.CharacterModel;
import com.bal.fourthproject.domain.GetAllCharactersUseCase;

import java.util.List;

public class FavouriteCharactersViewModel extends AndroidViewModel {


    private GetAllCharactersUseCase getAllCharactersUseCase;
    private final MutableLiveData<List<CharacterModel>> allFavouriteCharacters = new MutableLiveData<>();


    public LiveData<List<CharacterModel>> getCourses() {
        return allFavouriteCharacters;
    }


    public FavouriteCharactersViewModel(@NonNull Application application) {
        super(application);

    }
    public void loadCharacters() {
        new Thread(() -> {
            List<CharacterModel> characterModelList = getAllCharactersUseCase.execute();
            allFavouriteCharacters.postValue(characterModelList);
        }).start();
    }

    public void getAllCoursesViewModel(GetAllCharactersUseCase getAllCharactersUseCase) {
        this.getAllCharactersUseCase = getAllCharactersUseCase;
    }
}
