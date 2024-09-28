package com.bal.fourthproject.domain;


// Модель персонажа для domain слоя (если она отличается от сущности базы данных)
public class CharacterModel {

    private int id;
    private String name;
    private String status;
    private String species;
    private String imageUrl;

    public CharacterModel(int id, String name, String status, String species, String imageUrl) {
        this.id = id;
        this.name = name;
        this.status = status;
        this.species = species;
        this.imageUrl = imageUrl;
    }

    // Геттеры и сеттеры
    public int getId() { return id; }
    public String getName() { return name; }
    public String getStatus() { return status; }
    public String getSpecies() { return species; }
    public String getImageUrl() { return imageUrl; }
}
