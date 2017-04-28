package com.example.anzhuo.normalpractice.songlistpackage;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.example.anzhuo.normalpractice.MainActivity;
import com.example.anzhuo.normalpractice.MediaPlayerManager;
import com.example.anzhuo.normalpractice.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by anzhuo on 2016/8/16.
 */
public class SongAdapter extends BaseExpandableListAdapter {
    Context context;
    SQLiteDatabase existSong;
    ImageButton deleteButton;
    List<String> groupNameList=new ArrayList<String>();
    List<String> songNameList=new ArrayList<String>();

    Animation animation;
    float startX;
    float leaveX;
    View view;
    public SongAdapter(Context context,SQLiteDatabase existSong){
        this.context=context;
        this.existSong=existSong;
        animation=AnimationUtils.loadAnimation(context,R.anim.animation);
        getGroupList();
    }

    public void updateSongList(SQLiteDatabase database){
        this.existSong=database;
        getGroupList();
        notifyDataSetChanged();
    }

    public  void getGroupList(){
        groupNameList.clear();
        Cursor cursor= existSong.query("musicFolder", new String[]{"folderName"}, "_id>?", new String[]{"0"}, "folderName", null, "_id");
        if(cursor!=null){
            while (cursor.moveToNext()){
                String groupName=new String(cursor.getString(cursor.getColumnIndex("folderName")));
                groupNameList.add(groupName);
            }
        }
    }
    public ArrayList<String> getSongList(String groupName){
        songNameList.clear();
        ArrayList<String> snl;
        Cursor cursor = existSong.query("musicFolder", new String[]{"songName"}, "folderName=?", new String[]{groupName}, null, null,null);
        if(cursor!=null){
           while (cursor.moveToNext()){
               songNameList.add(cursor.getString(0));
           }
        }
        snl= (ArrayList<String>) songNameList;
        return snl;
    }

    public String getSongPath(int groupPosition, int childPosition){
        ArrayList<String> songPathList=null;
        if(groupNameList!=null) {
            String locateRaw = groupNameList.get(groupPosition);
           songPathList= getSongPathList(locateRaw);
        }
        return  songPathList.get(childPosition);
    }

    public ArrayList<String> getSongPathList(String groupName){
       ArrayList<String> returnSongList=new ArrayList<String>();
       Cursor cursor = existSong.query("musicFolder", new String[]{"songPath"}, "folderName=?", new String[]{groupName}, null, null,null);
       if(cursor!=null){
           while (cursor.moveToNext()){
               returnSongList.add(cursor.getString(0));
           }
       }
       return returnSongList;
   }

    @Override
    public int getGroupCount() {
//        getGroupList();
     return   groupNameList==null?0:groupNameList.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
//        getGroupList();
        if(groupNameList!=null) {
            String locateRaw = groupNameList.get(groupPosition);
            getSongList(locateRaw);
        }
        if (songNameList!= null) {
                return songNameList.size();
            } else {
                return 0;
            }

    }

    @Override
    public String getGroup(int groupPosition) {
//        getGroupList();
       if(groupNameList!=null){
         return groupNameList.get(groupPosition);
       }else {
           return null;
       }
    }

    @Override
    public String getChild(int groupPosition, int childPosition) {
//        getGroupList();
        if(groupNameList!=null) {
            String locateRaw = groupNameList.get(groupPosition);
            getSongList(locateRaw);
        }
         return  songNameList.get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getGroupView(final int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {

        ViewHolder1 viewHolder1;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.parent_item, null);
            viewHolder1 = new ViewHolder1();
            viewHolder1.fileName = (TextView) convertView.findViewById(R.id.tv_fileName);
            viewHolder1.fold = (ImageView) convertView.findViewById(R.id.ib_fold_unfold);
            convertView.setTag(viewHolder1);
        }
        viewHolder1 = (ViewHolder1) convertView.getTag();
        viewHolder1.fileName.setText(getGroup(groupPosition));
        if (isExpanded) {
            viewHolder1.fold.setImageResource(R.drawable.unfold);
        } else {
            viewHolder1.fold.setImageResource(R.drawable.fold);
        }
//        convertView.invalidate();
        return convertView;
    }

    @Override
    public View getChildView(final int groupPosition, final int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        ViewHolder2 viewHolder2;
        if(convertView==null){
            convertView=LayoutInflater.from(context).inflate(R.layout.child_item,null);
            viewHolder2=new ViewHolder2();
            viewHolder2.songName= (TextView) convertView.findViewById(R.id.tv_songName);
            viewHolder2.delete= (ImageButton) convertView.findViewById(R.id.ib_delete);
            convertView.setTag(viewHolder2);
        }else {
            viewHolder2= (ViewHolder2) convertView.getTag();
        }
        viewHolder2.songName.setText(getChild(groupPosition,childPosition));

        viewHolder2.songName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("tag","have been clicked");
                Bundle bundle=new Bundle();
                bundle.putInt("position",childPosition);
                bundle.putString("songName",getChild(groupPosition,childPosition));
                bundle.putStringArrayList("songPathList",getSongPathList(getGroup(groupPosition)));
                bundle.putStringArrayList("songNameList",getSongList(getGroup(groupPosition)));
                context.startActivity(new Intent(context, MainActivity.class).
                        putExtras(bundle));
            }
        });

        viewHolder2.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteButton.setVisibility(View.GONE);
                deleteItem(view,groupPosition,childPosition);
            }
        });
//        convertView.invalidate();
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
    public void deleteItem(View v,final int groupPosition,final int childPosition){
       v.setAnimation(animation);
       animation.setAnimationListener(new Animation.AnimationListener() {
           @Override
           public void onAnimationStart(Animation animation) {
           }
           @Override
           public void onAnimationEnd(Animation animation) {
               existSong.delete("musicFolder","songName=?",new String[]{getChild(groupPosition,childPosition)});
           }
           @Override
           public void onAnimationRepeat(Animation animation) {
           }
       });

    }
    class ViewHolder1{
        TextView fileName;
        ImageView fold;
    }
    class ViewHolder2{
        TextView songName;
        ImageButton delete;
    }

}
