package com.example.darshanpc.musicshare;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
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
