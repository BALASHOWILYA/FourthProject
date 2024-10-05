package com.bal.fourthproject.data;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EpisodeFetchService extends Service {

    private ExecutorService executorService;
    private Handler mainHander;

    @Override
    public void onCreate() {
        super.onCreate();

        executorService = Executors.newSingleThreadExecutor();
        mainHander = new Handler(Looper.getMainLooper());
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {


        int episodeId = intent.getIntExtra("episode_id", -1);

        if(episodeId != -1){
            fetchEpisodeById(episodeId);
        }

        return START_STICKY;

    }

    private void fetchEpisodeById(int episodeId) {
        executorService.execute(()->{
            RickAndMortyApiService apiService = ApiClient.getApiService();
            Call<Episode> call = apiService.getEpisodeById(episodeId);
            call.enqueue(new Callback<Episode>() {
                @Override
                public void onResponse(Call<Episode> call, Response<Episode> response) {
                    if (response.isSuccessful() && response.body() != null){
                        Episode episode = response.body();
                        Log.d("Episode", "Episode:" + episode.getName() + episode.getAirDate());
                        mainHander.post(() -> sendEpisodeBroadcast(episode));
                    }
                }

                @Override
                public void onFailure(Call<Episode> call, Throwable t) {
                    Log.d("Episode", "loading exception");

                }
            });


        });

    }

    private void sendEpisodeBroadcast(Episode episode) {
        Intent broadcastIntent = new Intent("com.bal.fourthproject.EPISODE_UPDATED");
        broadcastIntent.putExtra("episode", episode);
        LocalBroadcastManager.getInstance(this).sendBroadcast(broadcastIntent);
    }

    public EpisodeFetchService() {
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