package com.bal.fourthproject.data;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface RickAndMortyApiService {

    @GET("api/character")
    Call<CharacterResponse> getCharacters();

    @GET("api/character")
    Call<CharacterResponse> searchCharacters(@Query("name") String name,
                                             @Query("gender") String gender,
                                             @Query("species") String species,
                                             @Query("origin") String origin
                                             );

    @GET("api/episode/{id}")
    Call<Episode> getEpisodeById(@Path("id") int id);



}
