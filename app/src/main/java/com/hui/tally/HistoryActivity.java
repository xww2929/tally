package com.hui.tally;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.hui.tally.adapter.AccountAdapter;
import com.hui.tally.db.AccountBean;
import com.hui.tally.db.DBManager;
import com.hui.tally.utils.CalendarDialog;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class HistoryActivity extends AppCompatActivity {
    ListView historyLv;
    TextView timeTv;
    List<AccountBean> mDatas;
    AccountAdapter adapter;
    int year,month;
    int dialogSelPos = -1;
    int dialogSelMonth = -1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        historyLv=findViewById(R.id.history_lv);
        timeTv=findViewById(R.id.history_tv_time);
        mDatas=new ArrayList<>();
        adapter=new AccountAdapter(this,mDatas);
        historyLv.setAdapter(adapter);
        initTime();
        timeTv.setText(year+"年"+month+"月");
        loadData(year,month);
        setLVClickListener();
    }
    //设置ListView的每一个Item长按事件
    private void setLVClickListener() {
        historyLv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                AccountBean accountBean=mDatas.get(i);
                deleteItem(accountBean);
                return false;
            }
        });
    }

    private void deleteItem(AccountBean accountBean) {
        int delId=accountBean.getId();
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setTitle("提示信息").setMessage("您确定要删除这条记录吗？")
                .setNegativeButton("取消",null)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        DBManager.deleteItemFromAccounttbById(delId);
                        mDatas.remove(accountBean);//实时刷新，从数据源移除
                        adapter.notifyDataSetChanged();
                    }
                });
        builder.create().show();
    }
    //获取指定年份月份收支情况的列表
    private void loadData(int year,int month) {
        List<AccountBean> list=DBManager.getAccountListOneMonthFromAccounttb(year,month);
        mDatas.clear();
        mDatas.addAll(list);
        adapter.notifyDataSetChanged();
    }

    private void initTime() {
        Calendar calendar=Calendar.getInstance();
        year=calendar.get(Calendar.YEAR);
        month=calendar.get(Calendar.MONTH)+1;

    }

    public void onClick(View view){
        if(view.getId()==R.id.history_iv_back){
            finish();
        } else if (view.getId()==R.id.history_iv_rili) {
            CalendarDialog dialog=new CalendarDialog(this,dialogSelPos,dialogSelMonth);
            dialog.show();
            dialog.setDialogSize();
            dialog.setOnRefreshListener(new CalendarDialog.OnRefreshListener() {
                @Override
                public void OnRefresh(int selPos, int year, int month) {
                    timeTv.setText(year+"年"+month+"月");
                    loadData(year,month);
                    dialogSelPos = selPos;
                    dialogSelMonth = month;
                }
            });
        }
    }
}