package com.example.anzhuo.normalpractice.browsesystemfiles;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.anzhuo.normalpractice.R;
import com.example.anzhuo.normalpractice.songlistpackage.SongsList;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static com.example.anzhuo.normalpractice.R.id.cb_project_item_choose;

/**
 * Created by anzhuo on 2016/7/28.
 */
public class BrowseFileActivity extends Activity implements AdapterView.OnItemClickListener,View.OnClickListener{
  private ListView listView;
    ImageButton backButton;
    ImageButton chooseButton;
    TextView filePath;
    SQLiteDatabase saveDirectory;
    FileAdapter fileAdapter;
    ArrayList<String> sendDir;
    List<FileType>  database=new ArrayList<FileType>();
    String state= Environment.getExternalStorageState();
    File rootFile=Environment.getExternalStorageDirectory();
    String[] fileTypes={"xml","txt","lrc","pdf","zip","rar","pptx","apk","png","jpeg","jpg","gif","mp3","wav","flac","wmv","aac","mp4","3gp","rmvb","avi","wma","rm","flash"};
    int [] typeIcon={R.drawable.xml32,R.drawable.text,R.drawable.text,R.drawable.pdf,R.drawable.zip,R.drawable.zip,R.drawable.ppt,R.drawable.appicon,R.drawable.image,
            R.drawable.image,R.drawable.image,R.drawable.image,R.drawable.music,R.drawable.music,R.drawable.music,R.drawable.music,R.drawable.music,R.drawable.movies,
            R.drawable.movies,R.drawable.movies,R.drawable.movies,R.drawable.movies,R.drawable.movies,R.drawable.movies};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.browse_activity);
        saveDirectory=openOrCreateDatabase("directory.db",MODE_PRIVATE,null);
        saveDirectory.execSQL("create table if not exists directories(_id integer primary key autoincrement,dirPath text,isChecked integer)");
        if(saveDirectory!=null){
            Cursor c=saveDirectory.rawQuery("select * from directories",null);
            if(c!=null){
                saveDirectory.delete("directories","_id>0",null);
            }
        }
        backButton= (ImageButton) findViewById(R.id.ib_project_main_back);
        filePath= (TextView) findViewById(R.id.tv_project_main_path);
        listView= (ListView) findViewById(R.id.lv_project_main_list);
        chooseButton= (ImageButton) findViewById(R.id.ib_directory_choose);
        listView.setOnItemClickListener(this);
        fileAdapter=new FileAdapter(this,database,saveDirectory);
        sendDir=new ArrayList<String>();
        scanFile(rootFile);
        listView.setAdapter(fileAdapter);
        chooseButton.setOnClickListener(this);
        backButton.setOnClickListener(this);
    }


    public void scanFile(File sourceFile) {
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            filePath.setText(sourceFile.getPath() + "/");
            File[] singleFile = sourceFile.listFiles();
            if(sourceFile==null){
                Toast.makeText(BrowseFileActivity.this,"该文件夹为空",Toast.LENGTH_SHORT).show();
            }else {
                for (File file : singleFile) {
                    if (file.isDirectory()) {
                        ContentValues contentValues=new ContentValues();
                        contentValues.put("dirPath",file.getPath());
                        contentValues.put("isChecked",-1);
                        saveDirectory.insert("directories",null,contentValues);
                        contentValues.clear();
                        if(file!=null && file.list().length>0){
                            FileType fileType = new FileType();
                            fileType.setImage(R.drawable.folder_full);
                            fileType.setFileName(file.getName());
                            fileType.setFilePath(file.getPath());
                            fileType.setType(2);
                            database.add(fileType);
                        } else{
                            FileType fileType = new FileType();
                            fileType.setImage(R.drawable.folder);
                            fileType.setFileName(file.getName());
                            fileType.setFilePath(file.getPath());
                            database.add(fileType);
                        }
                        fileAdapter.notifyDataSetChanged();
                    }else{
                        String fileName = file.getName();
                        String fileParts = fileName.substring(fileName.lastIndexOf(".") + 1);
                       for(int i=0;i<fileTypes.length;i++){
                           if(fileParts.equals(fileTypes[i])){
                               ContentValues contentValues=new ContentValues();
                               contentValues.put("dirPath",file.getParentFile().getPath());
                               Log.d("addedfileParent",file.getParentFile().getPath());
                               contentValues.put("isChecked",-1);
                               saveDirectory.insert("directories",null,contentValues);
                               contentValues.clear();
                               FileType fileType=new FileType();
                               fileType.setImage(typeIcon[i]);
                               fileType.setFileName(fileName);
                               fileType.setType(0);
                               fileType.setFilePath(file.getParentFile().getPath());
                               database.add(fileType);
                               fileAdapter.notifyDataSetChanged();
                           }
                       }
                    }
                }
            }
        } else {
            Log.i("tag", "您没有权限");
        }
    }

    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        ImageView iv= (ImageView) view.findViewById(R.id.iv_project_item_img);
        TextView tv= (TextView) view.findViewById(R.id.tv_project_item_text);
        String fileName=tv.getText().toString();
        String tag= (String)iv.getTag();
        final String currentPath=filePath.getText().toString();
        File files=new File(currentPath);
        if(files!=null) {
            switch (tag) {
                case "fullFolder":
                    File[] childFile = files.listFiles();
                    for (File file : childFile) {
                        if (file.getName().equals(fileName)) {
                            saveDirectory.delete("directories","_id>?",new String[]{"0"});
                            database.clear();
                            scanFile(file);
                            listView.setOnItemClickListener(this);
                        }
                    }
                    break;
                case "emptyFolder":
                    Toast.makeText(this,"该文件夹为空", Toast.LENGTH_SHORT).show();
                    break;
                case "song":
                    Log.d("songname",fileName);
                    Uri uri = Uri.parse("file:///"+currentPath+File.separator+fileName);
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setDataAndType(uri, "audio/*");
                    startActivity(intent);
                default:
            }
        }else{
            Toast.makeText(this,"该文件夹为空",Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.ib_directory_choose:
                if(saveDirectory!=null){
                    Cursor c=saveDirectory.rawQuery("select dirPath,isChecked from directories",null);
                    if(c!=null) {
                        while (c.moveToNext()) {
                        int flag=c.getInt(c.getColumnIndex("isChecked"));
                        if(flag==1){
                            Log.d("addedCategories",c.getString(c.getColumnIndex("dirPath")));

                            String dir=c.getString(c.getColumnIndex("dirPath"));
                            sendDir.add(dir);
                        }
                        }
                        }
                }
                Intent intent=new Intent(BrowseFileActivity.this, SongsList.class);
                intent.putStringArrayListExtra("backDir",sendDir);
                startActivity(intent);
                finish();
                break;
            case R.id.ib_project_main_back:
                String currentPath=filePath.getText().toString();
                File file=new File(currentPath);
                if(file.exists()){
                    String parentPath=file.getParent();
                    File parentFile=new File(parentPath);
                    if(currentPath.equals("/storage/emulated/")){
                     Toast.makeText(this,"已是根目录！",Toast.LENGTH_SHORT).show();
                    }else {
                        saveDirectory.delete("directories", "_id>?", new String[]{"0"});
                        database.clear();
                        scanFile(parentFile);
                    }
                }
            break;
        }
    }

    @Override
    protected void onDestroy() {
        saveDirectory.close();
        super.onDestroy();
    }
}
