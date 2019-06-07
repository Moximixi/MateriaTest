package com.yzq.zxinglibrary.android;

import android.content.Intent;
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

public class MultiCaptureActivity extends AppCompatActivity implements ListFragment.ListListener{
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

        MyPagerAdapter myPagerAdapter = new MyPagerAdapter(getSupportFragmentManager());

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


    //重写fragment中的接口方法
    @Override
    public void List_set(){
        //TODO
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
                CaptureFragment fragment=new CaptureFragment();
               //CaptureFragment fragment= CaptureFragment.newInstance("","");
                Bundle bundle = new Bundle();
                bundle.putString("Activity","MultiCpatureActivity");
                fragment.setArguments(bundle);
                return fragment;
            }
            else {
                ListFragment fragment = ListFragment.newInstance("","");
                return fragment;
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
            Intent intent = getIntent();
            intent.putExtra(Constant.CODED_CONTENT, "");
            setResult(1, intent);
            MultiCaptureActivity.this.finish();
        }
        else if(item.getItemId()==android.R.id.home){
            finish();
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



}
