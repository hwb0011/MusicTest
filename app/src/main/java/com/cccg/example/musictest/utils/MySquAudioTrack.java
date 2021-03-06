package com.cccg.example.musictest.utils;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.util.Log;

/**
 * Created by CCCG-黄文镔 on 2017/7/20.
 */

public class MySquAudioTrack {
    private static final String TAG = "MySquAudioTrack";

    public static final int NONE=0;             //声道标志
    public static final int LEFT=1;
    public static final int RIGHT=2;
    public static final int DOUBLE=3;
    public static short[] over=new short[4*(MyWave.units+MyWave.unit+MyWave.unit_+MyWave.unit)];        //数据波结束信号波

    private float maxVolume;                    //最大音量值
    private AudioTrack audioTrack;              //音轨播放器

    public MySquAudioTrack(){          //配置相应数量数据波的AudioTrack
        over= GetWave.squ(over,4);
        int buffer=AudioTrack.getMinBufferSize(MyWave.RATE,AudioFormat.CHANNEL_CONFIGURATION_STEREO,AudioFormat.ENCODING_PCM_16BIT);
        audioTrack=new AudioTrack(AudioManager.STREAM_MUSIC, MyWave.RATE,AudioFormat.CHANNEL_CONFIGURATION_STEREO,AudioFormat.ENCODING_PCM_16BIT, buffer*2, AudioTrack.MODE_STREAM);
        maxVolume=audioTrack.getMaxVolume();
    }

    public void play(short[] wave,int waveLen){
        if (audioTrack!=null){
            audioTrack.play();                  //开始播放音频
            audioTrack.write(wave,0,waveLen);   //写入要播放的音频数据
        }
    }

    public void stop(){
        if (audioTrack!=null){
            audioTrack.stop();                  //停止播放音频
            audioTrack.flush();                 //清空音频数据
        }
    }

    public void over(){
        if(audioTrack!=null){
            audioTrack.play();                  //开始播放音频
            audioTrack.write(over,0,over.length);       //写入两段停止信号
            audioTrack.write(over,0,over.length);
        }
    }

    public void close(){
        if (audioTrack!=null){
            audioTrack.stop();
            audioTrack.release();
        }
    }

    public void setVolume(int channel){         //设置音频播放的声道
        switch (channel){
            case NONE:
                audioTrack.setStereoVolume(0,0);
                break;
            case LEFT:
                audioTrack.setStereoVolume(maxVolume,0);
                break;
            case RIGHT:
                audioTrack.setStereoVolume(0,maxVolume);
                break;
            case DOUBLE:
                audioTrack.setStereoVolume(maxVolume,maxVolume);
                break;
            default:break;
        }
    }
}