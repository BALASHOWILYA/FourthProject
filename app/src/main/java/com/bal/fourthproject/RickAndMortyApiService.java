package com.bal.fourthproject;

import retrofit2.Call;
import retrofit2.http.GET;

public interface RickAndMortyApiService {

    @GET("api/character")
    Call<CharacterResponse> getCharacters();
}
