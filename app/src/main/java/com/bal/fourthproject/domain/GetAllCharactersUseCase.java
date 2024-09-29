package com.bal.fourthproject.domain;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class GetAllCharactersUseCase {
    private final CharacterRepository characterRepository;

    public GetAllCharactersUseCase(CharacterRepository characterRepository) {
        this.characterRepository = characterRepository;
    }

    public List<CharacterModel> execute() {
        CompletableFuture<List<CharacterModel>> future = CompletableFuture.supplyAsync(characterRepository::getAllCharacters);
        try {
            return future.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            return null;
        }
    }
}
