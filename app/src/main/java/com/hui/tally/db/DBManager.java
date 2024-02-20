package com.hui.tally.db;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.hui.tally.utils.FloatUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/*
* 负责管理数据库的类
* 主要对于表当中的内容进行操作，增删改查
* */
public class DBManager {

    private  static SQLiteDatabase db;

    //初始化数据库对象
    public static void initDB(Context context){
        DBOpenHelper helper=new DBOpenHelper(context);//得到帮助类对象
        db=helper.getWritableDatabase();//得到数据库对象
    }
    /*
    * 读取数据库中的数据
    * 写入内存集合里
    * kind：表示收入或者支出
    * */
    //@SuppressLint("Range")
    public static List<TypeBean>getTypeList(int kind){
        List<TypeBean>list=new ArrayList<>();
        //读取typetb表当中的数据
        String sql="select * from typetb where kind = "+kind;
        Cursor cursor =db.rawQuery(sql,null);
        //循环读取游标内容，存储到对象当中
        while (cursor.moveToNext()) {
            //cursor.getString(cursor.getColumnIndex("typename"));
            String typename=cursor.getString(cursor.getColumnIndexOrThrow("typename"));
            int imageId=cursor.getInt(cursor.getColumnIndexOrThrow("imageId"));
            int sImageId=cursor.getInt(cursor.getColumnIndexOrThrow("sImageId"));
            int kind1=cursor.getInt(cursor.getColumnIndexOrThrow("kind"));
            int id=cursor.getInt(cursor.getColumnIndexOrThrow("id"));
            TypeBean typeBean=new TypeBean(id,typename,imageId,sImageId,kind1);
            list.add(typeBean);
        }
        return list;
    }
    /*
    * 向记账表插入一条元素
    * */
    public static void insertItemToAccounttb(AccountBean bean){
        ContentValues values=new ContentValues();
        values.put("typename",bean.getTypename());
        values.put("sImageId",bean.getsImageId());
        values.put("beizhu",bean.getBeizhu());
        values.put("money",bean.getMoney());
        values.put("time",bean.getTime());
        values.put("year",bean.getYear());
        values.put("month",bean.getMonth());
        values.put("day",bean.getDay());
        values.put("kind",bean.getKind());
        db.insert("accounttb",null,values);
        Log.i("animee","insertItemToAccounttb:ok!!!");

    }

    /*
    * 获取记账表的某一天的所有支出和收入
    *
    * */
    public static List<AccountBean>getAccountListOneDayFromAccounttb(int year,int month,int day){
        List<AccountBean>list=new ArrayList<>();
        String sql="select * from accounttb where year=? and month=? and day=? order by id desc";
        Cursor cursor=db.rawQuery(sql,new String[]{year+"",month+"",day+""});
        //遍历符合要求的每一行数据
        while (cursor.moveToNext()) {
            @SuppressLint("Range") int id=cursor.getInt(cursor.getColumnIndex("id"));
            @SuppressLint("Range") String typename=cursor.getString(cursor.getColumnIndex("typename"));
            @SuppressLint("Range") String beizhu=cursor.getString(cursor.getColumnIndex("beizhu"));
            @SuppressLint("Range") String time=cursor.getString(cursor.getColumnIndex("time"));
            @SuppressLint("Range") int sImageId=cursor.getInt(cursor.getColumnIndex("sImageId"));
            @SuppressLint("Range") int kind=cursor.getInt(cursor.getColumnIndex("kind"));
            @SuppressLint("Range") float money=cursor.getFloat(cursor.getColumnIndex("money"));
            AccountBean accountBean=new AccountBean(id,typename,sImageId,beizhu,money,time,year,month,day,kind);
            list.add(accountBean);
        }
        return list;
    }

