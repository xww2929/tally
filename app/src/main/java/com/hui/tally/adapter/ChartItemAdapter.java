package com.hui.tally.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.hui.tally.R;
import com.hui.tally.db.ChartItemBean;
import com.hui.tally.utils.FloatUtils;

import java.util.List;

/*
* 账单详情页面，listView的适配器
* */
public class ChartItemAdapter extends BaseAdapter {
    Context context;
    List<ChartItemBean>mDatas;

    LayoutInflater inflater;
    public ChartItemAdapter(Context context, List<ChartItemBean> mDatas) {
        this.context = context;
        this.mDatas = mDatas;
        inflater=LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return mDatas.size();
    }

    @Override
    public Object getItem(int i) {
        return mDatas.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder holder=null;
        if (view==null) {
            view=inflater.inflate(R.layout.item_chartfrag_lv,viewGroup,false);
            holder=new ViewHolder(view);
            view.setTag(holder);
        }else{
            holder= (ViewHolder) view.getTag();
        }
        //获取显示内容
        ChartItemBean bean=mDatas.get(i);
        holder.iv.setImageResource(bean.getsImageId());
        holder.typeTv.setText(bean.getType());
        float ratio=bean.getRatio();
        String pert=FloatUtils.ratioToPercent(ratio);
        holder.ratioTv.setText(pert);

        holder.totalTv.setText("￥"+bean.getTotalMoney());

        return view;
    }



    class ViewHolder{
        TextView typeTv,ratioTv,totalTv;
        ImageView iv;
        public ViewHolder(View view){
            typeTv=view.findViewById(R.id.item_chartfrag_tv_type);
            ratioTv=view.findViewById(R.id.item_chartfrag_tv_pert);
            totalTv=view.findViewById(R.id.item_chartfrag_tv_sum);
            iv=view.findViewById(R.id.item_chartfrag_iv);

        }
    }


}
