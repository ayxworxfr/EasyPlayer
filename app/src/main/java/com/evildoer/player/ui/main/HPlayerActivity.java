package com.evildoer.player.ui.main;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.PowerManager;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.dou361.ijkplayer.bean.VideoijkBean;
import com.dou361.ijkplayer.listener.OnShowThumbnailListener;
import com.dou361.ijkplayer.widget.PlayStateParams;
import com.dou361.ijkplayer.widget.PlayerView;
import com.evildoer.player.R;
import com.evildoer.player.data.model.Video;
import com.evildoer.player.utils.MediaUtils;
import com.evildoer.player.utils.PlayerUtils;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


public class HPlayerActivity extends AppCompatActivity {

    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    private PlayerView player;
    private Context mContext;
    private List<VideoijkBean> list;
    private PowerManager.WakeLock wakeLock;
    private View rootView;

    private ContentResolver mContentResolver;
    private ListView mPlaylist;
    private MediaCursorAdapter mCursorAdapter;

    @SuppressLint("InvalidWakeLockTag")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.mContext = this;
        rootView = getLayoutInflater().from(this).inflate(R.layout.activity_h, null);
        setContentView(rootView);

        Bundle bundle=getIntent().getExtras();
//        String path =bundle.getString("path");
        Video video = (Video)bundle.getSerializable("video");

        /**虚拟按键的隐藏方法*/
        rootView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

            @Override
            public void onGlobalLayout() {

                //比较Activity根布局与当前布局的大小
                int heightDiff = rootView.getRootView().getHeight() - rootView.getHeight();
                if (heightDiff > 100) {
                    //大小超过100时，一般为显示虚拟键盘事件
                    rootView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
                } else {
                    //大小小于100时，为不显示虚拟键盘或虚拟键盘隐藏
                    rootView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);

                }
            }
        });

        this.verifyPermission(HPlayerActivity.this);
        /**常亮*/
        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        wakeLock = pm.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK, "liveTAG");
        wakeLock.acquire();
        list = new ArrayList<VideoijkBean>();

        //这里模拟一个本地视频的播放，视频需要将testvideo文件夹的视频放到安卓设备的内置sd卡根目录中
