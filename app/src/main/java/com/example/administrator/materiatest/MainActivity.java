package com.example.administrator.materiatest;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.media.Image;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.yzq.zxinglibrary.android.MultiCaptureActivity;
import com.yzq.zxinglibrary.android.SingelCaptureActivity;
import com.yzq.zxinglibrary.common.Constant;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private int REQUEST_CODE_SCAN = 111;
    private int REQUEST_CODE_MultiSCAN = 112;
    private final int REQUEST_CODE_CAMEAR=200;
    private final int REQUEST_CODE_INTERNET=201;
    private final int REQUEST_CODE_ACCESS_NETWORK_STATE=202;
    private final int REQUEST_CODE_EDITACTIVITY=233;
    private final int REQUEST_PERMISSIONS=234;
    final String[] items4 = new String[]{"标题", "作者", "出版社", "出版时间"};

    public static List<Book> bookList=new ArrayList<>();  //设置成public static方便点
    private ListView listView;  //主页显示的书
    private BookAdapter adapter;  //listview适配器
    private SQLiteHelper myhelder;  //数据库用来加载书
    private SQLiteDatabase db;

    private DrawerLayout mDrawLayout;  //测拉栏

    private MenuItem searchItem;  //顶部菜单栏的搜索控件
    private SearchView searchView ;
    public EditText temp_tagText;
    public SubMenu addLabel;//添加标签

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        //权限申请
        initPermission();


        //顶部的toolbar
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //侧拉栏
        mDrawLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
        ActionBar actionBar = getSupportActionBar();
        //侧拉栏的布局
        NavigationView navigationView=(NavigationView)findViewById(R.id.nav_view);

        //listview的适配器
        adapter=new BookAdapter(MainActivity.this,R.layout.book_item,
                bookList);
        listView=(ListView)findViewById(R.id.list_view);
        listView.setAdapter(adapter);
        /*
        这是个辅助函数,详细见MainActivity的最后一个函数
        作用是把数据库中的书添加到arraylist中并且显示在主页面
         */
        show();


       if(actionBar!=null){
           actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_menu);
        }
        //测拉菜单的各种按钮逻辑
        //加标签，搜索，设置页面，关于页面。
        navigationView.setCheckedItem(R.id.nav_book);

        final List<Label> labels = LabelLab.get(this).getLabels();
        Menu menu=navigationView.getMenu();
        //动态添加菜单选项
        addLabel=menu.addSubMenu(1,0,0,"标签");
        addLabel.add(1,1,1,"添加新标签").setIcon(R.drawable.add);
        menu.add(2,1,0,"设置").setIcon(R.drawable.setting);
        menu.add(2,2,0,"关于").setIcon(R.drawable.about);
        //动态显示标签
        for (int i = 0; i < labels.size(); i++) {
            Label label=labels.get(i);
            addLabel.add(1,(int)label.getId(),1,label.getTitle()).setIcon(R.drawable.ic_label);
        }


        navigationView.setNavigationItemSelectedListener(new NavigationView.
                OnNavigationItemSelectedListener(){
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                mDrawLayout.closeDrawers();
                //标签
                if(item.getGroupId()==1){
                    //添加标签的按钮
                    if(item.getItemId()==1){
                        AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this);
                        View view1 = View.inflate(MainActivity.this, R.layout.add_layout, null);
                        Button calcle=(Button)view1.findViewById(R.id.add_button_cancle);
                        Button commit=(Button)view1.findViewById(R.id.add_button_commit);
                        temp_tagText=(EditText)view1.findViewById(R.id.add_tag_edit);
                        alertDialog
                                .setTitle("添加标签")
                                .setView(view1)
                                .create();
                        final AlertDialog show=alertDialog.show();
                        calcle.setOnClickListener(new View.OnClickListener(){
                            @Override
                            public void onClick(View v) {
                                show.dismiss();
                            }
                        });
                        commit.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Label newLabel=new Label();
                                String str=temp_tagText.getText().toString();
                                newLabel.setTitle(str);
                                addNewLabel(newLabel);
                                show.dismiss();
                            }
                        });
                    }
                    //点击标签
                    else if(item.getItemId()>1){
                        getLabelBook(item.getItemId());
                    }
                }

                if(item.getGroupId()==2){
                    switch(item.getItemId()){
                        case 1: //设置
                            Intent intent=new Intent(MainActivity.this,SettingActivity.class);
                            startActivity(intent);
                            break;
                        case 2://关于
                            Intent intent2=new Intent(MainActivity.this,AboutActivity.class);
                            startActivity(intent2);
                            break;
                        default:
                            break;
                    }
                }
                switch(item.getItemId()){
                    case R.id.nav_search:
                        // onSearchRequested();
                        break;
                    default:
                        break;
                }
                return true;
            }
        });

      //两个悬浮按钮的逻辑
        final com.getbase.floatingactionbutton.FloatingActionButton addbook =
                (com.getbase.floatingactionbutton.FloatingActionButton) findViewById(R.id.addbook);
        addbook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try{
                    Intent intent = new Intent(MainActivity.this, SingelCaptureActivity.class);
                    startActivityForResult(intent, REQUEST_CODE_SCAN);
                }catch (SecurityException e){
                    e.printStackTrace();
                }

            }
        });
        final com.getbase.floatingactionbutton.FloatingActionButton massaddition =
                (com.getbase.floatingactionbutton.FloatingActionButton) findViewById(R.id.massaddition);
        massaddition.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                try{
                    Intent intent = new Intent(MainActivity.this, MultiCaptureActivity.class);
                    startActivityForResult(intent, REQUEST_CODE_MultiSCAN);
                }catch (SecurityException e){
                    e.printStackTrace();
                }


            }
        });

        //这是listview中点击某一本书之后的操作，添加在这里
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
             //   Toast.makeText(MainActivity.this,"ghij",Toast.LENGTH_SHORT).show();
            }
        });
    }
    //扫描返回结果
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // 扫描二维码/条码回传
        if (requestCode == REQUEST_CODE_SCAN){
            if(resultCode == RESULT_OK) {
            if (data != null) {
                String content = data.getStringExtra(Constant.CODED_CONTENT);
                Intent intent=new Intent(MainActivity.this,EditActivity.class);
                intent.putExtra("isInternet",true);
                intent.putExtra("ISBN",content);
                startActivityForResult(intent,REQUEST_CODE_EDITACTIVITY);
                //Toast.makeText(MainActivity.this,"扫描结果为:"+content,Toast.LENGTH_SHORT).show();
            }
         }
         else if(resultCode==408){
                String content = data.getStringExtra(Constant.CODED_CONTENT);
                Intent intent=new Intent(MainActivity.this,EditActivity.class);
                intent.putExtra("isInternet",false);
                intent.putExtra("ISBN",content);
                startActivityForResult(intent,REQUEST_CODE_EDITACTIVITY);
            }
            else if(resultCode==407){
                String content = data.getStringExtra(Constant.CODED_CONTENT);
                Intent intent=new Intent(MainActivity.this,EditActivity.class);
                intent.putExtra("isInternet",false);
                intent.putExtra("ISBN",content);
                startActivityForResult(intent,REQUEST_CODE_EDITACTIVITY);
            }

        }
        //编辑界面返回信息
        else if(requestCode==REQUEST_CODE_EDITACTIVITY){
            if(resultCode==0){
                //丢弃修改
            }
            else if(resultCode==1){
                //TODO
                //完成修改
                adapter.notifyDataSetChanged();
            }
        }
        else if(requestCode==REQUEST_CODE_MultiSCAN){
            //确认完成
            if(resultCode==1){
                //ArrayList<Book> booList=(ArrayList<Book>) data.getSerializableExtra("bookList");
                //Bundle bundle=data.getExtras();
                //ArrayList list = bundle.getParcelableArrayList("list");
                //ArrayList<Book> booList= (ArrayList<Book>) list.get(0);
                //StringBuffer bf=new StringBuffer();


            }

        }
    }

    //菜单,包括搜索栏serachview的监听事件
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.toolbar,menu);
        searchItem = menu.findItem(R.id.menu_search);
        searchView = (SearchView) MenuItemCompat.getActionView(searchItem);

        MenuItemCompat.setOnActionExpandListener(searchItem,new MenuItemCompat.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {return true;}
            //点击左侧的返回键的时候要回到原来的界面，这里是重新绑定了原来的适配器和bookList。然后重新显示
            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                adapter=new BookAdapter(MainActivity.this,R.layout.book_item,
                        bookList);
                listView.setAdapter(adapter);
                adapter.notifyDataSetChanged();
                return true;
            }
        });
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            //根据输入框的关键字来检索出书籍，就是根据关键字匹配，然后重新用一个bookList和adapter绑定，显示
            @Override
            public boolean onQueryTextSubmit(String query) {
                List<Book> tmp=new ArrayList<>();
                BookAdapter tmpAdapter=new BookAdapter(MainActivity.this,R.layout.book_item,
                        tmp);
                listView.setAdapter(tmpAdapter);
                for(Book book:bookList)
                    if(book.getTitle().contains(query))
                        tmp.add(book);

                tmpAdapter.notifyDataSetChanged();
                return false;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        return true;
    }

    @Override  //顶部菜单的各个控件的函数
    public boolean onOptionsItemSelected(MenuItem item) {

        switch(item.getItemId()){
            case android.R.id.home:
                mDrawLayout.openDrawer(GravityCompat.START);
                break;
            case R.id.menu_more:
                final AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this);
                final int index[]={-1};
                alertDialog.setTitle("排序依据")
                        .setSingleChoiceItems(items4, 0, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {index[0]=which;}
                        })
                        .setPositiveButton("排序", new DialogInterface.OnClickListener() {
                            final Collator myCollator = Collator.getInstance(java.util.Locale.CHINA);
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //处理点击排序之后的各种逻辑
                                if(index[0]==-1)index[0]=0;  //默认是按照标题排序的
                                switch(index[0]){
                                    case 0:
                                        //按标题
                                        Comparator<Book> BookComparatorBytitle = new Comparator<Book>() {
                                            @Override
                                            public int compare(Book o1, Book o2) {
                                                if(myCollator.compare(o1.getTitle(),o2.getTitle())>0)return 1;
                                                else return -1;
                                            }
                                        };
                                        Collections.sort(bookList, BookComparatorBytitle);
                                        break;
                                    case 1:
                                        //按作者
                                        Comparator<Book> BookComparatorByauthor = new Comparator<Book>() {
                                            @Override
                                            public int compare(Book o1, Book o2) {
                                                if(myCollator.compare(o1.getAuthor(),o2.getAuthor())>0)return 1;
                                                else return -1;
                                            }
                                        };
                                        Collections.sort(bookList, BookComparatorByauthor);
                                        break;
                                    case 2:
                                        //按出版社
                                        Comparator<Book> BookComparatorBypublisher = new Comparator<Book>() {
                                            @Override
                                            public int compare(Book o1, Book o2) {
                                                if(myCollator.compare(o1.getPublisher(),o2.getPublisher())>0)return 1;
                                                else return -1;
                                            }
                                        };
                                        Collections.sort(bookList, BookComparatorBypublisher);
                                        break;
                                    case 3:
                                        //按出版时间
                                        Comparator<Book> BookComparatorBytime = new Comparator<Book>() {
                                            @Override
                                            public int compare(Book o1, Book o2) {
                                                if(myCollator.compare(o1.getTime_Year(),o2.getTime_Year())>0)return 1;
                                                else if(myCollator.compare(o1.getTime_Year(),o2.getTime_Year())<0)return -1;
                                                else if(myCollator.compare(o1.getTime_Month(),o2.getTime_Month())>0)return 1;
                                                else if(myCollator.compare(o1.getTime_Month(),o2.getTime_Month())<0)return -1;
                                                else return 0;
                                            }
                                        };
                                        Collections.sort(bookList, BookComparatorBytime);
                                        break;
                                }
                                dialog.dismiss();
                                adapter.notifyDataSetChanged();
                                index[0]=-1;
                            }
                        })
                        .create().show();
                break;
            case R.id.menu_all:
                break;
            case R.id.menu_default:
                Intent intent=new Intent(MainActivity.this,DefaultShelfActivity.class);
                startActivity(intent);
                break;
            default:

        }
        return true;
    }

    //重写权限的回调方法
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_CAMEAR:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //这里写操作 如send（）； send函数中New SendMsg （号码，内容）；
                } else {
                    Toast.makeText(this, "你没启动相机权限", Toast.LENGTH_SHORT).show();
                }
                break;
            case REQUEST_CODE_INTERNET:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //这里写操作 如send（）； send函数中New SendMsg （号码，内容）；
                } else {
                    Toast.makeText(this, "你没启动网络权限", Toast.LENGTH_SHORT).show();
                }
                break;
            case REQUEST_CODE_ACCESS_NETWORK_STATE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //这里写操作 如send（）； send函数中New SendMsg （号码，内容）；
                } else {
                    Toast.makeText(this, "你没启动网络状态权限", Toast.LENGTH_SHORT).show();
                }
                break;
            case REQUEST_PERMISSIONS:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //成功
                    Toast.makeText(this, "用户授权相机权限", Toast.LENGTH_SHORT).show();
                } else {
                    // 勾选了不再询问
                    Toast.makeText(this, "用户拒绝相机权限", Toast.LENGTH_SHORT).show();
                    /**
                     * 跳转到 APP 详情的权限设置页
                     *
                     * 可根据自己的需求定制对话框，点击某个按钮在执行下面的代码
                     */
                    //Intent intent = Util.getAppDetailSettingIntent(PhotoFromSysActivity.this);
                   // startActivity(intent);
                }
                break;
            default:
        }
    }

   //权限申请
    private void initPermission() {
        /**
         * 初始化相机相关权限
         * 适配6.0+手机的运行时权限
         */
        String[] permissions = new String[]{Manifest.permission.CAMERA,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE};
        //检查权限
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            // 之前拒绝了权限，但没有点击 不再询问 这个时候让它继续请求权限
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.CAMERA)) {
                //Toast.makeText(this, "用户曾拒绝打开相机权限", Toast.LENGTH_SHORT).show();
                ActivityCompat.requestPermissions(this, permissions, REQUEST_PERMISSIONS);
            } else {
                //注册相机权限
                ActivityCompat.requestPermissions(this, permissions, REQUEST_PERMISSIONS);
            }
        }

        //相册拍照
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_PERMISSIONS);
        }

        //申请网络权限
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.INTERNET}, REQUEST_CODE_INTERNET);
        }
        //申请网络状态权限
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_NETWORK_STATE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_NETWORK_STATE}, REQUEST_CODE_ACCESS_NETWORK_STATE);
        }
    }
    /*
    这是一个辅助函数。功能：打开数据库，将所有的书取出添加到bookList中，
    并显示到主页面上
     */

    public void show() {
        myhelder = SQLiteHelper.getInstance(getApplicationContext());
        db = myhelder.getReadableDatabase();
        //获得游标
        Cursor cursor = db.query("BookShelf", null, null, null, null, null, null);
        //判断游标是否为空
        if (cursor.moveToFirst()) {
            while (cursor.moveToNext()) {
                String isbn = cursor.getString(cursor.getColumnIndex("ISBN"));
                String title = cursor.getString(cursor.getColumnIndex("title"));
                String author = cursor.getString(cursor.getColumnIndex("author"));
                String publisher = cursor.getString(cursor.getColumnIndex("publisher"));
                String time_year = cursor.getString(cursor.getColumnIndex("time_year"));
                String time_month = cursor.getString(cursor.getColumnIndex("time_month"));
                //避免显示重复数据
                Boolean flag = true;
                for (Book book : bookList) {
                    if (book.getISBN().equals(isbn)) {
                        flag = false;
                        break;
                    }
                }
                if (flag == false) continue;

                byte[] bytes = cursor.getBlob(cursor.getColumnIndex("img_bitmap"));
                Bitmap bitmap;
                //判断空情况
                if(bytes!=null) {
                    bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length, null);
                }
                else {
                    ImageView imageView =new ImageView(this);
                    imageView.setImageResource(R.drawable.bookshelf);
                    bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.img_init_bitmap);
                }
                //构造函数会将图片的格式统一
                bookList.add(new Book(title, author, publisher, time_year, time_month, bitmap));
            }
        }
        cursor.close();
        db.close();
        myhelder.close();
        //默认按照标题排序
        final Collator myCollator = Collator.getInstance(java.util.Locale.CHINA);
        Comparator<Book> BookComparatorBytitle = new Comparator<Book>() {
            @Override
            public int compare(Book o1, Book o2) {
                if (myCollator.compare(o1.getTitle(), o2.getTitle()) > 0) return 1;
                else return -1;
            }
        };
        Collections.sort(bookList, BookComparatorBytitle);
        adapter.notifyDataSetChanged();

    }

    private void addNewLabel(Label label){
        LabelLab.get(MainActivity.this).addLabel(label);
        addLabel.add(1,label.getId(),1,label.getTitle()).setIcon(R.drawable.ic_label);
    }

    //获取标签对应的书籍并且显示
    private void getLabelBook(int id){

    }
}


