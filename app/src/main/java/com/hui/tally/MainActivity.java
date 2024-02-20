package com.hui.tally;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.hui.tally.adapter.AccountAdapter;
import com.hui.tally.db.AccountBean;
import com.hui.tally.db.DBManager;
import com.hui.tally.utils.BudgetDialog;
import com.hui.tally.utils.MoreDialog;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    ListView todayLv;//展示今日收支情况的listview
    ImageView searchIv;
    Button editBtn;
    ImageButton moreBtn;
    //声明数据源
    List<AccountBean> mDatas;
    AccountAdapter adapter;
    int year,month,day;
    //头布局相关控件
    TextView topOutTv,topInTv,topbudgetTv,topConTv;
    ImageView topShowIv;
    View headerView;
    SharedPreferences preferences;

    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initTime();
        initView();
        preferences =getSharedPreferences("budget", Context.MODE_PRIVATE);
        //添加ListView头布局
        addLVHeaderView();
        mDatas=new ArrayList<>();
        //设置适配器，加载每一行数据到列表中
        adapter=new AccountAdapter(this,mDatas);
        todayLv.setAdapter(adapter);
    }
    //初始化自带的view的方法
    private void initView() {
        todayLv=findViewById(R.id.main_lv);
        editBtn=findViewById(R.id.main_btn_edit);
        moreBtn=findViewById(R.id.main_btn_more);
        searchIv=findViewById(R.id.main_iv_search);
        editBtn.setOnClickListener(this);
        moreBtn.setOnClickListener(this);
        searchIv.setOnClickListener(this);
        setLVLongClickListener();
    }
    //设置listview的长按事件
    private void setLVLongClickListener() {
        todayLv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (i==0) { //点击了头布局
                    return false;
                }
                int pos=i-1;
                AccountBean clickBean=mDatas.get(pos);//拿到正在被点击的这条信息
                //弹出是否删除的对话框
                showDeleteItemDialog(clickBean);
                return false;
            }
        });
    }
    //弹出是否删除某一条记录的对话框
    private void showDeleteItemDialog(AccountBean clickBean) {
        AlertDialog.Builder builder =new AlertDialog.Builder(this);
        builder.setTitle("提示信息").setMessage("您确定要删除这条记录吗？")
                .setNegativeButton("取消",null)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //执行删除的操作
                        DBManager.deleteItemFromAccounttbById(clickBean.getId());//获取id，从数据库中删除
                        mDatas.remove(clickBean);//获取对象，从listview中移除
                        adapter.notifyDataSetChanged();
                        setTopTvShow();//改变头布局textview显示的内容
                    }
                });
        builder.create().show();
    }

    //给listview添加头布局的方法
    private void addLVHeaderView() {
        //将布局转换成view对象
        headerView = getLayoutInflater().inflate(R.layout.item_mainlv_top, null);
        todayLv.addHeaderView(headerView);
        //查找头布局当中的控件
        topOutTv=headerView.findViewById(R.id.item_mainlv_top_tv_out);
        topInTv=headerView.findViewById(R.id.item_mainlv_top_tv_in);
        topbudgetTv=headerView.findViewById(R.id.item_mainlv_top_tv_budget);
        topConTv=headerView.findViewById(R.id.item_mainlv_top_tv_day);
        topShowIv=headerView.findViewById(R.id.item_mainlv_top_iv_hide);

        topbudgetTv.setOnClickListener(this);
        headerView.setOnClickListener(this);
        topShowIv.setOnClickListener(this);


    }
    //获取今日的具体时间
    private void initTime() {
        Calendar calendar=Calendar.getInstance();
        year=calendar.get(Calendar.YEAR);
        month=calendar.get(Calendar.MONTH)+1;
        day=calendar.get(Calendar.DAY_OF_MONTH);
//        year=accountBean.getYear();
//        month=accountBean.getMonth();
//        day=accountBean.getDay();
    }

    //当activity获取焦点时，会调用的方法
    @Override
    protected void onResume() {
        super.onResume();
        loadDBData();
        setTopTvShow();
    }
    //设置头布局当中文本内容的显示
    private void setTopTvShow() {
        //获取今日支出和收入的总金额，显示在view当中
        float incomeOneDay=DBManager.getSumMoneyOneDay(year,month,day,1);
        float outcomeOneDay=DBManager.getSumMoneyOneDay(year,month,day,0);
        String infoOneDay="今日支出 ￥"+outcomeOneDay+"收入 ￥"+incomeOneDay;
        topConTv.setText(infoOneDay);
        //获取本月支出和收入的总金额
        float incomeOneMonth=DBManager.getSumMoneyOneMonth(year,month,1);
        float outcomeOneMonth=DBManager.getSumMoneyOneMonth(year,month,0);
        topInTv.setText("￥"+incomeOneMonth);
        topOutTv.setText("￥"+outcomeOneMonth);
        //设置显示预算剩余
        float bmoney=preferences.getFloat("bmoney",0);//预算
        if (bmoney==0) {
            topbudgetTv.setText("￥ 0");
        }else{
            float syMoney=bmoney-outcomeOneMonth;
            topbudgetTv.setText("￥"+syMoney);
        }
    }

    private void loadDBData() {
        List<AccountBean> list=DBManager.getAccountListOneDayFromAccounttb(year,month,day);
        mDatas.clear();
        mDatas.addAll(list);
        adapter.notifyDataSetChanged();
    }
    //
    @Override
    public void onClick(View view) {
        if (view.getId()==R.id.main_iv_search) {
            startActivity(new Intent(this,SearchActivity.class));
        } else if (view.getId()==R.id.main_btn_edit) {
            startActivity(new Intent(this,RecordActivity.class));
        }else if (view.getId()==R.id.main_btn_more) {
            MoreDialog moreDialog=new MoreDialog(this);
            moreDialog.show();
            moreDialog.setDialogSize();
        }else if (view.getId()==R.id.item_mainlv_top_tv_budget) {
            showBudgetDialog();
        }else if (view.getId()==R.id.item_mainlv_top_iv_hide) {
            //切换TextView明文和密文
            toggleShow();
        }

        if(view==headerView){
            startActivity(new Intent(this,MonthChatActivity.class));
        }
    }
    //显示预算设置对话框
    private void showBudgetDialog() {
        BudgetDialog dialog=new BudgetDialog(this);
        dialog.show();
        dialog.setDialogSize();
        dialog.setOnEnsureListener(new BudgetDialog.OnEnsureListener() {
            @Override
            public void onEnsure(float money) {
                //将预算金额写入到共享参数中，进行存储
                SharedPreferences.Editor editor=preferences.edit();
                editor.putFloat("bmoney",money);
                editor.commit();

                //计算剩余金额
                float outcomeOneMonth=DBManager.getSumMoneyOneMonth(year,month,0);
                float syMoney=money-outcomeOneMonth;//预算剩余
                topbudgetTv.setText("￥"+syMoney);
            }
        });
    }

    //点击头布局眼睛时，明文变密文，密文变明文
    boolean isShow=true;
    private void toggleShow() {
        if (isShow) {//明文转密文
            PasswordTransformationMethod passwordMethod=PasswordTransformationMethod.getInstance();
            topInTv.setTransformationMethod(passwordMethod);//设置隐藏
            topOutTv.setTransformationMethod(passwordMethod);//设置隐藏
            topbudgetTv.setTransformationMethod(passwordMethod);//设置隐藏
            topShowIv.setImageResource(R.mipmap.ih_hide);
            isShow=false;
        }else{//密文转明文
            HideReturnsTransformationMethod hideReturnsMethod=HideReturnsTransformationMethod.getInstance();
            topInTv.setTransformationMethod(hideReturnsMethod);//设置隐藏
            topOutTv.setTransformationMethod(hideReturnsMethod);//设置隐藏
            topbudgetTv.setTransformationMethod(hideReturnsMethod);//设置隐藏
            topShowIv.setImageResource(R.mipmap.ih_show);
            isShow=true;
        }
    }
}