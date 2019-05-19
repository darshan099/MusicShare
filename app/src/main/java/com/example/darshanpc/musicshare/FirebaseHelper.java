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

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

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
        Map<String,Object> map1=new HashMap<String,Object>();
        databaseReferenceuser= FirebaseDatabase.getInstance().getReference().child(roomid+"/users");
        map1.put("user",preferences.getString("username",""));
        databaseReferenceuser.updateChildren(map1);
    }

    public void addSong(String songName,Context context)
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
        databaseReference1.updateChildren(map1);


    }

}
