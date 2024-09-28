package com.bal.fourthproject.data.database;

import com.bal.fourthproject.domain.CharacterModel;
import com.bal.fourthproject.domain.CharacterRepository;

import java.util.ArrayList;
import java.util.List;

public class CharacterRepositoryImpl implements CharacterRepository {

    private CharacterDao characterDao;

    public CharacterRepositoryImpl(CharacterDao characterDao) {
        this.characterDao = characterDao;
    }

    @Override
    public void addCharacter(CharacterModel characterModel) {
        CharacterEntity characterEntity = new CharacterEntity(
                characterModel.getId(),
                characterModel.getName(),
                characterModel.getStatus(),
                characterModel.getSpecies(),
                characterModel.getImageUrl()
        );

        new Thread(() -> characterDao.insertCharacter(characterEntity)).start();
    }


    @Override
    public List<CharacterModel> getAllCharacters() {
        List<CharacterEntity> entities = characterDao.getAllCharacters();
        List<CharacterModel> models = new ArrayList<>();

        for (CharacterEntity entity : entities) {
            models.add(new CharacterModel(
                    entity.getId(),
                    entity.getName(),
                    entity.getStatus(),
                    entity.getSpecies(),
                    entity.getImageUrl()
            ));
        }

        return models;
    }
}
