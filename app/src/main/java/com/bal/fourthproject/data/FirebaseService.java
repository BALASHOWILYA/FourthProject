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
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class FirebaseService extends Service {

    public static final String ACTION_FETCH_QUOTES = "com.bal.fourthproject.FETCH_QUOTES";
    public static final String QUOTES_EXTRA = "quotes_extra";
    public static final String ACTION_DELETE_QUOTE = "com.bal.fourthproject.DELETE_QUOTE";
    public static final String QUOTE_KEY_EXTRA = "quote_key_extra";
    public static final String ACTION_DELETE_RESULT = "com.bal.fourthproject.DELETE_RESULT";
    public static final String ACTION_UPDATE_QUOTE = "com.bal.fourthproject.ACTION_UPDATE_QUOTE";
    public static final String ACTION_UPDATE_RESULT = "com.bal.fourthproject.ACTION_UPDATE_RESULT";
    public static final String QUOTE_UPDATE_KEY_EXTRA = "quote_update_key";
    public static final String QUOTE_UPDATE_DATA_EXTRA = "quote_update_data";
    private DatabaseReference quotesDatabase;



    private ExecutorService executorService;
    private Handler mainHander;

    @Override
    public void onCreate() {
        super.onCreate();
        quotesDatabase = FirebaseDatabase.getInstance().getReference("quotes");  // Путь к коллекции цитат
        executorService = new ThreadPoolExecutor(
                3, // core pool size
                5, // maximum pool size
                60L, // keep-alive time
                TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(10), // Bounded queue with a capacity of 10 tasks
                new ThreadPoolExecutor.DiscardOldestPolicy() // Discards oldest task if queue is full
        );

        mainHander = new Handler(Looper.getMainLooper());
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null && ACTION_DELETE_QUOTE.equals(intent.getAction())) {
            String quoteKey = intent.getStringExtra(QUOTE_KEY_EXTRA);
            if (quoteKey != null) {
                try {
                    executorService.execute(()->{
                        deleteQuoteFromFirebase(quoteKey);
                    });

                } catch (OutOfMemoryError e){
                    executorService.shutdown();

                }

            }
        }
        else if(intent != null && ACTION_UPDATE_QUOTE.equals(intent.getAction()))
        {
            try {
                executorService.execute(()->{
                    String key = intent.getStringExtra(QUOTE_UPDATE_KEY_EXTRA);
                    Map<String, Object> updatedData = (Map<String, Object>) intent.getSerializableExtra(QUOTE_UPDATE_DATA_EXTRA);

                    if (key != null && updatedData != null) {
                        updateQuote(key, updatedData);
                    }
                });
            } catch (OutOfMemoryError e){
                executorService.shutdown();
            }



        }
        else {
            try{

                executorService.execute(this::loadQuotesFromFirebase);
            }
            catch (OutOfMemoryError e){
                executorService.shutdown();

            }
        }
        return START_STICKY;
    }

    private void updateQuote(String key, Map<String, Object> updatedData) {
        quotesDatabase.child(key).updateChildren(updatedData)
                .addOnSuccessListener(aVoid -> {
                    sendUpdateResultBroadcast(true);
                })
                .addOnFailureListener(e -> {
                    sendUpdateResultBroadcast(false);
                });
    }

    private void sendUpdateResultBroadcast(boolean isUpdated) {
        Intent resultIntent = new Intent(ACTION_UPDATE_RESULT);
        resultIntent.putExtra("isUpdated", isUpdated);
        sendBroadcast(resultIntent);
    }

    private void loadQuotesFromFirebase() {

        quotesDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
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
        DatabaseReference quotesRef = quotesDatabase.child(quoteKey);
        quotesRef.removeValue().addOnCompleteListener(task -> {
            mainHander.post(() -> {
                Intent intent = new Intent(ACTION_DELETE_RESULT);
                intent.putExtra("isDeleted", task.isSuccessful());
                sendBroadcast(intent);
            });
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        executorService.shutdown();
    }


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
