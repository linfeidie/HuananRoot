package ac.scri.com.huananroot;

import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.kongqw.serialportlibrary.CommandControl;
import com.kongqw.serialportlibrary.Device;
import com.kongqw.serialportlibrary.SerialPortManager;
import com.kongqw.serialportlibrary.listener.OnOpenSerialPortListener;
import com.kongqw.serialportlibrary.listener.OnSerialPortDataListener;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import ac.scri.com.huananroot.adapter.ZhanPointRecycleAdapter;
import ac.scri.com.huananroot.view.PointRecycleAdapter;
import ac.scri.com.huananroot.view.nicedialog.BaseNiceDialog;
import ac.scri.com.huananroot.view.nicedialog.NiceDialog;
import ac.scri.com.huananroot.view.nicedialog.ViewConvertListener;
import ac.scri.com.huananroot.view.nicedialog.ViewHolder;

public class MainActivity2 extends AppCompatActivity implements OnOpenSerialPortListener {

    public static final String TAG = MainActivity2.class.getSimpleName();

    // Used to load the 'native-lib' library on application startup.
//    static {
//        System.loadLibrary("native-lib");
//    }
    private SerialPortManager mSerialPortManager;
    private SharedPreferencesHelper sharedPreferencesHelper;
    private static volatile boolean isStarted = false;
    public static final int TEST = 0;
    public static final int PRODUCE = 1;
    private int crrentEnv = TEST;
    private boolean stateChange = false;
    private Toast mToast;
    private static int index = 0;
    private PointRecycleAdapter adapter;
    private ZhanPointRecycleAdapter zhanPointRecycleAdapter;
    private RecyclerView mRecyclerView;
    private RecyclerView rv_zhan_point;
    private RecyclerView recyclerView;
    private Button iv_add;
    private Button bt_start;
    private TextView tv_state, tv_power, tv_loading, tv_error, tv_zhan_point;
    private TextView tv_empty_site,tv_empty_zhan;
    private EditText et_ip_address;
    private List<SiteNode> siteNodes ;//;
    private static List<SiteNode> zhanPoints = new ArrayList<SiteNode>();
    public List<String> mDirs = new ArrayList<>();
    //private HorizontalProgressViewModel model;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);//横屏
        setContentView(R.layout.activity_main_real);
        sharedPreferencesHelper = new SharedPreferencesHelper(
                MainActivity2.this, "huanan");
        //recyclerView = (RecyclerView) findViewById(R.id.rv_progress);

        // 引用
        //model = new HorizontalProgressViewModel();

       //model.setViewUp(this, recyclerView, null);
        TextView tv = (TextView) findViewById(R.id.sample_text);
        initSerialPort();

