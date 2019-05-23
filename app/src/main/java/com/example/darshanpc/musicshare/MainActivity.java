package com.example.darshanpc.musicshare;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.media.MediaPlayer;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.sql.Time;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static java.security.AccessController.getContext;

public class MainActivity extends AppCompatActivity {
    public static MediaPlayer player;
    int init=1;
    long onBackPressed;
    PlayMedia playMedia;
    public static String currentSong,currentSongUrl;
    public static int currentSongPos,isButtonPressed;
    Dialog dialog;
    ImageView slideuparrow;
    ImageButton imageButtonPlay,imageButtonPause,imageButtonNext,imageButtonPrevious;
    public static TextView textViewSongName,textViewStartTime,textViewEndTime;
    public static SeekBar seekBarSong;
    Handler myHandler;
    TabLayout tabLayout;
    double starttime=0;
    SlidingUpPanelLayout slidingPanelLayout;
    ViewPager viewPager;
    @SuppressLint("StaticFieldLeak")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        slidingPanelLayout = findViewById(R.id.sliding_panel);
        tabLayout=findViewById(R.id.tabs);
        slideuparrow=findViewById(R.id.slideuparrow);
        imageButtonPlay=findViewById(R.id.song_play);
        imageButtonPause=findViewById(R.id.song_pause);
        imageButtonNext=findViewById(R.id.song_next);
        imageButtonPrevious=findViewById(R.id.song_previous);
        textViewSongName=findViewById(R.id.song_name);
        textViewStartTime=findViewById(R.id.seekbar_start);
        textViewEndTime=findViewById(R.id.seekbar_end);
        seekBarSong=findViewById(R.id.song_seekbar);

        player=new MediaPlayer();

        playMedia=new PlayMedia();

        myHandler = new Handler();
        seekBarSong.setProgress((int) starttime);
        myHandler.postDelayed(UpdateSongTime, 100);

        tabLayout.setTabTextColors(ColorStateList.valueOf(Color.WHITE));
        tabLayout.addTab(tabLayout.newTab().setText("Search"));
        tabLayout.addTab(tabLayout.newTab().setText("Home"));
        tabLayout.addTab(tabLayout.newTab().setText("Chat"));

