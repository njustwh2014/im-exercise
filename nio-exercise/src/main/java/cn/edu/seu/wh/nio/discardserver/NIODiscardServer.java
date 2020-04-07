package cn.edu.seu.wh.nio.discardserver;

import cn.edu.seu.wh.nio.config.FileConfigProperties;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

/**
 * @program:imexercise
 * @description:利用NIO实现discard服务器
 * @author: Huan Wang(https://github.com/njustwh2014)
 * @create:2020-04-06 20:46
 **/
public class NIODiscardServer {

    private static Logger logger=Logger.getLogger(NIODiscardServer.class);

    public static  void startServer() throws IOException {
        // 1. 获取选择器Selector
        Selector selector=Selector.open();
        // 2. 获取ServerSocketChannel
        ServerSocketChannel serverSocketChannel=ServerSocketChannel.open();
        // 3. 设置ServerSocketChannel为非阻塞
        serverSocketChannel.configureBlocking(false);
        // 4. 绑定连接，设置ServerSocket监听的端口
        serverSocketChannel.bind(new InetSocketAddress(FileConfigProperties.SOCKET_SERVER_PORT));
        logger.info("服务器启动成功！");
        // 5.将ServerSocketChannel的“接收新连接”IO事件注册到Selector上
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
        // 6. 轮询感兴趣的IO时间 SelectionKey集合
        while(selector.select()>0){
            // 7. 获取SelectionKey集合
            Iterator<SelectionKey> selectionKeyIterator=selector.selectedKeys().iterator();

            while(selectionKeyIterator.hasNext()){
                // 8. 获取单个selectionKey，并处理
                SelectionKey selectionKey=selectionKeyIterator.next();

                // 9. 判断key具体是什么事件
                if(selectionKey.isAcceptable()){
                    // 10. 若选择键的IO事件是“连接就绪”，就获取客户端连接
                    SocketChannel socketChannel=serverSocketChannel.accept();

                    // 11.设置上述SocketChannel为非阻塞
                    socketChannel.configureBlocking(false);

                    // 12. 将上述通道的可读事件，注册到Selector
                    socketChannel.register(selector,SelectionKey.OP_READ);
                }else if(selectionKey.isReadable()){
                    // 13.若选择键的IO事件是可读事件，读取数据
                    SocketChannel socketChannel=(SocketChannel)selectionKey.channel();
                    // 14.读取数据后丢弃
                    ByteBuffer buffer=ByteBuffer.allocate(1024);
                    int length=0;
                    while((length=socketChannel.read(buffer))>0){
                        // 切换buffer为读取模式
                        buffer.flip();
                        logger.info("收到消息："+new String(buffer.array(),0,length));
                        //切换buffer回写入模式
                        buffer.clear();
                    }
                }

                // 15.移除当前选择键
                selectionKeyIterator.remove();
            }
        }
        //16.关闭连接
        serverSocketChannel.close();
    }

    public static void main(String[] args) throws IOException {

        NIODiscardServer.startServer();
    }
}
