package cn.edu.seu.wh.nio.reactormodel.sinnglethread;

import cn.edu.seu.wh.nio.config.FileConfigProperties;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

/**
 * @program:imexercise
 * @description:利用单线程Reactor模式实现EchoServer
 * @author: Huan Wang(https://github.com/njustwh2014)
 * @create:2020-04-07 16:06
 **/
public class EchoServerReactor implements Runnable {
    private static Logger logger=Logger.getLogger(EchoServerReactor.class);

    Selector selector;
    ServerSocketChannel serverSocketChannel;

    public EchoServerReactor() throws IOException {
        //初始化

        //打开选择器和serverSocketChannel
        selector=Selector.open();
        serverSocketChannel=ServerSocketChannel.open();

        //区别？
        //serverSocketChannel.bind(new InetSocketAddress(FileConfigProperties.SOCKET_SERVER_IP,FileConfigProperties.SOCKET_SERVER_PORT));
        serverSocketChannel.socket().bind(new InetSocketAddress(FileConfigProperties.SOCKET_SERVER_IP,FileConfigProperties.SOCKET_SERVER_PORT));
        serverSocketChannel.configureBlocking(false);

        //注册serverSocketChannel的Accept事件
        SelectionKey selectionKey=serverSocketChannel.register(selector,SelectionKey.OP_ACCEPT);

        // attach AcceptHandler到selectionKey
        selectionKey.attach(new AcceptHandler());
        logger.info("服务器启动成功！");

    }

    @Override
    public void run() {
        //选择器轮询
        try{
            while(!Thread.interrupted()){
                selector.select();
                Set<SelectionKey> selectionKeySet=selector.selectedKeys();
                Iterator<SelectionKey> selectionKeyIterator=selectionKeySet.iterator();

                while(selectionKeyIterator.hasNext()){
                    //反应器负责dispatch收到事件
                    SelectionKey selectionKey=selectionKeyIterator.next();
                    dispatch(selectionKey);
                }
                selectionKeySet.clear();
            }
        }catch(IOException e){
            logger.error(e.getMessage());
        }

    }

    /**
    * @Description: 反应器分发事件
    * @Param: [selectionKey]
    * @return: void
    * @thorws:
    * @Author: Mr.Wang
    * @Date: 2020/4/7
    */
    private void dispatch(SelectionKey selectionKey) {
        Runnable handler=(Runnable)selectionKey.attachment();
        if(handler!=null){
            handler.run();
        }
    }

    class AcceptHandler implements Runnable {
        @Override
        public void run() {
            // 接收新连接
            //需要为新连接创建一个输入输出的handler处理器
            try {
                logger.info("accept handler run...");
                SocketChannel socketChannel=serverSocketChannel.accept();
                if(socketChannel!=null){
                    new EchoHandler(socketChannel,selector);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) throws IOException {
        EchoServerReactor echoServerReactor=new EchoServerReactor();
        echoServerReactor.run();
    }
}
