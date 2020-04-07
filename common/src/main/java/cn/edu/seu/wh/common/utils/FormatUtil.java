package cn.edu.seu.wh.common.utils;

import java.math.RoundingMode;
import java.text.DecimalFormat;

/**
 * @program:imexercise
 * @description:格式工具
 * @author: Huan Wang(https://github.com/njustwh2014)
 * @create:2020-04-06 15:05
 **/
public class FormatUtil {

    /**
    * @Description: 设置数字格式，保留有效小数位数为fractions
    * @Param: [fractions:保留有效小数位数]
    * @return: java.text.DecimalFormat:数字格式
    * @thorws:
    * @Author: Mr.Wang
    * @Date: 2020/4/6
    */
    public static DecimalFormat decimalFormat(int fractions) {

        DecimalFormat df = new DecimalFormat("#0.0");
        df.setRoundingMode(RoundingMode.HALF_UP);
        df.setMinimumFractionDigits(fractions);
        df.setMaximumFractionDigits(fractions);
        return df;
    }
}
