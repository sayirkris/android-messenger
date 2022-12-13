package com.e.chatapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Build;
import android.os.Bundle;
import android.os.Message;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.e.chatapp.databinding.ActivityMessageBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MessageActivity extends AppCompatActivity {
    
    private RecyclerView recyclerView;
    private EditText editMessageInput;
    private TextView txtChattingWith;
    private ProgressBar progressBar;
    private ImageView imgToolbar, imgSend;


    private MessageAdapter messageAdapter;
    private ArrayList<Messages> messages;


    String usernameOfTheRoommate, emailOfRoommate, chatRoomId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        // HIDING STATUS BAR
        getSupportActionBar().hide();

        // CHANGING COLOR OF NAVIGATION BAR
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            getWindow().setNavigationBarColor(getResources().getColor(R.color.primary));
        }

        usernameOfTheRoommate = getIntent().getStringExtra("username_of_roommate");
        emailOfRoommate = getIntent().getStringExtra("email_of_roommate");

        recyclerView = findViewById(R.id.recyclerMessages);
        imgSend = findViewById(R.id.imgSendMessage);
        editMessageInput = findViewById(R.id.editText);
        txtChattingWith = findViewById(R.id.txtChattingWith);
        progressBar = findViewById(R.id.progressMessages);
        imgToolbar = findViewById(R.id.img_toolbar);

        txtChattingWith.setText(usernameOfTheRoommate);

        messages = new ArrayList<>();



        imgSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseDatabase.getInstance().getReference("messages/"+ chatRoomId).push().setValue(new Messages(FirebaseAuth.getInstance().getCurrentUser().getEmail(),emailOfRoommate,editMessageInput.getText().toString()));
                editMessageInput.setText("");
            }
        });

        messageAdapter = new MessageAdapter(messages, getIntent().getStringExtra("my_img"), getIntent().getStringExtra("img_of_roommate"), MessageActivity.this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(messageAdapter);

        Glide.with(MessageActivity.this).load(getIntent().getStringExtra("img_of_roommate")).placeholder(R.drawable.account_img2).error(R.drawable.account_img2).into(imgToolbar);


        setUpChatRoom();
    }

    private void setUpChatRoom(){
        FirebaseDatabase.getInstance().getReference("user/" + FirebaseAuth.getInstance().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String myUsername = snapshot.getValue(User.class).getUsername();
                if(usernameOfTheRoommate.compareTo(myUsername) > 0){
                    chatRoomId = myUsername + usernameOfTheRoommate;
                }else if(usernameOfTheRoommate.compareTo(myUsername) == 0){
                    chatRoomId = myUsername + usernameOfTheRoommate;
                }else{
                    chatRoomId = usernameOfTheRoommate + myUsername;
                }
                attachMessageListener(chatRoomId);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void attachMessageListener(String chatRoomId){
        FirebaseDatabase.getInstance().getReference("messages/" + chatRoomId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                messages.clear();
                for(DataSnapshot dataSnapshot:snapshot.getChildren()){
                    messages.add(dataSnapshot.getValue(Messages.class));
                }
                messageAdapter.notifyDataSetChanged();
                recyclerView.scrollToPosition(messages.size()-1);
                recyclerView.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}