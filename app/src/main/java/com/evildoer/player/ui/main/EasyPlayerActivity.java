package com.evildoer.player.ui.main;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

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
//                Intent intent = new Intent(EasyPlayerActivity.this, OriginPlayerActivity.class);
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

