package com.bal.fourthproject.presentation;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.bal.fourthproject.R;
import com.bal.fourthproject.data.DataFetchService;


public class SearchCharactersFragment extends Fragment {

    private static final String TAG = "SearchCharactersFragment";
    private EditText search_gender, search_origin, search_species, search_name;
    private Button searchButton;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_search_characters, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        search_name = view.findViewById(R.id.search_name);
        search_gender = view.findViewById(R.id.search_gender);
        search_origin = view.findViewById(R.id.search_origin);
        search_species = view.findViewById(R.id.search_species);
        searchButton = view.findViewById(R.id.search_button);


        searchButton.setOnClickListener(v -> {

            String name = search_name.getText().toString();
            String gender = search_gender.getText().toString();
            String species = search_species.getText().toString();
            String origin = search_origin.getText().toString();
            performSearch(name, gender, origin, species);
        });


    }

    private void performSearch(String name, String gender, String origin, String species) {


        try {
            if (!TextUtils.isEmpty(name) || !TextUtils.isEmpty(gender) || !TextUtils.isEmpty(origin) || !TextUtils.isEmpty(species)) {
                Context context = getContext();
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
}