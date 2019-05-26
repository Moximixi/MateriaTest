package com.example.administrator.materiatest;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import com.getbase.floatingactionbutton.*;
import com.yzq.zxinglibrary.android.CaptureActivity;
import com.yzq.zxinglibrary.common.Constant;

public class MainActivity extends AppCompatActivity {
    private DrawerLayout mDrawLayout;
    private int REQUEST_CODE_SCAN = 111;
    final String[] items4 = new String[]{"标题", "作者", "出版社", "出版时间"};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        mDrawLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
        ActionBar actionBar = getSupportActionBar();

        NavigationView navigationView=(NavigationView)findViewById(R.id.nav_view);


       if(actionBar!=null){
           actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_menu);
        }
        //测拉菜单的
        navigationView.setCheckedItem(R.id.nav_book);
        navigationView.setNavigationItemSelectedListener(new NavigationView.
                OnNavigationItemSelectedListener(){
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                mDrawLayout.closeDrawers();
                switch(item.getItemId()){
                    case R.id.nav_add:
                        AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this);
                        View view1 = View.inflate(MainActivity.this, R.layout.add_layout, null);
                        Button calcle=(Button)view1.findViewById(R.id.add_button_cancle);
                        Button commit=(Button)view1.findViewById(R.id.add_button_commit);
                        alertDialog
                                .setTitle("添加标签")
                                .setView(view1)
                                .create();
                        final AlertDialog show=alertDialog.show();
                        calcle.setOnClickListener(new View.OnClickListener(){
                            @Override
                            public void onClick(View v) {
                                //Toast.makeText(MainActivity.this,"取消",Toast.LENGTH_SHORT).show();
                            }
                        });
                        commit.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                //Toast.makeText(MainActivity.this,"确定",Toast.LENGTH_SHORT).show();
                                show.dismiss();
                            }
                        });
                        break;
                    case R.id.nav_search:
                           // onSearchRequested();
                        break;
                    case R.id.nav_setting:
                        Intent intent=new Intent(MainActivity.this,SettingActivity.class);
                        startActivity(intent);
                        break;
                    case R.id.nav_about:
                        Intent intent2=new Intent(MainActivity.this,AboutActivity.class);
                        startActivity(intent2);
                        break;
                    default:
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
                    Intent intent = new Intent(MainActivity.this, CaptureActivity.class);
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
            }
        });
    }
    //扫描返回结果
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // 扫描二维码/条码回传
        if (requestCode == REQUEST_CODE_SCAN && resultCode == RESULT_OK) {
            if (data != null) {
                String content = data.getStringExtra(Constant.CODED_CONTENT);
                Toast.makeText(MainActivity.this,"扫描结果为:"+content,Toast.LENGTH_SHORT).show();
            }
        }
    }
    //菜单
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.toolbar,menu);
        return true;
    }
    @Override  //顶部菜单的
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case android.R.id.home:
                mDrawLayout.openDrawer(GravityCompat.START);
                break;
            case R.id.menu_more:
                final AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this);
                alertDialog.setTitle("排序依据")
                        .setSingleChoiceItems(items4, 0, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                       //处理点击排序之后的各种逻辑
                                    }
                                }
                        )
                        .setPositiveButton("排序", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
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

}
