package com.example.administrator.materiatest;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SearchView;

import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.SectionDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    private DrawerLayout mDrawLayout;
    final String[] items4 = new String[]{"标题", "作者", "出版社", "出版时间"};
    EditText temp_tagText;
    public NavigationView navigationView;
    public ListView nav_labels_lv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        mDrawLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
        ActionBar actionBar = getSupportActionBar();

        navigationView=(NavigationView)findViewById(R.id.nav_view);
        nav_labels_lv=(ListView)findViewById(R.id.label_holder);

       if(actionBar!=null){
           actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_menu);
        }


        //侧拉菜单的
        navigationView.setCheckedItem(R.id.nav_book);

        final List<Label> labels = LabelLab.get(this).getLabels();
        for (int i = 0; i < labels.size(); i++) {
            // add labels
            IDrawerItem drawerItem = new PrimaryDrawerItem()
                    .withName(labels.get(i).getTitle())
                    .withIcon(R.drawable.ic_label)
                    .withIdentifier(i + 10)// identifier begin from 10
                    .withCheckable(true);
            //mDrawer.addItemAtPosition(drawerItem, i + 4);
        }



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
                                Label addLabel=new Label();
                                String newLabel=temp_tagText.getText().toString();
                                addLabel.setTitle(newLabel);
                                LabelLab.get(MainActivity.this).addLabel(addLabel);
                                navigationView.getMenu().add(2,2,2,newLabel);
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
