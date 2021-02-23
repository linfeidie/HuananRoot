package com.kongqw.serialportlibrary;

/**
 * 文件描述：.
 * <p>
 * 作者：Created by 林飞堞 on 2019/8/28
 * <p>
 * 版本号：SerialProtDongnao
 */
public class CommandControl {
    //启动
    public static byte[] start(){
        byte[] buff = new byte[8];
        buff[0] =  (byte)0X55;
        buff[1] = (byte)0X08;
        buff[2] = (byte)0X01;
        buff[3] = (byte)0X01;
        buff[4] = (byte)0X01;
        buff[5] = (byte)0X00;
        buff[6] = CRC8Util.calcCrc8(buff,0,6);
        buff[7] = (byte)0XAA;

        return buff;

    }


    //停止
    public static byte[] stop(){
        byte[] buff = new byte[8];
        buff[0] =  (byte)0X55;
        buff[1] = (byte)0X08;
        buff[2] = (byte)0X01;
        buff[3] = (byte)0X01;
        buff[4] = (byte)0X00;
        buff[5] = (byte)0X00;
        buff[6] = CRC8Util.calcCrc8(buff,0,6);
        buff[7] = (byte)0XAA;

        return buff;

    }

    //移动一个线路(按钮例如:加工区3)
    public static byte[] moveLine(byte[] bytes){
        int size = bytes.length + 7 ;
        byte[] buff = new byte[size];
        buff[0] =  (byte)0X55;
        buff[1] = (byte)(size & 0xff);
        buff[2] = (byte)0X01;
        buff[3] = (byte)0X02;
        buff[4] = (byte)0X01;
        for (int i = 0; i < bytes.length; i++) {
            buff[5+i] = (byte) bytes[i];//左
        }
        buff[size-2] = CRC8Util.calcCrc8(buff,0,size-2);
        buff[size-1] = (byte)0XAA;

        return buff;
    }

    //回程
    public static byte[] goback(){
        byte[] buff = new byte[8];
        buff[0] =  (byte)0X55;
        buff[1] = (byte)0X08;
        buff[2] = (byte)0X01;
        buff[3] = (byte)0X02;
        buff[4] = (byte)0X02;
        buff[5] = (byte)0X00;

        buff[6] = CRC8Util.calcCrc8(buff,0,6);
        buff[7] = (byte)0XAA;

        return buff;
    }

    //屏幕传给PLC状态  state状态外面传进来
    public static byte[] orderToPLC(byte state){
        byte[] buff = new byte[6];
        buff[0] =  (byte)0X7e;
        buff[1] = (byte)0X06;
        buff[2] = (byte)0X01;
        buff[3] = state;//状态
        buff[4] = CRC8Util.calcCrc8(buff,0,4);
        buff[5] = (byte)0X7f;

        return buff;
    }
/*
* PLC下发控制
* */
    public static byte[] PLCOrder(byte function){
        byte[] buff = new byte[7];
        buff[0] =  (byte)0X7f;
        buff[1] = (byte)0X07;
        buff[2] = (byte)0X01;//AGVid
        buff[3] = function;//功能码
        buff[4] = 00;//数据
        buff[5] = CRC8Util.calcCrc8(buff,0,5);
        buff[6] = (byte)0X7e;
        return buff;
    }

/*
* 发货
* */
    public static byte[] deliver_goods() {
        byte[] buff = new byte[8];
        buff[0] = (byte) 0X55;
        buff[1] = (byte) 0X08;
        buff[2] = (byte) 0X01;
        buff[3] = (byte) 0X01;
        buff[4] = (byte) 0X03;
        buff[5] = (byte) 0X00;
        buff[6] = CRC8Util.calcCrc8(buff, 0, 6);
        buff[7] = (byte) 0XAA;

        return buff;
    }

    /*
     * 收货
     * */
    public static byte[] receiving_goods() {
        byte[] buff = new byte[8];
        buff[0] = (byte) 0X55;
        buff[1] = (byte) 0X08;
        buff[2] = (byte) 0X01;
        buff[3] = (byte) 0X01;
        buff[4] = (byte) 0X04;
        buff[5] = (byte) 0X00;
        buff[6] = CRC8Util.calcCrc8(buff, 0, 6);
        buff[7] = (byte) 0XAA;

        return buff;
    }

    //修改超声监测
    public static byte[] fix_supersound(byte front,byte after) {
        byte[] buff = new byte[9];
        buff[0] = (byte) 0X55;
        buff[1] = (byte) 0X09;
        buff[2] = (byte) 0X01;
        buff[3] = (byte) 0X03;
        buff[4] = (byte) 0X02;
        buff[5] = front;
        buff[6] = after;
        buff[7] = CRC8Util.calcCrc8(buff, 0, 7);
        buff[8] = (byte) 0XAA;

        return buff;
    }




}
