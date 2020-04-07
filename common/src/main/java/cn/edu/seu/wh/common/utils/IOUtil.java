package cn.edu.seu.wh.common.utils;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.ByteBuffer;
import java.text.DecimalFormat;

/**
 * @program:imexercise
 * @description:IO工具类
 * @author: Huan Wang(https://github.com/njustwh2014)
 * @create:2020-04-06 15:04
 **/
public class IOUtil {

    public static String getUserHomeResourcePath(String resName) {
        String p = System.getProperty("user.dir") + File.separator + resName;

        return p;
    }


    /**
    * @Description:
     *      * 取得当前类路径下的resName资源的完整路径
     *      * url.getPath()获取到的路径被utf-8编码了
     *      * 需要用URLDecoder.decode(path, "UTF-8")解码
    * @Param: [resName:需要获取完整路径的资源,需要以/打头]
    * @return: java.lang.String:完整路径
    * @thorws:
    * @Author: Mr.Wang
    * @Date: 2020/4/6
    */
    public static String getResourcePath(String resName) {
        URL url = IOUtil.class.getResource(resName);
        String path = url.getPath();
        String decodePath = null;
        try {
            decodePath = URLDecoder.decode(path, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return decodePath;
    }

    /**
    * @Description:
     *      * 构建当前类路径下的 resName资源的完整路径
     *      * url.getPath()获取到的路径被utf-8编码了
     *      * 需要用URLDecoder.decode(path, "UTF-8")解码
    * @Param: [resName:需要获取完整路径的资源,需要以/打头]
    * @return: java.lang.String:完整路径
    * @thorws:
    * @Author: Mr.Wang
    * @Date: 2020/4/6
    */
    public static String builderResourcePath(String resName) {
        URL url = IOUtil.class.getResource("/");
        String path = url.getPath();
        String decodePath = null;
        try {
            decodePath = URLDecoder.decode(path, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return decodePath + resName;
    }

    public static void closeQuietly(java.io.Closeable o) {
        if (null == o) return;
        try {
            o.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static DecimalFormat fileSizeFormater = FormatUtil.decimalFormat(1);

    /**
    * @Description: 格式化文件大小
    * @Param: [length]
    * @return: java.lang.String
    * @thorws:
    * @Author: Mr.Wang
    * @Date: 2020/4/6
    */
    public static String getFormatFileSize(long length) {
        double size = ((double) length) / (1 << 30);
        if (size >= 1) {
            return fileSizeFormater.format(size) + "GB";
        }
        size = ((double) length) / (1 << 20);
        if (size >= 1) {
            return fileSizeFormater.format(size) + "MB";
        }
        size = ((double) length) / (1 << 10);
        if (size >= 1) {
            return fileSizeFormater.format(size) + "KB";
        }
        return length + "B";
    }




    /**
    * @Description:
     *      * ByteBuffer中字符串是以ascii中0（null）字符结束
     *      * 如果是以别的字符结束，则修改if(btemp[i]==0){
     *      * break;
     *      * }即可
    * @Param: [buf, MaxLen]
    * @return: java.lang.String
    * @thorws:
    * @Author: Mr.Wang
    * @Date: 2020/4/6
    */
    public static String getVarStrFromBuf(ByteBuffer buf, int MaxLen) {
        byte[] btemp = new byte[MaxLen];
        byte j = -1;
        int avaibleBytes = buf.array().length - buf.position();
        if (!(avaibleBytes > 0)) {
            return "";
        }
        int len = 0;
        for (int i = 0; i < MaxLen; i++) {
            btemp[i] = buf.get();
            len = i;
            if (btemp[i] == 0) {
                break;
            }
        }
        try {
            return new String(btemp, 0, len, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }
}
