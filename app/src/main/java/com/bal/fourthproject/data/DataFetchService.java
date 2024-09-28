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

    @Override
    public void onCreate() {
        super.onCreate();


        executorService = Executors.newSingleThreadExecutor();
        mainHandler = new Handler(Looper.getMainLooper());
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        executorService.execute(new Runnable() {
            @Override
            public void run() {

                RickAndMortyApiService apiService = ApiClient.getRetrofitInstance().create(RickAndMortyApiService.class);
                Call<CharacterResponse> call = apiService.getCharacters();
                call.enqueue(new Callback<CharacterResponse>() {
                    @Override
                    public void onResponse(Call<CharacterResponse> call, Response<CharacterResponse> response) {
                        if (response.isSuccessful()) {



                            List<Character> characters = response.body().getCharacters();
                            mainHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    sendBroadcast(characters);
                                }
                            });
                        } else {
                            Log.e("DataFetchService", "Ошибка запроса " + response.code());
                        }
                    }

                    @Override
                    public void onFailure(Call<CharacterResponse> call, Throwable t) {
                        Log.e("DataFetchService", t.getMessage());
                    }
                });
            }
        });

        return START_STICKY;
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