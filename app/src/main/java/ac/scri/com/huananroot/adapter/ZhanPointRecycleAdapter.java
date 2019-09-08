package ac.scri.com.huananroot.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import ac.scri.com.huananroot.R;
import ac.scri.com.huananroot.SiteNode;
import ac.scri.com.huananroot.Tool;

/**
 * 文件描述：.
 * <p>
 * 作者：Created by linfeidie on 2019/8/21
 * <p>
 * 版本号：HuananRoot
 */
public class ZhanPointRecycleAdapter extends RecyclerView.Adapter<ZhanPointRecycleAdapter.ZPViewHolder> {

    private Context context;
    private List<SiteNode> list;
    public ZhanPointRecycleAdapter(Context context, List<SiteNode> list) {
        this.context = context;
        this.list = list;
    }

    public void addData(List<SiteNode> list) {

        this.list = list;
        //添加动画
        //notifyItemInserted(position);
        notifyDataSetChanged();
    }
    @NonNull
    @Override
    public ZPViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int position) {
        ZPViewHolder holder = new ZPViewHolder(LayoutInflater.from(
                context).inflate(R.layout.item_zhan_point, viewGroup,
                false));
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ZPViewHolder holder, int position) {
        SiteNode siteNode = list.get(position);
        holder.tv_name.setText(siteNode.nodeName);
        holder.tv_num.setText(holder.getAdapterPosition()+"");
        holder.tv_current.setText(Tool.trimStr(siteNode.dirs.toString()).replace(",","->"));
    }

    @Override
    public int getItemCount() {
        return list == null?0:list.size();
    }

    class ZPViewHolder extends RecyclerView.ViewHolder {
        TextView tv_name;
        TextView tv_num;
        TextView tv_current;
        //因为删除有可能会删除中间条目，然后会造成角标越界，所以必须整体刷新一下！
        public ZPViewHolder(View view) {
            super(view);
            tv_name = view.findViewById(R.id.tv_name);
            tv_num =  view.findViewById(R.id.tv_num);
            tv_current =  view.findViewById(R.id.tv_current);
        }
    }
}
