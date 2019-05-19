package com.example.darshanpc.musicshare;

import android.annotation.SuppressLint;
import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.util.SparseArray;

import java.io.IOException;

import at.huber.youtubeExtractor.VideoMeta;
import at.huber.youtubeExtractor.YouTubeExtractor;
import at.huber.youtubeExtractor.YtFile;


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
        }
        return ytFiles.get(itags[7]);
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
                                }
                            });
                            MainActivity.player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                                @Override
                                public void onCompletion(MediaPlayer mp) {
                                    //TODO: get next song title and put it as current song
                                }
                            });
                        }
                    }
                }.extract(downloadurl,true,true);
            return null;
        }
    }
}
