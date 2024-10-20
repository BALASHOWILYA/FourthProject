package com.bal.fourthproject.data;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;

import androidx.annotation.NonNull;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class FirebaseService extends Service {

    public static final String ACTION_FETCH_QUOTES = "com.bal.fourthproject.FETCH_QUOTES";
    public static final String QUOTES_EXTRA = "quotes_extra";
    public static final String ACTION_DELETE_QUOTE = "com.bal.fourthproject.DELETE_QUOTE";
    public static final String QUOTE_KEY_EXTRA = "quote_key_extra";
    public static final String ACTION_DELETE_RESULT = "com.bal.fourthproject.DELETE_RESULT";


    private ExecutorService executorService;
    private Handler mainHander;

    @Override
    public void onCreate() {
        super.onCreate();

        executorService = Executors.newFixedThreadPool(2);
        mainHander = new Handler(Looper.getMainLooper());
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null && ACTION_DELETE_QUOTE.equals(intent.getAction())) {
            String quoteKey = intent.getStringExtra(QUOTE_KEY_EXTRA);
            if (quoteKey != null) {
                executorService.execute(()->{
                    deleteQuoteFromFirebase(quoteKey);
                });

            }
        } else {
            executorService.execute(this::loadQuotesFromFirebase);
        }
        return START_STICKY;
    }

    private void loadQuotesFromFirebase() {
        DatabaseReference quotesRef = FirebaseDatabase.getInstance().getReference("quotes");

        quotesRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Map<String, String>> quotesList = new ArrayList<>();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Map<String, String> quoteData = (Map<String, String>) dataSnapshot.getValue();
                    quotesList.add(quoteData);
                }
                mainHander.post(() -> sendQuotesBroadcast(quotesList));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Логирование ошибки
                android.util.Log.e("FirebaseService", "Ошибка при загрузке данных: " + error.getMessage());

                // Уведомление пользователя об ошибке (по желанию можно создать и Notification)
                mainHander.post(() -> {
                    Intent intent = new Intent(ACTION_FETCH_QUOTES);
                    intent.putExtra("error", true);  // Можем отправить информацию об ошибке
                    sendBroadcast(intent);
                });
            }
        });
    }

    private void sendQuotesBroadcast(List<Map<String, String>> quotesList) {
        Intent intent = new Intent(ACTION_FETCH_QUOTES);
        intent.putExtra(QUOTES_EXTRA, (ArrayList<Map<String, String>>) quotesList);
        sendBroadcast(intent);
    }

    private void deleteQuoteFromFirebase(String quoteKey) {
        DatabaseReference quotesRef = FirebaseDatabase.getInstance().getReference("quotes").child(quoteKey);
        quotesRef.removeValue().addOnCompleteListener(task -> {
            mainHander.post(() -> {
                Intent intent = new Intent(ACTION_DELETE_RESULT);
                intent.putExtra("isDeleted", task.isSuccessful());
                sendBroadcast(intent);
            });
        });
    }


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
