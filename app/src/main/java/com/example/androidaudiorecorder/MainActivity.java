package com.example.androidaudiorecorder;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.example.androidaudiorecorder.dummy.DummyContent;
import com.google.android.material.tabs.TabLayout;

public class MainActivity extends AppCompatActivity
        implements ItemFragment.OnListFragmentInteractionListener {
    TabLayout tabLayout;
    ViewPager viewPager;

    PageAdapter pageAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tabLayout = (TabLayout) findViewById(R.id.tablayout);
        viewPager = (ViewPager) findViewById(R.id.viewpager);


        pageAdapter = new PageAdapter(getSupportFragmentManager());
        pageAdapter.addFragment(new RecordFragment(), "Main");
        pageAdapter.addFragment(new ItemFragment(), "Library");

        viewPager.setAdapter(pageAdapter);

        tabLayout.setupWithViewPager(viewPager);
    }

    @Override
    public void onListFragmentInteraction(DummyContent.DummyItem item) {
        //ItemFragment와 상호작용하는 콜백 메서드
        Toast.makeText(this, item.toString(), Toast.LENGTH_SHORT).show();
    }
}

