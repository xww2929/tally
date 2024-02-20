package com.hui.tally.frag_chart;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.IValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.utils.ViewPortHandler;
import com.hui.tally.R;
import com.hui.tally.db.BarChartItemBean;
import com.hui.tally.db.DBManager;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class OutcomChartFragment extends BaseChartFragment {
    int kind=0;
    @Override
    public void onResume() {
        super.onResume();
        loadData(year,month,kind);
    }

    @Override
    protected void setAxisData(int year, int month) {
        List<IBarDataSet> sets=new ArrayList<>();
        //获取这个月每天的总金额
        List<BarChartItemBean> list=DBManager.getSumMoneyOneDayInMonth(year,month,kind);
        if (list.size()==0) {
            barChart.setVisibility(View.GONE);
            chartTv.setVisibility(View.VISIBLE);
        }else {
            barChart.setVisibility(View.VISIBLE);
            chartTv.setVisibility(View.GONE);
            //设置有多少根柱子
            List<BarEntry>barEntries=new ArrayList<>();
            for (int i = 0; i < 31; i++) {
                //初始化每一个柱子，添加到柱状图中
                BarEntry entry=new BarEntry(i,0.0f);
                barEntries.add(entry);
            }
            for (int i = 0; i < list.size(); i++) {
                BarChartItemBean itemBean=list.get(i);
                int day=itemBean.getDay();//获取日期
                //根据天数，获取x轴的位置
                int xIndex=day-1;
                BarEntry barEntry=barEntries.get(xIndex);
                barEntry.setY(itemBean.getSummoney());
            }
            BarDataSet barDataSet=new BarDataSet(barEntries,"");
            barDataSet.setValueTextColor(Color.BLACK);
            barDataSet.setValueTextSize(8f);
            barDataSet.setColor(Color.RED);
            //设置柱子上数据显示的格式
            barDataSet.setValueFormatter(new IValueFormatter() {
                @Override
                public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
                    if (value==0) {
                        return "";
                    }
                    return value+"";
                }
            });

            sets.add(barDataSet);

            BarData barData=new BarData(sets);
            barData.setBarWidth(0.4f);//设置柱子的宽度
            barChart.setData(barData);
        }
    }

    @Override
    protected void setYAxis(int year, int month) {
        //获取本月最高的一天为多少，将他设置为y轴最大值
        float maxMoney= DBManager.getMaxMoneyOneDayInMonth(year,month,kind);
        float max= (float) Math.ceil(maxMoney);//向上取整
        //设置y轴
        YAxis yAxis_right=barChart.getAxisRight();
        yAxis_right.setAxisMaximum(max);
        yAxis_right.setAxisMinimum(0f);
        yAxis_right.setEnabled(false);

        YAxis yAxis_left=barChart.getAxisLeft();
        yAxis_left.setAxisMaximum(max);
        yAxis_left.setAxisMinimum(0f);
        yAxis_left.setEnabled(false);

        //设置不显示图例
        Legend legend =barChart.getLegend();
        legend.setEnabled(false);
    }


    @Override
    public void setDate(int year, int month) {
        super.setDate(year, month);
        loadData(year,month,kind);
    }
}