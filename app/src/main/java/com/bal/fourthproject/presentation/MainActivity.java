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

import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private FavouriteCharactersFragment favouriteCharactersFragment = new FavouriteCharactersFragment();
    private AllCharactersFragment allCharactersFragment = new AllCharactersFragment();
    private EditText indexEpisode;
    private EditText search_gender, search_origin, search_species, search_name;
    private Button searchButton, searchEpisodeButton, filterCharacters;
    private ShapeableImageView imageView;

    FirebaseAuth auth;
    
    TextView name, mail;


    private final ActivityResultLauncher<Intent> activityResultLauncher=
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == RESULT_OK){
                        Task<GoogleSignInAccount> accountTask = GoogleSignIn.getSignedInAccountFromIntent(result.getData());
                        try {
                            GoogleSignInAccount signInAccount = accountTask.getResult(ApiException.class);

                            AuthCredential authCredential = GoogleAuthProvider.getCredential(signInAccount.getIdToken(), null);

                            auth.signInWithCredential(authCredential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if(task.isSuccessful()){
                                        auth = FirebaseAuth.getInstance();

                                        Glide.with(MainActivity.this).load(Objects.requireNonNull(auth.getCurrentUser()).getPhotoUrl()).into(imageView);
                                        name.setText(auth.getCurrentUser().getDisplayName());
                                        mail.setText(auth.getCurrentUser().getEmail());

                                        makeText(MainActivity.this, "Signed in successfully!", Toast.LENGTH_LONG).show();
                                    }
                                    else{
                                        makeText(MainActivity.this, "Signed in failed!", Toast.LENGTH_LONG).show();
                                    }
                                }
                            });

                        } catch (ApiException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });



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

        imageView = findViewById(R.id.imageView);
        name = findViewById(R.id.nameTV);
        mail = findViewById(R.id.emailTV);





        if (savedInstanceState == null) {
            addFragment(allCharactersFragment, R.id.second_fragment_container);
        }




        GoogleSignInOptions options = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.client_id))
                .requestEmail()
                .build();


        GoogleSignInClient googleSignInClient = GoogleSignIn.getClient(MainActivity.this, options);
        auth = FirebaseAuth.getInstance();

        SignInButton signInButton = findViewById(R.id.SignIn);
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = googleSignInClient.getSignInIntent();
                activityResultLauncher.launch(intent);
            }
        });

        MaterialButton signOut = findViewById(R.id.SignOut);
        signOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().addAuthStateListener(new FirebaseAuth.AuthStateListener() {
                    @Override
                    public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                        if (firebaseAuth.getCurrentUser() == null) {
                            googleSignInClient.signOut().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    makeText(MainActivity.this, "Signed out successfully", Toast.LENGTH_LONG).show();
                                    startActivity(new Intent(MainActivity.this, MainActivity.class));
                                }
                            });
                        }
                    }
                });

                FirebaseAuth.getInstance().signOut();
            }
        });

        if (auth.getCurrentUser() != null) {
            Glide.with(MainActivity.this).load(Objects.requireNonNull(auth.getCurrentUser().getDisplayName())).into(imageView);
            name.setText(auth.getCurrentUser().getDisplayName());
            mail.setText(auth.getCurrentUser().getEmail());
        }



    }

    private void services(){

        search_name = findViewById(R.id.search_name);
        search_gender = findViewById(R.id.search_gender);
        search_origin = findViewById(R.id.search_origin);
        search_species = findViewById(R.id.search_species);
        indexEpisode = findViewById(R.id.index_episode);
        searchEpisodeButton = findViewById(R.id.search_episode_button);
        searchButton = findViewById(R.id.search_button);


        searchEpisodeButton.setOnClickListener(v -> {
            String idString = indexEpisode.getText().toString();
            int id = Integer.parseInt(idString);


            Intent secondIntent = new Intent(this, EpisodeFetchService.class);
            secondIntent.putExtra("episode_id", id);
            startService(secondIntent);
        });


        searchButton.setOnClickListener(v -> {

            String name = search_name.getText().toString();
            String gender = search_gender.getText().toString();
            String species = search_species.getText().toString();
            String origin = search_origin.getText().toString();
            performSearch(name, gender, origin, species);
        });


        search_name = findViewById(R.id.search_name);
        search_gender = findViewById(R.id.search_gender);
        search_origin = findViewById(R.id.search_origin);
        search_species = findViewById(R.id.search_species);
        indexEpisode = findViewById(R.id.index_episode);
        searchEpisodeButton = findViewById(R.id.search_episode_button);
        searchButton = findViewById(R.id.search_button);

        LocalBroadcastManager.getInstance(this).registerReceiver(episodeReceiver, new IntentFilter("com.bal.fourthproject.EPISODE_UPDATED"));


        Intent intent = new Intent(this, DataFetchService.class);
        startService(intent);


        searchEpisodeButton.setOnClickListener(v -> {
            String idString = indexEpisode.getText().toString();
            int id = Integer.parseInt(idString);


            Intent secondIntent = new Intent(this, EpisodeFetchService.class);
            secondIntent.putExtra("episode_id", id);
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