    /*
     * 获取记账表的某一月的所有支出和收入
     *
     * */
    public static List<AccountBean>getAccountListOneMonthFromAccounttb(int year,int month){
        List<AccountBean>list=new ArrayList<>();
        String sql="select * from accounttb where year=? and month=? order by id desc";
        Cursor cursor=db.rawQuery(sql,new String[]{year+"",month+""});
        //遍历符合要求的每一行数据
        while (cursor.moveToNext()) {
            @SuppressLint("Range") int id=cursor.getInt(cursor.getColumnIndex("id"));
            @SuppressLint("Range") String typename=cursor.getString(cursor.getColumnIndex("typename"));
            @SuppressLint("Range") String beizhu=cursor.getString(cursor.getColumnIndex("beizhu"));
            @SuppressLint("Range") String time=cursor.getString(cursor.getColumnIndex("time"));
            @SuppressLint("Range") int sImageId=cursor.getInt(cursor.getColumnIndex("sImageId"));
            @SuppressLint("Range") int kind=cursor.getInt(cursor.getColumnIndex("kind"));
            @SuppressLint("Range") float money=cursor.getFloat(cursor.getColumnIndex("money"));
            @SuppressLint("Range") int day=cursor.getInt(cursor.getColumnIndex("day"));
            AccountBean accountBean=new AccountBean(id,typename,sImageId,beizhu,money,time,year,month,day,kind);
            list.add(accountBean);
        }
        return list;
    }

    /*
    * 获取每一天的支出和收入的总金额 kind：支出==0，收入==1
    * */
    public static float getSumMoneyOneDay(int year,int month,int day,int kind){
        float total=0.0f;
        String sql="select sum(money) from accounttb where year=? and month=? and day=? and kind=?";
        Cursor cursor=db.rawQuery(sql,new String[]{year+"",month+"",day+"",kind+""});
        //遍历
        if (cursor.moveToFirst()) {
            @SuppressLint("Range") float money=cursor.getFloat(cursor.getColumnIndex("sum(money)"));
            total=money;
        }
        return  total;
    }

    /*
     * 获取一个月的支出和收入的总金额 kind：支出==0，收入==1
     * */
    public static float getSumMoneyOneMonth(int year,int month,int kind){
        float total=0.0f;
        String sql="select sum(money) from accounttb where year=? and month=? and kind=?";
        Cursor cursor=db.rawQuery(sql,new String[]{year+"",month+"",kind+""});
        //遍历
        if (cursor.moveToFirst()) {
            @SuppressLint("Range") float money=cursor.getFloat(cursor.getColumnIndex("sum(money)"));
            total=money;
        }
        return  total;
    }
    /*
     * 获取一年的支出和收入的总金额 kind：支出==0，收入==1
     * */
    public static float getSumMoneyOneYear(int year,int kind){
        float total=0.0f;
        String sql="select sum(money) from accounttb where year=? and kind=?";
        Cursor cursor=db.rawQuery(sql,new String[]{year+"",kind+""});
        //遍历
        if (cursor.moveToFirst()) {
            @SuppressLint("Range") float money=cursor.getFloat(cursor.getColumnIndex("sum(money)"));
            total=money;
        }
        return  total;
    }
    /*
    * 统计莫月份收入或者支出有多少条
    * */
    public static int getCountItemOneMonth(int year,int month,int kind){
        int total=0;
        String sql="select count(money) from accounttb where year=? and month=? and kind=?";
        Cursor cursor=db.rawQuery(sql,new String[]{year+"",month+"",kind+""});
        if (cursor.moveToFirst()) {
            @SuppressLint("Range") int count=cursor.getInt(cursor.getColumnIndex("count(money)"));
            total=count;
        }
        return total;
    }
    /*
    * 根据传入的id，删除accounttb表当中的一条数据
    * */
    public static int deleteItemFromAccounttbById(int id){
        int i=db.delete("accounttb","id=?",new String[]{id+""});
        return i;
    }
    /*
    * 根据备注搜索收入或者支出的情况列表
    * */
    public static List<AccountBean>getAccountListByRemarkFromAccounttb(String beizhu){
        List<AccountBean>list=new ArrayList<>();
        String sql="select * from accounttb where beizhu like '%"+beizhu+"%'";
        Cursor cursor=db.rawQuery(sql,null);
        while (cursor.moveToNext()) {
            @SuppressLint("Range") int id=cursor.getInt(cursor.getColumnIndex("id"));
            @SuppressLint("Range") String typename=cursor.getString(cursor.getColumnIndex("typename"));
            @SuppressLint("Range") String bz=cursor.getString(cursor.getColumnIndex("beizhu"));
            @SuppressLint("Range") String time=cursor.getString(cursor.getColumnIndex("time"));
            @SuppressLint("Range") int sImageId=cursor.getInt(cursor.getColumnIndex("sImageId"));
            @SuppressLint("Range") int kind=cursor.getInt(cursor.getColumnIndex("kind"));
            @SuppressLint("Range") float money=cursor.getFloat(cursor.getColumnIndex("money"));
            @SuppressLint("Range") int year=cursor.getInt(cursor.getColumnIndex("year"));
            @SuppressLint("Range") int month=cursor.getInt(cursor.getColumnIndex("month"));
            @SuppressLint("Range") int day=cursor.getInt(cursor.getColumnIndex("day"));
            AccountBean accountBean=new AccountBean(id,typename,sImageId,bz,money,time,year,month,day,kind);
            list.add(accountBean);
        }

        return list;
    }
    /*
    * 查询记账的表当中有几个年份信息
    * */
    public static List<Integer>getYearListFromAccounttb(){
        List<Integer>list=new ArrayList<>();
        String sql="select distinct(year) from accounttb order by year asc";
        Cursor cursor=db.rawQuery(sql,null);
        while (cursor.moveToNext()) {
            @SuppressLint("Range") int year=cursor.getInt(cursor.getColumnIndex("year"));
            list.add(year);
        }
        return list;
    }
    /*
     * 删除accounttb表中的所有数据
     * */
    public static void deleteAllAccounttb(){
        String sql="delete from accounttb";
        db.execSQL(sql);
    }
    /*
    * 查询指定年份和月份的收入或者支出每一种类型的总钱数
    * */

