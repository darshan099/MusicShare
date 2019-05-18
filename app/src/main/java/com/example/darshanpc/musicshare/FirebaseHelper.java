package com.example.darshanpc.musicshare;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

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
    }

    public void currentSong(String songName,Context context)
    {
        DatabaseReference databaseReference;
        SharedPreferences preferences= PreferenceManager.getDefaultSharedPreferences(context);
        String roomid=preferences.getString("roomid",null);
        databaseReference= FirebaseDatabase.getInstance().getReference().child(roomid);
        Map<String,Object> map=new HashMap<String,Object>();
        map.put("currentsong",songName);
        databaseReference.updateChildren(map);
    }

    public String getusers(Context context)
    {
        return null;
    }
}
