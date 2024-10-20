package com.bal.fourthproject.presentation;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.bal.fourthproject.R;
import com.bal.fourthproject.data.FirebaseAddQuoteService;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class AddQuateFragment extends Fragment {

    private EditText queteEditText, authorEditText;
    private Button addButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_add_quate, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        queteEditText = view.findViewById(R.id.quote_id);
        authorEditText = view.findViewById(R.id.author_id);
        addButton = view.findViewById(R.id.addQuoteButton);

        addButton.setOnClickListener(v -> {
            String quote = queteEditText.getText().toString();
            String author = authorEditText.getText().toString();

            if (quote.isEmpty()) {
                queteEditText.setError("Cannot be empty");
                return;
            }

            if (author.isEmpty()) {
                authorEditText.setError("Cannot be empty");
                return;
            }

            // Получаем текущего пользователя из Firebase
            FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
            if (currentUser != null) {
                String authorId = currentUser.getUid(); // Уникальный ID пользователя

                // Запуск сервиса для добавления данных в Firebase
                Intent serviceIntent = new Intent(requireContext(), FirebaseAddQuoteService.class);
                serviceIntent.setAction(FirebaseAddQuoteService.ACTION_ADD_QUOTE);
                serviceIntent.putExtra(FirebaseAddQuoteService.EXTRA_QUOTE, quote);
                serviceIntent.putExtra(FirebaseAddQuoteService.EXTRA_AUTHOR, author);
                serviceIntent.putExtra(FirebaseAddQuoteService.EXTRA_AUTHOR_ID, authorId); // Передаем authorId
                requireContext().startService(serviceIntent);

                queteEditText.getText().clear();
                authorEditText.getText().clear();

                switchToQuotesFragment();
            } else {
                Toast.makeText(getContext(), "Ошибка: пользователь не авторизован", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void switchToQuotesFragment() {
        QuotesFragment quotesFragment = new QuotesFragment();
        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.first_fragment_container, quotesFragment)
                .addToBackStack(null)
                .commit();
    }
}