//        String url1 = getLocalVideoPath("my_video.mp4");
        String url1 =video.getPath();
        if (!new File(url1).exists()) {
            url1 = "http://9890.vod.myqcloud.com/9890_4e292f9a3dd011e6b4078980237cc3d3.f20.mp4";
        }
        String url2 = "http://9890.vod.myqcloud.com/9890_4e292f9a3dd011e6b4078980237cc3d3.f30.mp4";
        VideoijkBean m1 = new VideoijkBean();
        m1.setStream("标清");
        m1.setUrl(url1);
        VideoijkBean m2 = new VideoijkBean();
        m2.setStream("高清");
        m2.setUrl(url2);
        list.add(m1);
        list.add(m2);
        //        player = new PlayerView(this, rootView)
        String title = video.getTitle();
        if(title != null && title.lastIndexOf('.') != -1){
            title = title.substring(0, title.lastIndexOf('.'));
        }else if(title == null){
            title = "video";
        }
        player = new PlayerView(this) {
            @Override
            public PlayerView toggleProcessDurationOrientation() {
                hideSteam(getScreenOrientation() == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                return setProcessDurationOrientation(getScreenOrientation() == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT ? PlayStateParams.PROCESS_PORTRAIT : PlayStateParams.PROCESS_LANDSCAPE);
            }

            @Override
            public PlayerView setPlaySource(List<VideoijkBean> list) {
                return super.setPlaySource(list);
            }
        }
                .setTitle(title)
                .setNetWorkTypeTie(false)           // 隐藏视频移动流量是播放提醒
                .setProcessDurationOrientation(PlayStateParams.PROCESS_PORTRAIT)
                .setScaleType(PlayStateParams.fillparent)
                .forbidTouch(false)
                .hideSteam(true)
                .hideCenterPlayer(true)
                .showThumbnail(new OnShowThumbnailListener() {
                    @SuppressLint("CheckResult")
                    @Override
                    public void onShowThumbnail(ImageView ivThumbnail) {
                        RequestOptions requestOptions = new RequestOptions();
                        requestOptions.placeholder(R.color.cl_default);
                        requestOptions.error(R.color.cl_error);
                        Glide.with(mContext)
                                .load("http://pic2.nipic.com/20090413/406638_125424003_2.jpg")
                                .apply(requestOptions)
                                .into(ivThumbnail);
                    }
                })
                .setPlaySource(list)
                .setChargeTie(true,60)
                .startPlay();

        if (getVideos(video.getDisplayName()) != null) {
            mPlaylist = findViewById(R.id.lv_playlist);
            verifyPermission(HPlayerActivity.this);
            mContentResolver = getContentResolver();
            mPlaylist.setAdapter(getVideos(video.getDisplayName()));
        }
        mPlaylist.setOnItemClickListener(itemClickListener);
    }

    private ListView.OnItemClickListener itemClickListener = new ListView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            Cursor cursor = mCursorAdapter.getCursor();
            if (cursor != null && cursor.moveToPosition(i)) {
                String path = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DATA));
                String title = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DISPLAY_NAME));
                int duration = (int) (cursor.getLong(cursor.getColumnIndex(MediaStore.Video.Media.DURATION)) / 1000);

                Video video = new Video();
                video.setPath(path);
                video.setTitle(title);
                video.setDuration(duration);
                Intent intent = new Intent(HPlayerActivity.this, HPlayerActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("video", (Serializable) video);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        }
    };

    private MediaCursorAdapter getVideos(String name) {
        Uri videoUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
        ContentResolver contentResolver = getContentResolver();
        String[] projection = new String[]{
                "_id", MediaStore.Video.VideoColumns.DATA, MediaStore.Video.VideoColumns.DURATION,
                MediaStore.Video.VideoColumns.DISPLAY_NAME, MediaStore.Video.VideoColumns.DATE_ADDED
        };
        String selection = null;
        if(name != null && !name.isEmpty()){
            selection = MediaStore.Video.VideoColumns.DISPLAY_NAME + " not in("+"\""+ name +"\""+")";
        }

        Cursor cursor = contentResolver.query(videoUri, projection, selection, null, null);
//        cursor.setNotificationUri(contentResolver, videoUri);
        if (cursor == null) {
            return null;
        }
        if (cursor.getCount() != 0) {
            while (cursor.moveToNext()) {
                String id = cursor.getString(cursor.getColumnIndex("_id"));
                String path = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DATA));
            }
        }
        mCursorAdapter = new MediaCursorAdapter(HPlayerActivity.this);
        mCursorAdapter.swapCursor(cursor);
        mCursorAdapter.notifyDataSetChanged();
//            cursor.close();
        return mCursorAdapter;
    }

    @Override
    //安卓重写返回键事件
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode==KeyEvent.KEYCODE_BACK){
            Intent intent = new Intent(this, EasyPlayerActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        }
        return true;
    }

    /**
     * 播放本地视频
     */
    private String getLocalVideoPath(String name) {
        List allVideoList = new ArrayList<Video>();
//        PlayerUtils.getVideoFile(allVideoList, new File("/sdcard"));// 获得视频文件
        PlayerUtils.getVideoFile(allVideoList, Environment.getExternalStorageDirectory());// 获得视频文件
        String sdCard = Environment.getExternalStorageDirectory().getPath();
        String uri = sdCard + File.separator + name;
        return uri;
    }

    public void verifyPermission(Context context){
        int permission = ActivityCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    HPlayerActivity.this,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE);
        }
    }


    @Override
    protected void onPause() {
        super.onPause();
        if (player != null) {
            player.onPause();
        }
        /**demo的内容，恢复系统其它媒体的状态*/
        MediaUtils.muteAudioFocus(mContext, true);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (player != null) {
            player.onResume();
        }
        /**demo的内容，暂停系统其它媒体的状态*/
        MediaUtils.muteAudioFocus(mContext, false);
        /**demo的内容，激活设备常亮状态*/
        if (wakeLock != null) {
            wakeLock.acquire();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (player != null) {
            player.onDestroy();
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (player != null) {
            player.onConfigurationChanged(newConfig);
        }
    }

    @Override
    public void onBackPressed() {
        if (player != null && player.onBackPressed()) {
            return;
        }
        super.onBackPressed();
        /**demo的内容，恢复设备亮度状态*/
        if (wakeLock != null) {
            wakeLock.release();
        }
    }

}
