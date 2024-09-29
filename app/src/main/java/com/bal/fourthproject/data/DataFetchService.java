package com.bal.fourthproject.data;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DataFetchService extends Service {

    private ExecutorService executorService;
    private Handler mainHandler;
    private static final String TAG = "DataFetchService";

    @Override
    public void onCreate() {
        super.onCreate();
        executorService = Executors.newSingleThreadExecutor();
        mainHandler = new Handler(Looper.getMainLooper());
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String characterName = intent.getStringExtra("name"); // получаем имя персонажа из интента
        if (characterName != null && !characterName.isEmpty()) {
            searchCharactersByName(characterName); // поиск по имени
        } else {
            fetchAllCharacters(); // если имя не введено, загружаем всех персонажей
        }
        return START_STICKY;
    }

    private void fetchAllCharacters() {
        executorService.execute(() -> {
            RickAndMortyApiService apiService = ApiClient.getApiService();
            Call<CharacterResponse> call = apiService.getCharacters();
            call.enqueue(new Callback<CharacterResponse>() {
                @Override
                public void onResponse(Call<CharacterResponse> call, Response<CharacterResponse> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        List<Character> characters = response.body().getCharacters();
                        mainHandler.post(() -> sendBroadcast(characters));
                    } else {
                        Log.e(TAG, "Ошибка: " + response.code());
                    }
                }

                @Override
                public void onFailure(Call<CharacterResponse> call, Throwable t) {
                    Log.e(TAG, "Ошибка загрузки данных: " + t.getMessage());
                }
            });
        });
    }

    private void searchCharactersByName(String name) {
        executorService.execute(() -> {
            RickAndMortyApiService apiService = ApiClient.getApiService();
            Call<CharacterResponse> call = apiService.searchCharacters(name);
            call.enqueue(new Callback<CharacterResponse>() {
                @Override
                public void onResponse(Call<CharacterResponse> call, Response<CharacterResponse> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        List<Character> characters = response.body().getCharacters();
                        for( Character character : characters)
                            Log.e(TAG, "Data: " + character.toString() );
                        mainHandler.post(() -> sendBroadcast(characters));
                    } else {
                        Log.e(TAG, "Ошибка: " + response.code());
                    }
                }

                @Override
                public void onFailure(Call<CharacterResponse> call, Throwable t) {
                    Log.e(TAG, "Ошибка загрузки данных: " + t.getMessage());
                }
            });
        });
    }

    private void sendBroadcast(List<Character> characters) {
        Intent broadcastIntent = new Intent("com.bal.fourthproject.DATA_UPDATED");
        broadcastIntent.putParcelableArrayListExtra("characters", new ArrayList<>(characters));
        LocalBroadcastManager.getInstance(this).sendBroadcast(broadcastIntent);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        executorService.shutdown();
    }
}
