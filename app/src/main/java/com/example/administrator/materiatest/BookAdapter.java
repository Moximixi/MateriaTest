package com.example.administrator.materiatest;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Administrator on 2019/6/4.
 */

public class BookAdapter extends ArrayAdapter<Book> {
    private int resourceId;
    public BookAdapter(Context context, int textViewResourceId,
                       List<Book> objects) {
        super(context, textViewResourceId, objects);
        resourceId=textViewResourceId;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Book book=getItem(position);

        View view=LayoutInflater.from(getContext()).inflate(resourceId,parent,false);

        ImageView bookImage=(ImageView)view.findViewById(R.id.book_image);
        bookImage.setImageBitmap(book.getBitmap());

        TextView bookName=(TextView)view.findViewById(R.id.book_name);
        bookName.setText(book.getTitle());

        TextView authorName=(TextView)view.findViewById(R.id.author_name);
        authorName.setText(book.getAuthor()+(book.getAuthor().equals("")?"":", ")+book.getPublisher());

        TextView publishTime=(TextView)view.findViewById(R.id.publish_time);
        if(!book.getTime_Year().equals(""))
            publishTime.setText(book.getTime_Year()+"-"+book.getTime_Month());

        return view;
       // return super.getView(position, convertView, parent);
    }
}
