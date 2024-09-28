package com.bal.fourthproject.data;

import retrofit2.Call;
import retrofit2.http.GET;

public interface RickAndMortyApiService {

    @GET("api/character")
    Call<CharacterResponse> getCharacters();
}
