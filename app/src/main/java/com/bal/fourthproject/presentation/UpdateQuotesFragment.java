package com.bal.fourthproject.presentation;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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
import com.bal.fourthproject.data.FirebaseService;

import java.lang.ref.SoftReference;
import java.util.HashMap;
import java.util.Map;


public class UpdateQuotesFragment extends Fragment {


    private String author;
    private String quote;
    private String authorId;
    private String key;
    private Button updateButton;
    private EditText authorEditText, quoteEditText;
    private SoftReference<QuotesFragment> quotesFragmentRef = new SoftReference<>(new QuotesFragment());

    private BroadcastReceiver updateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (FirebaseService.ACTION_UPDATE_RESULT.equals(intent.getAction())) {
                boolean isSuccess = intent.getBooleanExtra("isUpdated", false);
                if (isSuccess) {
                    Bundle bundle = new Bundle();
                    bundle.putString("quote", quote);
                    bundle.putString("author", author);
                    bundle.putString("authorId", authorId);
                    bundle.putString("key", key);

                    QuotesFragment quotesFragment = quotesFragmentRef.get();
                    if (quotesFragment == null) {
                        quotesFragment = new QuotesFragment();
                        quotesFragmentRef = new SoftReference<>(quotesFragment);
                    }
                    quotesFragment.setArguments(bundle);
                    replaceFragment(quotesFragment, R.id.first_fragment_container);
                    Toast.makeText(context, "Цитата успешно обновлена", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, "Ошибка при обновлении цитаты", Toast.LENGTH_SHORT).show();
                }
            }
        }
    };

    private void replaceFragment(Fragment fragment, int containerId) {
        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(containerId, fragment)
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            author = getArguments().getString("author");
            quote = getArguments().getString("quote");
            authorId = getArguments().getString("authorId");
            key = getArguments().getString("key");
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        IntentFilter updateFilter = new IntentFilter(FirebaseService.ACTION_UPDATE_RESULT);
        requireContext().registerReceiver(updateReceiver, updateFilter, Context.RECEIVER_EXPORTED);
    }

    @Override
    public void onPause() {
        super.onPause();
        requireContext().unregisterReceiver(updateReceiver);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_update_quotes, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        quoteEditText = view.findViewById(R.id.update_quote_id);
        authorEditText = view.findViewById(R.id.update_author_id);
        updateButton = view.findViewById(R.id.update_quote_buttom);

        if(quoteEditText != null && quote != null){
            quoteEditText.setText(quote);
        }
        if(authorEditText != null && author != null){
            authorEditText.setText(author);
        }

        updateButton.setOnClickListener(v->{

            quote = quoteEditText.getText().toString();
            author = authorEditText.getText().toString();
            Map<String, Object> updatedData = new HashMap<>();;
            if(quote !=  null && author != null && authorId != null && key != null){

                updatedData.put("quote", quote);
                updatedData.put("author", author);
                updatedData.put("authorId", authorId);
                updatedData.put("key", authorId);
            }

            Intent updateIntent = new Intent(requireContext(), FirebaseService.class);
            updateIntent.setAction(FirebaseService.ACTION_UPDATE_QUOTE);
            if(key != null)
                updateIntent.putExtra(FirebaseService.QUOTE_UPDATE_KEY_EXTRA, key);

            updateIntent.putExtra(FirebaseService.QUOTE_UPDATE_DATA_EXTRA, (HashMap) updatedData);

            requireContext().startService(updateIntent);

        });
    }
}