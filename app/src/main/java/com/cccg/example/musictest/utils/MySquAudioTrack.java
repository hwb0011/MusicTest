package com.cccg.example.musictest.utils;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;

/**
 * Created by CCCG-黄文镔 on 2017/7/20.
 */

public class MySquAudioTrack {

    public static final int RATE=88200;         //音频采样率，16位时为88200，8位时为44100
    public static final int NONE=0;             //声道标志
    public static final int LEFT=1;
    public static final int RIGHT=2;
    public static final int DOUBLE=3;
    public static final int units=266;          //三类数据波长度
    public static final int unit_=89;
    public static final int unit=88;
    public static short[] over=new short[8*(units+unit+unit_+unit)];        //数据波结束信号波

    private short[] wave=new short[RATE];       //记录生成的波形
    private int waveLen;                        //一个周期波长
    private float maxVolume;                    //最大音量值
    private AudioTrack audioTrack;              //音轨播放器

    public MySquAudioTrack(int count){          //配置相应数量数据波的AudioTrack
        over=squ(over,over.length/2,over.length);
        if(count>0){
            waveLen = 4*(units+unit)+count*(unit_+unit);
            int buffer=AudioTrack.getMinBufferSize(RATE,AudioFormat.CHANNEL_CONFIGURATION_STEREO,AudioFormat.ENCODING_PCM_16BIT);
            audioTrack=new AudioTrack(AudioManager.STREAM_MUSIC, RATE,AudioFormat.CHANNEL_CONFIGURATION_STEREO,AudioFormat.ENCODING_PCM_16BIT, buffer*2, AudioTrack.MODE_STREAM);
            wave=squ(wave,waveLen, waveLen);
            maxVolume=audioTrack.getMaxVolume();
        }else{
            return;
        }
    }

    public void play(){
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
            audioTrack.write(over,0,over.length);       //写入停止信号
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

    private short[] squ(short[] wave, int waveLen,int length) {     //生成对应数据波波形
        int j,k;
        for(int i=0;i<length;i++){
            j=i%waveLen;
            if((j>=0&&j<units)||(j>=units+unit&&j<2*units+unit)||(j>=2*units+2*unit&&j<3*units+2*unit)||(j>=3*units+3*unit&&j<4*units+3*unit)){
                wave[i]=32767;
            }else if((j>=units&&j<units+unit)||(j>=2*units+unit&&j<2*units+2*unit)||(j>=3*units+2*unit&&j<3*units+3*unit)||(j>=4*units+3*unit&&j<4*units+4*unit)){
                wave[i]=-32767;
            }else {
                k=(j-4*(units+unit))%(unit_+unit);
                if(k<unit_){
                    wave[i]=32767;
                }else {
                    wave[i]=-32767;
                }
            }
        }
        return wave;
    }
}