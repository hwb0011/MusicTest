package com.cccg.example.musictest.utils;

/**
 * 项目名称：com.cccg.example.musictest.utils
 * 类描述：
 * 创建人：黄文镔
 * 创建时间：2017/8/29
 * 修改人：
 * 修改时间：
 * 修改备注：
 */

public class MyWave {
    public static final int units=132;          //三类数据波长度
    public static final int unit_=44;
    public static final int unit=44;
    public static final int RATE=44100;         //音频采样率，16位时为88200，8位时为44100
    private int waveLen;                        //一个周期波长
    private short[] wave;

    public MyWave(short[] wave,int waveLen){
        this.wave=wave;
        this.waveLen=waveLen;
    }

    public int getWaveLen() {
        return waveLen;
    }

    public void setWaveLen(int waveLen) {
        this.waveLen = waveLen;
    }

    public short[] getWave() {
        return wave;
    }

    public void setWave(short[] wave) {
        this.wave = wave;
    }
}
