package com.yzq.zxinglibrary.android;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebViewFragment;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.zxing.Result;
import com.yzq.zxinglibrary.R;
import com.yzq.zxinglibrary.bean.ZxingConfig;
import com.yzq.zxinglibrary.camera.CameraManager;
import com.yzq.zxinglibrary.common.Constant;
import com.yzq.zxinglibrary.view.ViewfinderView;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

public class MultiCaptureActivity extends AppCompatActivity implements ListFragment.ListListener,CaptureFragment.CaptureListener{
    private TabLayout tablayout;
    private ViewPager viewPager;
    private Toolbar toolbar;
    //tab显示的三个切换页面的显示
    private String[] tabtitles = { "扫描", "已添加"};
    private static int len=0;
    private static Menu setting_menu;
    private static Boolean mViewPageStatus=false;
    private static Boolean flash_state=false;
    public ZxingConfig config;
    private CameraManager cameraManager;
    private CaptureActivityHandler handler;
    private CaptureFragment mCaptureFragment=null;
    private ListFragment mListFragment=null;
    private MyPagerAdapter myPagerAdapter;

    //数据库相关
    private SQLiteHelper myhelper;
    private SQLiteDatabase db;
    private Cursor cursor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multi_caputer);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("批量添加");
        toolbar.setNavigationIcon(R.drawable.img_cancel);
        tablayout = (TabLayout) findViewById(R.id.tablayout);
        viewPager = (ViewPager) findViewById(R.id.viewpager);

        myPagerAdapter = new MyPagerAdapter(getSupportFragmentManager());

        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tablayout));
        viewPager.setAdapter(myPagerAdapter);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if(position==0){
                    if(mViewPageStatus==true){
                        mViewPageStatus=false;
                    }
                    invalidateOptionsMenu(); //重新绘制menu
                }
                else if(position==1){
                    if(mViewPageStatus==false){
                        mViewPageStatus=true;
                    }
                    invalidateOptionsMenu(); //重新绘制menu
                }

            }

            @Override
            public void onPageScrollStateChanged(int state) {
                //state ==1的时辰默示正在滑动，state==2的时辰默示滑动完毕了，state==0的时辰默示什么都没做。

            }
        });

        tablayout.setupWithViewPager(viewPager);


        // 保持Activity处于唤醒状态
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(Color.BLACK);
        }


    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        len=0;

    }

    //重写fragment中的接口方法
    @Override
    public void List_set(int len){
        this.len=len;
        tabtitles[1]="已添加("+len+")";
        myPagerAdapter.notifyDataSetChanged();
    }

    @Override
    public void Capture_set(Book data){

        //ListFragment.bookList.add(data);
        len=ListFragment.bookList.size()+1;
        tabtitles[1]="已添加("+len+")";
        myPagerAdapter.notifyDataSetChanged();
        ListFragment.mCollectRecyclerAdapter.addData(data);
    }


    //处理按下back键
    @Override
    public void onBackPressed() {
        quitDialog();
    }

    public void quitDialog(){
        AlertDialog.Builder dialog=new AlertDialog.Builder(this);
        dialog.setTitle("警告！");
        dialog.setMessage("您所保存的书籍将不会被保留，确认退出吗？");
        dialog.setPositiveButton("确认", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });

        dialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        dialog.create();
        dialog.show();
    }


    //fragment切换的pager适配器
    class MyPagerAdapter extends FragmentPagerAdapter {

        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            if(position==1){
                tabtitles[position]="已添加("+len+")";
            }
            return tabtitles[position];

        }

        @Override
        public Fragment getItem(int position) {
            if(position==0)
            {
                if(mCaptureFragment==null) {
                    mCaptureFragment = new CaptureFragment();
                }
               //CaptureFragment fragment= CaptureFragment.newInstance("","");
                Bundle bundle = new Bundle();
                bundle.putString("Activity","MultiCpatureActivity");
                mCaptureFragment.setArguments(bundle);
                return mCaptureFragment;
            }
            else {
                if(mListFragment==null) {
                    mListFragment = ListFragment.newInstance("", "");
                }
                return mListFragment;
            }

        }

        @Override
        public int getCount() {
            return tabtitles.length;
        }
    }




    //自己写的代码（顶部菜单）
    //菜单
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.multi_caputer_setting,menu);
        setting_menu=menu;
        return true;
    }

    //自己写的代码（顶部菜单点击）
    @Override  //顶部菜单的
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==R.id.capture_flash) {
            if(flash_state){
                item.setIcon(R.drawable.falsh_close);
                flash_state=false;
            }
            else {
                item.setIcon(R.drawable.falsh_open);
                flash_state=true;
            }
            //CameraManager.get().switchFlashLight(handler);
            CameraManager.get().flashHandler();
        }
        else if(item.getItemId()==R.id.capture_yes){
            Toast.makeText(MultiCaptureActivity.this,"保存成功",Toast.LENGTH_SHORT);
            Intent intent = getIntent();
            Bundle bundle=new Bundle();
            //ArrayList list = new ArrayList();
            //list.add(ListFragment.bookList);
            //bundle.putSerializable("bookList",ListFragment.bookList);
            //bundle.putParcelableArrayList("list",list);
            //intent.putExtras(bundle);
            intent.putExtra(Constant.CODED_CONTENT, "");
            setResult(1, intent);
            //save(ListFragment.bookList);
            myhelper=SQLiteHelper.getInstance(getApplicationContext());
            db=myhelper.getWritableDatabase();

            for(int i=0;i<ListFragment.bookList.size();i++) {
                Book book=ListFragment.bookList.get(i);
                ContentValues values = new ContentValues();
                values.put("ISBN", book.getISBN());
                values.put("title", book.getTitle());
                values.put("author", book.getAuthor());
                values.put("translator", book.getTranslator());
                values.put("publisher", book.getPublisher());
                values.put("time_year", book.getTime_Year());
                values.put("time_month", book.getTime_Month());
                values.put("read_state", book.getRead_State());
                values.put("book_shelf","");
                values.put("note","");
                values.put("tag","");
                values.put("website", book.getWebsite());

                byte[] bitmap_byte = bitmapToBytes(book.getBitmap());
                values.put("img_bitmap", bitmap_byte);

                db.insert("BookShelf", null, values);
            }

            db.close();
            myhelper.close();
            MultiCaptureActivity.this.finish();


        }
        else if(item.getItemId()==android.R.id.home){
            quitDialog();
        }

        return true;
    }

    //刷新menu重新绘制回调的方法
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.clear();
        getMenuInflater().inflate(R.menu.multi_caputer_setting,menu);
        if (mViewPageStatus) {
            menu.findItem(R.id.capture_flash).setVisible(false);
        } else {
            menu.findItem(R.id.capture_flash).setVisible(true);
        }
        if(flash_state){
            menu.findItem(R.id.capture_flash).setIcon(R.drawable.falsh_open);
        }
        else {
            menu.findItem(R.id.capture_flash).setIcon(R.drawable.falsh_close);
        }
        return super.onPrepareOptionsMenu(menu);
    }


    public void save(final ArrayList<Book> bookList){

        new Thread(new Runnable() {
            @Override
            public void run() {
                myhelper=SQLiteHelper.getInstance(getApplicationContext());
                db=myhelper.getWritableDatabase();

                for(int i=0;i<4;i++) {
                    Book book=new Book();
                    book.setTitle("sadasd"+i);
                    ContentValues values = new ContentValues();
                    values.put("ISBN", book.getISBN());
                    values.put("title", book.getTitle());
                    values.put("author", book.getAuthor());
                    values.put("translator", book.getTranslator());
                    values.put("publisher", book.getPublisher());
                    values.put("time_year", book.getTime_Year());
                    values.put("time_month", book.getTime_Month());
                    values.put("read_state", book.getRead_State());
                    values.put("book_shelf","");
                    values.put("note","");
                    values.put("tag","");
                    values.put("website", book.getWebsite());

                    byte[] bitmap_byte = bitmapToBytes(book.getBitmap());
                    values.put("img_bitmap", bitmap_byte);

                    db.insert("BookShelf", null, values);
                }

                db.close();
                myhelper.close();

            }
        });
        MultiCaptureActivity.this.finish();
    }

    private static byte[] bitmapToBytes(Bitmap bitmap){
        if (bitmap == null) {
            return null;
        }
        final ByteArrayOutputStream os = new ByteArrayOutputStream();
        // 将Bitmap压缩成PNG编码，质量为100%存储
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, os);//除了PNG还有很多常见格式，如jpeg等。
        return os.toByteArray();
    }

}
