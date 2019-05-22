package com.example.darshanpc.musicshare;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
public class FirebaseHelper {

    public void addRoomId(String roomid, Context context)
    {
        DatabaseReference databaseReference;
        SharedPreferences preferences= PreferenceManager.getDefaultSharedPreferences(context);
        databaseReference= FirebaseDatabase.getInstance().getReference().child(roomid);
        Map<String,Object> map=new HashMap<String,Object>();
        map.put("roomCreator",preferences.getString("username",""));
        databaseReference.updateChildren(map);

        DatabaseReference databaseReferenceuser;
        databaseReferenceuser= FirebaseDatabase.getInstance().getReference().child(roomid+"/users");
        Map<String,Object> map1=new HashMap<String,Object>();
        String user_addition_key=databaseReferenceuser.push().getKey();
        databaseReferenceuser.updateChildren(map1);

        DatabaseReference databaseReferenceuservalue=databaseReferenceuser.child(user_addition_key);
        Map<String,Object> mapUser=new HashMap<String,Object>();
        map1.put("username",preferences.getString("username",""));
        databaseReferenceuservalue.updateChildren(mapUser);

    }

    public void addSong(String songName,String youtubeUrl,Context context)
    {
        DatabaseReference databaseReference;

        SharedPreferences preferences= PreferenceManager.getDefaultSharedPreferences(context);
        String roomid=preferences.getString("roomid",null);
        databaseReference= FirebaseDatabase.getInstance().getReference().child(roomid+"/songs");
        Map<String,Object> map=new HashMap<String,Object>();
        String user_message_key=databaseReference.push().getKey();
        databaseReference.updateChildren(map);

        DatabaseReference databaseReference1=databaseReference.child(user_message_key);
        Map<String,Object> map1=new HashMap<String,Object>();
        map1.put("added_song",songName);
        map1.put("songUrl",youtubeUrl);
        databaseReference1.updateChildren(map1);
    }
    public void enterMemberIntoRoomId(String roomid,Context context)
    {
        //TODO: update current playing song and start playing

        DatabaseReference databaseReferenceuser;
        SharedPreferences preferences= PreferenceManager.getDefaultSharedPreferences(context);
        databaseReferenceuser= FirebaseDatabase.getInstance().getReference().child(roomid+"/users");

        databaseReferenceuser.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(!dataSnapshot.hasChild("username"))
                {
                    Map<String,Object> map1=new HashMap<String,Object>();
                    String user_addition_key=databaseReferenceuser.push().getKey();
                    databaseReferenceuser.updateChildren(map1);
                    DatabaseReference databaseReferenceuservalue=databaseReferenceuser.child(user_addition_key);
                    Map<String,Object> mapUser=new HashMap<String,Object>();
                    mapUser.put("username",preferences.getString("username",""));
                    databaseReferenceuservalue.updateChildren(mapUser);
                    Log.i("wtf","im here");
                }
                else
                {
                    Toast.makeText(context, "You are already added!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

}
