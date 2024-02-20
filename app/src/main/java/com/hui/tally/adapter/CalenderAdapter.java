package com.hui.tally.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.hui.tally.R;

import java.util.ArrayList;
import java.util.List;

/*
* 历史账单界面，点击日历表，弹出对话框，当中的GridView对应的适配器
* */
public class CalenderAdapter extends BaseAdapter {
    Context context;
    List<String>mDatas;
    public int year;
    public int selPos = -1;

    public void setYear(int year) {
        this.year = year;
        mDatas.clear();
        loadDatas(year);
        notifyDataSetChanged();
    }

    public CalenderAdapter(Context context, int year) {
        this.context = context;
        this.year = year;
        mDatas=new ArrayList<>();
        loadDatas(year);
    }
    private void loadDatas(int year){
        for (int i = 1; i < 13; i++) {
            String data=year+"/"+i;
            mDatas.add(data);
        }
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
        view= LayoutInflater.from(context).inflate(R.layout.item_dialogcal_gv,viewGroup,false);
        TextView tv=view.findViewById(R.id.item_dialogcal_gv_tv);
        tv.setText(mDatas.get(i));
        tv.setBackgroundResource(R.color.grey_f3f3f3);
        tv.setTextColor(Color.BLACK);
        if (i==selPos) {
            tv.setBackgroundResource(R.color.green_006400);
            tv.setTextColor(Color.WHITE);
        }
        return view;
    }
}
