package com.evildoer.player.ui.main;

import android.os.Bundle;
//import android.support.design.widget.Snackbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.evildoer.player.R;
import com.evildoer.player.ui.frame.adapter.MainFragmentAdapter;
import com.google.android.material.tabs.TabLayout;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 主框架
 */
public class NavActivity extends AppCompatActivity {

    /**
     * 菜单标题
     */
//    private final int[] TAB_TITLES = new int[]{R.string.menu_msg, R.string.menu_contact, R.string.menu_find, R.string.menu_me};
    private final int[] TAB_TITLES = new int[]{R.string.menu_video, R.string.menu_zhibo};
    /**
     * 菜单图标
     */
//    private final int[] TAB_IMGS = new int[]{R.menu.tab_main_msg_selector, R.menu.tab_main_contact_selector, R.menu.tab_main_find_selector
//            , R.menu.tab_main_zhibo_selector};
    private final int[] TAB_IMGS = new int[]{R.menu.tab_main_video_selector, R.menu.tab_main_zhibo_selector};

    @BindView(R.id.view_pager)
    ViewPager viewPager;
    @BindView(R.id.tab_layout)
    TabLayout tabLayout;

    /**
     * 页卡适配器
     */
    private PagerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.active_nav);

        ButterKnife.bind(this);

        // 初始化页卡
        initPager();

        setTabs(tabLayout, getLayoutInflater(), TAB_TITLES, TAB_IMGS);
    }

    /**
     * 设置页卡显示效果
     * @param tabLayout
     * @param inflater
     * @param tabTitlees
     * @param tabImgs
     */
    private void setTabs(TabLayout tabLayout, LayoutInflater inflater, int[] tabTitlees, int[] tabImgs) {
        for (int i = 0; i < tabImgs.length; i++) {
            TabLayout.Tab tab = tabLayout.newTab();
            View view = inflater.inflate(R.layout.item_main_menu, null);
            // 使用自定义视图，目的是为了便于修改，也可使用自带的视图
            tab.setCustomView(view);

            TextView tvTitle = (TextView) view.findViewById(R.id.txt_tab);
            tvTitle.setText(tabTitlees[i]);
            ImageView imgTab = (ImageView) view.findViewById(R.id.img_tab);
            imgTab.setImageResource(tabImgs[i]);
            tabLayout.addTab(tab);
        }
    }

    private void initPager() {
        adapter = new MainFragmentAdapter(getSupportFragmentManager());
        viewPager.setAdapter(adapter);

        // 关联切换
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                // 取消平滑切换
                viewPager.setCurrentItem(tab.getPosition(), false);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

}

