package com.bal.fourthproject;

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

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private RecyclerView recyclerView;
    private CharacterAdapter characterAdapter;


    private final BroadcastReceiver dataReceiver = new BroadcastReceiver() {


        @Override
        public void onReceive(Context context, Intent intent) {


            if("com.bal.fourthproject.DATA_UPDATED".equals(intent.getAction())){
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


        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        characterAdapter = new CharacterAdapter(new ArrayList<>());
        recyclerView.setAdapter(characterAdapter);





        Intent intent = new Intent(this, DataFetchService.class);
        startService(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(dataReceiver);
    }

    private void updateUI(ArrayList<Character> characters) {

        characterAdapter.setCharacters(characters);

        for (Character characters1 : characters){
            Log.d(TAG, characters1.toString());
        }

    }
}