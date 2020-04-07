package cn.edu.seu.wh.nio.discardserver;

import cn.edu.seu.wh.nio.config.FileConfigProperties;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @program:imexercise
 * @description:利用NIO实现Discard的客户端
 * @author: Huan Wang(https://github.com/njustwh2014)
 * @create:2020-04-06 21:12
 **/
public class NIODiscardClient {

    private static Logger logger=Logger.getLogger(NIODiscardClient.class);

    public static void startClient(String msg) throws IOException {
        InetSocketAddress inetSocketAddress=new InetSocketAddress(FileConfigProperties.SOCKET_SERVER_IP,FileConfigProperties.SOCKET_SERVER_PORT);

        // 1. 获取SocketChannel,并连接到到远程地址
        SocketChannel socketChannel=SocketChannel.open(inetSocketAddress);
        // 2.设置SocketChannel为非阻塞
        socketChannel.configureBlocking(false);
        // 3. 自旋等待连接建立
        while(!socketChannel.finishConnect()){
            // 可以处理其他业务
        }
        logger.info("与服务器："+FileConfigProperties.SOCKET_SERVER_IP+":"+FileConfigProperties.SOCKET_SERVER_PORT+"建立连接！");

        // 3. 分配指定大小的缓冲区
        ByteBuffer buffer=ByteBuffer.allocate(1024);

        buffer.put(msg.getBytes());

        buffer.flip();

        socketChannel.write(buffer);

        // 4. 终止输出，并向对方发送结束标志
        socketChannel.shutdownOutput();

        socketChannel.close();
    }

    public static void main(String[] args) throws IOException {
        ExecutorService executorService= Executors.newFixedThreadPool(20);
        for(int i=0;i<1000;i++){
            executorService.submit(()->{
                try {
                    NIODiscardClient.startClient("hello world! from:"+Thread.currentThread().getName());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        }
        executorService.shutdown();
        NIODiscardClient.startClient("hello world! from:"+Thread.currentThread().getName());
    }
}
