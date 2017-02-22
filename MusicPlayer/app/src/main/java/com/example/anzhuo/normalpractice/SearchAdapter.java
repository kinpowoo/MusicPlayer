package com.example.anzhuo.normalpractice;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.anzhuo.normalpractice.javabeans.SearchResult;

import java.util.List;

/**
 * Created by Administrator on 2017/2/16 0016.
 */

public class SearchAdapter extends BaseAdapter {
    private Context context;
    private List<SearchResult.ResultBean.SongsBean> list;

    public SearchAdapter(Context context,List<SearchResult.ResultBean.SongsBean> list){
        this.context=context;
        this.list=list;
    }

    @Override
    public int getCount() {
        return list==null?0:list.size();
    }

    @Override
    public Object getItem(int i) {
        return list.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHold viewHold;
        if(view==null){
            view= LayoutInflater.from(context).inflate(R.layout.song_info_activity,null);
            viewHold=new ViewHold();
            viewHold.songName= (TextView) view.findViewById(R.id.tv_songItem_songName);
            viewHold.singer= (TextView) view.findViewById(R.id.tv_songItem_singer);
            viewHold.album= (TextView) view.findViewById(R.id.tv_songItem_album);
            view.setTag(viewHold);
        }
        viewHold= (ViewHold) view.getTag();
        final SearchResult.ResultBean.SongsBean song=list.get(i);
        viewHold.songName.setText(song.getName());
        viewHold.singer.setText(song.getArtists().get(0).getName()+"-");
        viewHold.album.setText(song.getAlbum().getName());

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              context.startActivity(new Intent(context,SingleSongActivity.class)
                      .putExtra("songId",song.getId()));
              // String url = "http://m"+((int)(Math.random()*10)/4+1)+".music.126.net/%s/"+song.getId()+".mp3" % (, encrypted_id(song_dfsId), song_dfsId)
            }
        });

        return view;
    }

    class ViewHold{
        TextView songName,singer,album;
    }
}