    public static List<ChartItemBean>getChartListFromAccounttb(int year, int month, int kind){
        List<ChartItemBean>list=new ArrayList<>();
        float sumMoneyOneMonth=getSumMoneyOneMonth(year,month,kind);//求出支出或者收入的总钱数
        String sql="select typename,sImageId,sum(money)as total from accounttb where year=? and month=? and kind=? group by typename order by total desc";
        Cursor cursor=db.rawQuery(sql,new String[]{year+"",month+"",kind+""});
        while (cursor.moveToNext()) {
            @SuppressLint("Range") int sImageId=cursor.getInt(cursor.getColumnIndex("sImageId"));
            @SuppressLint("Range") String typename=cursor.getString(cursor.getColumnIndex("typename"));
            @SuppressLint("Range") float total = cursor.getFloat(cursor.getColumnIndex("total"));
            //计算所占百分比 total/sumMonth
            float ratio= FloatUtils.div(total,sumMoneyOneMonth);
            ChartItemBean bean=new ChartItemBean(sImageId,typename,ratio,total);
            list.add(bean);
        }
        return list;
    }
    /*
    * 获取这个月当中某一天收入支出最大的金额，这个金额为多少
    * */
    public static float getMaxMoneyOneDayInMonth(int year,int month,int kind){
        String sql="select sum(money) from accounttb where year=? and month=? and kind=? group by day order by sum(money) desc";
        Cursor cursor=db.rawQuery(sql,new String[]{year+"",month+"",kind+""});
        if (cursor.moveToFirst()) {
            @SuppressLint("Range") float money=cursor.getFloat(cursor.getColumnIndex("sum(money)"));
            return money;
        }
        return 0;
    }
    /*
     * 根据指定月份，获取每一日收入或者支出的总钱数的集合
     * */
    public static List<BarChartItemBean>getSumMoneyOneDayInMonth(int year,int month,int kind){
        String sql="select day,sum(money) from accounttb where year=? and month=? and kind=? group by day";
        Cursor cursor=db.rawQuery(sql,new String[]{year+"",month+"",kind+""});
        List<BarChartItemBean>list=new ArrayList<>();
        while (cursor.moveToNext()) {
            @SuppressLint("Range") int day=cursor.getInt(cursor.getColumnIndex("day"));
            @SuppressLint("Range") float smoney=cursor.getFloat(cursor.getColumnIndex("sum(money)"));
            BarChartItemBean itemBean=new BarChartItemBean(year,month,day,smoney);
            list.add(itemBean);
        }
        return list;
    }

}
