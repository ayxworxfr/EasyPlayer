package com.evildoer.player.ui.main;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.evildoer.player.R;
import com.evildoer.player.data.model.Video;

import java.io.Serializable;

public class EasyPlayerActivity extends AppCompatActivity {

    private ContentResolver mContentResolver;
    private ListView mPlaylist;
    private MediaCursorAdapter mCursorAdapter;


    private final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

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
                Intent intent = new Intent(EasyPlayerActivity.this, HPlayerActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("video", (Serializable) video);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_easy_player);

        if (getVideos() != null) {
            mPlaylist = findViewById(R.id.lv_playlist);
            verifyPermission(EasyPlayerActivity.this);
            mContentResolver = getContentResolver();
            mPlaylist.setAdapter(getVideos());
        }
        mPlaylist.setOnItemClickListener(itemClickListener);
        final Button button = findViewById(R.id.title_menu);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopupMenu(button,EasyPlayerActivity.this);
            }
        });
    }

    private MediaCursorAdapter getVideos() {
        Uri videoUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
        ContentResolver contentResolver = getContentResolver();
        String[] projection = new String[]{
                "_id", MediaStore.Video.VideoColumns.DATA, MediaStore.Video.VideoColumns.DURATION,
                MediaStore.Video.VideoColumns.DISPLAY_NAME, MediaStore.Video.VideoColumns.DATE_ADDED
        };

        Cursor cursor = contentResolver.query(videoUri, projection, null, null, null);
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
        mCursorAdapter = new MediaCursorAdapter(EasyPlayerActivity.this);
        mCursorAdapter.swapCursor(cursor);
        mCursorAdapter.notifyDataSetChanged();
//            cursor.close();
        return mCursorAdapter;
    }

    private void showPopupMenu(final View view, final Context context) {
        final PopupMenu popupMenu = new PopupMenu(context, view);
        //menu 布局
        popupMenu.getMenuInflater().inflate(R.menu.title_menu, popupMenu.getMenu());
        //点击事件
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.add:
                        add(context);
                        break;
                    case R.id.exit:
                        popupMenu.dismiss();
                        break;
                    default:
                        break;
                }
                return false;
            }
        });
        //关闭事件
        popupMenu.setOnDismissListener(new PopupMenu.OnDismissListener() {
            @Override
            public void onDismiss(PopupMenu menu) {
                Toast.makeText(view.getContext(), "关闭", Toast.LENGTH_SHORT).show();
            }
        });
        //显示菜单，不要少了这一步
        popupMenu.show();
    }

    private void add(final Context context) {
        final EditText text = new EditText(context);
        new AlertDialog.Builder(context)
                .setTitle("网络视频")
                .setView(text)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        jump(text.getText().toString());
                        Toast.makeText(context, "跳转成功", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO Auto-generated method stub
                        dialog.dismiss();
                    }
                }).show();
    }

    public void jump(String path){
        Video video = new Video();
        video.setPath(path);
//        video.setPath("http://9890.vod.myqcloud.com/9890_4e292f9a3dd011e6b4078980237cc3d3.f20.mp4");
        video.setTitle("网络视频");
        Intent intent = new Intent(EasyPlayerActivity.this, OriginPlayerActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("video", (Serializable) video);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    public void verifyPermission(Context context) {
        int permission = ActivityCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    EasyPlayerActivity.this,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE);
        }
    }
}

