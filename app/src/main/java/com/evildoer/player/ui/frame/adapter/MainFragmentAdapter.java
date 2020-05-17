package com.evildoer.player.ui.frame.adapter;


import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.evildoer.player.ui.frame.ZhiboFragment;
import com.evildoer.player.ui.frame.VideoFragment;

/**
 * 主界面底部菜单适配器
 */
public class MainFragmentAdapter extends FragmentPagerAdapter {
    public MainFragmentAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int i) {
        Fragment fragment = null;
        switch (i) {
//            case 0:
//                fragment = new MsgFragment();
//                break;
//            case 1:
//                fragment = new ContactFragment();
//                break;
//            case 2:
//                fragment = new FindFragment();
//                break;
            case 0:
                fragment = new VideoFragment();
                break;
            case 1:
                fragment = new ZhiboFragment();
                break;
            default:
                break;
        }
        return fragment;
    }

    @Override
    public int getCount() {
        return 2;
    }

}

