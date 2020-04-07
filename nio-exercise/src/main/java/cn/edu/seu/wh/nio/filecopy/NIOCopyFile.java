package cn.edu.seu.wh.nio.filecopy;

import cn.edu.seu.wh.common.utils.IOUtil;
import org.apache.log4j.Logger;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * @program:imexercise
 * @description:利用NIO复制文件
 * @author: Huan Wang(https://github.com/njustwh2014)
 * @create:2020-04-06 17:14
 **/
public class NIOCopyFile {
    private static Logger logger=Logger.getLogger(NIOCopyFile.class);

    /**
    * @Description: 此方法用于演示文件通道以及字节缓冲区的使用，效率不高
    * @Param: [srcDir, destDir]
    * @return: void
    * @thorws:
    * @Author: Mr.Wang
    * @Date: 2020/4/6
    */
    @Deprecated
    public static void usingNIOCopyFile(String srcDir,String destDir){
        File src=new File(srcDir);
        File dest=new File(destDir);
        FileInputStream ins=null;
        FileOutputStream outs=null;
        FileChannel finChannel=null;
        FileChannel foutChannel=null;

        try{
            if(!src.exists()){
                logger.error("目标文件不存在！");
                return;
            }
            if(!dest.exists()){
                dest.createNewFile();
            }

            long startTime=System.currentTimeMillis();
            try{
                ins=new FileInputStream(src);
                outs=new FileOutputStream(dest);
                finChannel=ins.getChannel();
                foutChannel=outs.getChannel();

                int length=-1;

                ByteBuffer buffer=ByteBuffer.allocate(1024);

                while((length=finChannel.read(buffer))!=-1){
                    //翻转buffer为读取模式
                    buffer.flip();

                    int outLength=0;

                    while((outLength=foutChannel.write(buffer))!=0){
                        System.out.println("写入foutChannel字节数： "+outLength);
                    }
                    //清空buffer为，切换为写入模式
                    buffer.clear();
                }

                foutChannel.force(true);

            }finally {
                IOUtil.closeQuietly(foutChannel);
                IOUtil.closeQuietly(outs);
                IOUtil.closeQuietly(finChannel);
                IOUtil.closeQuietly(ins);
            }

            long endTime=System.currentTimeMillis();
            logger.info("利用NIO复制文件用时(ms): "+(endTime-startTime));

        }catch (IOException e){
            logger.error(e.getMessage());
        }
    }


    /**
    * @Description: 利用通道的transferFrom方法，改善效率
    * @Param: [srcDir, destDir]
    * @return: void
    * @thorws:
    * @Author: Mr.Wang
    * @Date: 2020/4/6
    */
    public static void usingNIOFastCopyFIle(String srcDir,String destDir){
        File src=new File(srcDir);
        File dest=new File(destDir);
        FileInputStream ins=null;
        FileOutputStream outs=null;
        FileChannel finChannel=null;
        FileChannel foutChannel=null;

        try{
            if(!src.exists()){
                logger.error("目标文件不存在！");
                return;
            }
            if(!dest.exists()){
                dest.createNewFile();
            }

            long startTime=System.currentTimeMillis();
            try{
                ins=new FileInputStream(src);
                outs=new FileOutputStream(dest);
                finChannel=ins.getChannel();
                foutChannel=outs.getChannel();

                long size=finChannel.size();
                long pos=0;
                while(pos<size){
                    long count=(size-pos)>1024?1024:(size-pos);
                    pos+=foutChannel.transferFrom(finChannel,pos,count);
                }
                foutChannel.force(true);

            }finally {
                IOUtil.closeQuietly(foutChannel);
                IOUtil.closeQuietly(outs);
                IOUtil.closeQuietly(finChannel);
                IOUtil.closeQuietly(ins);
            }

            long endTime=System.currentTimeMillis();
            logger.info("利用NIO和调用transfer方法复制文件用时(ms): "+(endTime-startTime));

        }catch (IOException e){
            logger.error(e.getMessage());
        }
    }
}
