package com.example.anzhuo.normalpractice;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.example.anzhuo.normalpractice.javabeans.SongAddress;


import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.zip.GZIPInputStream;

import okhttp3.Cache;
import okhttp3.CacheControl;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
/**
 * Created by wm on 2016/4/10.
 */
public class HttpUtils {
    public static final OkHttpClient mOkHttpClient = new OkHttpClient();


    public static void getOut(final String url) {
        try {
            Request request = new Request.Builder()
                    .url(url)
                    .build();
            Response response = mOkHttpClient.newCall(request).execute();
            if (response.isSuccessful()) {

                FileOutputStream fo = new FileOutputStream("/storage/emulated/0/" + "gedangein" + ".json");
                byte[] c = new byte[1024];
                while (response.body().source().read(c) != -1) {
                    fo.write(c);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static Bitmap getBitmapStream(Context context, String url, boolean forceCache) {
        try {
            File sdcache = context.getExternalCacheDir();
            //File cacheFile = new File(context.getCacheDir(), "[缓存目录]");
            Cache cache = new Cache(sdcache.getAbsoluteFile(), 1024 * 1024 * 30); //30Mb


            Request.Builder builder = new Request.Builder()
                    .url(url);
            if (forceCache) {
                builder.cacheControl(CacheControl.FORCE_CACHE);
            }
            Request request = builder.build();
            Response response = mOkHttpClient.newCall(request).execute();
            if (response.isSuccessful()) {
                return _decodeBitmapFromStream(response.body().byteStream(), 160, 160);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }



    private static int _calculateInSampleSize(BitmapFactory.Options options,
                                              int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int heightRatio = Math.round((float) height
                    / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);

            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }

        return inSampleSize;
    }

    /**
     * URL编码
     *
     * @param url
     * @return
     */
    public static String urlEncode(String url) {
        try {
            url = java.net.URLEncoder.encode(url, "UTF-8");
            url = url.replaceAll("%2F", "/");
            url = url.replaceAll("%3A", ":");
            url = url.replaceAll("\\+", "%20");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return url;
    }

    public static Bitmap _decodeBitmapFromStream(InputStream inputStream,
                                                 int reqWidth, int reqHeight) {
        byte[] byteArr = new byte[0];
        byte[] buffer = new byte[1024];
        int len;
        int count = 0;

        try {
            while ((len = inputStream.read(buffer)) > -1) {
                if (len != 0) {
                    if (count + len > byteArr.length) {
                        byte[] newbuf = new byte[(count + len) * 2];
                        System.arraycopy(byteArr, 0, newbuf, 0, count);
                        byteArr = newbuf;
                    }

                    System.arraycopy(buffer, 0, byteArr, count, len);
                    count += len;
                }
            }

            final BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeByteArray(byteArr, 0, count, options);

            options.inSampleSize = _calculateInSampleSize(options, reqWidth,
                    reqHeight);
            options.inPurgeable = false;
            options.inInputShareable = true;
            options.inJustDecodeBounds = false;
            options.inPreferredConfig = Bitmap.Config.RGB_565;

            return BitmapFactory.decodeByteArray(byteArr, 0, count, options);

        } catch (Exception e) {
            // close_print_exception

            return null;
        }
    }


    public static String getResposeString(String action1) {
        try {
            Request request = new Request.Builder()
                    .url(action1)
                    .build();
            Response response = mOkHttpClient.newCall(request).execute();
            if (response.isSuccessful()) {
                String c = response.body().string();
                Log.e("billboard", c);
                return c;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

//       mOkHttpClient.setCookieHandler(new CookieManager(
//                new PersistentCookieStore(getContext().getApplicationContext()),
//                CookiePolicy.ACCEPT_ALL));

        return null;
    }

    public static JSONObject getResposeJsonObject(String action1, Context context, boolean forceCache) {
        try {
            Log.e("action-cache", action1);
            File sdcache = context.getCacheDir();
            //File cacheFile = new File(context.getCacheDir(), "[缓存目录]");
            Cache cache = new Cache(sdcache.getAbsoluteFile(), 1024 * 1024 * 30); //30Mb
            Request.Builder builder = new Request.Builder()
                    .url(action1);
            if (forceCache) {
                builder.cacheControl(CacheControl.FORCE_CACHE);
            }
            Request request = builder.build();
            Response response = mOkHttpClient.newCall(request).execute();
            if (response.isSuccessful()) {
                String c = response.body().string();
                Log.e("cache", c);
                JSONObject object= (JSONObject) JSON.parse(c);
                return object;

            }

        } catch (Exception e) {
            e.printStackTrace();
        }

//       mOkHttpClient.setCookieHandler(new CookieManager(
//                new PersistentCookieStore(getContext().getApplicationContext()),
//                CookiePolicy.ACCEPT_ALL));

        return null;
    }


    public static JSONObject getResposeJsonObject(String action1) {
        try {
            Request request = new Request.Builder()
                    .url(action1)
//                    .addHeader("Referer","http://music.163.com/")
//                    .addHeader("Cookie", "appver=1.5.0.75771")
                    .build();
            Response response = mOkHttpClient.newCall(request).execute();
            if (response.isSuccessful()) {
                String c = response.body().string();
//                FileOutputStream fileOutputStream = new FileOutputStream("/sdcard/" + System.currentTimeMillis() + ".txt");
//                fileOutputStream.write(c.getBytes());
//                fileOutputStream.close();
                JSONObject object= (JSONObject) JSON.parse(c);
                return object;

            }

        } catch (Exception e) {
            e.printStackTrace();
        }

//       mOkHttpClient.setCookieHandler(new CookieManager(
//                new PersistentCookieStore(getContext().getApplicationContext()),
//                CookiePolicy.ACCEPT_ALL));

        return null;
    }

    public static void downMp3(final String url, final String name, final Handler handler) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Request request = new Request.Builder()
                            .url(url)
                            .build();
                    Response response = mOkHttpClient.newCall(request).execute();
                    if (response.isSuccessful()) {
                        String path=MainApplication.cachePath +File.separator+ name + ".mp3";
                        File file=new File(path);
                        if(file.exists()){
                            file.delete();
                        }
                        FileOutputStream fo = new FileOutputStream(file.getPath());
                        byte[] c = new byte[1024];
                        while (response.body().source().read(c) != -1) {
                            fo.write(c);
                        }
                        handler.sendEmptyMessage(1);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();


    }

    public static void postUrl(Context context, String j) {
        try {
            String action = "https://music.163.com/weapi/login/";
            RequestBody formBody =new FormBody.Builder()
                    //         .add("",)
                    .build();
            Log.e("post", "p");
            Request request = new Request.Builder()
                    .url(action)
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .header("Host", "music.163.com")
                    .header("Cookie", "appver=1.5.0.75771")
                    .header("Referer", "http://music.163.com/")
                    .header("Connection", "keep-alive")
                    .header("Accept-Encoding", "gzip,deflate")
                    .header("Accept", "*/*")
                    .header("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_9_2) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/33.0.1750.152 Safari/537.36")
                    .post(formBody)
                    .build();


            Response response = mOkHttpClient.newCall(request).execute();
            if (response.isSuccessful()) {
                Log.e("respose", response.body().string());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static SongAddress postNetease(int id) {
        try {
            String action = "http://music.163.com/weapi/song/enhance/player/url?csrf_token=";
            String []params=AES.getParams(id);

            String param=params[0];
            if(param.contains("/")){
               param=param.replace("/","%2F");
            }
            if(param.contains("+")){
                param=param.replace("+","%2B");
            }
            if(param.contains("=")){
                param=param.replace("=","%3D");
            }
            Log.e("post",param+"&encSecKey="+params[1]);
            String json = "params="+param+"&encSecKey="+params[1];
            RequestBody requestBody = RequestBody.create(MediaType.parse("application/x-www-form-urlencoded"),json);
            Log.e("post", "p");
            Request request = new Request.Builder()
                    .url(action)
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .header("Host", "music.163.com")
                    .header("Cookie","appver=1.5.0.75771")
//                    .header("Cookie", "_ntes_nnid=12dd97f23e25256a637e430a103e7946,1483775626317; _ntes_nuid=12dd97f23e25256a637e430a103e7946; usertrack=c+5+hlii6oAvjnlmBRpnAg==; JSESSIONID-WYYY=HequyPvM%2FTJgNhrTZJG229GNW61bGcJYWGhIGrM8IOsRDRhAOGj8cM5xQbwebNhEjGbhdybMzlKp%2BNklsqcjMAF%5CYXmj1FF3wPWob114POYycCUrQ8zFlr4PrIy4A3yWHFavxe89yA9zZvfa8KPR3JqS7MCRrk6v8jNQrblYV8yUoT4k%3A1487616860903; _iuqxldmzr_=32; __utma=94650624.921500120.1483775628.1487608101.1487615517.20; __utmb=94650624.8.10.1487615517; __utmc=94650624; __utmz=94650624.1486546268.12.4.utmcsr=baidu|utmccn=(organic)|utmcmd=organic; playerid=60866064")
                    .header("Referer", "http://music.163.com/")
                    .header("Connection", "keep-alive")
                    .header("Accept-Encoding", "gzip,deflate")
                    .header("Accept", "*/*")
                    .header("Accept-Language","zh-CN,zh;q=0.8")
                    .header("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_9_2) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/33.0.1750.152 Safari/537.36")
                    .post(requestBody)
                    .build();

            Response response = mOkHttpClient.newCall(request).execute();
            if (response.isSuccessful()) {
                InputStream gzipInputStream = new GZIPInputStream(response.body().byteStream());
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                int length = -1;

                byte[] cache = new byte[1024];

                while ((length = gzipInputStream.read(cache)) != -1) {
                    byteArrayOutputStream.write(cache, 0, length);
                }
                byte[] lens = byteArrayOutputStream.toByteArray();

                String result =new String(lens);

                Log.e("post",result);
                byteArrayOutputStream.close();
                gzipInputStream.close();
                return JSON.parseObject(result, SongAddress.class);
            }else{
                Log.e("post","get nothing");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


}