        viewPager=findViewById(R.id.viewpager);
        ViewPagerAdapter viewPagerAdapter=new ViewPagerAdapter(getSupportFragmentManager(),tabLayout.getTabCount());
        viewPager.setAdapter(viewPagerAdapter);

        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));


        SharedPreferences preferences= PreferenceManager.getDefaultSharedPreferences(this);




        //-------------get song details from database------------
        DatabaseReference databaseReferenceGetSong=FirebaseDatabase.getInstance().getReference().child(preferences.getString("roomid",""));
        databaseReferenceGetSong.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                currentSongPos=Integer.parseInt(Objects.requireNonNull(dataSnapshot.child("currentSongPosition").getValue()).toString());
                currentSong=Objects.requireNonNull(dataSnapshot.child("currentSong").getValue()).toString();
                currentSongUrl=Objects.requireNonNull(dataSnapshot.child("currentSongUrl").getValue()).toString();
                isButtonPressed=Integer.parseInt(Objects.requireNonNull(dataSnapshot.child("isButtonPressed").getValue()).toString());
                Log.i("details", Objects.requireNonNull(dataSnapshot.child("currentSong").getValue()).toString());
                Log.i("details", Objects.requireNonNull(dataSnapshot.child("currentSongPosition").getValue()).toString());
                Log.i("details", Objects.requireNonNull(dataSnapshot.child("currentSongUrl").getValue()).toString());
                SharedPreferences.Editor editor=preferences.edit();
                editor.putInt("currentSongPosition",currentSongPos);
                editor.putString("currentSong",currentSong);
                editor.putString("currentSongUrl",currentSongUrl);
                editor.commit();
                if(currentSongPos==-1 && HomeFragment.songList.size()>0)
                {
                    Log.i("i am here", "yes");
                    currentSongPos=currentSongPos+1;
                    DatabaseReference databaseReference=FirebaseDatabase.getInstance().getReference().child(preferences.getString("roomid",""));
                    Map<String,Object> taskMap=new HashMap<>();
                    taskMap.put("currentSongPosition",String.valueOf(currentSongPos));
                    taskMap.put("currentSong",HomeFragment.songList.get(currentSongPos));
                    taskMap.put("currentSongUrl",HomeFragment.songListUrl.get(currentSongPos));
                    databaseReference.updateChildren(taskMap);
                    playMedia.playVideoFromUrl(HomeFragment.songListUrl.get(currentSongPos),MainActivity.this);
                    textViewSongName.setText(HomeFragment.songList.get(currentSongPos));
                }
                else
                {
                    if(isButtonPressed==1) {
                        DatabaseReference databaseReference=FirebaseDatabase.getInstance().getReference().child(preferences.getString("roomid",""));
                        Map<String,Object> taskMap=new HashMap<>();
                        taskMap.put("isButtonPressed","0");
                        databaseReference.updateChildren(taskMap);
                        playMedia.playVideoFromUrl(currentSongUrl, MainActivity.this);
                        textViewSongName.setText(currentSong);
                    }
                    else if(init==1 && HomeFragment.songList.size()>0)
                    {
                        playMedia.playVideoFromUrl(HomeFragment.songListUrl.get(currentSongPos),MainActivity.this);
                        textViewSongName.setText(HomeFragment.songList.get(currentSongPos));
                        init=0;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



        //--------------------Tablayout listener-------------------------
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        //-------------------------Sliding panel listener--------------------
        slidingPanelLayout.addPanelSlideListener(new SlidingUpPanelLayout.PanelSlideListener() {

            @Override
            public void onPanelSlide(View panel, float slideOffset) {
                slideuparrow.setAlpha(1 - slideOffset);

            }

            @Override
            public void onPanelStateChanged(View panel, SlidingUpPanelLayout.PanelState previousState, SlidingUpPanelLayout.PanelState newState) {

            }
        });
        //------------------------Play button listener------------------------
        imageButtonPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!player.isPlaying())
                {
                    player.start();
                }
                imageButtonPlay.setVisibility(View.GONE);
                imageButtonPause.setVisibility(View.VISIBLE);
            }
        });
        //-------------------------Pause button listener--------------------
        imageButtonPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(player.isPlaying())
                {
                    player.pause();
                }
                imageButtonPlay.setVisibility(View.VISIBLE);
                imageButtonPause.setVisibility(View.GONE);
            }
        });
        //-------------------------next button listener---------------------
        imageButtonNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(currentSongPos==HomeFragment.songList.size()-1)
                {
                    Toast.makeText(MainActivity.this, "End Of Songs! Add new ones!", Toast.LENGTH_SHORT).show();
                }
                else {
                    currentSongPos=currentSongPos+1;
                    DatabaseReference databaseReference=FirebaseDatabase.getInstance().getReference().child(preferences.getString("roomid",""));
                    Map<String,Object> taskMap=new HashMap<>();
                    taskMap.put("currentSongPosition",String.valueOf(currentSongPos));
                    taskMap.put("currentSong",HomeFragment.songList.get(currentSongPos));
                    taskMap.put("currentSongUrl",HomeFragment.songListUrl.get(currentSongPos));
                    taskMap.put("isButtonPressed","1");
                    databaseReference.updateChildren(taskMap);
                }
            }
        });

        //-------------------------previous button listener---------------
        imageButtonPrevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(currentSongPos==0)
                {
                    Toast.makeText(MainActivity.this, "Starting of songs", Toast.LENGTH_SHORT).show();
                }
                else {
                    currentSongPos=currentSongPos-1;
                    DatabaseReference databaseReference=FirebaseDatabase.getInstance().getReference().child(preferences.getString("roomid",""));
                    Map<String,Object> taskMap=new HashMap<>();
                    taskMap.put("currentSongPosition",String.valueOf(currentSongPos));
                    taskMap.put("currentSong",HomeFragment.songList.get(currentSongPos));
                    taskMap.put("currentSongUrl",HomeFragment.songListUrl.get(currentSongPos));
                    taskMap.put("isButtonPressed","1");
                    databaseReference.updateChildren(taskMap);
                }
            }
        });
        //-------------------------Seekbar listener------------------------
        seekBarSong.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(player!=null & fromUser)
                {
                    player.seekTo(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }

        });


    }

    //------------------------------updating song time in seek bar-----------------------------
    private Runnable UpdateSongTime = new Runnable() {
        @SuppressLint("SetTextI18n")
        @Override
        public void run() {
            starttime = player.getCurrentPosition();
            seekBarSong.setProgress((int) starttime);
            String seconds = String.valueOf(((int) starttime % 60000) / 1000);
            String minutes = String.valueOf(((int) starttime / 60000));

            if (seconds.length() == 1) {
                textViewStartTime.setText("0" + minutes + ":0" + seconds);
            } else {
                textViewStartTime.setText("0" + minutes + ":" + seconds);
            }
            myHandler.postDelayed(this, 100);
        }
    };
    //-----------------check internet connection-------------------------
    public boolean isNetworkAvailable()
    {
        ConnectivityManager connectivityManager=(ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo=connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo!=null && activeNetworkInfo.isConnected();
    }
    public void getCurrentSongDetails(DataSnapshot dataSnapshot)
    {
        if(dataSnapshot!=null) {
            for (DataSnapshot ds : dataSnapshot.getChildren()) {
                String songPos, songName, songYoutubeUrl;
                songPos = Objects.requireNonNull(ds.child("currentSongPosition").getValue()).toString();
                songName = Objects.requireNonNull(ds.child("currentSong").getValue()).toString();
                songYoutubeUrl = Objects.requireNonNull(ds.child("currentSongUrl").getValue()).toString();
                Log.i("songdetails", songPos);
                Log.i("songdetails", songName);
                Log.i("songdetails", songYoutubeUrl);
            }
        }
    }
    @Override
    public void onBackPressed()
    {
        if(slidingPanelLayout.getPanelState()==SlidingUpPanelLayout.PanelState.EXPANDED)
        {
            slidingPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
        }
        else if(onBackPressed+2000 > System.currentTimeMillis())
        {
            super.onBackPressed();
            return;
        }
        else {
            Toast.makeText(this, "Press Back Again!", Toast.LENGTH_SHORT).show();
            onBackPressed=System.currentTimeMillis();
        }
    }

}
