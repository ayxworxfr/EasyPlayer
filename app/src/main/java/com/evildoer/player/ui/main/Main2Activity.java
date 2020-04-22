//package com.evildoer.player.ui.main;
//
//import androidx.appcompat.app.AppCompatActivity;
//
//import android.content.res.Configuration;
//import android.os.Bundle;
//import android.widget.ImageView;
//import android.widget.Toast;
//
//import com.bumptech.glide.Glide;
//import com.dou361.ijkplayer.bean.VideoijkBean;
//import com.dou361.ijkplayer.listener.OnShowThumbnailListener;
//import com.dou361.ijkplayer.widget.PlayStateParams;
//import com.dou361.ijkplayer.widget.PlayerView;
//import com.evildoer.player.R;
//import com.evildoer.player.listener.VideoListener;
//import com.evildoer.player.ui.frame.VideoPlayer;
//import com.evildoer.player.utils.MediaUtils;
//
//import java.io.IOException;
//import java.util.ArrayList;
//import java.util.List;
//
//import tv.danmaku.ijk.media.player.IMediaPlayer;
//
//public class MainActivity extends AppCompatActivity implements VideoListener {
//
//    private VideoPlayer videoPlayer;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity2_main);
//        videoPlayer = findViewById(R.id.video);
//
//        videoPlayer.setVideoListener(this);
////        videoPlayer.setPath("http://ipfs.ztgame.com.cn/QmRRGU4aUZEqJsHxKzBb1ns97GHw45eCRRZFe6Eu8GCmZ4.m3u8");
//        videoPlayer.setPath("I:\\ijktest.mp4");
//        try {
//            videoPlayer.load();
//        } catch (IOException e) {
//            Toast.makeText(this,"播放失败",Toast.LENGTH_SHORT);
//            e.printStackTrace();
//        }
//    }
//
//    @Override
//    public void onBufferingUpdate(IMediaPlayer iMediaPlayer, int i) {
//
//    }
//
//    @Override
//    public void onCompletion(IMediaPlayer iMediaPlayer) {
//
//    }
//
//    @Override
//    public boolean onError(IMediaPlayer iMediaPlayer, int i, int i1) {
//        return false;
//    }
//
//    @Override
//    public boolean onInfo(IMediaPlayer iMediaPlayer, int i, int i1) {
//        return false;
//    }
//
//    @Override
//    public void onPrepared(IMediaPlayer iMediaPlayer) {
//        videoPlayer.start();
//    }
//
//    @Override
//    public void onSeekComplete(IMediaPlayer iMediaPlayer) {
//
//    }
//
//    @Override
//    public void onVideoSizeChanged(IMediaPlayer iMediaPlayer, int i, int i1, int i2, int i3) {
//
//    }
//}
