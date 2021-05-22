package com.example.darshanpc.musicshare;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.media.AudioManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.preference.PreferenceManager;
import androidx.annotation.NonNull;
import com.google.android.material.tabs.TabLayout;
import android.media.MediaPlayer;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class MainActivity extends AppCompatActivity implements AudioManager.OnAudioFocusChangeListener,Thread.UncaughtExceptionHandler{
    public static MediaPlayer player;
    int init=1,audio_play_result;
    long onBackPressed;
    PlayMedia playMedia;
    public static String currentSong,currentSongUrl;
    public static int currentSongPos,isButtonPressed;
    ImageView slideuparrow;
    ImageButton imageButtonPlay,imageButtonPause,imageButtonNext,imageButtonPrevious;
    public static TextView textViewSongName,textViewStartTime,textViewEndTime;
    public static SeekBar seekBarSong;
    AudioManager audioManager;
    Handler myHandler;
    TabLayout tabLayout;
    double starttime=0;
    SlidingUpPanelLayout slidingPanelLayout;
    ViewPager viewPager;
    /**
     * getValueEventListener is used to get songs playing currently across all devices
     * and play them locally.
     */
    ValueEventListener getSongValueEventListener;
    DatabaseReference databaseReferenceGetSong;
    @SuppressLint("StaticFieldLeak")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        slidingPanelLayout = findViewById(R.id.sliding_panel);
        tabLayout = findViewById(R.id.tabs);
        slideuparrow = findViewById(R.id.slideuparrow);
        imageButtonPlay = findViewById(R.id.song_play);
        imageButtonPause = findViewById(R.id.song_pause);
        imageButtonNext = findViewById(R.id.song_next);
        imageButtonPrevious = findViewById(R.id.song_previous);
        textViewSongName = findViewById(R.id.song_name);
        textViewStartTime = findViewById(R.id.seekbar_start);
        textViewEndTime = findViewById(R.id.seekbar_end);
        seekBarSong = findViewById(R.id.song_seekbar);


        player = new MediaPlayer();
        player.setAudioStreamType(AudioManager.STREAM_MUSIC);

        playMedia = new PlayMedia();

        audioManager=(AudioManager)getSystemService(Context.AUDIO_SERVICE);
        audio_play_result=audioManager.requestAudioFocus(this,AudioManager.STREAM_MUSIC,AudioManager.AUDIOFOCUS_GAIN);

        myHandler = new Handler();
        seekBarSong.setProgress((int) starttime);
        myHandler.postDelayed(UpdateSongTime, 100);

        tabLayout.setTabTextColors(ColorStateList.valueOf(Color.WHITE));
        tabLayout.addTab(tabLayout.newTab().setText("Search"));
        tabLayout.addTab(tabLayout.newTab().setText("Home"));
        tabLayout.addTab(tabLayout.newTab().setText("Chat"));

        viewPager = findViewById(R.id.viewpager);
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(viewPagerAdapter);

        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));


        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);


        //-------------get song details from database------------
        databaseReferenceGetSong = FirebaseDatabase.getInstance().getReference().child(preferences.getString("roomid", ""));

        getSongValueEventListener=new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                currentSongPos = Integer.parseInt(Objects.requireNonNull(dataSnapshot.child("currentSongPosition").getValue()).toString());
                currentSong = Objects.requireNonNull(dataSnapshot.child("currentSong").getValue()).toString();
                currentSongUrl = Objects.requireNonNull(dataSnapshot.child("currentSongUrl").getValue()).toString();
                isButtonPressed = Integer.parseInt(Objects.requireNonNull(dataSnapshot.child("isButtonPressed").getValue()).toString());
                Log.i("details", Objects.requireNonNull(dataSnapshot.child("currentSong").getValue()).toString());
                Log.i("details", Objects.requireNonNull(dataSnapshot.child("currentSongPosition").getValue()).toString());
                Log.i("details", Objects.requireNonNull(dataSnapshot.child("currentSongUrl").getValue()).toString());

                SharedPreferences.Editor editor = preferences.edit();
                editor.putInt("currentSongPosition", currentSongPos);
                editor.putString("currentSong", currentSong);
                editor.putString("currentSongUrl", currentSongUrl);
                editor.commit();
                if (currentSongPos == -1 && HomeFragment.songList.size() > 0) {
                    currentSongPos = currentSongPos + 1;
                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child(preferences.getString("roomid", ""));
                    Map<String, Object> taskMap = new HashMap<>();
                    taskMap.put("currentSongPosition", String.valueOf(currentSongPos));
                    taskMap.put("currentSong", HomeFragment.songList.get(currentSongPos));
                    taskMap.put("currentSongUrl", HomeFragment.songListUrl.get(currentSongPos));
                    databaseReference.updateChildren(taskMap);
                    playMedia.playVideoFromUrl(HomeFragment.songListUrl.get(currentSongPos), MainActivity.this);

                    textViewSongName.setText(HomeFragment.songList.get(currentSongPos));
                    init = 0;


                } else {
                    if (isButtonPressed == 1) {
                        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child(preferences.getString("roomid", ""));
                        Map<String, Object> taskMap = new HashMap<>();
                        taskMap.put("isButtonPressed", "0");
                        databaseReference.updateChildren(taskMap);
                        playMedia.playVideoFromUrl(currentSongUrl, MainActivity.this);
                        textViewSongName.setText(currentSong);

                    } else if (init == 1 && HomeFragment.songList.size() > 0) {
                        playMedia.playVideoFromUrl(HomeFragment.songListUrl.get(currentSongPos), MainActivity.this);
                        textViewSongName.setText(HomeFragment.songList.get(currentSongPos));
                        init = 0;

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };

        databaseReferenceGetSong.addValueEventListener(getSongValueEventListener);

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
                slideuparrow.setRotation(slideOffset * 180);

            }

            @Override
            public void onPanelStateChanged(View panel, SlidingUpPanelLayout.PanelState previousState, SlidingUpPanelLayout.PanelState newState) {

            }
        });
        //------------------------Play button listener------------------------
        imageButtonPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!player.isPlaying()) {
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
                if (player.isPlaying()) {
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
                if (currentSongPos == HomeFragment.songList.size() - 1) {
                    Toast.makeText(MainActivity.this, "End Of Songs! Add new ones!", Toast.LENGTH_SHORT).show();
                } else {
                    currentSongPos = currentSongPos + 1;
                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child(preferences.getString("roomid", ""));
                    Map<String, Object> taskMap = new HashMap<>();
                    taskMap.put("currentSongPosition", String.valueOf(currentSongPos));
                    taskMap.put("currentSong", HomeFragment.songList.get(currentSongPos));
                    taskMap.put("currentSongUrl", HomeFragment.songListUrl.get(currentSongPos));
                    taskMap.put("isButtonPressed", "1");
                    databaseReference.updateChildren(taskMap);
                }
            }
        });

        //-------------------------previous button listener---------------
        imageButtonPrevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentSongPos <= 0) {
                    Toast.makeText(MainActivity.this, "Starting of songs", Toast.LENGTH_SHORT).show();
                } else {
                    currentSongPos = currentSongPos - 1;
                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child(preferences.getString("roomid", ""));
                    Map<String, Object> taskMap = new HashMap<>();
                    taskMap.put("currentSongPosition", String.valueOf(currentSongPos));
                    taskMap.put("currentSong", HomeFragment.songList.get(currentSongPos));
                    taskMap.put("currentSongUrl", HomeFragment.songListUrl.get(currentSongPos));
                    taskMap.put("isButtonPressed", "1");
                    databaseReference.updateChildren(taskMap);
                }
            }
        });
        //-------------------------Seekbar listener------------------------
        seekBarSong.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (player != null & fromUser) {
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
            try {
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
            catch (IllegalStateException e)
            {
                e.printStackTrace();
            }
        }
    };
    //-----------------check internet connection-------------------------
    public boolean isNetworkAvailable()
    {
        ConnectivityManager connectivityManager=(ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo=connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo!=null && activeNetworkInfo.isConnected();
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
        }
        else {
            Toast.makeText(this, "Press Back Again!", Toast.LENGTH_SHORT).show();
            onBackPressed=System.currentTimeMillis();
        }
    }
    //--------------audio focus change------------------
    @Override
    public void onAudioFocusChange(int focusChange) {
        if (focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT) {
            player.pause();
        } else if (focusChange == AudioManager.AUDIOFOCUS_GAIN) {
            player.start();
        } else if (focusChange == AudioManager.AUDIOFOCUS_LOSS) {
            player.pause();
        }
    }

    @Override
    public void uncaughtException(Thread t, Throwable e) {
        Toast.makeText(this, "Error occured. Possibly due to Internet Disconnection.", Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onPause() {
        databaseReferenceGetSong.removeEventListener(getSongValueEventListener);
        myHandler.removeCallbacks(UpdateSongTime);
        player.stop();
        player.release();
        super.onPause();
    }
}
