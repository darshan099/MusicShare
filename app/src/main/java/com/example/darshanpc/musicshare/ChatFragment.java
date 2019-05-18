package com.example.darshanpc.musicshare;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class ChatFragment extends Fragment {
    Button sendMessageButton;
    EditText sendMessageText;
    ListView conversation;
    ArrayList<String> listConversation=new ArrayList<String>();
    ArrayAdapter arrayAdapter;
    String username;
    String user_message_key;
    DatabaseReference databaseReference;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.chat_fragment,container,false);
        sendMessageButton=view.findViewById(R.id.chat_send);
        sendMessageText=view.findViewById(R.id.chat_text);
        conversation=view.findViewById(R.id.chat_list);

        SharedPreferences preferences= PreferenceManager.getDefaultSharedPreferences(getContext());
        username=preferences.getString("username","null");
        String roomid=preferences.getString("roomid",null);

        arrayAdapter=new ArrayAdapter(getContext(),android.R.layout.simple_list_item_1,listConversation);
        arrayAdapter.clear();
        conversation.setAdapter(arrayAdapter);

        databaseReference= FirebaseDatabase.getInstance().getReference().child(roomid+"/chat");

        sendMessageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Map<String,Object> map=new HashMap<String,Object>();
                user_message_key=databaseReference.push().getKey();
                databaseReference.updateChildren(map);

                DatabaseReference databaseReference1=databaseReference.child(user_message_key);
                Map<String,Object> map1=new HashMap<String,Object>();
                map1.put("user",username);
                map1.put("message",sendMessageText.getText().toString());
                databaseReference1.updateChildren(map1);
                sendMessageText.setText("");
            }
        });

        databaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                updateConversation(dataSnapshot);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                updateConversation(dataSnapshot);
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
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }
    public void updateConversation(DataSnapshot dataSnapshot)
    {
        String message,user,conversation;
        Iterator i = dataSnapshot.getChildren().iterator();
        while (i.hasNext())
        {
            message=(String)((DataSnapshot)i.next()).getValue();
            user=(String)((DataSnapshot)i.next()).getValue();
            conversation=user+": "+message;
            listConversation.add(conversation);
            //arrayAdapter.insert(conversation,arrayAdapter.getCount());
            arrayAdapter.notifyDataSetChanged();
        }
    }
}
