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

import com.kongqw.serialportlibrary.Device;
import com.kongqw.serialportlibrary.SerialPortManager;
import com.kongqw.serialportlibrary.listener.OnOpenSerialPortListener;
import com.kongqw.serialportlibrary.listener.OnSerialPortDataListener;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import ac.scri.com.huananroot.adapter.ZhanPointRecycleAdapter;
import ac.scri.com.huananroot.view.HorizontalProgressViewModel;
import ac.scri.com.huananroot.view.MainActivity;
import ac.scri.com.huananroot.view.Node;
import ac.scri.com.huananroot.view.PointRecycleAdapter;
import ac.scri.com.huananroot.view.nicedialog.BaseNiceDialog;
import ac.scri.com.huananroot.view.nicedialog.NiceDialog;
import ac.scri.com.huananroot.view.nicedialog.ViewConvertListener;
import ac.scri.com.huananroot.view.nicedialog.ViewHolder;

public class MainActivity2 extends AppCompatActivity implements OnOpenSerialPortListener {

    public static final String TAG = MainActivity2.class.getSimpleName();

    // Used to load the 'native-lib' library on application startup.
    static {
        System.loadLibrary("native-lib");
    }
    private SerialPortManager mSerialPortManager;
    private boolean isStarted = false;
    public static final int TEST = 0;
    public static final int PRODUCE = 1 ;
    private int crrentEnv = TEST;
    private boolean stateChange = false;
    private Toast mToast;
    private int  index = 1;
    private PointRecycleAdapter adapter;
    private ZhanPointRecycleAdapter zhanPointRecycleAdapter;
    private RecyclerView mRecyclerView;
    private RecyclerView rv_zhan_point;
    private Button iv_add;
    private Button bt_start;
    private List<SiteNode> list = new ArrayList<SiteNode>();
    private List<SiteNode> zhanPoints = new ArrayList<SiteNode>();
    public List<String> mDirs = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);//横屏
        setContentView(R.layout.activity_main_real);
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.rv_progress);

        // 引用
        HorizontalProgressViewModel model = new HorizontalProgressViewModel();

        model.setViewUp(this, recyclerView, getProgressList());
        // Example of a call to a native method
        TextView tv = (TextView) findViewById(R.id.sample_text);
        // Example of a call to a native method
        initSerialPort();

        initView();
        initRecycle();
        iv_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 添加自带默认动画
                NiceDialog.init().setLayoutId(R.layout.dialog_site_add)
                        .setConvertListener(new ViewConvertListener() {
                            String dir = "上";
                            @Override
                            public void convertView(final ViewHolder holder, final BaseNiceDialog dialog) {

                                ((RadioButton)holder.getView(R.id.rb_dir_top)).setChecked(true);
                                ((RadioButton)holder.getView(R.id.rb_work_yes)).setChecked(true);
                                ((RadioGroup)holder.getView(R.id.radioGroup_dir)).setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                                    @Override
                                    public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
                                        RadioButton radioButton_checked= (RadioButton) radioGroup.findViewById(checkedId);
                                        dir =  radioButton_checked.getText().toString();
                                    }
                                });
                                holder.setOnClickListener(R.id.bt_sure, new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        
                                        String name = ((EditText)holder.getView(R.id.et_name)).getText().toString();


                                        if(TextUtils.isEmpty(name) ) {
                                            Toast.makeText(MainActivity2.this,"信息不完整",Toast.LENGTH_SHORT).show();
                                            return;
                                        }

                                        int position = list.size();
                                      //  在list中添加数据，并通知条目加入一条
                                        SiteNode siteNode = new SiteNode();
                                        siteNode.nodeName = name;
                                        siteNode.noteDir = dir;
                                        siteNode.isWork= ((RadioButton)holder.getView(R.id.rb_work_yes)).isChecked();
                                        mDirs.add(dir);
                                        if(siteNode.isWork) {


                                            try {
                                                List<String> copy  = Tool.deepCopy(mDirs);
                                                siteNode.setDirs(copy);
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }

                                           mDirs.clear();

                                        }
                                        list.add(siteNode);
                                        zhanPoints = Tool.where(list, new Tool.Where<SiteNode>() {
                                            @Override
                                            public boolean where(SiteNode obj) {

                                                return obj.isWork;
                                            }
                                        });

                                        zhanPointRecycleAdapter.addData(zhanPoints);

                                        //adapter.addData(position,siteNode);

                                        adapter.setList(list);

                                        dialog.dismiss();
                                    }
                                });
                            }
                        })
                        .setDimAmount(0.3f)
                        .setPosition(Gravity.CENTER)
                        .setWidth(400)
                        .setOutCancel(true)
                        .show(getSupportFragmentManager());

            }
        });
    }

    private void initSerialPort() {
        Device device = new Device("ttySAC3","g_serial",new File("/dev/ttySAC3"));
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

                                boolean isRunning =  ((bytes[4]) & 0X1)  == 1;
                                Log.i(TAG, "状态:" + (isRunning?"运行":"空闲"));
                                if(isRunning) {
                                    isStarted = true;
                                }
                                boolean isLoading =  ((bytes[4]>>1) & 0X1)  == 1;
                                Log.i(TAG, "是否转载:" + (isLoading?"装载":"空载"));
                                boolean isError =  ((bytes[4]>>2) & 0X1)  == 1;
                                Log.i(TAG, "是否异常:" + (isError?"异常":"正常"));
                                int zhanPoint = (int)bytes[5];
                                Log.i(TAG, "当前站点:" + zhanPoint);
                                int power = (int)bytes[6];
                                Log.i(TAG, "当前电量:" + power);
                                //showToast(+"");

//                                tv_running.setText("状态:" + (isRunning?"运行":"空闲"));
//                                tv_loading.setText("是否转载:" + (isLoading?"装载":"空载"));
//                                tv_error.setText("是否异常:" + (isError?"异常":"正常"));
//                                tv_power.setText("当前电量:" + power+"%");
//                                tv_zhan_point.setText("当前站点:"+zhanPoint);

                                if(!isRunning && isStarted) {//空闲状态

                                    if(crrentEnv == TEST) {

                                        if(!stateChange) {
                                            stateChange = true;
//                                            if(index == obtainDirs().size()) {
//                                              Toast.makeText(MainActivity2.this,"已经走完全程",Toast.LENGTH_SHORT).show();
//                                                index = 0;
//                                            }else{
//                                                gotoOther(obtainDirs().get(index).replace("[","").replace("]",""));
//
//                                                index++;
//                                            }
                                            gotoOther(obtainDirs().get(index).replace("[","").replace("]",""));
                                            index ++;


                                        }


                                    }else if(crrentEnv == PRODUCE && isLoading) {
                                        //gotoOther();
                                    }

                                }else{
                                    stateChange = false;
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
                                showToast(String.format("发送\n%s", new String(finalBytes)));
                            }
                        });
                    }
                })
                .openSerialPort(device.getFile(), 115200);

        Log.i(TAG, "onCreate: openSerialPort = " + openSerialPort);
    }

    /**
     * 模拟节点数据
     * node1.nodeStatus: 0 已完成状态  1正在处理状态  -1待处理状态
     *
     * @return
     */
    private List<Node> getProgressList() {
        List<Node> list = new ArrayList<>();

        Node node1 = new Node();
        Node node2 = new Node();
        Node node3 = new Node();
        Node node4 = new Node();

        node1.nodeName = "出车";
        node1.nodeStatus = 0;

        node2.nodeName = "加工区";
        node2.nodeStatus = 0;

        node3.nodeName = "成品区";
        node3.nodeStatus = 0;

        node4.nodeName = "回车";
        node4.nodeStatus = -1;


        list.add(node1);
        list.add(node2);
        list.add(node3);
        list.add(node4);
        return list;

    }
    private void initRecycle() {
        //  纵向滑动
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(this,DividerItemDecoration.VERTICAL));
        list = initData();
        adapter = new PointRecycleAdapter(MainActivity2.this, list);
        mRecyclerView.setAdapter(adapter);
