package com.prometrx.myinstagramclone;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;

import com.google.android.material.tabs.TabLayout;
import com.prometrx.myinstagramclone.Fragments.ChatsFragment;
import com.prometrx.myinstagramclone.Fragments.FeedFragment;
import com.prometrx.myinstagramclone.Fragments.ProfileFragment;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //TabLayout & ViewPager
        TabLayout tabLayout = findViewById(R.id.tabLayout);
        ViewPager viewPager = findViewById(R.id.viewPager);

        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        //Add Fragments viewPagerAdapter.fragmentsAdd(new XFragment(), "title");
        viewPagerAdapter.fragmentsAdd(new ProfileFragment(),"Profile");
        viewPagerAdapter.fragmentsAdd(new FeedFragment(), "Feed");
        viewPagerAdapter.fragmentsAdd(new ChatsFragment(), "Chats");

        viewPager.setAdapter(viewPagerAdapter);
        tabLayout.setupWithViewPager(viewPager);
    }

    private class ViewPagerAdapter extends FragmentPagerAdapter {

        private ArrayList<Fragment> fragments;
        private ArrayList<String> fragmentsTitle;
        public ViewPagerAdapter(@NonNull FragmentManager fm) {
            super(fm);
            this.fragments = new ArrayList<>();
            this.fragmentsTitle = new ArrayList<>();

        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragmentsTitle.size();
        }

        public void fragmentsAdd(Fragment fragment, String title) {
            this.fragments.add(fragment);
            this.fragmentsTitle.add(title);
        }

        public CharSequence getPageTitle(int position) {
            return fragmentsTitle.get(position);
        }
    }
}