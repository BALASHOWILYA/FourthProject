package com.bal.fourthproject.presentation;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.bal.fourthproject.R;
import com.bal.fourthproject.data.DataFetchService;

public class CharacterSearchFragment extends Fragment {

    private EditText searchNameEditText;
    private Button searchButton;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_character_search, container, false);

        searchNameEditText = view.findViewById(R.id.search_name);
        searchButton = view.findViewById(R.id.search_button);

        searchButton.setOnClickListener(v -> performSearch());

        return view;
    }

    private void performSearch() {
        String name = searchNameEditText.getText().toString().trim();
        if (!TextUtils.isEmpty(name)) {
            Context context = getContext();
            if (context != null) {
                Intent intent = new Intent(context, DataFetchService.class);
                intent.putExtra("name", name);
                ContextCompat.startForegroundService(context, intent);
            }
        }
    }
}
