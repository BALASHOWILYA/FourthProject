package com.bal.fourthproject.presentation;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bal.fourthproject.data.Character;
import com.bal.fourthproject.data.DataFetchService;
import com.bal.fourthproject.R;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;


import com.bal.fourthproject.data.database.AppDatabase;
import com.bal.fourthproject.data.Character;
import com.bal.fourthproject.data.database.CharacterDao;
import com.bal.fourthproject.domain.CharacterModel;
import com.bal.fourthproject.domain.CharacterRepository;
import com.bal.fourthproject.data.database.CharacterRepositoryImpl;
import com.bal.fourthproject.data.DataFetchService;
import com.bal.fourthproject.R;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private FavouriteCharactersFragment favouriteCharactersFragment = new FavouriteCharactersFragment();
    private AllCharactersFragment allCharactersFragment = new AllCharactersFragment();




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        replaceFragment(favouriteCharactersFragment);
        // Инициализация базы данных, DAO и репозитория
        AppDatabase db = AppDatabase.getInstance(getApplicationContext());
        CharacterDao characterDao = db.characterDao();
        CharacterRepository characterRepository = new CharacterRepositoryImpl(characterDao);
        // Запуск сервиса для получения данных
        Intent intent = new Intent(this, DataFetchService.class);
        startService(intent);

        // Получение данных в отдельном потоке
        new Thread(new Runnable() {
            @Override
            public void run() {
                // Получение всех персонажей из репозитория
                List<CharacterModel> characters = characterRepository.getAllCharacters();

                // Вывод данных в лог
                for (CharacterModel character : characters) {
                    Log.d(TAG, "ID: " + character.getId() +
                            ", Name: " + character.getName() +
                            ", Status: " + character.getStatus() +
                            ", Species: " + character.getSpecies() +
                            ", Image URL: " + character.getImageUrl());
                }
            }
        }).start();
    }

    private void replaceFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.second_fragment_container, fragment)
                .commit();
    }

    private void addFragment(Fragment fragment){

        // Проверяем, добавлен ли фрагмент уже
        Fragment existingFragment = getSupportFragmentManager().findFragmentById(R.id.first_fragment_container);
        if (existingFragment != null && existingFragment.getClass().equals(fragment.getClass())) {
            // Фрагмент уже добавлен, не делаем ничего
            return;
        }

        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.first_fragment_container, fragment)
                .addToBackStack(null)
                .commit();

    }

    private void removeFragment(Fragment fragment){
        getSupportFragmentManager()
                .beginTransaction()
                .remove(fragment)
                .commit();
    }

}
