package cn.edu.seu.wh.nio.socketchanneltransferfile;

import cn.edu.seu.wh.common.utils.IOUtil;
import cn.edu.seu.wh.nio.config.FileConfigProperties;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;

/**
 * @program:imexercise
 * @description:NIO Send Client
 * @author: Huan Wang(https://github.com/njustwh2014)
 * @create:2020-04-07 11:43
 **/
public class NIOSendClient {
    private static Logger logger=Logger.getLogger(NIOSendClient.class);
    private Charset charset=Charset.forName("UTF-8");

    public void sendFile(){
        try{
            String sourcePath= FileConfigProperties.SOCKET_SEND_FILE;
            String srcPath= IOUtil.getResourcePath(sourcePath);
            logger.info("srcPath: "+srcPath);

            String destFile=FileConfigProperties.SOCKET_RECEIVE_FILE;

            logger.info("dest file: "+destFile);

            File file=new File(srcPath);

            if(!file.exists()){
                logger.info("文件不存在！");
                return ;
            }

            FileChannel fileChannel=new FileInputStream(file).getChannel();

            SocketChannel socketChannel=SocketChannel.open();


            socketChannel.socket().connect(new InetSocketAddress(FileConfigProperties.SOCKET_SERVER_IP,FileConfigProperties.SOCKET_SERVER_PORT));

            socketChannel.configureBlocking(false);

            while(!socketChannel.finishConnect()){
                //不断自旋，等待
            }

            logger.info("Client 成功连接服务器！");

            //发送文件名称

            ByteBuffer fileNameByteBuffer=charset.encode(destFile);

            socketChannel.write(fileNameByteBuffer);

            // 发送文件长度

            ByteBuffer buffer=ByteBuffer.allocate(FileConfigProperties.SERVER_BUFFER_SIZE);

            buffer.putLong(file.length());

            buffer.flip();

            socketChannel.write(buffer);
            buffer.clear();

            //发送文件内容
            logger.info("开始传输文件！");

            int length=0;
            long progress=0;

            while((length=fileChannel.read(buffer))>0){
                buffer.flip();
                socketChannel.write(buffer);
                buffer.clear();
                progress+=length;
                logger.info("| "+(100*progress/file.length())+"% |");
            }

            if(length==-1){
                IOUtil.closeQuietly(fileChannel);
                //在socketChannel传输通道关闭前，尽量发送一个输出结束标志给对端
                socketChannel.shutdownOutput();
                IOUtil.closeQuietly(socketChannel);
            }

            logger.info("=============文件传输完成============");

        }catch (IOException e){
            logger.error(e.getMessage());
        }
    }

    public static void main(String[] args) {
        NIOSendClient nioSendClient=new NIOSendClient();
        nioSendClient.sendFile();
    }
}
