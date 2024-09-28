package com.bal.fourthproject.data.database;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "characters")
public class CharacterEntity {

    @PrimaryKey(autoGenerate = true)
    private int id;
    private String name;
    private String status;
    private String species;
    private String imageUrl;

    public CharacterEntity(int id, String name, String status, String species, String imageUrl) {
        this.id = id;
        this.name = name;
        this.status = status;
        this.species = species;
        this.imageUrl = imageUrl;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getStatus() {
        return status;
    }

    public String getSpecies() {
        return species;
    }

    public String getImageUrl() {
        return imageUrl;
    }
}
