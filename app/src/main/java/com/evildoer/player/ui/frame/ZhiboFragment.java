package com.evildoer.player.ui.frame;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.evildoer.player.R;


/**
 * 直播
 */
public class ZhiboFragment extends Fragment {

    public ZhiboFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_zhibo, container, false);
    }

}

