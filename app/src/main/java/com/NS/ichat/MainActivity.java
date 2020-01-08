package com.NS.ichat;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.text.format.DateFormat;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.database.FirebaseListAdapter;
import com.github.library.bubbleview.BubbleTextView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import hani.momanii.supernova_emoji_library.Actions.EmojIconActions;
import hani.momanii.supernova_emoji_library.Helper.EmojiconEditText;


public class MainActivity extends AppCompatActivity {

    private static int SIGN_IN_CODE = 1;
    private RelativeLayout activityMain;
    private FirebaseListAdapter<Message> adapter;

    private EmojiconEditText emojiconEditText;
    private ImageView buttonEmoji, buttonSend;
    private EmojIconActions emojIconActions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        activityMain = findViewById(R.id.activityMain);

        //User is not authorized
        if(FirebaseAuth.getInstance().getCurrentUser() == null) {
            startActivityForResult(AuthUI.getInstance().createSignInIntentBuilder().build(), SIGN_IN_CODE);
        } else {
            Snackbar.make(activityMain, "Вы авторизованы", Snackbar.LENGTH_SHORT).show();
        }

        displayMessaged();

        emojiconEditText = findViewById(R.id.textField);
        buttonEmoji = findViewById(R.id.buttonEmoji);
        buttonSend = findViewById(R.id.buttonSend);
        emojIconActions = new EmojIconActions(getApplicationContext(), activityMain, emojiconEditText, buttonEmoji);
        emojIconActions.ShowEmojIcon();

        buttonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseDatabase.getInstance().getReference()
                        .push().setValue(new Message(
                                FirebaseAuth.getInstance().getCurrentUser().getEmail(), emojiconEditText.getText().toString()));
                emojiconEditText.setText("");
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == SIGN_IN_CODE) {

            if(resultCode == RESULT_OK) {
                Snackbar.make(activityMain, "Вы авторизованы", Snackbar.LENGTH_SHORT).show();
                displayMessaged();
            }

        } else {

            Snackbar.make(activityMain, "Вы не авторизованы", Snackbar.LENGTH_SHORT).show();
            finish();

        }
    }

    private void displayMessaged() {

        final ListView messages = findViewById(R.id.listOfMessages);
        adapter = new FirebaseListAdapter<Message>(MainActivity.this, Message.class, R.layout.list_item, FirebaseDatabase.getInstance().getReference()) {
            @Override
            protected void populateView(View v, Message model, int position) {

                TextView messageUser = v.findViewById(R.id.messageUser);
                TextView messageTime = v.findViewById(R.id.messageTime);
                BubbleTextView messageText = v.findViewById(R.id.messageText);

                messageUser.setText(model.getUserName());
                messageTime.setText(DateFormat.format("dd-MM-yyyy HH:mm:ss", model.getMessageTime()));
                messageText.setText(model.getTextMessage());
            }
        };

        messages.setAdapter(adapter);

    }
}
