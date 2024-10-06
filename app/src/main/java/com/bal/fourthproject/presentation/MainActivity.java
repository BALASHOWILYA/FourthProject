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

import com.bal.fourthproject.data.DataFetchService;
import com.bal.fourthproject.R;

import android.widget.Button;
import android.widget.EditText;


import com.bal.fourthproject.data.Episode;
import com.bal.fourthproject.data.EpisodeFetchService;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private FavouriteCharactersFragment favouriteCharactersFragment = new FavouriteCharactersFragment();
    private AllCharactersFragment allCharactersFragment = new AllCharactersFragment();
    private EditText indexEpisode;
    private EditText search_gender, search_origin, search_species, search_name;
    private Button searchButton, searchEpisodeButton, filterCharacters;

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
        search_name = findViewById(R.id.search_name);
        search_gender = findViewById(R.id.search_gender);
        search_origin = findViewById(R.id.search_origin);
        search_species = findViewById(R.id.search_species);
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
            String idString = indexEpisode.getText().toString();
            int id = Integer.parseInt(idString);



            Intent secondIntent = new Intent(this, EpisodeFetchService.class);
            secondIntent.putExtra("episode_id",id );
            startService(secondIntent);
        });


        searchButton.setOnClickListener(v -> {

            String name = search_name.getText().toString();
            String gender = search_gender.getText().toString();
            String species = search_species.getText().toString();
            String origin = search_origin.getText().toString();
            performSearch(name, gender, origin, species);
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

    private void performSearch(String name, String gender, String origin, String species) {
        try {
            if (!TextUtils.isEmpty(name) || !TextUtils.isEmpty(gender) || !TextUtils.isEmpty(origin) || !TextUtils.isEmpty(species)) {
                Context context = getApplicationContext();
                Intent intent = new Intent(context, DataFetchService.class);
                if(!TextUtils.isEmpty(name))
                    intent.putExtra("name", name);
                if(!TextUtils.isEmpty(gender))
                    intent.putExtra("gender", gender);
                if(!TextUtils.isEmpty(origin))
                    intent.putExtra("origin", origin);
                if(!TextUtils.isEmpty(species))
                    intent.putExtra("species", species);
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


