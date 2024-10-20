package com.bal.fourthproject.presentation;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.bal.fourthproject.R;
import com.bal.fourthproject.data.FirebaseService;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class QuotesFragment extends Fragment implements QuotesAdapter.OnDeleteClickListener {

    private FloatingActionButton floatingActionButton;
    private AddQuateFragment addQuateFragment = new AddQuateFragment();
    private RecyclerView quotesRecyclerView;
    private QuotesAdapter quotesAdapter;
    private List<Map<String, String>> quotesList = new ArrayList<>();
    private String autherId;  // UID пользователя

    private BroadcastReceiver quotesReceiver = new BroadcastReceiver() {
        @SuppressLint("NotifyDataSetChanged")
        @Override
        public void onReceive(Context context, Intent intent) {
            if (FirebaseService.ACTION_FETCH_QUOTES.equals(intent.getAction())) {
                boolean isError = intent.getBooleanExtra("error", false);
                if (isError) {
                    Toast.makeText(context, "Ошибка при загрузке данных", Toast.LENGTH_SHORT).show();
                    return;
                }
                ArrayList<Map<String, String>> receivedQuotes =
                        (ArrayList<Map<String, String>>) intent.getSerializableExtra(FirebaseService.QUOTES_EXTRA);

                if (receivedQuotes != null) {
                    quotesList.clear();
                    quotesList.addAll(receivedQuotes);
                    quotesAdapter.notifyDataSetChanged();  // Обновление адаптера
                } else {
                    // Обработка случая, когда список пустой
                    Toast.makeText(context, "Нет данных для отображения", Toast.LENGTH_SHORT).show();
                }
            }
        }
    };

    private BroadcastReceiver deleteReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (FirebaseService.ACTION_DELETE_RESULT.equals(intent.getAction())) {
                boolean isSuccess = intent.getBooleanExtra("isDeleted", false);
                if (isSuccess) {
                    Toast.makeText(context, "Цитата успешно удалена", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, "Ошибка при удалении цитаты", Toast.LENGTH_SHORT).show();
                }
            }
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_quotes, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Получение UID текущего пользователя
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            autherId = user.getUid();
        } else {
            Toast.makeText(requireContext(), "Пользователь не авторизован", Toast.LENGTH_SHORT).show();
            return;
        }

        floatingActionButton = view.findViewById(R.id.floatingButton);
        quotesRecyclerView = view.findViewById(R.id.quotesRecyclerView);
        quotesRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        quotesAdapter = new QuotesAdapter(quotesList, autherId, this);  // Передача UID в адаптер
        quotesRecyclerView.setAdapter(quotesAdapter);

        floatingActionButton.setOnClickListener(v -> {
            replaceFragment(addQuateFragment, R.id.first_fragment_container);
        });

        startQuotesFetchingService();
    }

    private void startQuotesFetchingService() {
        Intent serviceIntent = new Intent(requireContext(), FirebaseService.class);
        requireContext().startService(serviceIntent);
    }

    private void replaceFragment(Fragment fragment, int containerId) {
        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(containerId, fragment)
                .addToBackStack(null)
                .commit();
    }

    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    @Override
    public void onResume() {
        super.onResume();
        IntentFilter quotesFilter = new IntentFilter(FirebaseService.ACTION_FETCH_QUOTES);
        requireContext().registerReceiver(quotesReceiver, quotesFilter, Context.RECEIVER_EXPORTED);

        IntentFilter deleteFilter = new IntentFilter(FirebaseService.ACTION_DELETE_RESULT);
        requireContext().registerReceiver(deleteReceiver, deleteFilter, Context.RECEIVER_EXPORTED);
    }

    @Override
    public void onPause() {
        super.onPause();
        requireContext().unregisterReceiver(quotesReceiver);
        requireContext().unregisterReceiver(deleteReceiver);
    }

    @Override
    public void onDeleteClick(int position) {
        String key = quotesList.get(position).get("key");
        deleteQuoteUsingService(key);
        quotesList.remove(position);
        quotesAdapter.notifyItemRemoved(position);
    }

    private void deleteQuoteUsingService(String key) {
        Intent intent = new Intent(requireContext(), FirebaseService.class);
        intent.setAction(FirebaseService.ACTION_DELETE_QUOTE);
        intent.putExtra(FirebaseService.QUOTE_KEY_EXTRA, key);
        requireContext().startService(intent);
    }
}
