package com.example.darshanpc.musicshare;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Handler;
import android.preference.PreferenceManager;
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

import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.sql.Time;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    public static MediaPlayer player;
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


        dialog=new Dialog(this);
        dialog.setContentView(R.layout.alertdialog_unique_key);
        dialog.setTitle("Create or Join your music room!");
        RadioButton radioButtonJoin=dialog.findViewById(R.id.radiobuttonJoinRoom);
        RadioButton radioButtonCreate=dialog.findViewById(R.id.radiobuttonCreateRoom);
        EditText textViewCreateId=dialog.findViewById(R.id.generateRoomId);
        EditText editTextEnterId=dialog.findViewById(R.id.enterRoomId);
        Button submitRoomId=dialog.findViewById(R.id.submitRoomId);
        dialog.setCancelable(false);
        dialog.show();
        Objects.requireNonNull(dialog.getWindow()).setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        radioButtonJoin.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked)
                {
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
                }
            }
        });
        submitRoomId.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseHelper firebaseHelper=new FirebaseHelper();
                SharedPreferences.Editor editor=preferences.edit();
                if(editTextEnterId.getVisibility()==View.VISIBLE) {

                    Log.i("RoomId", editTextEnterId.getText().toString());
                }
                else if(textViewCreateId.getVisibility()==View.VISIBLE) {
                    firebaseHelper.addRoomId(textViewCreateId.getText().toString(),MainActivity.this);
                    editor.putString("roomid",textViewCreateId.getText().toString());
                    editor.putBoolean("isRoomCreator",true);
                    Log.i("RoomId", textViewCreateId.getText().toString());
                }
                editor.putBoolean("isRoomIdSelected",true);
                editor.apply();
                dialog.dismiss();
            }
        });


        if(!preferences.getBoolean("firsttime",false))
        {
            AlertDialog.Builder builder=new AlertDialog.Builder(this);
            builder.setTitle("Whats your nickname?");
            EditText addUsername=new EditText(this);
            builder.setView(addUsername);
            builder.setCancelable(false);
            builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    String username=addUsername.getText().toString();
                    SharedPreferences.Editor editor=preferences.edit();
                    editor.putBoolean("firsttime",true);
                    editor.putString("username",username);
                    editor.apply();

                }
            });
            AlertDialog alertDialog=builder.create();
            alertDialog.show();
        }



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

}
