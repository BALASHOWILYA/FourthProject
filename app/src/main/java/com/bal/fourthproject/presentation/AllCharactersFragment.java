package com.bal.fourthproject.presentation;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bal.fourthproject.R;
import com.bal.fourthproject.data.Character;
import com.bal.fourthproject.data.database.AppDatabase;
import com.bal.fourthproject.data.database.CharacterDao;
import com.bal.fourthproject.data.database.CharacterRepositoryImpl;
import com.bal.fourthproject.domain.CharacterRepository;

import java.util.ArrayList;

public class AllCharactersFragment extends Fragment {

    private RecyclerView recyclerView;
    private CharacterAdapter characterAdapter;
    private CharacterRepository characterRepository;

    private final BroadcastReceiver dataReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if ("com.bal.fourthproject.DATA_UPDATED".equals(intent.getAction())) {
                ArrayList<Character> characters = intent.getParcelableArrayListExtra("characters");
                if (characters != null) {
                    updateUI(characters);
                }
            }
        }
    };

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Инициализация базы данных, DAO и репозитория
        Context context = getContext();
        if (context != null) {
            AppDatabase db = AppDatabase.getInstance(context);
            CharacterDao characterDao = db.characterDao();
            characterRepository = new CharacterRepositoryImpl(characterDao);

            // Регистрация BroadcastReceiver
            LocalBroadcastManager.getInstance(requireActivity())
                    .registerReceiver(dataReceiver, new IntentFilter("com.bal.fourthproject.DATA_UPDATED"));
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_all_characters, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Настройка RecyclerView
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        // Инициализация адаптера с пустым списком
        characterAdapter = new CharacterAdapter(new ArrayList<>(), characterRepository);
        recyclerView.setAdapter(characterAdapter);
    }

    private void updateUI(ArrayList<Character> characters) {
        characterAdapter.setCharacters(characters);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // Отмена регистрации BroadcastReceiver
        LocalBroadcastManager.getInstance(requireActivity()).unregisterReceiver(dataReceiver);
    }
}
