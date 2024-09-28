package com.bal.fourthproject.data.database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface CharacterDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertCharacter(CharacterEntity characters);

    @Query("SELECT * FROM characters")
    List<CharacterEntity> getAllCharacters();
}

