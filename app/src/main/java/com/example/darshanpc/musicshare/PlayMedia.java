package com.example.darshanpc.musicshare;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.SparseArray;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import at.huber.youtubeExtractor.VideoMeta;
import at.huber.youtubeExtractor.YouTubeExtractor;
import at.huber.youtubeExtractor.YtFile;

import static com.example.darshanpc.musicshare.MainActivity.currentSongPos;


public class PlayMedia {
    public String downloadurl;
    public Context context;

    public void playVideoFromUrl(String downloadurl,Context context)
    {
        this.downloadurl=downloadurl;
        this.context=context;
        new asynctask().execute();
    }
    private YtFile getBestStreamLink(SparseArray<YtFile> ytFiles)
    {
        int[] itags = new int[]{249,171,139,250,251,172,140,141};
        if (ytFiles.get(itags[0]) != null) {
            return ytFiles.get(itags[0]);
        } else if (ytFiles.get(itags[1]) != null) {
            return ytFiles.get(itags[1]);
        } else if (ytFiles.get(itags[2]) != null) {
            return ytFiles.get(itags[2]);
        }
        else if (ytFiles.get(itags[3]) != null) {
            return ytFiles.get(itags[3]);
        }
        else if (ytFiles.get(itags[4]) != null) {
            return ytFiles.get(itags[4]);
        }
        else if (ytFiles.get(itags[5]) != null) {
            return ytFiles.get(itags[5]);
        }
        else if (ytFiles.get(itags[6]) != null) {
            return ytFiles.get(itags[6]);
        } else if (ytFiles.get(itags[7])!=null) {
            return ytFiles.get(itags[7]);
        }
        Toast.makeText(context, "Sorry! Failure Occured. Try another song.", Toast.LENGTH_SHORT).show();
        return null;
    }

    public class asynctask extends AsyncTask<Void,Void,Void>
    {

        @SuppressLint("StaticFieldLeak")
        @Override
        protected Void doInBackground(Void... voids) {

            new YouTubeExtractor(context){
                    @Override
                    public void onExtractionComplete(SparseArray<YtFile> ytfiles, VideoMeta vmeta)
                    {
                        if(ytfiles!=null)
                        {
                            YtFile getBestYtUrl=getBestStreamLink(ytfiles);
                            try {
                                if(MainActivity.player!=null) {
                                    MainActivity.player.reset();
                                    MainActivity.player.setDataSource(getBestYtUrl.getUrl());
                                    MainActivity.player.setAudioStreamType(AudioManager.STREAM_MUSIC);
                                    MainActivity.player.prepareAsync();
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            MainActivity.player.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                                @Override
                                public void onPrepared(MediaPlayer mp) {
                                    MainActivity.player.start();
                                    MainActivity.seekBarSong.setMax(MainActivity.player.getDuration());
                                    double endtime=MainActivity.player.getDuration();
                                    String seconds = String.valueOf(((int) endtime % 60000) / 1000);
                                    String minutes = String.valueOf(((int) endtime / 60000));
                                    if (seconds.length() == 1) {
                                        MainActivity.textViewEndTime.setText("0" + minutes + ":0" + seconds);
                                    } else {
                                        MainActivity.textViewEndTime.setText("0" + minutes + ":" + seconds);
                                    }
                                    Toast.makeText(context, "Now Playing: "+HomeFragment.songList.get(currentSongPos), Toast.LENGTH_SHORT).show();
                                }
                            });
                            MainActivity.player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                                @Override
                                public void onCompletion(MediaPlayer mp) {
                                    //TODO: get next song title and put it as current song
                                    if(currentSongPos==HomeFragment.songList.size()-1)
                                    {
                                        Toast.makeText(context, "End Of Songs! Add new ones!", Toast.LENGTH_SHORT).show();
                                    }
                                    else {
                                        SharedPreferences preferences= PreferenceManager.getDefaultSharedPreferences(context);
                                        currentSongPos=currentSongPos+1;
                                        DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference().child(preferences.getString("roomid",""));
                                        Map<String,Object> taskMap=new HashMap<>();
                                        taskMap.put("currentSongPosition",String.valueOf(currentSongPos));
                                        taskMap.put("currentSong",HomeFragment.songList.get(currentSongPos));
                                        taskMap.put("currentSongUrl",HomeFragment.songListUrl.get(currentSongPos));
                                        taskMap.put("isButtonPressed","1");
                                        databaseReference.updateChildren(taskMap);
                                    }
                                }
                            });
                        }
                    }
                }.extract(downloadurl,true,true);
            return null;
        }
    }
}