        initView();
        initRecycle();
        initSocket();
        iv_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 添加自带默认动画
                NiceDialog.init().setLayoutId(R.layout.dialog_site_add)
                        .setConvertListener(new ViewConvertListener() {
                            String dir = "上";

                            @Override
                            public void convertView(final ViewHolder holder, final BaseNiceDialog dialog) {

                                ((RadioButton) holder.getView(R.id.rb_dir_top)).setChecked(true);
                                ((RadioButton) holder.getView(R.id.rb_work_no)).setChecked(true);
                                ((RadioGroup) holder.getView(R.id.radioGroup_dir)).setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                                    @Override
                                    public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
                                        RadioButton radioButton_checked = (RadioButton) radioGroup.findViewById(checkedId);
                                        dir = radioButton_checked.getText().toString();
                                    }
                                });
                                holder.setOnClickListener(R.id.bt_cancel, new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        dialog.dismiss();
                                    }
                                });
                                holder.setOnClickListener(R.id.bt_sure, new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {

                                        String name = ((EditText) holder.getView(R.id.et_name)).getText().toString();


                                        if (TextUtils.isEmpty(name)) {
                                            Toast.makeText(MainActivity2.this, "信息不完整", Toast.LENGTH_SHORT).show();
                                            return;
                                        }

                                        int position = siteNodes.size();
                                        //  在list中添加数据，并通知条目加入一条
                                        SiteNode siteNode = new SiteNode();
                                        siteNode.nodeName = name;
                                        siteNode.noteDir = dir;
                                        siteNode.isWork = ((RadioButton) holder.getView(R.id.rb_work_yes)).isChecked();
                                        mDirs.add(dir);
                                        if (siteNode.isWork) {


                                            try {
                                                List<String> copy = Tool.deepCopy(mDirs);
                                                siteNode.setDirs(copy);
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }

                                            mDirs.clear();

                                        }
                                        siteNodes.add(siteNode);
                                        zhanPoints = Tool.where(siteNodes, new Tool.Where<SiteNode>() {
                                            @Override
                                            public boolean where(SiteNode obj) {

                                                return obj.isWork;
                                            }
                                        });

                                        zhanPointRecycleAdapter.addData(zhanPoints);
                                       // model.updateData(zhanPoints);
                                        if(zhanPointRecycleAdapter.getItemCount() != 0) {
                                            tv_empty_zhan.setVisibility(View.GONE);
                                        }

                                        //adapter.addData(position,siteNode);

                                        adapter.setList(siteNodes);
                                        if (adapter.getItemCount() != 0) {
                                            tv_empty_site.setVisibility(View.GONE);
                                        }
                                        dialog.dismiss();
                                    }
                                });
                            }
                        })
                        .setDimAmount(0.3f)
                        .setPosition(Gravity.CENTER)
                        .setWidth(400)
                        .setOutCancel(false)
                        .show(getSupportFragmentManager());

            }
        });


    }

    private void initSocket() {
        TaskCenter.sharedCenter().setDisconnectedCallback(new TaskCenter.OnServerDisconnectedCallbackBlock() {
            @Override
            public void callback(IOException e) {
                MainActivity2.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        showToast("断开连接");
                    }
                });

            }
        });


        TaskCenter.sharedCenter().setConnectedCallback(new TaskCenter.OnServerConnectedCallbackBlock() {
            @Override
            public void callback() {
                MainActivity2.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        showToast("连接成功");
                    }
                });

            }
        });
        TaskCenter.sharedCenter().setReceivedCallback(new TaskCenter.OnReceiveCallbackBlock() {
            @Override
            public void callback(final byte[] receicedbyges) {

                MainActivity2.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(receicedbyges[0] != 0X7F || receicedbyges[receicedbyges.length-1] != 0X7E) {
                            return;
                        }
                        if(receicedbyges[3] == 0X02 && receicedbyges[4] == 0X00) {
                            //showToast("可以发送下一条路线");
                            Log.d(TAG,"可以走下一条线路");
                            changelLine();
                        }else if(receicedbyges[3] == 0X01 && receicedbyges[4] == 0x01) {//PLC收货
                            //showToast("收货");
                            boolean sendBytes = mSerialPortManager.sendBytes(CommandControl.deliver_goods());
                            Log.i(TAG, sendBytes?"发送成功":"发送失败");
                        }else if(receicedbyges[3] == 0X01 && receicedbyges[4] == 0x02) {//PLC发货
                            //showToast("发货");
                            boolean sendBytes = mSerialPortManager.sendBytes(CommandControl.receiving_goods());
                            Log.i(TAG, sendBytes?"发送成功":"发送失败");
                        }
                    }
                });

            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        String siteNodesStr = (String) sharedPreferencesHelper.getSharedPreference("siteNodes", null);
        Gson gson = new Gson();
        Type listType = new TypeToken<List<SiteNode>>() {
        }.getType();
        siteNodes = gson.fromJson(siteNodesStr, listType);
        if(siteNodes == null) {
            siteNodes = new ArrayList<SiteNode>();
            return;
        }
        adapter.setList(siteNodes);
        zhanPoints = Tool.where(siteNodes, new Tool.Where<SiteNode>() {
            @Override
            public boolean where(SiteNode obj) {

                return obj.isWork;
            }
        });

        zhanPointRecycleAdapter.addData(zhanPoints);
        //model.updateData(zhanPoints);
        if(zhanPointRecycleAdapter.getItemCount() != 0) {
            tv_empty_zhan.setVisibility(View.GONE);
        }

        //adapter.addData(position,siteNode);

        adapter.setList(siteNodes);
        if (adapter.getItemCount() != 0) {
            tv_empty_site.setVisibility(View.GONE);
        }

    }

    private void initSerialPort() {
        Device device = new Device("ttySAC3", "g_serial", new File("/dev/ttySAC3"));
        Log.i(TAG, "onCreate: device = " + device);
        if (null == device) {
            finish();
            return;
        }

        mSerialPortManager = new SerialPortManager();

        // 打开串口
        boolean openSerialPort = mSerialPortManager.setOnOpenSerialPortListener(this)
                .setOnSerialPortDataListener(new OnSerialPortDataListener() {
                    @Override
                    public void onDataReceived(final byte[] bytes) {
                        Log.i(TAG, "onDataReceived [ String ]: " + new String(bytes));
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                boolean isRunning = ((bytes[4]) & 0X1) == 1;
                                Log.i(TAG, "状态:" + (isRunning ? "运行" : "空闲"));
                                if (isRunning) {
                                    isStarted = true;
                                }
                                boolean isLoading = ((bytes[4] >> 1) & 0X1) == 1;
                                Log.i(TAG, "是否装载:" + (isLoading ? "装载" : "空载"));
                                boolean isError = ((bytes[4] >> 2) & 0X1) == 1;
                                Log.i(TAG, "是否异常:" + (isError ? "异常" : "正常"));
                                int currentLine = (int) bytes[5];
                                Log.i(TAG, "当前路线:" + currentLine);
                                int power = (int) bytes[6];
                                Log.i(TAG, "当前电量:" + power);
                                int isOrder = (int) bytes[7];
                                Log.i(TAG, "是否发命令:" + isOrder);

                                tv_state.setText("状态:" + (isRunning ? "运行" : "空闲"));
                                tv_loading.setText("装载情况:" + (isLoading ? "装载" : "空载"));
                                tv_error.setText("异常情况:" + (isError ? "异常" : "正常"));
                                tv_power.setText("电量:" + power + "%");
                                tv_zhan_point.setText("当前路线:" + currentLine);


                                if (isOrder == 1 && obtainDirs() != null) {//准备完成了，向PLC发命令

                                    //changelLine();
                                    byte state = (byte) (bytes[4] | (byte) 0x08);//把进站状态加上
                                    TaskCenter.sharedCenter().send(CommandControl.orderToPLC(state));
                                }
                            }
                        });
                    }


                    @Override
                    public void onDataSent(byte[] bytes) {
                        Log.i(TAG, "onDataSent [ byte[] ]: " + Arrays.toString(bytes));
                        Log.i(TAG, "onDataSent [ String ]: " + new String(bytes));
                        final byte[] finalBytes = bytes;
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                //showToast(String.format("发送\n%s", new String(finalBytes)));
                            }
                        });
                    }
                })
                .openSerialPort(device.getFile(), 115200);

        Log.i(TAG, "onCreate: openSerialPort = " + openSerialPort);
    }

    //封装下一条路线
    private void changelLine() {
        if (index == obtainDirs().size()) {
            //Toast.makeText(MainActivity2.this, "已经走完全程", Toast.LENGTH_SHORT).show();
            bt_start.setEnabled(true);
            index = 0;
            //gotoOther(Tool.trimStr(obtainDirs().get(index)));
        } else {
            gotoOther(Tool.trimStr(obtainDirs().get(index)));
            index++;

        }
    }

    private void initRecycle() {
        //  纵向滑动
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        siteNodes = initData();
        adapter = new PointRecycleAdapter(MainActivity2.this, siteNodes);
        mRecyclerView.setAdapter(adapter);
//      添加动画
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        LinearLayoutManager linearLayoutManager2 = new LinearLayoutManager(this);
        rv_zhan_point.setLayoutManager(linearLayoutManager2);
        rv_zhan_point.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        zhanPointRecycleAdapter = new ZhanPointRecycleAdapter(MainActivity2.this, zhanPoints);
        rv_zhan_point.setItemAnimator(new DefaultItemAnimator());
        rv_zhan_point.setAdapter(zhanPointRecycleAdapter);
    }

    private void initView() {
        iv_add = (Button) findViewById(R.id.iv_add);
        bt_start = findViewById(R.id.bt_start);//启动
        mRecyclerView = (RecyclerView) findViewById(R.id.rv_site);
        rv_zhan_point = findViewById(R.id.rv_zhan_point);

        tv_state = findViewById(R.id.tv_state);
        tv_power = findViewById(R.id.tv_power);
        tv_error = findViewById(R.id.tv_error);
        tv_loading = findViewById(R.id.tv_loading);
        tv_zhan_point = findViewById(R.id.tv_zhan_point);

        tv_empty_site = findViewById(R.id.tv_empty_site);
        tv_empty_zhan = findViewById(R.id.tv_empty_zhan);
        et_ip_address = findViewById(R.id.et_ip_address);

    }

    protected ArrayList<SiteNode> initData() {
        ArrayList<SiteNode> mDatas = new ArrayList<SiteNode>();
//        for (int i = 0; i < 1; i++) {
//            SiteNode siteNode = new SiteNode();
//            siteNode.nodeName = "出库节点" ;
//            siteNode.nodeNum = 1;
//            siteNode.noteDir = "上";
//            siteNode.isWork = false;
//            mDatas.add(siteNode);
//        }
        return mDatas;
    }

    public void bt_start(View view) {
        if(bt_start.isEnabled()) {
            NiceDialog.init().setLayoutId(R.layout.dialog_request_sure).setConvertListener(new ViewConvertListener() {
                @Override
                public void convertView(ViewHolder holder, final BaseNiceDialog dialog) {
                    holder.setOnClickListener(R.id.bt_sure, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (obtainDirs() == null || obtainDirs().size() < 1) {
                                showToast("请添加站点");
                                return;
                            }
                            gotoOther(Tool.trimStr(obtainDirs().get(index)));
                            index ++;
                            bt_start.setEnabled(false);
                            dialog.dismiss();

                        }
                    });
                    holder.setOnClickListener(R.id.bt_cancel, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            dialog.dismiss();
                        }
                    });
                }
            }).setDimAmount(0.3f).setPosition(Gravity.CENTER).setWidth(400).setOutCancel(false).show(getSupportFragmentManager());
        }else {
            showToast("机器人还没走完全程,不可点击");
        }

    }
    //急停
    public void bt_stop(View view){
        cleanSite();
        if(mSerialPortManager != null) {
            mSerialPortManager.stop();
        }


    }
    /*
     * 清除站点
     * */
    public void bt_delete(View view) {
        NiceDialog.init().setLayoutId(R.layout.dialog_request_sure).setConvertListener(new ViewConvertListener() {
            @Override
            public void convertView(ViewHolder holder, final BaseNiceDialog dialog) {
                ((TextView)holder.getView(R.id.tv_message)).setText("确定全部删除吗?");
                holder.setOnClickListener(R.id.bt_sure, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        cleanSite();
                        dialog.dismiss();
                    }
                });
                holder.setOnClickListener(R.id.bt_cancel, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });
            }
        }).setDimAmount(0.3f).setPosition(Gravity.CENTER).setWidth(400).setOutCancel(false).show(getSupportFragmentManager());




    }

    /*
    * 清除节点数据
    * */
    private void cleanSite() {
        index = 0;
        siteNodes.clear();
        adapter.notifyDataSetChanged();
        tv_empty_site.setVisibility(View.VISIBLE);
        zhanPoints.clear();
        zhanPointRecycleAdapter.notifyDataSetChanged();
        bt_start.setEnabled(true);
        tv_empty_zhan.setVisibility(View.VISIBLE);
        mDirs.clear();
        sharedPreferencesHelper.clear();
    }

    private List<String> obtainDirs() {
        if (zhanPoints.size() == 0) {
            return null;
        }
        List<String> dirs = new ArrayList<>();
        for (int i = 0; i < zhanPoints.size(); i++) {
            dirs.add(zhanPoints.get(i).dirs.toString());
        }
        return dirs;
    }

    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     */
    // public native String stringFromJNI();
    @Override
    public void onSuccess(File device) {
        //Toast.makeText(getApplicationContext(), String.format("串口 [%s] 打开成功", device.getPath()), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onFail(File device, Status status) {
        switch (status) {
            case NO_READ_WRITE_PERMISSION:
                //showDialog(device.getPath(), "没有读写权限");xs
                break;
            case OPEN_FAIL:
            default:
                showDialog(device.getPath(), "串口打开失败");
                break;
        }
    }

    /**
     * 显示提示框
     *
     * @param title   title
     * @param message message
     */
    private void showDialog(String title, String message) {
        new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("退出", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                        finish();
                    }
                })
                .setCancelable(false)
                .create()
                .show();
    }

    private void gotoOther(String line) {

        mSerialPortManager.lineOrder(line);
        Log.i(TAG, "gotoOther()===" + line);


//        Log.i(TAG, "onSend: sendBytes = " + sendBytes);
//        showToast(sendBytes ? "发送成功" : "发送失败");
    }

    /**
     * Toast
     *
     * @param content content
     */
    private void showToast(String content) {
        if (null == mToast) {
            mToast = Toast.makeText(getApplicationContext(), null, Toast.LENGTH_SHORT);
        }
        mToast.setText(content);
        mToast.show();
    }


    @Override
    protected void onStop() {
        super.onStop();
        Gson gson = new Gson();
        String data = gson.toJson(siteNodes);
        sharedPreferencesHelper.put("siteNodes", data);
    }

    public void connect(View view) {
        String addressPort = et_ip_address.getText().toString();
        if(TextUtils.isEmpty(addressPort)) {
            showToast("IP地址不能为空");
            return;
        }
        String[] adp = addressPort.trim().split(":");
        if(adp.length != 2|| TextUtils.isEmpty(adp[0]) || TextUtils.isEmpty(adp[1])) {
            showToast("IP端口设置不正确,请重新设置");
            et_ip_address.setText("");
            return;
        }

        try {
            TaskCenter.sharedCenter().connect(adp[0],Integer.parseInt(adp[1]));
        } catch (NumberFormatException e) {
            e.printStackTrace();
            showToast("IP端口设置不正确,请重新设置");
        }
    }

    public void disconnect(View view) {
        //TaskCenter.sharedCenter().disconnect();

//        byte state = 0X0A;
//        TaskCenter.sharedCenter().send(CommandControl.orderToPLC(state));

//        byte a = 0X02;
//        byte b = 0X08;
//
//        byte c = (byte) (a|b);
//
//        showToast("IP");

        index = 0;
        bt_start.setEnabled(true);
    }
/*
* 修改超声参数
* */
    public void bt_supersound(View view){
        NiceDialog.init().setLayoutId(R.layout.dialog_supersound_fix).setConvertListener(new ViewConvertListener() {
            @Override
            public void convertView(final ViewHolder holder, final BaseNiceDialog dialog) {
                holder.setOnClickListener(R.id.tv_sure, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String front = ((EditText)holder.getView(R.id.et_front)).getText().toString();
                        String after = ((EditText)holder.getView(R.id.et_after)).getText().toString();
                        try {
                            int frontInt = Integer.parseInt(front);
                            int afterInt = Integer.parseInt(after);
                            mSerialPortManager.sendBytes(CommandControl.fix_supersound((byte) frontInt,(byte) afterInt));
                            dialog.dismiss();
                        } catch (NumberFormatException e) {
                            e.printStackTrace();
                            showToast("参数非法");
                        }
                    }
                });

            }
        }).setDimAmount(0.3f).setPosition(Gravity.CENTER).setWidth(400).setOutCancel(true).show(getSupportFragmentManager());
    }


}
