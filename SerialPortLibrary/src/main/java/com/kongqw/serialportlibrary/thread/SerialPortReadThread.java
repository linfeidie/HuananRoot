package com.kongqw.serialportlibrary.thread;

import android.util.Log;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Kongqw on 2017/11/14.
 * 串口消息读取线程
 */

public abstract class SerialPortReadThread extends Thread {

    public abstract void onDataReceived(byte[] bytes);

    private static final String TAG = SerialPortReadThread.class.getSimpleName();
    private InputStream mInputStream;
    private byte[] mReadBuffer;

    public SerialPortReadThread(InputStream inputStream) {
        mInputStream = inputStream;

    }
    byte[] fdata = null;
    long lastTime ;
    @Override
    public void run() {
        mReadBuffer = new byte[1024];
        super.run();

        while (!isInterrupted()) {
            try {
                if (null == mInputStream) {
                    return;
                }

                Log.i(TAG, "run: ");
                int size = mInputStream.read(mReadBuffer);


                if (-1 == size || 0 >= size) {
                    return;
                }
                long current = System.currentTimeMillis();
                if(current - lastTime > 300) {//大于500毫秒  缓存数组清0
                    fdata = null;
//                    Log.d("linfd",current +"");
//                    Log.d("linfd",lastTime +"");
//                    Log.d("linfd",(current - lastTime)+"");
                }
                lastTime = System.currentTimeMillis();

                byte[] readBytes = new byte[size];

                System.arraycopy(mReadBuffer, 0, readBytes, 0, size);

                if(fdata == null) {
                    fdata = new byte[size];
                    System.arraycopy(readBytes,0,fdata,0,size);
                }else {
                    readBytes = addBytes(fdata,readBytes);


                    if(readBytes.length == 10 && readBytes[0] == -86) {
                        Log.i(TAG, "通过啦");
                      //  Log.i(TAG, "run: readBytes = " + new String(readBytes));
                        onDataReceived(readBytes);
                    }

                }
                Log.i(TAG, "run: readBytes = " + new String(readBytes)+"--------"+readBytes.length);



            } catch (IOException e) {
                e.printStackTrace();
                return;
            }
        }
    }

    @Override
    public synchronized void start() {
        super.start();
    }

    /**
     * 关闭线程 释放资源
     */
    public void release() {
        interrupt();

        if (null != mInputStream) {
            try {
                mInputStream.close();
                mInputStream = null;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    public static byte[] addBytes(byte[] data1, byte[] data2) {
        byte[] data3 = new byte[data1.length + data2.length];
        System.arraycopy(data1, 0, data3, 0, data1.length);
        System.arraycopy(data2, 0, data3, data1.length, data2.length);
        return data3;

    }


}
