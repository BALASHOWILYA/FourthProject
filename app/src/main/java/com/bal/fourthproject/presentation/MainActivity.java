package com.bal.fourthproject.presentation;

import static android.widget.Toast.makeText;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.bal.fourthproject.data.DataFetchService;
import com.bal.fourthproject.R;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import com.bal.fourthproject.data.Episode;
import com.bal.fourthproject.data.EpisodeFetchService;
import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private FavouriteCharactersFragment favouriteCharactersFragment = new FavouriteCharactersFragment();
    private SearchCharactersFragment searchCharactersFragment = new SearchCharactersFragment();
    private QuotesFragment quotesFragment = new QuotesFragment();
    private AddQuateFragment addQuateFragment = new AddQuateFragment();
    private AuthFragment authFragment = new AuthFragment();
    private AllCharactersFragment allCharactersFragment = new AllCharactersFragment();
    private EditText indexEpisode;
    private Button  searchEpisodeButton, filterCharacters;



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

        if (savedInstanceState == null) {
            addFragment(authFragment, R.id.first_fragment_container);
        }
        services();



    }

    private void services(){


        LocalBroadcastManager.getInstance(this).registerReceiver(episodeReceiver, new IntentFilter("com.bal.fourthproject.EPISODE_UPDATED"));


        Intent intent = new Intent(this, DataFetchService.class);
        startService(intent);

    }


    private void addFragment(Fragment fragment, int containerId) {
        if (getSupportFragmentManager().findFragmentById(containerId) == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(containerId, fragment)
                    .commitAllowingStateLoss();
        }
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(episodeReceiver);
    }
}


