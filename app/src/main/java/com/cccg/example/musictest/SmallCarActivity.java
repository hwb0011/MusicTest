package com.cccg.example.musictest;

import android.content.Intent;
import android.media.AudioManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.cccg.example.musictest.utils.GetWave;
import com.cccg.example.musictest.utils.MySinAudioTrack;
import com.cccg.example.musictest.utils.MyWave;

public class SmallCarActivity extends AppCompatActivity implements View.OnTouchListener {
    private Button btnPlay250;                  //播放250HZ、1KHZ、3KHZ正弦波音频的按钮
    private Button btnPlay1K;
    private Button btnPlay3K;
    private MySinAudioTrack mAudioTrack;
    private MyWave wave250;
    private MyWave wave1K;
    private MyWave wave3K;
    private boolean isPlay250=false;            //标记250HZ、1KHZ、3KHZ正弦波音频的播放与否
    private boolean isPlay1K=false;
    private boolean isPlay3K=false;
    private AudioManager audioManager;          //通过AudioManager获取音量信息
    private int volume;                         //不低于最大音量80%的音量大小
    private boolean isThreadRun=true;
    private MyRunnable mRunnable;
    private Thread mThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_small_car);
        initView();
        audioManager=(AudioManager) getSystemService(AUDIO_SERVICE);                    //获取系统的音量管理器
        int max = audioManager.getStreamMaxVolume( AudioManager.STREAM_MUSIC );         //获取媒体音量的最大值
        int current = audioManager.getStreamVolume( AudioManager.STREAM_MUSIC );        //获取当前媒体音量的值
        while (0.8*max>=current) {              //限定音量不低于最大音量的80%
            //当前音量低于最大音量的80%时，增加音量值，再获取增加后的音量值
            audioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_RAISE, AudioManager.FLAG_SHOW_UI);
            current = audioManager.getStreamVolume( AudioManager.STREAM_MUSIC );
        }
        volume=current;                         //记录下不低于最大音量80%的音量值
        initData();
    }

    @Override
    protected void onResume() {                 //在onResume方法中对音量进行限制，防止用户在应用外对音量大小进行了操作而影响应用
        super.onResume();
        int max = audioManager.getStreamMaxVolume( AudioManager.STREAM_MUSIC );         //获取媒体音量的最大值
        int current = audioManager.getStreamVolume( AudioManager.STREAM_MUSIC );        //获取当前媒体音量的值
        while (0.8*max>=current) {              //限定音量不低于最大音量的80%
            //当前音量低于最大音量的80%时，增加音量值，再获取增加后的音量值
            audioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_RAISE, AudioManager.FLAG_SHOW_UI);
            current = audioManager.getStreamVolume( AudioManager.STREAM_MUSIC );
        }
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(SmallCarActivity.this,MainActivity.class));
        isThreadRun=false;
        finish();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode==KeyEvent.KEYCODE_VOLUME_DOWN&&volume>=audioManager.getStreamVolume(AudioManager.STREAM_MUSIC)){
            //当用户按下音量减小键，判断当前音量是否高于记录音量值，若不高于则屏蔽按键并弹出提示
            Toast.makeText(SmallCarActivity.this,"已屏蔽音量减小按键，遥控器需要在最大媒体音量下使用。",Toast.LENGTH_SHORT).show();
            return true;
        }
        return super.onKeyDown(keyCode,event);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mThread.interrupt();
        mThread=null;
        mRunnable=null;
        mAudioTrack.close();
        mAudioTrack=null;
        audioManager=null;
        btnPlay250=null;
        btnPlay1K=null;
        btnPlay3K=null;
        wave250=null;
        wave1K=null;
        wave3K=null;
    }

    private void initView(){                    //初始化按钮并添加触摸监听事件
        btnPlay250=(Button) findViewById(R.id.btn_play250);
        btnPlay1K=(Button) findViewById(R.id.btn_play1K);
        btnPlay3K=(Button) findViewById(R.id.btn_play3K);
        btnPlay250.setOnTouchListener(this);
        btnPlay1K.setOnTouchListener(this);
        btnPlay3K.setOnTouchListener(this);
    }

    private void initData(){
        //初始化AudioTrack频率与声道
        wave250=new MyWave(GetWave.sin(new short[MyWave.RATE],125),GetWave.waveLen);
        wave1K=new MyWave(GetWave.sin(new short[MyWave.RATE],500),GetWave.waveLen);
        wave3K=new MyWave(GetWave.sin(new short[MyWave.RATE],1500),GetWave.waveLen);
        mAudioTrack=new MySinAudioTrack();
        mAudioTrack.setVolume(MySinAudioTrack.LEFT);
        //开启线程，监测AudioTrack的播放状态，执行对应的AudioTrack的播放操作
        mRunnable=new MyRunnable();
        mThread=new Thread(mRunnable);
        mThread.start();
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        //当按钮按下时，让对应的AudioTrack播放标志置true。
        //当按钮松开时，让对应的AudioTrack播放标志置false，立即停止AudioTrack的播放。
        switch (view.getId()){
            case R.id.btn_play250:
                switch (motionEvent.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        isPlay250=true;
                        break;
                    case MotionEvent.ACTION_UP:
                        isPlay250=false;
                        break;
                    default:
                        break;
                }
                break;
            case R.id.btn_play1K:
                switch (motionEvent.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        isPlay1K=true;
                        break;
                    case MotionEvent.ACTION_UP:
                        isPlay1K=false;
                        break;
                    default:
                        break;
                }
                break;
            case R.id.btn_play3K:
                switch (motionEvent.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        isPlay3K=true;
                        break;
                    case MotionEvent.ACTION_UP:
                        isPlay3K=false;
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

    class MyRunnable implements Runnable{

        @Override
        public void run() {
            while (isThreadRun){
                if (isPlay250){
                    mAudioTrack.play(wave250.getWave(),wave250.getWaveLen());
                }
                if (isPlay1K){
                    mAudioTrack.play(wave1K.getWave(),wave1K.getWaveLen());
                }
                if (isPlay3K){
                    mAudioTrack.play(wave3K.getWave(),wave3K.getWaveLen());
                }
            }
        }
    }
}
