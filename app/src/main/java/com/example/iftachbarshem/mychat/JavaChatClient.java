package com.example.iftachbarshem.mychat;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class JavaChatClient {
    private ExecutorService executor = Executors.newSingleThreadExecutor();
    private ArrayList<Callback> subscribers = new ArrayList<>();

    public JavaChatClient() {
        scheduleReceivedMessages();
    }

    public void subscribe(Callback callback) {
        if (!subscribers.contains(callback)) {
            subscribers.add(callback);
        }
    }

    public void unsubscribe(Callback callback) {
        subscribers.remove(callback);
    }

    public void sendMessage(final String msg) {
        executor.submit(new Runnable() {
            @Override
            public void run() {
                notifyMessageReceived(msg);
            }
        });
    }

    private void notifyMessageReceived(String msg) {
        for (Callback subscriber: subscribers) {
            subscriber.onNewMessage(msg);
        }
    }

    private void scheduleReceivedMessages() {
        final Handler handler = new Handler(Looper.getMainLooper());

        handler.post(new Runnable() {
            @Override
            public void run() {
                final String msg = String.format("Message is: %s", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date()));
                final Chat chat = new Chat(msg, Model.getInstance().getDefaultSender());
                Log.d("ChatClient", "Message received: $msg");
                executor.submit(new Runnable() {
                    @Override
                    public void run() {
                        notifyMessageReceived(chat.toJson());
                    }
                });
                handler.postDelayed(this, 1000);
            }
        });
    }


    public interface Callback {
        void onNewMessage(String msg);
    }
}
