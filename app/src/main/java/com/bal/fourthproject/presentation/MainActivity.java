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
    private RecyclerView recyclerView;
    private CharacterAdapter characterAdapter;

    private final BroadcastReceiver dataReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if ("com.bal.fourthproject.DATA_UPDATED".equals(intent.getAction())) {
                ArrayList<Character> characters = intent.getParcelableArrayListExtra("characters");
                updateUI(characters);
            }
        }
    };

    @Override
    protected void onStart() {
        super.onStart();
        LocalBroadcastManager.getInstance(this).registerReceiver(dataReceiver, new IntentFilter("com.bal.fourthproject.DATA_UPDATED"));
    }

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

        // Инициализация базы данных, DAO и репозитория
        AppDatabase db = AppDatabase.getInstance(this);
        CharacterDao characterDao = db.characterDao();
        CharacterRepository characterRepository = new CharacterRepositoryImpl(characterDao);

        // Настройка RecyclerView
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Инициализация адаптера с использованием репозитория
        characterAdapter = new CharacterAdapter(new ArrayList<>(), characterRepository);
        recyclerView.setAdapter(characterAdapter);

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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(dataReceiver);
    }

    private void updateUI(ArrayList<Character> characters) {
        characterAdapter.setCharacters(characters);
    }
}
