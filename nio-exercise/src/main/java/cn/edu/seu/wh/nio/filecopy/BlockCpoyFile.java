package cn.edu.seu.wh.nio.filecopy;

import cn.edu.seu.wh.common.utils.IOUtil;
import cn.edu.seu.wh.nio.config.FileConfigProperties;
import org.apache.log4j.Logger;

import java.io.*;

/**
 * @program:imexercise
 * @description:阻塞方式复制文件
 * @author: Huan Wang(https://github.com/njustwh2014)
 * @create:2020-04-06 15:22
 **/
public class BlockCpoyFile {
    private static Logger logger=Logger.getLogger(BlockCpoyFile.class);

    public static void main(String[] args) {
        String srcPath= FileConfigProperties.FILE_RESOURCE_SRC_PATH;
        String srcPathDecode=IOUtil.getResourcePath(srcPath);
        String destPath=FileConfigProperties.FILE_RESOURCE_DEST_PATH;
        String destPathDecode=IOUtil.builderResourcePath(destPath);

        logger.info("src path: "+srcPathDecode);
        logger.info("dest path: "+destPathDecode);

        blockingCopyFile(srcPathDecode,destPathDecode);
        NIOCopyFile.usingNIOCopyFile(srcPathDecode,destPathDecode);
        NIOCopyFile.usingNIOFastCopyFIle(srcPathDecode,destPathDecode);
    }


    /**
    * @Description: 同步阻塞复制文件
    * @Param: [srcDir, destDir]
    * @return: void
    * @thorws:
    * @Author: Mr.Wang
    * @Date: 2020/4/6
    */
    public static void blockingCopyFile(String srcDir,String destDir){
        InputStream in=null;
        OutputStream out=null;
        File src=new File(srcDir);
        File dest=new File(destDir);

        if(!src.exists()){
            //src不存在，则退出
            logger.error("源文件不存在！");
            return;
        }

        try{
            if(!dest.exists()){
                //如果目标文件不存在，则创建
                dest.createNewFile();
            }

            long startTime=System.currentTimeMillis();

            in=new FileInputStream(src);
            out=new FileOutputStream(dest);
            byte[] buf=new byte[1000];
            int readBytes=0;
            while((readBytes=in.read(buf))!=-1){
                out.write(buf,0,readBytes);
            }
            out.flush();

            long endTime=System.currentTimeMillis();

            logger.info("OIO阻塞复制文件用时(ms): "+(endTime-startTime));


        }catch (IOException e){
            logger.error(e.getMessage());
        }finally {
            IOUtil.closeQuietly(in);
            IOUtil.closeQuietly(out);
        }


    }
}
