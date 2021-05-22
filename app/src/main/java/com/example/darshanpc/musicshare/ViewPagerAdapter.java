package com.example.darshanpc.musicshare;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import android.util.Log;

public class ViewPagerAdapter extends FragmentPagerAdapter{
    int mNoOftabs;
    public ViewPagerAdapter(FragmentManager fm,int NoOfTabs) {
        super(fm);
        this.mNoOftabs=NoOfTabs;
    }

    @Override
    public Fragment getItem(int i) {
        if(i==0)
        {
            SearchFragment searchFragment=new SearchFragment();
            Log.i("search_fragment","started");
            return searchFragment;
        }
        if(i==1)
        {
            HomeFragment homeFragment=new HomeFragment();
            Log.i("home_fragment","started");
            return homeFragment;
        }
        if(i==2)
        {
            ChatFragment chatFragment=new ChatFragment();
            Log.i("chat_fragment","started");
            return chatFragment;
        }
        return null;

    }

    @Override
    public int getCount() {
        return mNoOftabs;
    }

}
