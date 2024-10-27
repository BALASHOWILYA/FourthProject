package com.bal.fourthproject.data.database;

import com.bal.fourthproject.domain.CharacterModel;
import com.bal.fourthproject.domain.CharacterRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class CharacterRepositoryImpl implements CharacterRepository {

    private final CharacterDao characterDao;
    private final ExecutorService executorService;

    public CharacterRepositoryImpl(CharacterDao characterDao) {
        this.characterDao = characterDao;
        // Инициализируем ExecutorService для выполнения операций в отдельных потоках
        this.executorService = Executors.newSingleThreadExecutor(); // Используем одиночный поток для операций с базой данных
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

        // Выполняем операцию вставки в отдельном потоке
        executorService.execute(() -> characterDao.insertCharacter(characterEntity));
    }

    @Override
    public List<CharacterModel> getAllCharacters() {
        // Используем Callable для асинхронного выполнения получения данных из БД

        Callable<List<CharacterEntity>> task = characterDao::getAllCharacters;
        Future<List<CharacterEntity>> future = executorService.submit(task);

        List<CharacterModel> models = new ArrayList<>();
        try {
            List<CharacterEntity> entities = future.get(); // Получаем результат из потока
            for (CharacterEntity entity : entities) {
                models.add(new CharacterModel(
                        entity.getId(),
                        entity.getName(),
                        entity.getStatus(),
                        entity.getSpecies(),
                        entity.getImageUrl()
                ));
            }
        } catch (Exception e) {
            e.printStackTrace(); // Логирование ошибок
        }

        return models;
    }

    // Закрытие ExecutorService при необходимости
    public void shutdown() {
        executorService.shutdown();
    }
}
