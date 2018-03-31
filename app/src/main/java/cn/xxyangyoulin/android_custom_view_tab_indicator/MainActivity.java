package cn.xxyangyoulin.android_custom_view_tab_indicator;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import com.mnn.nnn.viewpagerzhidinyi.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class MainActivity extends AppCompatActivity {
    private ViewPager mViewPager;
    private ViewPagerIndicator mIndicator;

    private List<String> mTitles = Arrays.asList("短信", "收藏", "设置", "短信2", "收藏2", "设置2");
    private List<VpFragment> mContent = new ArrayList<>();
    private PagerAdapter mAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main1);

        initView();

        initData();

        mIndicator.setTabShowCount(4);
        mIndicator.setWordColor(0x88FFFFFF);
        mIndicator.setWordSize(16);

        mIndicator.setTabTitles(mTitles);

        mViewPager.setAdapter(mAdapter);
        mIndicator.setViewPager(mViewPager, 0);

        mIndicator.setOnPageChangeListener(new ViewPagerIndicator.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                Snackbar.make(mViewPager, "位置"+(position+1), Snackbar.LENGTH_SHORT).show();
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private void initData() {
        for (String title : mTitles) {
            VpFragment fragment = VpFragment.newInstance(title);
            mContent.add(fragment);
        }
        mAdapter = new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                return mContent.get(position);
            }

            @Override
            public int getCount() {
                return mContent.size();
            }
        };
    }

    private void initView() {
        mIndicator = (ViewPagerIndicator) findViewById(R.id.indicator);
        mViewPager = (ViewPager) findViewById(R.id.view_pager);
    }


}
