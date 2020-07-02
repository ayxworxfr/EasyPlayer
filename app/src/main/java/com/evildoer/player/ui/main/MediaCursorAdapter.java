package com.evildoer.player.ui.main;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.evildoer.player.R;

import java.io.File;

class MediaCursorAdapter extends CursorAdapter {

    private Context mContext;
    private String currentVideo;
    private LayoutInflater mLayoutInflater;
    private static final int NORMAL_LENGTH = 20;
    private String[] projection = new String[]{
            "_id", MediaStore.Video.VideoColumns.DATA, MediaStore.Video.VideoColumns.DURATION,
            MediaStore.Video.VideoColumns.DISPLAY_NAME, MediaStore.Video.VideoColumns.DATE_ADDED
    };

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
            viewHolder.tvMenu = itemView.findViewById(R.id.tv_menu);
            itemView.setTag(viewHolder);

            return itemView;
        }

        return null;
    }

    @Override
    public void bindView(View view, final Context context, final Cursor cursor) {
        ViewHolder viewHolder = (ViewHolder) view.getTag();
        int titleIndex = cursor.getColumnIndex(MediaStore.Video.Media.DISPLAY_NAME);
        long duration = cursor.getLong(cursor.getColumnIndex(MediaStore.Video.Media.DURATION));
        final String path = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DATA));
        duration = duration / 1000;
        String title = cursor.getString(titleIndex);
        String artist = getDate(duration);


        int titleLength = title.length();
        int position = cursor.getPosition();
        int count = cursor.getCount();

        if (viewHolder != null) {
            viewHolder.tvTitle.setText(title);
            if (titleLength > NORMAL_LENGTH) {
                String reTitle = title.substring(0, NORMAL_LENGTH / 2) +
                        "..." + title.substring(titleLength - NORMAL_LENGTH / 2, titleLength);
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
            final Button button = viewHolder.tvMenu;
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showPopupMenu(button, context, path,cursor);
                }
            });

            // HPlayerActivity发送的请求隐藏button
            if(getActivityByContext(mContext) instanceof HPlayerActivity){
                button.setVisibility(View.INVISIBLE);
            }
        }
    }


    private void showPopupMenu(final View view, final Context context, final String path, final Cursor cursor) {
        final PopupMenu popupMenu = new PopupMenu(context, view);
        //menu 布局
        popupMenu.getMenuInflater().inflate(R.menu.list_menu, popupMenu.getMenu());
        //点击事件
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.rename:
                        reName(context, path);
                        break;
                    case R.id.delete:
                        delete(context,path,cursor);
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

    public static Activity getActivityByContext(Context context){
        while(context instanceof ContextWrapper){
            if(context instanceof Activity){
                return (Activity) context;
            }
            context = ((ContextWrapper) context).getBaseContext();
        }
        return null;
    }

    private void delete(final Context context, final String path, final Cursor cursor) {
        new AlertDialog.Builder(context)
                .setTitle("是否删除")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        File file = new File(path);
                        /**  如果是文件则删除  如果都删除可不必判断  */
                        if (file.exists()) {
                            DeleteVideoDatabase(path,context);
                            file.delete();
                            Toast.makeText(context, "删除成功", Toast.LENGTH_SHORT).show();
                        }
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

    private void reName(final Context context, final String path) {
        final EditText text = new EditText(context);
        new AlertDialog.Builder(context)
                .setTitle("重命名")
                .setView(text)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String filename = text.getText().toString();
                        filename = filename.trim().replaceAll("\\n", "");
                        changeFileName(path,filename,context);
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

    public void changeFileName(String filePath,String reName,Context context){
        File file = new File(filePath);
        //前面路径必须一样才能修改成功
        String path = filePath.substring(0, filePath.lastIndexOf("/")+1)+reName+filePath.substring(filePath.lastIndexOf("."), filePath.length());
        File newFile = new File(path);
        boolean result = file.renameTo(newFile);
        if (result){
            Toast.makeText(context, "重命名成功", Toast.LENGTH_SHORT).show();
            UpdateVideoDatabase(filePath,path,context);
        }else{
            Toast.makeText(context, "重命名失败, 文件已存在", Toast.LENGTH_SHORT).show();
        }
    }

    public void UpdateVideoDatabase(String oldPath,String newPath,Context context){
        ContentResolver contentResolver = context.getContentResolver();
        ContentValues values = new ContentValues();
        values.put(MediaStore.Audio.Media.DATA,newPath);
        contentResolver.update(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, values, MediaStore.Audio.Media.DATA + " = '" + oldPath + "'",null);
        Cursor cursor = contentResolver.query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, projection, null, null, null);
        changeCursor(cursor);
        notifyDataSetChanged();
    }

    public void DeleteVideoDatabase(String path,Context context){
        ContentResolver contentResolver = context.getContentResolver();
        contentResolver.delete(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, MediaStore.Audio.Media.DATA + " = '" + path + "'", null);
        Cursor cursor = contentResolver.query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, projection, null, null, null);
        changeCursor(cursor);
        notifyDataSetChanged();
    }



    public static String getDate(long date) {
        long h = date / 3600;
        long m = (date % 3600) / 60;
        long s = (date % 3600) % 60;
        if (h == 0) {
            if (s < 10)
                return m + ":0" + s;
            else
                return m + ":" + s;
        } else {
            if (m < 10) {
                if (s < 10)
                    return h + ":0" + m + ":0" + s;
                else
                    return h + ":0" + m + ":" + s;
            } else {
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
        Button tvMenu;
    }
}