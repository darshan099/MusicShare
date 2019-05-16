package com.example.darshanpc.musicshare;

import android.annotation.SuppressLint;
import android.content.Context;
import android.media.AudioManager;
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
        int[] itags = new int[]{249,250,251,171,172,139,140,141};
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
                                    MainActivity.player.prepare();
                                    MainActivity.player.start();
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }.extract(downloadurl,true,true);
            return null;
        }
    }
}
