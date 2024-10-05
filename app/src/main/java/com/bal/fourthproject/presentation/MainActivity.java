package com.bal.fourthproject.presentation;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.TextUtils;
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
import android.widget.Button;
import android.widget.EditText;


import com.bal.fourthproject.data.Episode;
import com.bal.fourthproject.data.EpisodeFetchService;
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
    private EditText searchNameEditText, indexEpisode;
    private Button searchButton, searchEpisodeButton;

    private BroadcastReceiver episodeReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Episode episode = (Episode) intent.getParcelableExtra("episode");
            if (episode != null){
                Log.d("InMainActivityEpisode", episode.getName() + " "  + episode.getAirDate());
            }
        }
    };

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
        searchNameEditText = findViewById(R.id.search_name);
        indexEpisode = findViewById(R.id.index_episode);
        searchEpisodeButton = findViewById(R.id.search_episode_button);
        searchButton = findViewById(R.id.search_button);

        if (savedInstanceState == null) {
            addFragment(allCharactersFragment, R.id.second_fragment_container);
        }

        LocalBroadcastManager.getInstance(this).registerReceiver(episodeReceiver, new IntentFilter("com.bal.fourthproject.EPISODE_UPDATED"));


        Intent intent = new Intent(this, DataFetchService.class);
        startService(intent);

        searchEpisodeButton.setOnClickListener(v->{
            String id = indexEpisode.getText().toString();




            Intent secondIntent = new Intent(this, EpisodeFetchService.class);
            secondIntent.putExtra("episode_id",id );
            startService(secondIntent);
        });


        searchButton.setOnClickListener(v -> {
            String name = searchNameEditText.getText().toString();
            performSearch(name);
        });
    }


    private void addFragment(Fragment fragment, int containerId) {
        if (getSupportFragmentManager().findFragmentById(containerId) == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(containerId, fragment)
                    .commitAllowingStateLoss();
        }
    }

    private void performSearch(String name) {
        try {
            if (!TextUtils.isEmpty(name)) {
                Context context = getApplicationContext();
                Intent intent = new Intent(context, DataFetchService.class);
                intent.putExtra("name", name);
                context.startService(intent);
            } else {
                Log.e(TAG, "Search name is empty");
            }
        } catch (Exception e) {
            Log.e(TAG, "Error during search", e);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(episodeReceiver);
    }
}


