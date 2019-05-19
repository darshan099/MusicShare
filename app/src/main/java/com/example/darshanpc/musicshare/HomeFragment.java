package com.example.darshanpc.musicshare;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Iterator;

public class HomeFragment extends Fragment {
    TextView textViewRoomId,textViewMembers;
    public ArrayList<String> songList;
    View view;
    LayoutInflater inflater;
    ListView songListView;
    ArrayAdapter songArrayAdapter;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.home_fragment,container,false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        songList=new ArrayList<>();
        songListView=view.findViewById(R.id.room_music_list);
        songArrayAdapter=new ArrayAdapter(getContext(),android.R.layout.simple_list_item_1,songList);
        songListView.setAdapter(songArrayAdapter);
        textViewRoomId=view.findViewById(R.id.room_id);
        textViewMembers=view.findViewById(R.id.room_members);
        SharedPreferences preferences= PreferenceManager.getDefaultSharedPreferences(getContext());
        String roomid=preferences.getString("roomid",null);
        textViewRoomId.setText("Room Id: "+roomid);
        DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference().child(roomid+"/songs");;
        databaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                String song;
                Iterator i = dataSnapshot.getChildren().iterator();
                while (i.hasNext())
                {
                    song=(String)((DataSnapshot)i.next()).getValue();
                    Log.i("song",song);
                    songList.add(song);
                    songArrayAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                songList.clear();
                String song;
                Iterator i = dataSnapshot.getChildren().iterator();
                while (i.hasNext())
                {
                    song=(String)((DataSnapshot)i.next()).getValue();
                    Log.i("song",song);
                    songList.add(song);
                }
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
}
