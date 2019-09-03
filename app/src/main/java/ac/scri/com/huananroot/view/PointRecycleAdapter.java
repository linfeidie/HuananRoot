package ac.scri.com.huananroot.view;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import ac.scri.com.huananroot.R;
import ac.scri.com.huananroot.SiteNode;

/**
 * 文件描述：.
 * <p>
 * 作者：Created by linfeidie on 2019/8/20
 * <p>
 * 版本号：HuananRoot
 */

/**
 * Created by qzs on 2017/9/04.
 */
public class PointRecycleAdapter extends RecyclerView.Adapter<PointRecycleAdapter.MyViewHolder> {
    private Context context;

    private List<SiteNode> list;
    public PointRecycleAdapter(Context context, List<SiteNode> list) {
        this.context = context;
        this.list = list;
    }

    public void setList(List<SiteNode> list) {
        this.list = list;
        notifyDataSetChanged();
    }
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        MyViewHolder holder = new MyViewHolder(LayoutInflater.from(
                context).inflate(R.layout.item_site_point, parent,
                false));
        return holder;
    }
    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        SiteNode siteNode = list.get(position);
        holder.tv_name.setText(siteNode.nodeName);
        holder.tv_num.setText(getItemCount()+"");
        holder.tv_dir.setText(siteNode.noteDir+"");
        holder.tv_work.setText(siteNode.isWork?"是":"否");
    }
    @Override
    public int getItemCount() {
        return list.size();
    }
    //  添加数据
    public void addData(int position,SiteNode siteNode) {
//      在list中添加数据，并通知条目加入一条
//        SiteNode siteNode = new SiteNode();
//        siteNode.nodeName = "我是节点"+position;
        list.add(position, siteNode);
        //添加动画
        notifyItemInserted(position);
    }
    //  删除数据
    public void removeData(int position) {
        list.remove(position);
        //删除动画
        notifyItemRemoved(position);
        notifyDataSetChanged();
    }
    /**
     * ViewHolder的类，用于缓存控件
     */
    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tv_name;
        TextView tv_num;
        TextView tv_dir;
        TextView tv_work;
        //因为删除有可能会删除中间条目，然后会造成角标越界，所以必须整体刷新一下！
        public MyViewHolder(View view) {
            super(view);
            tv_name = view.findViewById(R.id.tv_name);
           tv_num =  view.findViewById(R.id.tv_num);
            tv_dir =  view.findViewById(R.id.tv_dir);
            tv_work =  view.findViewById(R.id.tv_work);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(context, "当前点击 "+ list.get(getLayoutPosition()), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}
