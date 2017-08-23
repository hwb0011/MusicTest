package com.cccg.example.musictest.utils;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;

/**
 * Created by CCCG-黄文镔 on 2017/7/20.
 */

public class MySinAudioTrack {

    public static final int RATE=44100;         //音频采样率，16位时为88200，8位时为44100
    public static final int NONE=0;             //声道标志
    public static final int LEFT=1;
    public static final int RIGHT=2;
    public static final int DOUBLE=3;

    private short[] wave=new short[RATE];       //记录生成的波形
    private int length;                         //整段波长
    private int waveLen;                        //一个周期波长
    private float maxVolume;                    //最大音量值
    private AudioTrack audioTrack;              //音轨播放器

    public MySinAudioTrack(int rate){           //配置对应音频频率的AudioTrack
        if(rate>0){
            waveLen = RATE/rate;
            length = waveLen * rate;
            int buffer=AudioTrack.getMinBufferSize(RATE,AudioFormat.CHANNEL_CONFIGURATION_STEREO,AudioFormat.ENCODING_PCM_8BIT);
            audioTrack=new AudioTrack(AudioManager.STREAM_MUSIC, RATE,AudioFormat.CHANNEL_CONFIGURATION_STEREO,AudioFormat.ENCODING_PCM_8BIT, buffer*2, AudioTrack.MODE_STREAM);
            wave=sin(wave, waveLen, length);
            maxVolume=audioTrack.getMaxVolume();
        }else{
            return;
        }
    }

    public void play(){
        if (audioTrack!=null){
            audioTrack.play();                  //开始播放音频
            audioTrack.write(wave,0,length);    //写入要播放的音频数据
        }
    }

    public void stop(){
        if (audioTrack!=null){
            audioTrack.stop();                  //停止播放音频
            audioTrack.flush();                 //清空音频数据
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

    private short[] sin(short[] wave, int waveLen, int length) {        //生成正弦波波形
        for (int i = 0; i < length; i++) {
            wave[i] =   (short) (32767 * (Math.sin(2*Math.PI* ((i % waveLen) * 1.00 / waveLen))));
        }
        return wave;
    }
}
