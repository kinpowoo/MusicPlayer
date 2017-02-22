package com.example.anzhuo.normalpractice;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.example.anzhuo.normalpractice.javabeans.SearchResult;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by Administrator on 2017/2/16 0016.
 */

public class SearchActivity extends Activity implements View.OnClickListener,TextWatcher{
    private ClearEditText songName;
    private TextView searchButton;
    private ListView listView;
    List<SearchResult.ResultBean.SongsBean> list;
    SearchAdapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_activity);
        songName= (ClearEditText) findViewById(R.id.search_tab);
        searchButton= (TextView) findViewById(R.id.search_confirm);
        listView= (ListView) findViewById(R.id.listview);
        songName.addTextChangedListener(this);
        searchButton.setOnClickListener(this);

        list=new ArrayList<>();
        adapter=new SearchAdapter(this,list);
        listView.setAdapter(adapter);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.search_confirm:
                String keyword=songName.getText().toString();
                new MyAsyncTask().execute(keyword);
                break;
        }
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void afterTextChanged(Editable editable) {

    }

    class MyAsyncTask extends AsyncTask<String,Void,List<SearchResult.ResultBean.SongsBean>>{


        @Override
        protected List<SearchResult.ResultBean.SongsBean> doInBackground(String... strings) {
            return CurlPackage.curlData(strings[0]);
        }

        @Override
        protected void onPostExecute(List<SearchResult.ResultBean.SongsBean> data) {
            super.onPostExecute(data);
            if(list.size()>0){
                list.clear();
            }
            list.addAll(data);
            adapter.notifyDataSetChanged();
        }
    }

}
