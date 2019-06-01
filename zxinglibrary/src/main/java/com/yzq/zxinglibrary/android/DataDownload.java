package com.yzq.zxinglibrary.android;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;

import static android.content.ContentValues.TAG;

/**
 * Created by Administrator on 2019/6/1.
 */

public class DataDownload {
    private Message msg=Message.obtain();

    public void scanResult(final Handler handler , final String ISBN){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String url="http://api.douban.com/book/subject/isbn/"+ISBN+"?apikey=0df993c66c0c636e29ecbb5344252a4a";
                    Log.d(TAG,url);
                    //下载图书
                    InputStream inputStream=download(url);
                    inputStream.close();
                    handler.sendMessage(msg);
                }
                catch (IOException e){
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private InputStream download(String url_s){
        InputStream inputStream=null;
        try {
            URL url = new URL(url_s);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setConnectTimeout(3000);
            connection.connect();


            if (connection.getResponseCode() == 200) {  //200表示连接成功
                msg.what=1;
                inputStream = connection.getInputStream();
                if(inputStream!=null){
                    InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                    BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                    StringBuffer bf = new StringBuffer();
                    String temp;
                    while ((temp = bufferedReader.readLine()) != null) {
                        bf.append(temp);
                    }
                    if(!bf.toString().contains("title") && !bf.toString().contains("author")){
                        msg.what=0;
                    }
                }
                else {
                    msg.what=0;
                }

            }

        }catch (IOException e){
            e.printStackTrace();
        }
        finally {
            return inputStream;
        }
    }



}


