package cn.edu.seu.wh.common.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * @program:imexercise
 * @description:日期工具
 * @author: Huan Wang(https://github.com/njustwh2014)
 * @create:2020-04-07 10:34
 **/
public class DataUtil {

    /**
    * @Description: 取得今天日期
    * @Param: []
    * @return: java.lang.String
    * @thorws:
    * @Author: Mr.Wang
    * @Date: 2020/4/7
    */
    public static String getToday() {

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        return sdf.format(new Date().getTime());
    }

    /**
    * @Description: 取得昨天日期
    * @Param: []
    * @return: java.lang.String
    * @thorws:
    * @Author: Mr.Wang
    * @Date: 2020/4/7
    */
    public static String getYestoday() {

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, 1);
        Date date = calendar.getTime();
        return sdf.format(date.getTime());
    }

    /**
    * @Description: 取得现在时刻
    * @Param: []
    * @return: java.lang.String
    * @thorws:
    * @Author: Mr.Wang
    * @Date: 2020/4/7
    */
    public static String getNow() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

        return sdf.format(new Date().getTime());

    }
}
