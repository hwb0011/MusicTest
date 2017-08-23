package com.cccg.example.musictest;

import android.content.Intent;
import android.media.AudioManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.cccg.example.musictest.utils.MySquAudioTrack;

public class BigCarActivity extends AppCompatActivity implements View.OnTouchListener {

    private ImageButton btnGo;                      //前后左右四个方向控制按钮
    private ImageButton btnBack;
    private ImageButton btnLeft;
    private ImageButton btnRight;
    private MySquAudioTrack atkGo;                  //前后左右四个方向对应的自定义方波AudioTrack
    private MySquAudioTrack atkBack;
    private MySquAudioTrack atkLeft;
    private MySquAudioTrack atkRight;
    private MySquAudioTrack atkGoLeft;              //前进并左转AudioTrack
    private MySquAudioTrack atkGoRight;             //前进并右转AudioTrack
    private MySquAudioTrack atkBackLeft;            //后退并左转AudioTrack
    private MySquAudioTrack atkBackRight;           //后退并右转AudioTrack
    private static final int STATE_NONE=0;          //播放状态标志值
    private static final int STATE_GO=1;
    private static final int STATE_BACK=2;
    private static final int STATE_LEFT=3;
    private static final int STATE_RIGHT=4;
    private static final int STATE_GO_LEFT=5;
    private static final int STATE_GO_RIGHT=6;
    private static final int STATE_BACK_LEFT=7;
    private static final int STATE_BACK_RIGHT=8;
    private int state;                              //记录播放状态标志
    private int volume;                             //不低于最大音量80%的音量大小
    private AudioManager audioManager;              //通过AudioManager获取音量信息
    private Thread mThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_big_car);
        initView();
        audioManager=(AudioManager) getSystemService(AUDIO_SERVICE);
        int max = audioManager.getStreamMaxVolume( AudioManager.STREAM_MUSIC );         //获取媒体音量的最大值
        int current = audioManager.getStreamVolume( AudioManager.STREAM_MUSIC );        //获取当前媒体音量的值
        while (0.8*max>=current) {                  //限定音量不低于最大音量的80%
            //当前音量低于最大音量的80%时，增加音量值，再获取增加后的音量值
            audioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_RAISE, AudioManager.FLAG_SHOW_UI);
            current = audioManager.getStreamVolume( AudioManager.STREAM_MUSIC );
        }
        volume=current;                             //记录下不低于最大音量80%的音量值
        initData();
    }

    @Override
    protected void onResume() {                     //在onResume方法中对音量进行限制，防止用户在应用外对音量大小进行了操作而影响应用
        super.onResume();
        int max = audioManager.getStreamMaxVolume( AudioManager.STREAM_MUSIC );         //获取媒体音量的最大值
        int current = audioManager.getStreamVolume( AudioManager.STREAM_MUSIC );        //获取当前媒体音量的值
        while (0.8*max>=current) {                  //限定音量不低于最大音量的80%
            //当前音量低于最大音量的80%时，增加音量值，再获取增加后的音量值
            audioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_RAISE, AudioManager.FLAG_SHOW_UI);
            current = audioManager.getStreamVolume( AudioManager.STREAM_MUSIC );
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode==KeyEvent.KEYCODE_VOLUME_DOWN&&volume>=audioManager.getStreamVolume(AudioManager.STREAM_MUSIC)){
            //当用户按下音量减小键，判断当前音量是否高于记录音量值，若不高于则屏蔽按键并弹出提示
            Toast.makeText(BigCarActivity.this,"已屏蔽音量减小按键，遥控器需要在较大媒体音量下使用。",Toast.LENGTH_SHORT).show();
            return true;
        }
        return super.onKeyDown(keyCode,event);
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(BigCarActivity.this,MainActivity.class));
        mThread.interrupt();
        mThread=null;
        finish();
    }

    private void initView() {                       //初始化按钮并添加触摸监听事件
        btnGo=(ImageButton) findViewById(R.id.btn_go);
        btnGo.setOnTouchListener(this);
        btnBack=(ImageButton) findViewById(R.id.btn_back);
        btnBack.setOnTouchListener(this);
        btnLeft=(ImageButton) findViewById(R.id.btn_left);
        btnLeft.setOnTouchListener(this);
        btnRight=(ImageButton) findViewById(R.id.btn_right);
        btnRight.setOnTouchListener(this);
    }


    private void initData() {
        //初始化AudioTrack数据波数量与声道
        atkGo=new MySquAudioTrack(10);
        atkGo.setVolume(MySquAudioTrack.LEFT);
        atkBack=new MySquAudioTrack(40);
        atkBack.setVolume(MySquAudioTrack.LEFT);
        atkLeft=new MySquAudioTrack(58);
        atkLeft.setVolume(MySquAudioTrack.LEFT);
        atkRight=new MySquAudioTrack(64);
        atkRight.setVolume(MySquAudioTrack.LEFT);
        atkGoLeft=new MySquAudioTrack(28);
        atkGoLeft.setVolume(MySquAudioTrack.LEFT);
        atkGoRight=new MySquAudioTrack(34);
        atkGoRight.setVolume(MySquAudioTrack.LEFT);
        atkBackLeft=new MySquAudioTrack(52);
        atkBackLeft.setVolume(MySquAudioTrack.LEFT);
        atkBackRight=new MySquAudioTrack(46);
        atkBackRight.setVolume(MySquAudioTrack.LEFT);
        //开启线程，监测AudioTrack的播放状态，执行对应的AudioTrack的播放操作
        mThread=new Thread(new Runnable() {
            @Override
            public void run() {
                while (true){
                    switch (state){
                        case STATE_NONE:
                            break;
                        case STATE_GO:
                            atkGo.play();
                            break;
                        case STATE_BACK:
                            atkBack.play();
                            break;
                        case STATE_LEFT:
                            atkLeft.play();
                            break;
                        case STATE_RIGHT:
                            atkRight.play();
                            break;
                        case STATE_GO_LEFT:
                            atkGoLeft.play();
                            break;
                        case STATE_GO_RIGHT:
                            atkGoRight.play();
                            break;
                        case STATE_BACK_LEFT:
                            atkBackLeft.play();
                            break;
                        case STATE_BACK_RIGHT:
                            atkBackRight.play();
                            break;
                        default:
                            break;
                    }
                }
            }
        });
        mThread.start();
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        //当按钮按下时，记录对应的AudioTrack播放标志。
        //当按钮松开时，记录对应的AudioTrack播放标志，立即停止AudioTrack的播放，播放数据波停止音频。
        switch (view.getId()){
            case R.id.btn_go:
                switch (motionEvent.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        if(btnLeft.isPressed()){
                            state=STATE_GO_LEFT;
                            atkLeft.stop();
                            atkLeft.over();
                        }else if (btnRight.isPressed()){
                            state=STATE_GO_RIGHT;
                            atkRight.stop();
                            atkRight.over();
                        }else if(btnBack.isPressed()){
                            Toast.makeText(BigCarActivity.this,"前进后退不可以同时按",Toast.LENGTH_SHORT).show();
                        }else {
                            state=STATE_GO;
                        }
                        break;
                    case MotionEvent.ACTION_UP:
                        state=STATE_NONE;
                        atkGo.stop();
                        atkGoLeft.stop();
                        atkGoRight.stop();
                        atkGo.over();
                        if(btnLeft.isPressed()){
                            state=STATE_LEFT;
                        }else if (btnRight.isPressed()){
                            state=STATE_RIGHT;
                        }
                        break;
                    default:
                        break;
                }
                break;
            case R.id.btn_back:
                switch (motionEvent.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        if(btnLeft.isPressed()){
                            state=STATE_BACK_LEFT;
                            atkLeft.stop();
                            atkLeft.over();
                        }else if (btnRight.isPressed()){
                            state=STATE_BACK_RIGHT;
                            atkRight.stop();
                            atkRight.over();
                        }else if(btnGo.isPressed()){
                            Toast.makeText(BigCarActivity.this,"前进后退不可以同时按",Toast.LENGTH_SHORT).show();
                        }else {
                            state=STATE_BACK;
                        }
                        break;
                    case MotionEvent.ACTION_UP:
                        state=STATE_NONE;
                        atkBack.stop();
                        atkBackLeft.stop();
                        atkBackRight.stop();
                        atkBack.over();
                        if(btnLeft.isPressed()){
                            state=STATE_LEFT;
                        }else if (btnRight.isPressed()){
                            state=STATE_RIGHT;
                        }
                        break;
                    default:
                        break;
                }
                break;
            case R.id.btn_left:
                switch (motionEvent.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        if(btnGo.isPressed()){
                            state=STATE_GO_LEFT;
                            atkGo.stop();
                            atkGo.over();
                        }else if (btnBack.isPressed()){
                            state=STATE_BACK_LEFT;
                            atkBack.stop();
                            atkBack.over();
                        }else if(btnRight.isPressed()){
                            Toast.makeText(BigCarActivity.this,"左转右转不可以同时按",Toast.LENGTH_SHORT).show();
                        }else {
                            state=STATE_LEFT;
                        }
                        break;
                    case MotionEvent.ACTION_UP:
                        state=STATE_NONE;
                        atkLeft.stop();
                        atkGoLeft.stop();
                        atkBackLeft.stop();
                        atkLeft.over();
                        if(btnGo.isPressed()){
                            state=STATE_GO;
                        }else if (btnBack.isPressed()){
                            state=STATE_BACK;
                        }
                        break;
                    default:
                        break;
                }
                break;
            case R.id.btn_right:
                switch (motionEvent.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        if(btnGo.isPressed()){
                            state=STATE_GO_RIGHT;
                            atkGo.stop();
                            atkGo.over();
                        }else if (btnBack.isPressed()){
                            state=STATE_BACK_RIGHT;
                            atkBack.stop();
                            atkBack.over();
                        }else if(btnLeft.isPressed()){
                            Toast.makeText(BigCarActivity.this,"左转右转不可以同时按",Toast.LENGTH_SHORT).show();
                        }else {
                            state=STATE_RIGHT;
                        }
                        break;
                    case MotionEvent.ACTION_UP:
                        state=STATE_NONE;
                        atkRight.stop();
                        atkGoRight.stop();
                        atkBackRight.stop();
                        atkRight.over();
                        if(btnGo.isPressed()){
                            state=STATE_GO;
                        }else if (btnBack.isPressed()){
                            state=STATE_BACK;
                        }
                        break;
                    default:
                        break;
                }
                break;
            default:
                break;
        }
        return false;
    }
}
