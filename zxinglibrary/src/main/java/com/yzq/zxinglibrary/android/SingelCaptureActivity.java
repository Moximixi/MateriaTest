package com.yzq.zxinglibrary.android;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.hardware.Camera;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ContextThemeWrapper;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import com.yzq.zxinglibrary.R;
import com.yzq.zxinglibrary.bean.ZxingConfig;
import com.yzq.zxinglibrary.camera.CameraManager;
import com.yzq.zxinglibrary.common.Constant;

public class SingelCaptureActivity extends AppCompatActivity implements ListFragment.ListListener{
    private TabLayout tablayout;
    private ViewPager viewPager;
    private Toolbar toolbar;
    //tab显示的三个切换页面的显示
    private String[] tabtitles = { "扫描"};
    private static int len=0;
    private static Menu setting_menu;
    private static Boolean mViewPageStatus=false;
    private static Boolean flash_state=false;
    public ZxingConfig config;
    private CameraManager cameraManager;
    private CaptureActivityHandler handler;
    private CaptureFragment captureFragment;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_singel_caputer);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.caputer_return);


        //tablayout = (TabLayout) findViewById(R.id.tablayout);
        viewPager = (ViewPager) findViewById(R.id.viewpager);

        MyPagerAdapter myPagerAdapter = new MyPagerAdapter(getSupportFragmentManager());

        //viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tablayout));
        viewPager.setAdapter(myPagerAdapter);


        //tablayout.setupWithViewPager(viewPager);


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

                captureFragment=new CaptureFragment();
               //CaptureFragment fragment= CaptureFragment.newInstance("","");
                Bundle bundle = new Bundle();
                bundle.putString("Activity","SingelCpatureActivity");
                captureFragment.setArguments(bundle);
                return captureFragment;
        }

        @Override
        public int getCount() {
            return tabtitles.length;
        }
    }




    //自己写的代码（顶部菜单）
    //菜单
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.singel_caputer_setting,menu);
        setting_menu=menu;
        return true;
    }

    //自己写的代码（顶部菜单点击）
    @Override  //顶部菜单的
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==R.id.menu_flash) {
            if(flash_state){
                item.setIcon(R.drawable.falsh_close);
                flash_state=false;
            }
            else {
                item.setIcon(R.drawable.falsh_open);
                flash_state=true;
            }
            if(captureFragment.getHandle()!=null) {
                //CameraManager.get().switchFlashLight(captureFragment.getHandle());
                CameraManager.get().flashHandler();
            }
        }
        else if(item.getItemId()==R.id.menu_write){
            initDialog();
        }
        else if(item.getItemId()==R.id.action_add){
            Intent intent = getIntent();
            intent.putExtra(Constant.CODED_CONTENT, "");
            setResult(407, intent);
            this.finish();
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
        getMenuInflater().inflate(R.menu.singel_caputer_setting,menu);
        if (mViewPageStatus) {
            menu.findItem(R.id.menu_flash).setVisible(false);
        } else {
            menu.findItem(R.id.menu_flash).setVisible(true);
        }
        if(flash_state){
            menu.findItem(R.id.menu_flash).setIcon(R.drawable.falsh_open);
        }
        else {
            menu.findItem(R.id.menu_flash).setIcon(R.drawable.falsh_close);
        }
        return super.onPrepareOptionsMenu(menu);
    }


    private void initDownload(final String ISBN){
        Handler handler=new Handler(){
            @Override
            public void handleMessage(Message msg){
                super.handleMessage(msg);
                switch (msg.what){
                    case 1:{
                        //Log.d(TAG,"扫描连接成功");
                        Intent intent = getIntent();
                        intent.putExtra(Constant.CODED_CONTENT, ISBN);
                        setResult(RESULT_OK, intent);
                        SingelCaptureActivity.this.finish();
                        break;
                    }
                    default: {
                        //Log.d(TAG,"扫描连接失败");
                        creatDialog(ISBN);
                    }
                    break;

                }
            }
        };

        DataDownload data=new DataDownload();
        data.scanResult(handler,ISBN);

    }


    private  void creatDialog(final String ISBN){
        AlertDialog.Builder dialog=new AlertDialog.Builder(SingelCaptureActivity.this);
        dialog.setTitle("无法获取详情");
        dialog.setMessage("此书的ISBN"+ISBN+",我们无法获取书籍详情,请检查ISBN,您可以手动输入详情或重新扫描");
        dialog.setPositiveButton("手动输入", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = getIntent();
                intent.putExtra(Constant.CODED_CONTENT, ISBN);
                setResult(408, intent);
                SingelCaptureActivity.this.finish();
            }
        });

        dialog.setNegativeButton("重新扫描", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();

            }
        });

        dialog.create();
        dialog.show();
    }

    public void initDialog(){
        final EditText et = new EditText(this);
        AlertDialog.Builder dialog=new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.AlertDialogCustom) );
        dialog.setTitle("手动添加");
        dialog.setMessage("请输入书籍的ISBN码（13位数字）");
        dialog.setView(et);
        dialog.setPositiveButton("添加", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                initDownload(et.getText().toString());
            }
        });

        dialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //TODO
            }
        });

        AlertDialog alertDialog=dialog.create();
        alertDialog.show();


        final Button post_button1=alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
        post_button1.setEnabled(false);
        //设置editText的监听事件
        et.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(s.length()!=13){
                    post_button1.setEnabled(false);

                }
                else {
                    post_button1.setEnabled(true);

                }
            }

        });
    }




    public void openFlash(){
     try{
         Camera m_Camera = Camera.open();
         Camera.Parameters mParameters;
         mParameters = m_Camera.getParameters();
         mParameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
         m_Camera.setParameters(mParameters);
     } catch(Exception ex){}
 }

}
