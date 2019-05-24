package com.example.darshanpc.musicshare;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class GenerateRoom extends AppCompatActivity {

    RadioButton radioButtonJoin,radioButtonCreate;
    TextView textViewCreateId;
    TextView textViewRoomCreateHint,textViewRoomJoinHint;
    EditText editTextEnterId;
    Button submitRoomId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_generate_room);

        textViewRoomJoinHint=findViewById(R.id.room_join_hint);
        textViewRoomCreateHint=findViewById(R.id.room_create_hint);
        radioButtonJoin=findViewById(R.id.radiobuttonJoinRoom);
        radioButtonCreate=findViewById(R.id.radiobuttonCreateRoom);
        textViewCreateId=findViewById(R.id.generateRoomId);
        editTextEnterId=findViewById(R.id.enterRoomId);
        submitRoomId=findViewById(R.id.submitRoomId);

        SharedPreferences preferences= PreferenceManager.getDefaultSharedPreferences(this);
        if(!preferences.getBoolean("firsttime",false)) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Whats your nickname?");
            EditText addUsername = new EditText(this);
            builder.setView(addUsername);
            builder.setCancelable(false);
            builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    String username = addUsername.getText().toString();
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putBoolean("firsttime", true);
                    editor.putString("username", username);
                    editor.apply();

                }
            });
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        }
        radioButtonJoin.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked)
                {
                    textViewRoomJoinHint.setVisibility(View.VISIBLE);
                    textViewRoomCreateHint.setVisibility(View.GONE);
                    textViewCreateId.setVisibility(View.GONE);
                    editTextEnterId.setVisibility(View.VISIBLE);
                }
            }
        });

        radioButtonCreate.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked)
                {

                    long time=System.currentTimeMillis();
                    String time_string=String.valueOf(time);
                    editTextEnterId.setVisibility(View.GONE);
                    textViewCreateId.setVisibility(View.VISIBLE);
                    textViewCreateId.setText(time_string);
                    textViewRoomJoinHint.setVisibility(View.GONE);
                    textViewRoomCreateHint.setVisibility(View.VISIBLE);
                }
            }
        });

        submitRoomId.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseHelper firebaseHelper=new FirebaseHelper();
                if(editTextEnterId.getVisibility()==View.VISIBLE) {
                    if(isNetworkAvailable()) {
                        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
                        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (dataSnapshot.hasChild(editTextEnterId.getText().toString())) {
                                    Log.i("db", "present");
                                    SharedPreferences.Editor editor=preferences.edit();
                                    editor.putString("roomid",editTextEnterId.getText().toString());
                                    editor.commit();
                                    firebaseHelper.enterMemberIntoRoomId(editTextEnterId.getText().toString(), GenerateRoom.this);
                                    Intent intent=new Intent(GenerateRoom.this,MainActivity.class);
                                    startActivity(intent);

                                } else {
                                    Log.i("db", "not present");
                                    Toast.makeText(GenerateRoom.this, "Invalid roomid. Enter Again!", Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                        Log.i("RoomId", editTextEnterId.getText().toString());
                    }
                    else
                    {
                        Toast.makeText(GenerateRoom.this, "Connect To Internet", Toast.LENGTH_SHORT).show();
                    }
                }
                else if(textViewCreateId.getVisibility()==View.VISIBLE) {
                    if(isNetworkAvailable()) {
                        firebaseHelper.addRoomId(textViewCreateId.getText().toString(), GenerateRoom.this);
                        SharedPreferences.Editor editor=preferences.edit();
                        editor.putString("roomid", textViewCreateId.getText().toString());
                        editor.putBoolean("isRoomCreator", true);
                        editor.commit();
                        Log.i("RoomId", textViewCreateId.getText().toString());
                        Intent intent=new Intent(GenerateRoom.this,MainActivity.class);
                                    startActivity(intent);
                    }
                    else
                    {
                        Toast.makeText(GenerateRoom.this, "Connect To Internet", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }
    //-----------------check internet connection-------------------------
    public boolean isNetworkAvailable()
    {
        ConnectivityManager connectivityManager=(ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo=connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo!=null && activeNetworkInfo.isConnected();
    }
}
