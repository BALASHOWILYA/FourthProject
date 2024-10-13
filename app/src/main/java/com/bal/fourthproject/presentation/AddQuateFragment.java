package com.bal.fourthproject.presentation;

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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;


public class AddQuateFragment extends Fragment {


    private EditText queteEditText, authorEditText;
    private Button addButton;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add_quate, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        queteEditText = view.findViewById(R.id.quote_id);
        authorEditText = view.findViewById(R.id.author_id);
        addButton = view.findViewById(R.id.addQuoteButton);

        addButton.setOnClickListener(v ->
        {
            String quote = queteEditText.getText().toString();
            String author = authorEditText.getText().toString();

            if(quote.isEmpty()){
                queteEditText.setError("Cannot be empty");
                return;
            }

            if(author.isEmpty()){
                authorEditText.setError("Cannot be empty");
                return;
            }

            addQuoteToDB(quote, author);
        });

    }

    private void addQuoteToDB(String quote, String author) {

        HashMap<String, Object> quoteHashMap = new HashMap<>();
        quoteHashMap.put("quote", quote);
        quoteHashMap.put("author", author);

        // Write a message to the database
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference quotesRef = database.getReference("quotes");
        String key = quotesRef.push().getKey();
        quoteHashMap.put("key", key);

        quotesRef.child(key).setValue(quoteHashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Toast.makeText(requireContext(), "added", Toast.LENGTH_LONG).show();
                queteEditText.getText().clear();
                authorEditText.getText().clear();
            }
        });




    }
}