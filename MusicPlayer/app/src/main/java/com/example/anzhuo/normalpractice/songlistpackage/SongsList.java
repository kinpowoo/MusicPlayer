package com.example.anzhuo.normalpractice.songlistpackage;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.PopupMenu;

import com.example.anzhuo.normalpractice.ClearEditText;
import com.example.anzhuo.normalpractice.R;
import com.example.anzhuo.normalpractice.SearchActivity;
import com.example.anzhuo.normalpractice.browsesystemfiles.BrowseFileActivity;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by anzhuo on 2016/8/16.
 */
public class SongsList extends Activity implements TextWatcher, ExpandableListView.OnGroupExpandListener,View.OnClickListener {
    String state = Environment.getExternalStorageState();
    File sdcardFile = Environment.getExternalStorageDirectory();
    File rootFileMusic = new File("/mnt/sdcard/");
    File[] rootFileInit={rootFileMusic};
    ProgressDialog progressDialog;
    SQLiteDatabase existSong;
    ExpandableListView listView;
    ClearEditText searchArea;
    SongAdapter songAdapter;
    ImageButton menuButton;
    File[] convertFile;
    File[] customDirectory;
    ArrayList<String> getList;
    private Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
                songAdapter.notifyDataSetChanged();
        }
    };
    CharacterParser characterParser = new CharacterParser();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.songs_list);
        existSong=openOrCreateDatabase("existSong.db",MODE_PRIVATE,null);
        existSong.execSQL("create table if not exists musicFolder(_id integer primary key autoincrement,folderName text,folderPath text,songName text,songPath text)");
        listView = (ExpandableListView) findViewById(R.id.lv_songList);
        menuButton= (ImageButton) findViewById(R.id.ib_songList_menu);
        searchArea = (ClearEditText) findViewById(R.id.cet_search);
        songAdapter = new SongAdapter(this,existSong);
        listView.setAdapter(songAdapter);
        searchArea.addTextChangedListener(this);
        listView.setOnGroupExpandListener(this);
        listView.setGroupIndicator(null);
        menuButton.setOnClickListener(this);
        getList=getIntent().getStringArrayListExtra("backDir");
        if(getList!=null){
            getIntentData(getList);
        }

    }
   public void getIntentData(ArrayList<String> list){
           customDirectory=new File[list.size()];
           for(int i=0;i<list.size();i++){
               File chooseDirectory=new File(list.get(i));
               customDirectory[i]=chooseDirectory;
           }
//       progressDialog=ProgressDialog.show(this,null,"正在扫描内存中的文件........");
           scanFiles(customDirectory);
   }

    public void scanFiles(File[] originFile) {
        if(state.equals(Environment.MEDIA_MOUNTED)) {
          convertFile=originFile;
        new Thread() {
            @Override
            public void run() {
                    for (File initFile : convertFile) {
                        if (initFile!=null) {
                            scanFile(initFile);
                        }
                    }
                handler.sendEmptyMessage(1);
            }
        }.start();
        }
    }
    public void scanFile(File initFile){
        if(initFile.list().length>0) {
            File[] directories = initFile.listFiles();
            for (File file : directories) {
                if (file.isFile()) {
                    boolean FLAG = true;
                    if (isMp3(file.getName())) {
                        Log.d("走进了外面",file.getName());
                        Cursor c =existSong.query("musicFolder", new String[]{"songName"}, "songName=?", new String[]{file.getName()}, null, null, null);
                        if(c.getCount()>0){

                        }else {
                            ContentValues values = new ContentValues();
                            values.put("folderName", file.getParentFile().getName());
                            values.put("folderPath", file.getParent());
                            values.put("songName", file.getName());
                            values.put("songPath", file.getPath());
                            existSong.insert("musicFolder", null, values);
                            values.clear();
                            songAdapter.updateSongList(existSong);
                            listView.invalidate();
                        }
                    }
                }
            }
            }
    }


    public boolean isMusicFile(String directoryName) {
        if (directoryName.contains("music") || directoryName.contains("download") || directoryName.equalsIgnoreCase("music")||directoryName.equalsIgnoreCase("download")
            || directoryName.equalsIgnoreCase("android")|| directoryName.contains("kugou")|| directoryName.equalsIgnoreCase("netease")) {
            return true;
        }else {
            return false;
        }
    }

    public boolean isMp3(String fileName) {
            String type = fileName.substring(fileName.lastIndexOf(".")+1, fileName.length());
            if (type.equals("mp3") || type.equals("aac") || type.equals("wav") || type.equals("acc") || type.equals("flac")) {
                return true;
            }else {
                return false;
            }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        filterData(s.toString());
    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    private void filterData(String s) {
        SQLiteDatabase newDatabase=openOrCreateDatabase("filter.db",MODE_PRIVATE,null);
        newDatabase.execSQL("create table if not exists musicFolder(_id integer primary key autoincrement,folderName text,folderPath text,songName text,songPath text)");
        Cursor c=newDatabase.rawQuery("select * from musicFolder",null);
            if(c!=null){
                newDatabase.delete("musicFolder","_id>?",new String[]{"0"});
            }
        if (TextUtils.isEmpty(s)) {
           songAdapter.updateSongList(existSong);
        } else {
            if(existSong!=null) {
                Cursor cursor = existSong.rawQuery("select * from musicFolder", null);
                if (cursor != null){
                    while(cursor.moveToNext()){
                      String songName=cursor.getString(cursor.getColumnIndex("songName"));
                        if (songName.contains(s)){
                            String songPath=cursor.getString(cursor.getColumnIndex("songPath"));
                            String folderName=cursor.getString(cursor.getColumnIndex("folderName"));
                            String folderPath=cursor.getString(cursor.getColumnIndex("folderPath"));
                            ContentValues values=new ContentValues();
                            values.put("folderName",folderName);
                            values.put("folderPath",folderPath);
                            values.put("songName",songName);
                            values.put("songPath",songPath);
                            newDatabase.insert("musicFolder",null,values);
                            values.clear();
                        }
                    }
                }

            }
            songAdapter.updateSongList(newDatabase);
        }
    }

    @Override
    public void onGroupExpand(int groupPosition) {
        for (int i = 0, count = listView
                .getExpandableListAdapter().getGroupCount(); i < count; i++) {
            if (groupPosition != i) {// 关闭其他分组
                listView.collapseGroup(i);
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.ib_songList_menu:
             showPopmenu(menuButton);
                     break;

        }
    }
    public void showPopmenu(View v){
        PopupMenu popupMenu=new PopupMenu(this,v);
        popupMenu.getMenuInflater().inflate(R.menu.main_menu,popupMenu.getMenu());
        popupMenu.show();
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
              switch (item.getItemId()) {
                  case R.id.scanDirectory:
                      Intent intent=new Intent(SongsList.this, BrowseFileActivity.class);
                      startActivity(intent);
                      break;
                  case R.id.onlineSearch:
                      startActivity(new Intent(SongsList.this, SearchActivity.class));
                      break;
              }
                return false;
            }
        });

    }

    @Override
    protected void onStop() {
//        existSong.execSQL("drop table musicFolder");
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        existSong.close();
        super.onDestroy();
    }
}
