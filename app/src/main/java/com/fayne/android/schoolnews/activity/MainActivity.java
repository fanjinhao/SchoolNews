package com.fayne.android.schoolnews.activity;

import android.os.Bundle;

import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;

import com.fayne.android.schoolnews.R;
import com.fayne.android.schoolnews.fragment.MainFragment;
import com.jaeger.library.StatusBarUtil;


public class MainActivity extends BaseActivity {

    private TabLayout mTab;
    private ViewPager mViewPager;
    private FragmentPagerAdapter mAdapter;
    private String []title;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        StatusBarUtil.setColor(this, 0x3F51B5);
        mTab = findViewById(R.id.id_table_layout);
        mViewPager = findViewById(R.id.id_view_pager);

        initData();
    }

    private void initData() {
        title = new String[] {"安科新闻", "通知公告", "校园动态", "学术信息"};
        mAdapter = new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                return MainFragment.newInstance(position);
            }

            @Override
            public int getCount() {
                return title.length;
            }

            @Override
            public CharSequence getPageTitle(int position) {
                return title[position % title.length];
            }
        };
        mViewPager.setAdapter(mAdapter);
        mTab.setupWithViewPager(mViewPager);
    }
}
