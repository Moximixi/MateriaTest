package com.example.administrator.materiatest;

import android.graphics.Bitmap;
import android.graphics.Matrix;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Administrator on 2019/6/1.
 */

public class Book implements Serializable{
    public Book(){}
    //自定义的一个构造函数，主要是用在主界面显示书上
    //显示四个属性,标题，作者，出版时间，还有一张图片
    //图片按照固定的长宽格式化。80*120
    public Book(String mtitle,String mauthor,String mpublisher,
                String myear,String mmonth,Bitmap bitmap){
        title=mtitle;
        author=mauthor;
        publisher=mpublisher;
        time_Month=mmonth;
        time_Year=myear;
        //将所有的图片做一个缩放操作,保证所有的图片加载后都是80和120
        Matrix matrix = new Matrix();
        matrix.postScale(80.0f/bitmap.getWidth(), 120.0f/bitmap.getHeight());
        mBitmap=Bitmap.createBitmap(bitmap,0,0,bitmap.getWidth(),bitmap.getHeight(),
                matrix,true);

    }
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getTranslator() {
        return translator;
    }

    public void setTranslator(String translator) {
        this.translator = translator;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public String getTime_Year() {
        return time_Year;
    }

    public void setTime_Year(String time_Year) {
        this.time_Year = time_Year;
    }

    public String getTime_Month() {
        return time_Month;
    }

    public void setTime_Month(String time_Month) {
        this.time_Month = time_Month;
    }

    public String getISBN() {
        return ISBN;
    }

    public void setISBN(String ISBN) {
        this.ISBN = ISBN;
    }

    public String getRead_State() {
        return read_State;
    }

    public void setRead_State(String read_State) {
        this.read_State = read_State;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public static ArrayList<String> getBook_Shelf() {
        return book_Shelf;
    }

    public static ArrayList<String> getBook_Tag() {
        return book_Tag;
    }

    public Bitmap getBitmap() {
        return mBitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        mBitmap = bitmap;
    }

    private String title="";
    private String author="";
    private String translator="";
    private String publisher="";
    private String time_Year="";
    private String time_Month="";
    private String ISBN="";

    private String read_State="";
    private static ArrayList<String> book_Shelf =new ArrayList<String>();
    private String note="";
    private static ArrayList<String> book_Tag=new ArrayList<String>();
    private String website="";

    private Bitmap mBitmap=null;

}
