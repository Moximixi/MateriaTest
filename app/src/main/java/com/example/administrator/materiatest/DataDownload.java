package com.example.administrator.materiatest;

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
                Book book=null;
                try {
                    String url="http://api.douban.com/book/subject/isbn/"+ISBN+"?apikey=0df993c66c0c636e29ecbb5344252a4a";
                    Log.d(TAG,url);
                    InputStream inputStream=download(url);
                    //获取图书信息
                    if(inputStream!=null) {
                        InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                        StringBuffer bf = new StringBuffer();
                        String temp;
                        while ((temp = bufferedReader.readLine()) != null) {
                            bf.append(temp);
                        }
                        Log.d(TAG,bf.toString());
                        book = parseXMLWithPull(bf.toString());

                        //String b=bf.toString();
                       // b=b.substring(b.indexOf("img1"),b.indexOf("jpg")+3);
                        //Log.d(TAG,"++++++++++ "+b);

                        if(bf.indexOf("http://img")!=-1 && bf.indexOf("http://img")<bf.indexOf("jpg") + 3)
                        {
                            StringBuffer bitmap_website = new StringBuffer().append(bf.substring(bf.indexOf("http://img"), bf.indexOf("jpg") + 3));


                            inputStream.close();

                            //下载图片
                            InputStream bitmapStream = download(bitmap_website.toString());
                            if (bitmapStream != null) {
                                Bitmap bitmap = BitmapFactory.decodeStream(bitmapStream);
                                book.setBitmap(bitmap);
                            }
                            bitmapStream.close();
                        }

                    }

                    book.getBook_Shelf().add("默认书架");
                    msg.obj = book;
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
            connection.setConnectTimeout(5000);
            connection.connect();

            if (connection.getResponseCode() == 200) {  //200表示连接成功
                inputStream = connection.getInputStream();
                msg.what=1;
            }
            else if(connection.getResponseCode()==408){ //请求超时
                msg.what=0;
            }
        }catch (IOException e){
            e.printStackTrace();
        }
        finally {
            return inputStream;
        }
    }

    //处理返回来的xml信息
    private Book parseXMLWithPull(String xmlData){
        Book book=null;
        try {
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            XmlPullParser xmlPullParser = factory.newPullParser();
            //设置输入的内容
            xmlPullParser.setInput(new StringReader(xmlData));
            //获取当前解析事件，返回的是数字
            int eventType = xmlPullParser.getEventType();
            //保存内容
            book=new Book();
            while (eventType != (XmlPullParser.END_DOCUMENT)){
                switch (eventType){
                    //开始解析XML
                    case XmlPullParser.START_TAG:{
                        //nextText()用于获取结点内的具体内容
                        if(xmlPullParser.getName().equals("db:attribute")) {  //判断是否是attribute标签，若是再判断是何种属性
                            if (xmlPullParser.getAttributeValue(0).equals("isbn13")){
                                book.setISBN(xmlPullParser.nextText());
                                Log.d(TAG,book.getISBN());
                            }
                            else if (xmlPullParser.getAttributeValue(0).equals("title"))
                                book.setTitle(xmlPullParser.nextText());
                            else if (xmlPullParser.getAttributeValue(0).equals("author"))
                                book.setAuthor(xmlPullParser.nextText());
                            else if (xmlPullParser.getAttributeValue(0).equals("translator"))
                                book.setTranslator(xmlPullParser.nextText());
                            else if (xmlPullParser.getAttributeValue(0).equals("author"))
                                book.setAuthor(xmlPullParser.nextText());
                            else if (xmlPullParser.getAttributeValue(0).equals("pubdate")){
                                String s=xmlPullParser.nextText();
                                book.setTime_Year(s.substring(0,4));
                                if(s.indexOf("-",5)==-1){
                                    s=s.substring(5,s.length());
                                }
                                else {
                                    s=s.substring(5, s.indexOf("-", 5));
                                }
                                if (s.length()==1){
                                    s="0"+s;
                                }
                                book.setTime_Month(s);
                            }
                        }
                        else if(xmlPullParser.getName().equals("id")){
                            book.setWebsite(xmlPullParser.nextText());
                        }


                    } break;
                    //结束解析
                    case XmlPullParser.END_TAG:{
                    } break;
                    default: break;
                }
                //下一个
                eventType = xmlPullParser.next();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            return book;
        }
    }

}