//      添加动画
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        LinearLayoutManager linearLayoutManager2 = new LinearLayoutManager(this);
       rv_zhan_point.setLayoutManager(linearLayoutManager2);
       rv_zhan_point.addItemDecoration(new DividerItemDecoration(this,DividerItemDecoration.VERTICAL));
       zhanPointRecycleAdapter = new ZhanPointRecycleAdapter(MainActivity2.this,zhanPoints);
       rv_zhan_point.setItemAnimator(new DefaultItemAnimator());
       rv_zhan_point.setAdapter(zhanPointRecycleAdapter);
    }
    private void initView() {
        iv_add = (Button) findViewById(R.id.iv_add);
        bt_start = findViewById(R.id.bt_start);//启动
        mRecyclerView = (RecyclerView) findViewById(R.id.rv_site);
        rv_zhan_point  = findViewById(R.id.rv_zhan_point);

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

    public void bt_start(View view){

        gotoOther(obtainDirs().get(0).replace("[","").replace("]",""));
        Log.i(TAG,  obtainDirs().toString());
       // List<String> dirs = obtainDirs();

       // Toast.makeText(this,dirs.toString(),Toast.LENGTH_SHORT).show();
    }

    private List<String> obtainDirs() {
        if(zhanPoints.size() == 0) {
            return null;
        }
        List<String> dirs= new ArrayList<>();
        for (int i = 0; i < zhanPoints.size(); i++) {
            dirs.add(zhanPoints.get(i).dirs.toString());
        }
        return dirs;
    }

    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     */
    public native String stringFromJNI();

    @Override
    public void onSuccess(File device) {
        Toast.makeText(getApplicationContext(), String.format("串口 [%s] 打开成功", device.getPath()), Toast.LENGTH_SHORT).show();
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
        Log.i(TAG,  "gotoOther()==="+line);



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
}
