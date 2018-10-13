package com.example.iftachbarshem.mychat;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity implements JavaChatClient.Callback {
    final static long TIMESTAMP_TO_COLLECT = 60000;

    private EditText editText;
    private ChatAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Model.getInstance().subscribe(this);
        setContentView(R.layout.layout_chat);

        adapter = new ChatAdapter();
        recyclerView = findViewById(R.id.recycler_view);

        // use a linear layout manager
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

        editText = findViewById(R.id.edit_text);
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String text = s.toString();
                if (!text.isEmpty() && text.endsWith("\n")) {
                    Chat chat = new Chat(text.substring(0, text.length()-1), Model.getInstance().getMyName());
                    Model.getInstance().getJavaChatClient().sendMessage(chat.toJson());
                    editText.setText("");
                }

            }

            @Override
            public void afterTextChanged(Editable s) {
                String text = s.toString();
                if (text.isEmpty()) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                    // Find the currently focused view, so we can grab the correct window token from it.
                    View view = getCurrentFocus();
                    // If no view currently has focus, create a new one, just so we can grab a window token from it
                    if (view != null) {
                        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                    }
                }
            }
        });

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Model.getInstance().unsubscribe(this);
    }

    @Override
    public void onNewMessage(String msg) {
        Chat chat = new Chat(msg);
        adapter.addChat(chat);
        System.out.println("new message arrive " + chat.getMsg());
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                adapter.notifyDataSetChanged();
            }
        });
    }

    /**
     * chat list and active now adapters
     */
    class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        private List<Holder> holders;

        public class TimestampViewHolder extends RecyclerView.ViewHolder {
            TextView textTimestamp;

            public TimestampViewHolder(View itemView) {
                super(itemView);
                textTimestamp = itemView.findViewById(R.id.timestamp);
            }
        }
        public class ChatViewHolder extends RecyclerView.ViewHolder {
            TextView textViewItemName;

            ChatViewHolder(View itemView) {
                super(itemView);
                textViewItemName = itemView.findViewById(R.id.text_view_item_name);
            }
        }
        class Holder {
            Chat chat;
            long timestamp;
        }
        public ChatAdapter() {
            holders = new ArrayList<Holder>();
        }

        public void addChat(Chat chat) {
            if (holders.isEmpty() || holders.get(holders.size()-1).timestamp /TIMESTAMP_TO_COLLECT != chat.getTimestamp() /TIMESTAMP_TO_COLLECT) {
                Holder holder = new Holder();
                holder.timestamp = chat.getTimestamp();
                holders.add(holder);
            }
            Holder holder = new Holder();
            holder.timestamp = chat.getTimestamp();
            holder.chat = chat;
            holders.add(holder);
        }

        @Override
        public int getItemCount() {
            return holders.size();
        }
        @Override
        public int getItemViewType(int position) {
            return position;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, final int position) {
            Holder holder = holders.get(position);
            View view;
            RecyclerView.ViewHolder viewHolder;
            if (holder.chat != null) {
                view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.layout_list_view_row_items, viewGroup, false);
                viewHolder = new ChatViewHolder(view);
            }
            else {
                view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.layout_timestaml_item, viewGroup, false);
                viewHolder = new TimestampViewHolder(view);
            }
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
            if (viewHolder instanceof ChatViewHolder) {
                //sets the text for item name and item description from the current item object
                Chat chat = holders.get(position).chat;
                ((ChatViewHolder)viewHolder).textViewItemName.setText(chat.getMsg());
                if (chat.getSender().equals(Model.getInstance().getMyName())) {
                    ((ChatViewHolder)viewHolder).textViewItemName.setGravity(Gravity.RIGHT);
                }
            }
            else {
                TextView textTimestamp = ((TimestampViewHolder)viewHolder).textTimestamp;
                String dateString = new SimpleDateFormat("MM/dd/yyyy HH:mm").format(new Date(holders.get(position).timestamp));
                textTimestamp.setText(dateString);

            }
        }

    }

}
