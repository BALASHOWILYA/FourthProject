package com.bal.fourthproject.domain;

import java.util.List;

public interface CharacterRepository {

    void addCharacter(CharacterModel characterModel);

    List<CharacterModel> getAllCharacters();
}
