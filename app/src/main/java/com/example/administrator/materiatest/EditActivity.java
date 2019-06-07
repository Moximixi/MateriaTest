package com.example.administrator.materiatest;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.net.Uri;
import android.nfc.Tag;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;

import static android.content.ContentValues.TAG;

public class EditActivity extends AppCompatActivity {
    private Spinner spn_state,spn_shelf;
    private ImageView mImageView;
    private List<String> shelf_list;
    private EditText titel,author,translator,publisher,time_year,time_month,ISBN,note,tag,website;
    private ArrayList<String> tagList;
    private Set<String> tagSet;
    private boolean[] tag_state;
    private String[] tagArray;
    //数据库相关
    private SQLiteHelper myhelper;
    private SQLiteDatabase db;
    private Cursor cursor;

    private Book book=null;

    private final int CAMERA_RESULT_CODE=500;
    private final int CROP_RESULT_CODE=501;
    private final int ALBUM_RESULT_CODE=502;

    private String imag_file;
    private File file;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        titel=(EditText)findViewById(R.id.edit_biaoti);
        author=(EditText)findViewById(R.id.edit_zuozhe);
        translator=(EditText)findViewById(R.id.edit_yizhe);
        publisher=(EditText)findViewById(R.id.edit_chubanshe);
        time_year=(EditText)findViewById(R.id.edit_year);
        time_month=(EditText)findViewById(R.id.edit_month);
        ISBN=(EditText)findViewById(R.id.edit_ISBN);
        note=(EditText)findViewById(R.id.edit_biji);
        website=(EditText)findViewById(R.id.edit_wangzhi);
        spn_state=(Spinner)findViewById(R.id.edit_spinner_state);
        tag=(EditText)findViewById(R.id.edit_biaoqian);
        spn_shelf=(Spinner)findViewById(R.id.edit_spinner_shelf);
        mImageView=(ImageView)findViewById(R.id.edit_imageView);

        //初始化编辑组件
        init();

        mImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(EditActivity.this);
                builder.setTitle("更换封面");
                builder.setItems(new String[]{"拍摄新图片","从相册选择图片"}, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which){
                            case 0:
                                openSysCamera();
                                break;
                            case 1:
                                openSysAlbum();
                        }
                    }
                });
                builder.create().show();
            }
        });

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(R.string.edit_toolbar_name);
        toolbar.setNavigationIcon(R.drawable.edit_img_cancel);
        String[] str_state={"阅读状态未设置","未读","阅读中","已读"};
        ArrayAdapter<String> adapter_state=new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,str_state);
        adapter_state.setDropDownViewResource(R.layout.edit_spinner_textview);
        spn_state.setAdapter(adapter_state);
        spn_state.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                String str=parent.getItemAtPosition(position).toString();
                //Toast.makeText(EditActivity.this, "你点击的是:"+str, Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // TODO Auto-generated method stub
            }
        });

        shelf_list=new ArrayList<String>();
        shelf_list.add("默认书架");
        shelf_list.add("添加新书架");
        ArrayAdapter<String> adapter_shelf=new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,shelf_list);
        adapter_shelf.setDropDownViewResource(R.layout.edit_spinner_textview);
        spn_shelf.setAdapter(adapter_shelf);
        spn_shelf.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            final View  dialog_view=(RelativeLayout) getLayoutInflater().inflate(R.layout.edit_dialog_shelf_layout,null);
            final EditText dialog_edit=(EditText)dialog_view.findViewById(R.id.dialog_edit);
            final TextView dialog_text=(TextView)dialog_view.findViewById(R.id.dialog_text);
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                String str=parent.getItemAtPosition(position).toString();
                if(str.equals("添加新书架")){
                    AlertDialog.Builder dialog=new AlertDialog.Builder(EditActivity.this);
                    dialog.setTitle(R.string.dialog_title_shelf);
                    dialog.setView(dialog_view);
                    dialog.setPositiveButton(R.string.dialog_ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            int length=shelf_list.size()-1;
                            shelf_list.remove(length);
                            shelf_list.add(dialog_edit.getText().toString());
                            shelf_list.add("添加新书架");

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
                            }
                            dialog_text.setText(s.length()+"/10");
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
        //标签选项里面的内容
        tagList=new ArrayList<String>();
        tagList.add("标签1");
        tagList.add("标签2");
        tagList.add("标签3");
        //用来存放选择的标签
        tagSet=new TreeSet<String>();

         //tag标签
        //监听事件
        tag.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //为弹出窗口的listview进行设置
                //Toast.makeText(EditActivity.this,"2333333",Toast.LENGTH_SHORT).show();;
                InitTagDialog();

    }


        });
    }


    public void InitAddTagDialog(){
        final View  dialog_view1=(RelativeLayout) getLayoutInflater().inflate(R.layout.edit_dialog_shelf_layout,null);
        final EditText dialog_edit1=(EditText)dialog_view1.findViewById(R.id.dialog_edit);
        final TextView dialog_text1=(TextView)dialog_view1.findViewById(R.id.dialog_text);

        dialog_text1.setText("0/15");
        final AlertDialog.Builder dialog1=new AlertDialog.Builder(EditActivity.this);
        dialog1.setTitle(R.string.dialog_title_shelf);
        dialog1.setView(dialog_view1);
        dialog1.setPositiveButton(R.string.dialog_ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                tagList.add(dialog_edit1.getText().toString());
                //tagArray=tagList.toArray(new String[tagList.size()]);
                InitTagDialog();
            }
        });

        dialog1.setNegativeButton(R.string.dialog_cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //TODO
                InitTagDialog();
            }
        });

        AlertDialog alertDialog1=dialog1.create();
        alertDialog1.show();

        final Button post_button1=alertDialog1.getButton(DialogInterface.BUTTON_POSITIVE);
        //设置editText的监听事件
        dialog_edit1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(s.length()==0||s.length()>15){
                    post_button1.setEnabled(false);
                    dialog_text1.setTextColor(getResources().getColor(R.color.red));
                }
                else {
                    post_button1.setEnabled(true);
                    dialog_text1.setTextColor(getResources().getColor(R.color.normal_gray));
                }
                dialog_text1.setText(s.length()+"/15");
            }
        });
    }

    public void InitTagDialog(){
        tag_state=new boolean[tagList.size()];
        tagArray=tagList.toArray(new String[tagList.size()]);
        tagSet.clear();
        //初始化已选的标签勾中状态
        String temp=tag.getText().toString();
        String[] temps=temp.split(",");
            for(int i=0;i<tagList.size();i++){
                for(String s:temps){
                if(tagList.get(i).equals(s)){
                    tag_state[i]=true;
                    tagSet.add(s);
                }
            }
        }



        final AlertDialog.Builder dialog=new AlertDialog.Builder(EditActivity.this);
        dialog.setTitle(R.string.dialog_title2);
        dialog.setMultiChoiceItems(tagArray,tag_state,new DialogInterface.OnMultiChoiceClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                if(isChecked){
                    tagSet.add(tagList.get(which));
                }
                else if(!isChecked){
                    tagSet.remove(tagList.get(which));
                }
            }
        });
        dialog.setPositiveButton(R.string.dialog_ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                StringBuffer bf=new StringBuffer();
                for(String str:tagSet){
                    bf.append(str+",");
                }
                if(bf.length()>1) {
                    bf.deleteCharAt(bf.length() - 1);
                    tag.setTextColor(getResources().getColor(R.color.black));
                    tag.setText(bf.toString());
                }
                else {
                    tag.setTextColor(getResources().getColor(R.color.gray));
                    tag.setText(R.string.dialog_addTag);
                }
            }
        });

        dialog.setNeutralButton(R.string.dialog_addTag, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                InitAddTagDialog();//打开另一个弹窗
            }
        });

        AlertDialog alertDialog=dialog.create();
        alertDialog.show();
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
           //Toast.makeText(EditActivity.this,"sadasad",Toast.LENGTH_SHORT).show();
            //数据库操作

            myhelper=SQLiteHelper.getInstance(getApplicationContext());
            db=myhelper.getWritableDatabase();

            ContentValues values = new ContentValues();
            values.put("ISBN",ISBN.getText().toString());
            values.put("title",titel.getText().toString());
            values.put("author",author.getText().toString());
            values.put("translator",translator.getText().toString());
            values.put("publisher",publisher.getText().toString());
            values.put("time_year",time_year.getText().toString());
            values.put("time_month",time_month.getText().toString());
            values.put("read_state",spn_state.toString());
            values.put("book_shelf",spn_shelf.toString());
            values.put("note",note.getText().toString());
            values.put("tag",tag.getText().toString());
            values.put("website",website.getText().toString());

            mImageView.setDrawingCacheEnabled(true);
            Bitmap bitmap = Bitmap.createBitmap(mImageView.getDrawingCache());
            mImageView.setDrawingCacheEnabled(false);

            byte[] bitmap_byte = bitmapToBytes(bitmap);
            values.put("img_bitmap",bitmap_byte);

            db.insert("BookShelf",null,values);
            db.close();
            myhelper.close();

            setResult(1);
            finish();

            //startActivity(new Intent(EditActivity.this,MainActivity.class));
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
                      setResult(0);
                      finish();
                    //startActivity(new Intent(EditActivity.this,MainActivity.class));
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
            return true;

        }
        return super.onOptionsItemSelected(item);
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


    private void init(){
        Boolean flag=getIntent().getBooleanExtra("isInternet",false);
        String Isbn=getIntent().getStringExtra("ISBN");

        if(flag) {

            Handler handler = new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    super.handleMessage(msg);
                    switch (msg.what) {
                        case 1: {
                            //Toast.makeText(EditActivity.this,"下载成功",Toast.LENGTH_SHORT).show();
                            book = (Book) msg.obj;
                            titel.setText(book.getTitle());
                            author.setText(book.getAuthor());
                            translator.setText(book.getTranslator());
                            publisher.setText(book.getPublisher());
                            time_year.setText(book.getTime_Year());
                            time_month.setText(book.getTime_Month());
                            ISBN.setText(book.getISBN());
                            website.setText(book.getWebsite());
                            mImageView.setImageBitmap(book.getBitmap());

                            break;
                        }


                    }
                }
            };
            DataDownload data = new DataDownload();
            data.scanResult(handler, Isbn);
        }
        else {
            ISBN.setText(Isbn);
        }

    }




    //打开相机
    public void openSysCamera(){
        if(!ISBN.getText().toString().equals("")){
            imag_file=ISBN.getText().toString();
        }
        else {
            imag_file=String.valueOf(new Random().nextInt(1000))+String.valueOf(new Random().nextInt(1000));
        }
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(
                new File(Environment.getExternalStorageDirectory(), imag_file)));
        startActivityForResult(cameraIntent, CAMERA_RESULT_CODE);
    }

    //返回结果
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case CAMERA_RESULT_CODE:
                File tempFile = new File(Environment.getExternalStorageDirectory(), imag_file);
                cropPic(Uri.fromFile(tempFile));
                break;
            case CROP_RESULT_CODE:
                // 裁剪时,这样设置 cropIntent.putExtra("return-data", true); 处理方案如下
                if (data != null) {
                    Bundle bundle = data.getExtras();
                    if (bundle != null) {
                        Bitmap bitmap = bundle.getParcelable("data");
                        mImageView.setImageBitmap(bitmap);
                        // 把裁剪后的图片保存至本地 返回路径
                        String urlpath = FileUtilcll.saveFile(this, "crop.jpg", bitmap);
                        //L.e("裁剪图片地址->" + urlpath);
                    }
                }

                // 裁剪时,这样设置 cropIntent.putExtra("return-data", false); 处理方案如下
               //  try {
              //      ivHead.setImageBitmap(BitmapFactory.decodeStream(
              // getActivity().getContentResolver().openInputStream(imageUri)));
              //                } catch (FileNotFoundException e) {
              //                    e.printStackTrace();
              //                }
                break;

            case ALBUM_RESULT_CODE:
                // 相册
                cropPic(data.getData());
                break;

        }

    }


    /**
     * 裁剪图片
     *
     * @param data
     */
    private void cropPic(Uri data) {
        if (data == null) {
            return;
        }
        Intent cropIntent = new Intent("com.android.camera.action.CROP");
        cropIntent.setDataAndType(data, "image/*");

        //需要加上这两句话 ： uri 权限
        cropIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        cropIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        // 开启裁剪：打开的Intent所显示的View可裁剪
        cropIntent.putExtra("crop", "true");
        // 裁剪宽高比
        cropIntent.putExtra("aspectX", 1);
        cropIntent.putExtra("aspectY", 1.5);
        // 裁剪输出大小
        cropIntent.putExtra("outputX", 200);
        cropIntent.putExtra("outputY", 300);
        cropIntent.putExtra("scale", true);
        /**
         * return-data
         * 这个属性决定我们在 onActivityResult 中接收到的是什么数据，
         * 如果设置为true 那么data将会返回一个bitmap
         * 如果设置为false，则会将图片保存到本地并将对应的uri返回，当然这个uri得有我们自己设定。
         * 系统裁剪完成后将会将裁剪完成的图片保存在我们所这设定这个uri地址上。我们只需要在裁剪完成后直接调用该uri来设置图片，就可以了。
         */
        cropIntent.putExtra("return-data", true);
        // 当 return-data 为 false 的时候需要设置这句
        //        cropIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        // 图片输出格式
        //        cropIntent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        // 头像识别 会启动系统的拍照时人脸识别
        //        cropIntent.putExtra("noFaceDetection", true);
        startActivityForResult(cropIntent, CROP_RESULT_CODE);
    }

    /**
     * 打开系统相册
     */
    private void openSysAlbum() {
        Intent albumIntent = new Intent(Intent.ACTION_PICK);
        albumIntent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        startActivityForResult(albumIntent, ALBUM_RESULT_CODE);
    }


    private void useCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        file = new File(Environment.getExternalStorageDirectory().getAbsolutePath()
                + "/test/" + System.currentTimeMillis() + ".jpg");
        file.getParentFile().mkdirs();
        //改变Uri com.xykj.customview.fileprovider注意和xml中的一致
        Uri uri = FileProvider.getUriForFile(this, "com.gjp.activity.teste.fileprovider", file);
        //添加权限
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        startActivityForResult(intent, CAMERA_RESULT_CODE);
    }

    private void getPhoto(){
        //在这里跳转到手机系统相册里面
        Intent intent = new Intent(
                Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivityForResult(intent, ALBUM_RESULT_CODE);
    }



}
