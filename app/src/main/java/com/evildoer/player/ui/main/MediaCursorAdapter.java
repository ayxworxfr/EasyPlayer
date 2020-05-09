package com.evildoer.player.ui.main;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.evildoer.player.R;

import java.io.File;

class MediaCursorAdapter extends CursorAdapter {

    private Context mContext;
    private LayoutInflater mLayoutInflater;
    private static final int NORMAL_LENGTH = 20;

    private static final String TAG = MediaCursorAdapter.class.getSimpleName();

    public MediaCursorAdapter(Context context) {
        super(context, null, 0);
        mContext = context;

        mLayoutInflater = LayoutInflater.from(mContext);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        View itemView = mLayoutInflater.inflate(R.layout.list_item, viewGroup, false);

        if (itemView != null) {
            ViewHolder viewHolder = new ViewHolder();
            viewHolder.tvTitle = itemView.findViewById(R.id.tv_title);
            viewHolder.tvTime = itemView.findViewById(R.id.tv_time);
            viewHolder.ivImage = itemView.findViewById(R.id.iv_image);
            viewHolder.divider = itemView.findViewById(R.id.divider);
            itemView.setTag(viewHolder);

            return itemView;
        }

        return null;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ViewHolder viewHolder = (ViewHolder) view.getTag();
        int titleIndex = cursor.getColumnIndex(MediaStore.Video.Media.DISPLAY_NAME);
        long duration = cursor.getLong(cursor.getColumnIndex(MediaStore.Video.Media.DURATION));
        String path = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DATA));
        duration=duration/1000;
        String title = cursor.getString(titleIndex);
        String artist = getDate(duration);


        int titleLength = title.length();
        int position = cursor.getPosition();
        int count = cursor.getCount();

        if (viewHolder != null) {
            viewHolder.tvTitle.setText(title);
            if (titleLength > NORMAL_LENGTH) {
                String reTitle = title.substring(0, NORMAL_LENGTH/2) +
                        "..." + title.substring(titleLength-NORMAL_LENGTH/2, titleLength);
                viewHolder.tvTitle.setText(reTitle);
            }

            viewHolder.tvTime.setText(artist);
            Glide.with(context)
                    .load(Uri.fromFile(new File(path)))
                    .into(viewHolder.ivImage);
            viewHolder.divider.setVisibility(View.VISIBLE);

            if (position == count - 1) {
                viewHolder.divider.setVisibility(View.INVISIBLE);
            }
        }
    }

    public static String getDate(long date ) {
        long h = date/3600;
        long m = (date%3600)/60;
        long s = (date%3600)%60;
        if(h==0){
            if(s<10)
                return  m + ":0" + s;
            else
                return m + ":" + s;
        }
        else {
            if(m<10) {
                if (s < 10)
                    return h + ":0" + m + ":0" + s;
                else
                    return h + ":0" + m + ":" + s;
            }
            else {
                if (s < 10)
                    return h + ":0" + m + ":0" + s;
                else
                    return h + ":0" + m + ":" + s;
            }
        }
    }

    public class ViewHolder {
        TextView tvTitle;
        TextView tvTime;
        ImageView ivImage;
        View divider;
    }
}
