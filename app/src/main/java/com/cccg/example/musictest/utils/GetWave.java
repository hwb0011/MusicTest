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

public class GetWave {
    public static int waveLen;                        //一个周期波长

    public static short[] squ(short[] wave, int count) {     //生成对应数据波波形
        waveLen = 4*(MyWave.units+MyWave.unit)+count*(MyWave.unit_+MyWave.unit);
        int j;
        for(int i=0;i<waveLen;i++){
            if((i>=0&&i<MyWave.units)||
                    (i>=MyWave.units+MyWave.unit&&i<2*MyWave.units+MyWave.unit)||
                    (i>=2*MyWave.units+2*MyWave.unit&&i<3*MyWave.units+2*MyWave.unit)||
                    (i>=3*MyWave.units+3*MyWave.unit&&i<4*MyWave.units+3*MyWave.unit)){
                wave[i]=32767;
            }else if((i>=MyWave.units&&i<MyWave.units+MyWave.unit)||
                    (i>=2*MyWave.units+MyWave.unit&&i<2*MyWave.units+2*MyWave.unit)||
                    (i>=3*MyWave.units+2*MyWave.unit&&i<3*MyWave.units+3*MyWave.unit)||
                    (i>=4*MyWave.units+3*MyWave.unit&&i<4*MyWave.units+4*MyWave.unit)){
                wave[i]=-32767;
            }else {
                j=(i-4*(MyWave.units+MyWave.unit))%(MyWave.unit_+MyWave.unit);
                if(j<MyWave.unit_){
                    wave[i]=32767;
                }else {
                    wave[i]=-32767;
                }
            }
        }
        return wave;
    }

    public static short[] sin(short[] wave, int rate) {        //生成正弦波波形
        waveLen = MyWave.RATE/rate;
        for (int i = 0; i < waveLen; i++) {
            wave[i] =   (short) (32767 * (Math.sin(2*Math.PI* ((i % waveLen) * 1.00 / waveLen))));
        }
        return wave;
    }
}
