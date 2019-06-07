package com.yzq.zxinglibrary.android;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;


import com.google.zxing.BarcodeFormat;
import com.google.zxing.Result;
import com.yzq.zxinglibrary.R;
import com.yzq.zxinglibrary.bean.ZxingConfig;
import com.yzq.zxinglibrary.camera.CameraManager;
import com.yzq.zxinglibrary.common.Constant;
import com.yzq.zxinglibrary.decode.DecodeHandlerInterface;
import com.yzq.zxinglibrary.view.ViewfinderView;

import java.io.IOException;
import java.util.Vector;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link CaptureFragment.CaptureListener} interface
 * to handle interaction events.
 * Use the {@link CaptureFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CaptureFragment extends Fragment implements SurfaceHolder.Callback,
        DecodeHandlerInterface {

    public static final String SCAN_RESULT_ACTION = "com.zxing.fragment.ACTION_SCAN_RESULT";
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private CaptureActivityHandler handler;
    private ViewfinderView viewfinderView;
    private boolean hasSurface;
    private Vector<BarcodeFormat> decodeFormats;
    private String characterSet;
    private InactivityTimer inactivityTimer;
    private MediaPlayer mediaPlayer;
    private boolean playBeep;
    private static final float BEEP_VOLUME = 0.10f;
    private boolean vibrate;
    private Button cancelScanButton;
    private View view;
    public ZxingConfig config;
    private BeepManager beepManager;
    private SurfaceHolder surfaceHolder;
    private SurfaceView previewView;

    public CaptureFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ListFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ListFragment newInstance(String param1, String param2) {
        ListFragment fragment = new ListFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public interface CaptureListener {
        // TODO: Update argument type and name
        void Cputure_set();

    }


    /** Called when the fragment is first created. */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

               /*先获取配置信息*/
        try {
            config = (ZxingConfig) getActivity().getIntent().getExtras().get(Constant.INTENT_ZXING_CONFIG);
        } catch (Exception e) {

            Log.i("config", e.toString());
        }

        if (config == null) {
            config = new ZxingConfig();
        }


        view = inflater.inflate(R.layout.fragment_camera, container, false);
        // ViewUtil.addTopView(getApplicationContext(), this,
        // R.string.scan_card);
        CameraManager.init(getActivity());
        viewfinderView = (ViewfinderView) view
                .findViewById(R.id.viewfinder_view);
        viewfinderView.setZxingConfig(config);
        previewView = (SurfaceView) view.findViewById(R.id.preview_view);

        hasSurface = false;
        inactivityTimer = new InactivityTimer(getActivity());

        beepManager = new BeepManager(getActivity());
        beepManager.setPlayBeep(config.isPlayBeep());
        beepManager.setVibrate(config.isShake());

        viewfinderView.setCameraManager(CameraManager.get());

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        handler = null;
        CameraManager.init(getActivity());
        surfaceHolder = previewView.getHolder();
        if (hasSurface) {
            initCamera(surfaceHolder);
        } else {
            surfaceHolder.addCallback(this);
            surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        }
        decodeFormats = null;
        characterSet = null;

        playBeep = true;
        AudioManager audioService = (AudioManager) getActivity()
                .getSystemService(Context.AUDIO_SERVICE);
        if (audioService.getRingerMode() != AudioManager.RINGER_MODE_NORMAL) {
            playBeep = false;
        }
        initBeepSound();
        vibrate = true;

        beepManager.updatePrefs();
        // quit the scan view

        inactivityTimer.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (handler != null) {
            handler.quitSynchronously();
            handler = null;
        }
        inactivityTimer.onPause();
        CameraManager.get().closeDriver();
        beepManager.close();

    }

    @Override
    public void onDestroy() {
        inactivityTimer.shutdown();
        restartScan();
        super.onDestroy();
    }


    public CaptureActivityHandler getHandle(){
        return this.handler;
    }

    /**
     * Handler scan result
     *
     * @param result
     * @param barcode
     */
    public void handleDecode(Result result, Bitmap barcode) {
        inactivityTimer.onActivity();
        beepManager.playBeepSoundAndVibrate();
        String resultString = result.getText();
        // FIXME
        if (resultString.equals("")) {

            Toast.makeText(getActivity(), "Scan failed!", Toast.LENGTH_SHORT)
                    .show();
        } else {

            sendBroadcastToFragment(resultString);
        }
    }

    public void sendBroadcastToFragment(String result) {

        Intent intent = new Intent();
        intent.setAction(SCAN_RESULT_ACTION);
        intent.putExtra("result", result);
        getActivity().sendBroadcast(intent);
    }

    private void initCamera(SurfaceHolder surfaceHolder) {
        try {
            CameraManager.get().openDriver(surfaceHolder);
        } catch (IOException ioe) {
            return;
        } catch (RuntimeException e) {
            return;
        }
        if (handler == null) {
            handler = new CaptureActivityHandler(this, decodeFormats, characterSet,CameraManager.get());
        }

    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width,
                               int height) {

    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if (!hasSurface) {
            hasSurface = true;
            initCamera(holder);
        }

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        hasSurface = false;

    }

    public ViewfinderView getViewfinderView() {
        return viewfinderView;
    }

    public Handler getHandler() {
        return handler;
    }

    public void drawViewfinder() {
        viewfinderView.drawViewfinder();

    }

    private void initBeepSound() {
        if (playBeep && mediaPlayer == null) {
            // The volume on STREAM_SYSTEM is not adjustable, and users found it
            // too loud,
            // so we now play on the music stream.
            getActivity().setVolumeControlStream(AudioManager.STREAM_MUSIC);
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.setOnCompletionListener(beepListener);

            AssetFileDescriptor file = getResources().openRawResourceFd(
                    R.raw.beep);
            try {
                mediaPlayer.setDataSource(file.getFileDescriptor(),
                        file.getStartOffset(), file.getLength());
                file.close();
                mediaPlayer.setVolume(BEEP_VOLUME, BEEP_VOLUME);
                mediaPlayer.prepare();
            } catch (IOException e) {
                mediaPlayer = null;
            }
        }
    }

    private static final long VIBRATE_DURATION = 200L;

    private void playBeepSoundAndVibrate() {
        if (playBeep && mediaPlayer != null) {
            mediaPlayer.start();
        }
        if (vibrate) {
            Vibrator vibrator = (Vibrator) getActivity().getSystemService(
                    Context.VIBRATOR_SERVICE);
            vibrator.vibrate(VIBRATE_DURATION);
        }
    }

    /**
     * When the beep has finished playing, rewind to queue up another one.
     */
    private final MediaPlayer.OnCompletionListener beepListener = new MediaPlayer.OnCompletionListener() {
        public void onCompletion(MediaPlayer mediaPlayer) {
            mediaPlayer.seekTo(0);
        }
    };

    /**
     * you should get result like this.
     *
     * String scanResult = data.getExtras().getString("result");
     */


    //扫描结果
    @Override
    public void resturnScanResult(int resultCode, final Result data) {
        playBeepSoundAndVibrate();
        Boolean flag=false;
        flag=netState(getContext());
        if(flag) {
            Bundle bundle = this.getArguments();
            if (bundle.getString("Activity").equals("SingelCpatureActivity")) {
                //Toast.makeText(getActivity(),"sadsa",Toast.LENGTH_SHORT);
                //initDialog(data.getText().toString());
                initDownload(data.getText());
            } else if (bundle.getString("Activity").equals("MultiCpatureActivity")) {
                //Toast.makeText(getActivity(),"sadsa",Toast.LENGTH_SHORT);
                Handler handler=new Handler(){
                    @Override
                    public void handleMessage(Message msg) {
                        super.handleMessage(msg);
                        switch (msg.what){
                            case 1:{
                                Book book=(Book) msg.obj;
                                initDialog(book);
                                break;
                            }
                            default: {
                                //Log.d(TAG,"扫描连接失败");
                                String message="您可以再次扫描或使用单个添加功能手动添加本书。";
                                creatDialog(data.getText().toString(),message);
                            }
                            break;

                        }
                    }
                };
                MultiDataDownload download=new MultiDataDownload();
                download.scanResult(handler,data.getText().toString());

            }
        }

        else{
            String message="您可以手动输入详情或重新扫描";
            creatDialog(data.getText(),message);
        }
    }


    private void initDownload(final String ISBN){
        Handler handler=new Handler(){
            @Override
            public void handleMessage(Message msg){
                super.handleMessage(msg);
                switch (msg.what){
                    case 1:{
                        //Log.d(TAG,"扫描连接成功");
                        Intent intent = getActivity().getIntent();
                        intent.putExtra(Constant.CODED_CONTENT, ISBN);
                        getActivity().setResult(RESULT_OK, intent);
                        getActivity().finish();
                        break;
                    }
                    default: {
                        //Log.d(TAG,"扫描连接失败");
                        String message="您可以手动输入详情或重新扫描";
                        creatDialog(ISBN,message);
                    }
                    break;

                }
            }
        };

        DataDownload data=new DataDownload();
        data.scanResult(handler,ISBN);
    }

    //自己写的代码，用来创建一个dialog
    private  void creatDialog(final String ISBN,String message){
        AlertDialog.Builder dialog=new AlertDialog.Builder(getActivity());
        dialog.setTitle("无法获取详情");
        dialog.setMessage("此书的ISBN是"+ISBN+",我们无法获取书籍详情,请检查ISBN,"+message);
        dialog.setPositiveButton("手动输入", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = getActivity().getIntent();
                intent.putExtra(Constant.CODED_CONTENT, ISBN);
                getActivity().setResult(408, intent);
                getActivity().finish();
            }
        });

        dialog.setNegativeButton("重新扫描", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                restartScan();
            }
        });

        dialog.create();
        dialog.show();
    }


    //判断是否有网络
    private Boolean netState(Context context){
        if (context != null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
            if (mNetworkInfo != null && mNetworkInfo.isConnected()) {
                return true;
            }
            else {
                return false;
            }
        }
        return false;

    }






    //用来进行重新扫描
    private void restartScan(){

        //viewfinderView.setCameraManager(CameraManager.get());
        handler = null;

        surfaceHolder = previewView.getHolder();
        if (hasSurface) {

            initCamera(surfaceHolder);
        } else {
            // 重置callback，等待surfaceCreated()来初始化camera
            surfaceHolder.addCallback(this);
        }

        beepManager.updatePrefs();
        initBeepSound();
        inactivityTimer.onResume();
    }

    @Override
    public void launchProductQuary(String url) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        startActivity(intent);
    }



    public void initDialog(Book book){
        final ImageView img = new ImageView(getActivity());
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(100,
                100);
        img.setLayoutParams(params);
        img.setImageBitmap(book.getBitmap());

        AlertDialog.Builder dialog=new AlertDialog.Builder(getContext() );
        dialog.setTitle("确认添加");
        dialog.setMessage("确认添加图书:《"+book.getTitle()+"》?");
        dialog.setView(img);
        dialog.setCancelable(false);
        dialog.setPositiveButton("添加", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //initDownload(et.getText().toString());
                restartScan();
            }
        });

        dialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                restartScan();
            }
        });

        AlertDialog alertDialog=dialog.create();
        alertDialog.show();

    }

}