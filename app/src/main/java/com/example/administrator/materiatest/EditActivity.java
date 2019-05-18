package com.example.administrator.materiatest;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class EditActivity extends AppCompatActivity {
    private Spinner spn_state,spn_shelf;
    private List<String> shelf_list;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(R.string.edit_toolbar_name);
        toolbar.setNavigationIcon(R.drawable.imag_menu_close_clear_cancel);
        spn_state=(Spinner)findViewById(R.id.edit_spinner_state);
        String[] str_state={"阅读状态未设置","未读","阅读中","已读"};
        ArrayAdapter<String> adapter_state=new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,str_state);
        adapter_state.setDropDownViewResource(R.layout.edit_spinner_textview);
        spn_state.setAdapter(adapter_state);
        spn_state.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                String str=parent.getItemAtPosition(position).toString();
                Toast.makeText(EditActivity.this, "你点击的是:"+str, Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // TODO Auto-generated method stub
            }
        });

        spn_shelf=(Spinner)findViewById(R.id.edit_spinner_shelf);
        shelf_list=new ArrayList<String>();
        shelf_list.add("默认书架");
        shelf_list.add("添加新书架");
        ArrayAdapter<String> adapter_shelf=new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,shelf_list);
        adapter_shelf.setDropDownViewResource(R.layout.edit_spinner_textview);
        spn_shelf.setAdapter(adapter_shelf);
        spn_shelf.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            final EditText dialog_edit=new EditText(EditActivity.this);
            final TextView dialog_text=new TextView(EditActivity.this);
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                String str=parent.getItemAtPosition(position).toString();
                if(str.equals("添加新书架")){
                  int length=shelf_list.size()-1;
                  shelf_list.remove(length);
                    final AlertDialog.Builder dialog=new AlertDialog.Builder(EditActivity.this);
                    dialog.setTitle(R.string.dialog_title_shelf);
                    dialog.setView(dialog_edit);
                    dialog_text.setGravity(Gravity.RIGHT);
                    dialog.setView(dialog_text);
                    dialog.setPositiveButton(R.string.dialog_ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //TODO
                            // 保存数据
                            startActivity(new Intent(EditActivity.this,MainActivity.class));
                        }
                    });

                    dialog.setNegativeButton(R.string.dialog_cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //TODO
                        }
                    });

                    AlertDialog alertDialog=dialog.create();
                    alertDialog.show();

                    final Button post_button=alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
                    //设置editText的监听事件
                    dialog_edit.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                        }

                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {

                        }

                        @Override
                        public void afterTextChanged(Editable s) {
                            if(s.length()==0||s.length()>10){
                                post_button.setEnabled(false);
                                dialog_text.setTextColor(getResources().getColor(R.color.red));
                            }
                            else {
                                post_button.setEnabled(true);
                                dialog_text.setTextColor(getResources().getColor(R.color.normal_gray));
                                dialog_text.setText(s.length()+"/10");
                            }
                        }
                    });


                }//添加新书架判断语句结尾
                //Toast.makeText(EditActivity.this, "你点击的是:"+str, Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // TODO Auto-generated method stub
            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_edit, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        else if(id==android.R.id.home){
            AlertDialog.Builder dialog=new AlertDialog.Builder(this);
            dialog.setTitle(R.string.dialog_title);
            dialog.setMessage(R.string.dialog_message);
            dialog.setPositiveButton(R.string.dialog_throw, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    //TODO
                    // 保存数据
                      startActivity(new Intent(EditActivity.this,MainActivity.class));
                }
            });

            dialog.setNegativeButton(R.string.dialog_cancel, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    //TODO
                }
            });

            dialog.create();
            dialog.show();
        }
        return super.onOptionsItemSelected(item);
    }
}
