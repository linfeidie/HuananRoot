package ac.scri.com.huananroot;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import ac.scri.com.huananroot.adapter.ZhanPointRecycleAdapter;
import ac.scri.com.huananroot.view.HorizontalProgressViewModel;
import ac.scri.com.huananroot.view.Node;
import ac.scri.com.huananroot.view.PointRecycleAdapter;
import ac.scri.com.huananroot.view.nicedialog.BaseNiceDialog;
import ac.scri.com.huananroot.view.nicedialog.NiceDialog;
import ac.scri.com.huananroot.view.nicedialog.ViewConvertListener;
import ac.scri.com.huananroot.view.nicedialog.ViewHolder;

public class MainActivity2 extends AppCompatActivity {

    // Used to load the 'native-lib' library on application startup.
    static {
        System.loadLibrary("native-lib");
    }

    private PointRecycleAdapter adapter;
    private ZhanPointRecycleAdapter zhanPointRecycleAdapter;
    private RecyclerView mRecyclerView;
    private RecyclerView rv_zhan_point;
    private Button iv_add;
    private List<SiteNode> list = new ArrayList<SiteNode>();
    private List<SiteNode> zhanPoints = new ArrayList<SiteNode>();
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

        initView();
        initRecycle();
        iv_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //              添加自带默认动画
                NiceDialog.init().setLayoutId(R.layout.dialog_site_add)
                        .setConvertListener(new ViewConvertListener() {
                            @Override
                            public void convertView(final ViewHolder holder, final BaseNiceDialog dialog) {
                                holder.setOnClickListener(R.id.bt_sure, new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        String name = ((EditText)holder.getView(R.id.et_name)).getText().toString();
                                        String dir = ((EditText)holder.getView(R.id.et_dir)).getText().toString();
                                         String work = ((EditText)holder.getView(R.id.et_work)).getText().toString();
                                        String num = ((EditText)holder.getView(R.id.et_num)).getText().toString();
                                        int position = list.size();
                                      //  在list中添加数据，并通知条目加入一条
                                        SiteNode siteNode = new SiteNode();
                                        siteNode.nodeName = name;
                                        siteNode.nodeNum = Integer.parseInt(num) ;
                                        siteNode.noteDir = dir;
                                        if(work.equals("是")) {
                                            zhanPoints.add(siteNode);
                                            zhanPointRecycleAdapter.addData(zhanPoints);
                                        }
//                                        if(dir.equals("上")) {
//                                            siteNode.dodeDirection = SiteNode.DodeDirection.UP;
//                                        }else  if(dir.equals("下")) {
//                                            siteNode.dodeDirection = SiteNode.DodeDirection.DOWN;
//                                        }else  if(dir.equals("左")) {
//                                            siteNode.dodeDirection = SiteNode.DodeDirection.LEFT;
//                                        }else  if(dir.equals("右")) {
//                                            siteNode.dodeDirection = SiteNode.DodeDirection.RIGHT;
//                                        }
                                            siteNode.isWork = work.equals("是");
                                        //list.add(position, siteNode);
                                        adapter.addData(position,siteNode);

                                        dialog.dismiss();
                                    }
                                });
                            }
                        })
                        .setDimAmount(0.3f)
                        .setPosition(Gravity.CENTER)
                        .setWidth(250)
                        .show(getSupportFragmentManager());

            }
        });
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
        mRecyclerView = (RecyclerView) findViewById(R.id.rv_site);
        rv_zhan_point  = findViewById(R.id.rv_zhan_point);

    }

    protected ArrayList<SiteNode> initData() {
        ArrayList<SiteNode> mDatas = new ArrayList<SiteNode>();
        for (int i = 0; i < 1; i++) {
            SiteNode siteNode = new SiteNode();
            siteNode.nodeName = "出库节点" ;
            siteNode.nodeNum = 1;
            siteNode.noteDir = "上";
            siteNode.isWork = false;
            mDatas.add(siteNode);
        }
        return mDatas;
    }

    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     */
    public native String stringFromJNI();
}
