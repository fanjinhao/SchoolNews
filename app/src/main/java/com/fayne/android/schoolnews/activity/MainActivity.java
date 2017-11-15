package com.fayne.android.schoolnews.activity;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TableLayout;

import com.fayne.android.schoolnews.R;

public class MainActivity extends AppCompatActivity {

    private TableLayout mTableLayout;
    private ViewPager mViewPager;
    private FragmentPagerAdapter mAdapter;
    private String []title;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTableLayout = findViewById(R.id.id_table_layout);
        mViewPager = findViewById(R.id.id_view_pager);

        initData();
    }

    private void initData() {
        title = new String[] {"安科新闻", "通知公告", "校园动态", "学术信息"};
        mAdapter = new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                return null;
            }

            @Override
            public int getCount() {
                return 0;
            }
        };
    }
}
