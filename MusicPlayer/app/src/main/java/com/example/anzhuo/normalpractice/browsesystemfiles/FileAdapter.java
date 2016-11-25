package com.example.anzhuo.normalpractice.browsesystemfiles;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.anzhuo.normalpractice.R;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by anzhuo on 2016/7/29.
 */
public class FileAdapter extends BaseAdapter {
    List<FileType> database;
    Context mContext;
    FileType fileType;
    SQLiteDatabase directories;
    public FileAdapter(Context context, List<FileType> list, SQLiteDatabase directories) {
        this.database = list;
        this.mContext = context;
        this.directories = directories;
    }

    @Override
    public int getCount() {
        return database == null ? 0 : database.size();
    }

    @Override
    public Object getItem(int position) {
        return database.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final int index=position;
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.filesystem_item, null);
            viewHolder = new ViewHolder();
            viewHolder.imageView = (ImageView) convertView.findViewById(R.id.iv_project_item_img);
            viewHolder.textView = (TextView) convertView.findViewById(R.id.tv_project_item_text);
            viewHolder.checkBox = (CheckBox) convertView.findViewById(R.id.cb_project_item_choose);
            convertView.setTag(viewHolder);
        }
        fileType = (FileType) database.get(position);
        final String chooseDir = fileType.getFilePath();
        viewHolder = (ViewHolder) convertView.getTag();

        viewHolder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    database.get(index).setType(FileType.TYPE_CHECKED);
                    ContentValues values = new ContentValues();
                    values.put("isChecked", 1);
                   directories.update("directories", values, "dirPath=?", new String[]{chooseDir});
                    values.clear();
                } else {
                    database.get(index).setType(FileType.TYPE_UNCHECKED);
                    ContentValues values = new ContentValues();
                    values.put("isChecked", -1);
                    directories.update("directories", values, "dirPath=?", new String[]{chooseDir});
                    values.clear();
                }
            }
        });
             if(database.get(position).getType()==FileType.TYPE_CHECKED){
                 viewHolder.checkBox.setChecked(true);
             }else {
                 viewHolder.checkBox.setChecked(false);
             }

        switch (fileType.getImage()) {
            case R.drawable.folder_full:
                viewHolder.imageView.setImageResource(fileType.getImage());
                viewHolder.imageView.setTag("fullFolder");
                viewHolder.textView.setText(fileType.getFileName());
                break;
            case R.drawable.folder:
                viewHolder.imageView.setImageResource(fileType.getImage());
                viewHolder.imageView.setTag("emptyFolder");
                viewHolder.textView.setText(fileType.getFileName());
                break;
            default:
                viewHolder.textView.setText(fileType.getFileName());
                viewHolder.imageView.setImageResource(fileType.getImage());
        }
        return convertView;
    }
    class ViewHolder {
        ImageView imageView;
        TextView textView;
        CheckBox checkBox;
    }
}


