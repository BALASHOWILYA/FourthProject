package com.bal.fourthproject.data;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class FirebaseAddQuoteService extends Service {

    public static final String ACTION_ADD_QUOTE = "com.bal.fourthproject.ADD_QUOTE";
    public static final String EXTRA_QUOTE = "extra_quote";
    public static final String EXTRA_AUTHOR = "extra_author";

    private ExecutorService executorService;

    @Override
    public void onCreate() {
        super.onCreate();
        executorService = Executors.newSingleThreadExecutor();
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (ACTION_ADD_QUOTE.equals(intent.getAction())) {
            String quote = intent.getStringExtra(EXTRA_QUOTE);
            String author = intent.getStringExtra(EXTRA_AUTHOR);

            if (quote != null && author != null) {
                executorService.execute(()->{
                    addQuoteToDB(quote, author);
                });
            } else {
                Toast.makeText(this, "Данные не переданы", Toast.LENGTH_SHORT).show();
            }
        }
        return START_NOT_STICKY;
    }

    private void addQuoteToDB(String quote, String author) {
        HashMap<String, Object> quoteHashMap = new HashMap<>();
        quoteHashMap.put("quote", quote);
        quoteHashMap.put("author", author);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference quotesRef = database.getReference("quotes");
        String key = quotesRef.push().getKey();
        quoteHashMap.put("key", key);

        quotesRef.child(key).setValue(quoteHashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(FirebaseAddQuoteService.this, "Quote added", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(FirebaseAddQuoteService.this, "Failed to add quote", Toast.LENGTH_LONG).show();
                }
                stopSelf(); // Останавливаем сервис после выполнения задачи
            }
        });
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
