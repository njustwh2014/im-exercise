package cn.edu.seu.wh.nio.datagramchannel;

import cn.edu.seu.wh.nio.config.FileConfigProperties;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.Iterator;

/**
 * @program:imexercise
 * @description:利用Datagram实现UDP Server
 * @author: Huan Wang(https://github.com/njustwh2014)
 * @create:2020-04-06 22:29
 **/
public class UDPServer {

    private static Logger logger=Logger.getLogger(UDPServer.class);

    public static void startServer() throws IOException {

        //获取DatagramChannel数据包通道
        DatagramChannel datagramChannel=DatagramChannel.open();

        // 设置为非阻塞模式
        datagramChannel.configureBlocking(false);

        // 绑定监听地址
        datagramChannel.bind(new InetSocketAddress(FileConfigProperties.SOCKET_SERVER_IP,FileConfigProperties.SOCKET_SERVER_PORT));

        // 开启一个Selector
        Selector selector=Selector.open();

        // 将通道注册到selector
        datagramChannel.register(selector, SelectionKey.OP_READ);

        // 通过选择器，监听IO事件
        while(selector.select()>0){

            Iterator<SelectionKey> selectionKeyIterator=selector.selectedKeys().iterator();

            ByteBuffer buffer=ByteBuffer.allocate(FileConfigProperties.SEND_BUFFER_SIZE);

            //迭代处理IO事件

            while(selectionKeyIterator.hasNext()){
                SelectionKey key=selectionKeyIterator.next();
                if(key.isReadable()){
                    //可读事件

                    //读取datagramChannel的数据
                    SocketAddress client=datagramChannel.receive(buffer);

                    buffer.flip();

                    logger.info("接收到数据："+new String(buffer.array(),0,buffer.limit()));

                    buffer.clear();
                }
                selectionKeyIterator.remove();
            }
        }

        // 关闭选择器和通道

        selector.close();

        datagramChannel.close();
    }

    public static void main(String[] args) throws IOException {
        UDPServer.startServer();
    }
}
