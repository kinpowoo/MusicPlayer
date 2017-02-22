package com.example.anzhuo.normalpractice;

import android.util.*;
import android.util.Base64;

import com.alibaba.fastjson.JSON;
import com.example.anzhuo.normalpractice.javabeans.People;
import com.example.anzhuo.normalpractice.javabeans.SearchResult;
import com.example.anzhuo.normalpractice.javabeans.SingleSongInfo;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.security.spec.AlgorithmParameterSpec;
import java.util.List;
import java.util.zip.GZIPInputStream;

import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * Created by Administrator on 2017/1/14 0014.
 */

public class CurlPackage {


    public static void beanToJson() {
        People p = new People();
        String json = JsonUtils.beanToJson(p);
        Log.d("result", json);
    }

    public static List<SearchResult.ResultBean.SongsBean> curlData(String keyWord) {
        String url = "http://music.163.com/api/search/get/";

        final String NETEASE_API_URL = "http://music.163.com/weapi";
        String params = "{\"s\":" + "\"" + keyWord + "\"" + "," + "\"type\":1,\"limit\":30}";
        String param = "s=" + keyWord + "&type=1";
        OkHttpClient client = new OkHttpClient();
        Request.Builder builder = new Request.Builder();


        builder.addHeader("Accept", "*/*");
        builder.addHeader("Accept-Encoding", "gzip,deflate,sdch");
        builder.addHeader("Accept-Language", "zh-CN,zh;q=0.8,gl;q=0.6,zh-TW;q=0.4");
        builder.addHeader("Connection", "keep-alive");
        builder.addHeader("Referer", "http://music.163.com/search/");

        builder.addHeader("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_9_2) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/33.0.1750.152 Safari/537.36");
        builder.addHeader("Cookie", "appver=1.5.0.75771");

        RequestBody requestBody = RequestBody.create(MediaType.parse("application/x-www-form-urlencoded"), param);
        builder.post(requestBody);
        builder.url(url);


        Call call = client.newCall(builder.build());
        Response response = null;
        try {
            response = call.execute();
            GZIPInputStream gzipInputStream = new GZIPInputStream(response.body().byteStream());
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            int length = -1;

            byte[] cache = new byte[1024];

            while ((length = gzipInputStream.read(cache)) != -1) {
                byteArrayOutputStream.write(cache, 0, length);
            }
            String result = new String(byteArrayOutputStream.toByteArray());
            SearchResult resultBean = JSON.parseObject(result, SearchResult.class);
            Log.d("result", resultBean.getResult().getSongs().get(0).getName());
            byteArrayOutputStream.close();
            gzipInputStream.close();

            return resultBean.getResult().getSongs();
//             Log.d("result",result);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static SingleSongInfo getSongDetail(int songId) {
        String detail_url = "http://music.163.com/api/song/detail/?id=" + songId + "&ids=[" + songId + "]";
        OkHttpClient client = new OkHttpClient();
        Request builder = new Request.Builder()
                .url(detail_url)
                .header("Cookie", "appver=1.5.75771")
                .header("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_9_2) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/33.0.1750.152 Safari/537.36")
                .header("Referer", "http://music.163.com/")
                .build();

        Call call = client.newCall(builder);
        Response response = null;

        try {
            response = call.execute();
            InputStream gzipInputStream = new BufferedInputStream(response.body().byteStream());
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            int length = -1;

            byte[] cache = new byte[1024];

            while ((length = gzipInputStream.read(cache)) != -1) {
                byteArrayOutputStream.write(cache, 0, length);
            }
            String result = new String(byteArrayOutputStream.toByteArray());
            SingleSongInfo singleSong = JSON.parseObject(result, SingleSongInfo.class);
            Log.d("result", singleSong.getSongs().get(0) + "iiiii");
            byteArrayOutputStream.close();
            gzipInputStream.close();
            return singleSong;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }






    public static String getSong(int songId) {

        String detail_url = "http://music.163.com/song/enhance/player/url?csrf_token=";
        OkHttpClient client = new OkHttpClient();
        String []params=AES.getParams(songId);
        RequestBody body=RequestBody.create(MediaType.parse("application/x-www-form-urlencoded"),params.toString());
        Request builder = new Request.Builder()
                .url(detail_url)
                .header("Cookie", "appver=1.5.75771")
                .header("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_9_2) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/33.0.1750.152 Safari/537.36")
                .header("Referer", "http://music.163.com/")
                .post(body)
                .build();
        Call call = client.newCall(builder);
        Response response = null;
        try {
            response = call.execute();
            InputStream gzipInputStream = new BufferedInputStream(response.body().byteStream());
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            int length = -1;

            byte[] cache = new byte[1024];

            while ((length = gzipInputStream.read(cache)) != -1) {
                byteArrayOutputStream.write(cache, 0, length);
            }
            String result = new String(byteArrayOutputStream.toByteArray());

            Log.d("test",result);
            byteArrayOutputStream.close();
            gzipInputStream.close();
            return result;
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

}






