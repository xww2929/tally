package com.hui.tally;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.hui.tally.adapter.ChartVPAdapter;
import com.hui.tally.db.DBManager;
import com.hui.tally.frag_chart.IncomChartFragment;
import com.hui.tally.frag_chart.OutcomChartFragment;
import com.hui.tally.frag_record.IncomeFragment;
import com.hui.tally.frag_record.OutcomeFragment;
import com.hui.tally.utils.CalendarDialog;

import java.time.Year;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class MonthChatActivity extends AppCompatActivity {

    Button inBtn,outBtn;

    TextView dateTv,inTv,outTv;
    ViewPager chartVp;
    private int year;
    private int month;
    int selectPos = -1,selectMonth = -1;
    List<Fragment>chartFragList;

    private ChartVPAdapter chartVPAdapter;
    private IncomChartFragment incomChartFragment;
    private OutcomChartFragment outcomChartFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_month_chat);
        initView();
        initTime();
        initStatistics(year,month);
        initFrag();
        setVPSelectListener();
    }

    private void setVPSelectListener() {
        chartVp.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener(){
            @Override
            public void onPageSelected(int position) {
                setButtonStyle(position);
            }
        });
    }

    private void initFrag() {
        chartFragList=new ArrayList<>();
        //添加fragment对象
        incomChartFragment = new IncomChartFragment();
        outcomChartFragment = new OutcomChartFragment();
        //添加数据到fragment当中
        Bundle bundle=new Bundle();
        bundle.putInt("year",year);
        bundle.putInt("month",month);
        incomChartFragment.setArguments(bundle);
        outcomChartFragment.setArguments(bundle);
        //将Fragment添加到数据源当中
        chartFragList.add(outcomChartFragment);
        chartFragList.add(incomChartFragment);
        //使用适配器
        chartVPAdapter = new ChartVPAdapter(getSupportFragmentManager(), chartFragList);
        chartVp.setAdapter(chartVPAdapter);
        //将fragment加载到activity中


    }

    //初始化某年某月的收支情况
    private void initStatistics(int year, int month) {
        float inMoneyOneMonth=DBManager.getSumMoneyOneMonth(year,month,1);//收入总钱数
        float outMoneyOneMonth=DBManager.getSumMoneyOneMonth(year,month,0);//支出总钱数
        int incountItemOneMonth=DBManager.getCountItemOneMonth(year,month,1);//收入多少笔
        int outcountItemOneMonth=DBManager.getCountItemOneMonth(year,month,0);//支出多少笔
        dateTv.setText(year+"年"+month+"月账单");
        inTv.setText("共"+incountItemOneMonth+"笔收入，￥ "+inMoneyOneMonth);
        outTv.setText("共"+outcountItemOneMonth+"笔支出，￥ "+outMoneyOneMonth);


    }

    //初始化时间的方法
    private void initTime() {
        Calendar calendar=Calendar.getInstance();
        year =calendar.get(Calendar.YEAR);
        month=calendar.get(calendar.MONTH)+1;

    }

    private void initView() {
        inBtn=findViewById(R.id.chart_btn_in);
        outBtn=findViewById(R.id.chart_btn_out);
        dateTv=findViewById(R.id.chart_tv_date);
        inTv=findViewById(R.id.chart_tv_in);
        outTv=findViewById(R.id.chart_tv_out);
        chartVp=findViewById(R.id.chart_vp);

    }

    public void onClick(View view){
        if (view.getId()==R.id.chart_iv_back){
            finish();
        }else if(view.getId()==R.id.chart_iv_rili){
            showCalendarDialog();
        }else if(view.getId()==R.id.chart_btn_in){
            setButtonStyle(1);
            chartVp.setCurrentItem(1);
        }else if(view.getId()==R.id.chart_btn_out){
            setButtonStyle(0);
            chartVp.setCurrentItem(0);
        }
    }
    //显示日历的对话框
    private void showCalendarDialog() {
        CalendarDialog dialog=new CalendarDialog(this,selectPos,selectMonth);
        dialog.show();
        dialog.setDialogSize();
        dialog.setOnRefreshListener(new CalendarDialog.OnRefreshListener() {
            @Override
            public void OnRefresh(int selPos, int year, int month) {
                MonthChatActivity.this.selectPos=selPos;
                MonthChatActivity.this.selectMonth=month;
                initStatistics(year,month);
                incomChartFragment.setDate(year,month);
                outcomChartFragment.setDate(year,month);

            }
        });
    }


    //设置按钮样式的改变,支出0，收入1
    private void setButtonStyle(int kind){
        if (kind==0) {
            outBtn.setBackgroundResource(R.drawable.main_recordbtn_bg);
            outBtn.setTextColor(Color.WHITE);
            inBtn.setBackgroundResource(R.drawable.dialog_btn_bg);
            inBtn.setTextColor(Color.BLACK);
        }else {
            inBtn.setBackgroundResource(R.drawable.main_recordbtn_bg);
            inBtn.setTextColor(Color.WHITE);
            outBtn.setBackgroundResource(R.drawable.dialog_btn_bg);
            outBtn.setTextColor(Color.BLACK);
        }
    }
}