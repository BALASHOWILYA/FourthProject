package com.bal.fourthproject.presentation;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bal.fourthproject.R;
import com.bal.fourthproject.data.database.CharacterDao;
import com.bal.fourthproject.data.database.CharacterRepositoryImpl;
import com.bal.fourthproject.domain.CharacterModel;
import com.bal.fourthproject.domain.CharacterModel;
import com.bal.fourthproject.data.database.AppDatabase;

import java.util.List;

public class FavouriteCharactersFragment extends Fragment {

    private RecyclerView recyclerView;
    private FavouriteCharactersAdapter favouriteCharactersAdapter;
    private FavouriteCharactersViewModel viewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_favourite_characters, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Инициализация RecyclerView
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        favouriteCharactersAdapter = new FavouriteCharactersAdapter();
        recyclerView.setAdapter(favouriteCharactersAdapter);

        // Инициализация базы данных и репозитория
        CharacterDao characterDao = AppDatabase.getInstance(requireContext()).characterDao();
        CharacterRepositoryImpl characterRepository = new CharacterRepositoryImpl(characterDao);

        // Инициализация ViewModel
        viewModel = new ViewModelProvider(this, new FavouriteCharactersViewModelFactory(characterRepository)).get(FavouriteCharactersViewModel.class);

        // Наблюдаем за данными
        viewModel.getCharacters().observe(getViewLifecycleOwner(), characters -> {
            if (characters != null) {
                favouriteCharactersAdapter.setCharacters(characters);
            }
        });

        // Загрузка персонажей
        viewModel.loadCharacters();
    }
}
