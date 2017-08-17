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

import com.cccg.example.musictest.utils.MySinAudioTrack;

public class SmallCarActivity extends AppCompatActivity implements View.OnTouchListener {
    private Button btnPlay250;                  //播放250HZ、1KHZ、3KHZ正弦波音频的按钮
    private Button btnPlay1K;
    private Button btnPlay3K;
    private MySinAudioTrack atk250;             //播放250HZ、1KHZ、3KHZ正弦波音频的自定义AudioTrack
    private MySinAudioTrack atk1K;
    private MySinAudioTrack atk3K;
    private boolean isPlay250=false;            //标记250HZ、1KHZ、3KHZ正弦波音频的播放与否
    private boolean isPlay1K=false;
    private boolean isPlay3K=false;
    private AudioManager audioManager;          //通过AudioManager获取音量信息
    private int volume;                         //不低于最大音量80%的音量大小

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
        atk250=new MySinAudioTrack(125);
        atk1K=new MySinAudioTrack(500);
        atk3K=new MySinAudioTrack(1500);
        atk250.setVolume(MySinAudioTrack.LEFT);
        atk1K.setVolume(MySinAudioTrack.LEFT);
        atk3K.setVolume(MySinAudioTrack.RIGHT);
        //开启线程，监测AudioTrack的播放状态，执行对应的AudioTrack的播放操作
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true){
                    if (isPlay250){
                        atk250.play();
                    }
                    if (isPlay1K){
                        atk1K.play();
                    }
                    if (isPlay3K){
                        atk3K.play();
                    }
                }
            }
        }).start();
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
                        atk250.stop();
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
                        atk1K.stop();
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
                        atk3K.stop();
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
