package com.bal.fourthproject.presentation;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bal.fourthproject.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Objects;


public class QuotesFragment extends Fragment {

   private FloatingActionButton floatingActionButton;
   private AddQuateFragment addQuateFragment = new AddQuateFragment();





    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_quotes, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        floatingActionButton = view.findViewById(R.id.floatingButton);
        floatingActionButton.setOnClickListener(v -> {

            replaceFragment(addQuateFragment, R.id.first_fragment_container);
        });
    }

    private void replaceFragment(Fragment fragment, int containerId) {
        if (requireActivity().getSupportFragmentManager().findFragmentById(containerId) == null) {
            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(containerId, fragment)

                    .commitAllowingStateLoss();
        }
    }
}