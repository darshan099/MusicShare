package com.example.darshanpc.musicshare;

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
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Objects;

public class HomeFragment extends Fragment implements Thread.UncaughtExceptionHandler{
    TextView textViewRoomId,textViewMembers;
    public static ArrayList<String> songList=new ArrayList<String>();
    public static ArrayList<String> songListUrl=new ArrayList<String>();
    Button buttonLeave;
    DatabaseReference databaseReferenceSong;
    DatabaseReference databaseReferenceMembers;
    ListView songListView;
    ArrayAdapter songArrayAdapter;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.home_fragment,container,false);
        songListView=view.findViewById(R.id.room_music_list);
        songArrayAdapter=new ArrayAdapter(getContext(),android.R.layout.simple_list_item_1,songList);
        songArrayAdapter.clear();
        songListView.setAdapter(songArrayAdapter);
        textViewRoomId=view.findViewById(R.id.room_id);
        textViewMembers=view.findViewById(R.id.room_members);
        buttonLeave=view.findViewById(R.id.room_leave);
        SharedPreferences preferences= PreferenceManager.getDefaultSharedPreferences(getContext());
        String roomid=preferences.getString("roomid",null);
        textViewRoomId.setText("Room Id: "+roomid);
        //--------------------Get song array---------------------------------------
        databaseReferenceSong= FirebaseDatabase.getInstance().getReference().child(roomid+"/songs");
        databaseReferenceSong.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                updateSongList(dataSnapshot);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                updateSongList(dataSnapshot);
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
        //--------------------get Members array-------------------------------------
        databaseReferenceMembers=FirebaseDatabase.getInstance().getReference().child(roomid+"/users");
        databaseReferenceMembers.orderByChild("username");
        databaseReferenceMembers.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                showMembers(dataSnapshot);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    public void updateSongList(DataSnapshot dataSnapshot)
    {
        String song,songurl;
        Iterator i = dataSnapshot.getChildren().iterator();
        while (i.hasNext())
        {
            song=(String)((DataSnapshot)i.next()).getValue();
            songurl=(String)((DataSnapshot)i.next()).getValue();
            Log.i("song",song);
            Log.i("url",songurl);
            songList.add(song);
            songListUrl.add(songurl);
            songArrayAdapter.notifyDataSetChanged();
        }
        Log.i("size",String.valueOf(songList.size()));
    }

    public void showMembers(DataSnapshot dataSnapshot)
    {
        StringBuilder stringMembers= new StringBuilder();
        for(DataSnapshot ds: dataSnapshot.getChildren())
        {
            stringMembers.append(Objects.requireNonNull(ds.child("username").getValue()).toString()).append(",");
        }
        textViewMembers.setText(stringMembers);
    }

    @Override
    public void uncaughtException(Thread t, Throwable e) {
        Toast.makeText(getContext(), "Error occured. Probably due to Internet Disconnection", Toast.LENGTH_LONG).show();
    }
}
