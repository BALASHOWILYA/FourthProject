package com.bal.fourthproject.data;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.text.style.TtsSpan;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.bal.fourthproject.R;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DataFetchService extends Service {

    private ExecutorService executorService;
    private ExecutorService secondExecutorService;
    private Handler mainHandler;
    private final String TAG = "DataFetchService";

    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannel();
        executorService = Executors.newSingleThreadExecutor();
        secondExecutorService = Executors.newSingleThreadExecutor();
        mainHandler = new Handler(Looper.getMainLooper());
    }
    private void createNotificationChannel() {
        NotificationChannel channel = new NotificationChannel("channel_id",
                "Data Fetch Service",
                NotificationManager.IMPORTANCE_DEFAULT);
        NotificationManager manager = getSystemService(NotificationManager.class);
        if (manager != null) {
            manager.createNotificationChannel(channel);
        }
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {


        String characterName = intent.getStringExtra("name");
        String characterGender = intent.getStringExtra("gender");
        String characterOrigin = intent.getStringExtra("origin");
        String characterSpecies = intent.getStringExtra("species");
        Log.d("MData", characterGender + characterName + characterSpecies + characterOrigin);
        if (characterName != null && !characterName.isEmpty() || characterGender != null && !characterGender.isEmpty() || characterOrigin != null && !characterOrigin.isEmpty() || characterSpecies != null && !characterSpecies.isEmpty()) {
            searchCharactersByName(characterName, characterGender, characterOrigin, characterSpecies);

        } else {
            fetchAllCharacters();
        }



        return START_STICKY;
    }

    private void fetchAllCharacters() {
        try {
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
        } catch (OutOfMemoryError e){
            executorService.shutdown();
        }

    }

    private void searchCharactersByName(String name, String gender, String origin, String species) {
        try {
            secondExecutorService.execute(()->{
                RickAndMortyApiService apiService = ApiClient.getApiService();
                Call<CharacterResponse> call = apiService.searchCharacters(name, gender, species, origin);

                call.enqueue(new Callback<CharacterResponse>() {
                    @Override
                    public void onResponse(Call<CharacterResponse> call, Response<CharacterResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {

                            List<Character> characters = response.body().getCharacters();
                            mainHandler.post(() -> sendBroadcast(characters));
                            for (Character character : characters) {
                                Log.d("DataFetchService", "Character: " + character.toString());
                            }
                        } else {
                            Log.e("DataFetchService", "Ошибка: " + response.code());
                        }
                    }

                    @Override
                    public void onFailure(Call<CharacterResponse> call, Throwable t) {
                        Log.e("DataFetchService", "Ошибка загрузки данных: " + t.getMessage());
                    }
                });
            });
        } catch (OutOfMemoryError e ){
            secondExecutorService.shutdown();
        }


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
        secondExecutorService.shutdown();
    }
}
