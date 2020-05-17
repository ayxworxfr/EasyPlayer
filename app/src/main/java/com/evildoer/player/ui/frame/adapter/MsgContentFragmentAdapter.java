package com.evildoer.player.ui.frame.adapter;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import com.evildoer.player.ui.frame.MsgContentFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * 消息内容子页面适配器
 */
public class MsgContentFragmentAdapter extends FragmentPagerAdapter {
    private List<String> names;

    public MsgContentFragmentAdapter(FragmentManager fm) {
        super(fm);
        this.names = new ArrayList<>();
    }

    /**
     * 数据列表
     *
     * @param datas
     */
    public void setList(List<String> datas) {
        this.names.clear();
        this.names.addAll(datas);
        notifyDataSetChanged();
    }

    @Override
    public Fragment getItem(int position) {
        MsgContentFragment fragment = new MsgContentFragment();
        Bundle bundle = new Bundle();
        bundle.putString("name", names.get(position));
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public int getCount() {
        return names.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        String plateName = names.get(position);
        if (plateName == null) {
            plateName = "";
        } else if (plateName.length() > 15) {
            plateName = plateName.substring(0, 15) + "...";
        }
        return plateName;
    }
}
