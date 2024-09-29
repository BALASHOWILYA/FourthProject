package com.bal.fourthproject.data;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface RickAndMortyApiService {

    @GET("api/character")
    Call<CharacterResponse> getCharacters();

    @GET("api/character")
    Call<CharacterResponse> searchCharacters(@Query("name") String name);
}
