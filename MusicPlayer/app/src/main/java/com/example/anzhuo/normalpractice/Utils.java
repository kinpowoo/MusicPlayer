package com.example.anzhuo.normalpractice;
import java.text.NumberFormat;

/**
 * Created by jianbo on 2016/11/25.
 */
public class Utils {

    public static int getDecimal(double time){
        NumberFormat ddf1= NumberFormat.getNumberInstance() ;
        ddf1.setMaximumFractionDigits(3);
        String s= ddf1.format(time) ;
        return (int)(Math.floor(100*Double.valueOf(s)));

    }

}
