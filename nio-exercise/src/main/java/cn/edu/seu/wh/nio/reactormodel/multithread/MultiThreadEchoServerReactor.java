package cn.edu.seu.wh.nio.reactormodel.multithread;

import cn.edu.seu.wh.nio.config.FileConfigProperties;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @program:imexercise
 * @description:多线程Reactor模式
 * 1.引入多个selector
 * 2.设计一个新的子反应器(SubReactor)类，一个子反应器负责查询一个选择器
 * 3.开启多个反应器的处理线程，一个线程负责执行一个子反应器(SubReactor)
 * @author: Huan Wang(https://github.com/njustwh2014)
 * @create:2020-04-08 11:57
 **/
public class MultiThreadEchoServerReactor {
    private static Logger logger=Logger.getLogger(MultiThreadEchoServerReactor.class);
    private ServerSocketChannel serverSocketChannel;
    //利用原子变量，实现线程同步
    private AtomicInteger next=new AtomicInteger(0);
    //选择器集合，引入多个选择器,默认为2个
    private int numSelectors=2;
    private Selector[] selectors;
    private SubReactor[] subReactors;

    public MultiThreadEchoServerReactor() throws IOException {
        this.init();
    }



    public MultiThreadEchoServerReactor(int numSelectors) throws IOException {
        this.numSelectors = numSelectors;
        this.init();
    }



    private void init() throws IOException {
        //初始化选择器
        selectors=new Selector[numSelectors];
        subReactors=new SubReactor[numSelectors];
        for(int i=0;i<numSelectors;i++){
            selectors[i]=Selector.open();
        }
        serverSocketChannel=ServerSocketChannel.open();
        InetSocketAddress address=new InetSocketAddress(FileConfigProperties.SOCKET_SERVER_IP,FileConfigProperties.SOCKET_SERVER_PORT);
        serverSocketChannel.socket().bind(address);
        serverSocketChannel.configureBlocking(false);
        //第一个选择器，负责监听新连接事件
        SelectionKey selectionKey=serverSocketChannel.register(selectors[0],SelectionKey.OP_ACCEPT);
        //绑定AcceptHandler
        selectionKey.attach(new MultiThreadAcceptHandler());
        //每个子反应器负责一个选择器
        for(int i=0;i<numSelectors;i++){
            subReactors[i]=new SubReactor(selectors[i]);
        }
        logger.info("服务器启动成功！");
    }

    public void startService(){
        for(int i=0;i<numSelectors;i++){
            new Thread(subReactors[i]).start();
        }
    }



    private class SubReactor implements Runnable {
        //每个线程负责一个选择器的查询和选择

        private final Selector selector;
        public SubReactor(Selector selector) {
            this.selector=selector;
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

    private class MultiThreadAcceptHandler implements Runnable {

        @Override
        public void run() {
            // 接收新连接
            //需要为新连接创建一个输入输出的handler处理器
            try {
                logger.info("accept handler run...");
                SocketChannel socketChannel=serverSocketChannel.accept();
                if(socketChannel!=null){
                    new MultiThreadEchoHandler(selectors[next.get()],socketChannel);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            if(next.incrementAndGet()==numSelectors){
                next.set(0);
            }
        }
    }

    public static void main(String[] args) throws IOException {
        MultiThreadEchoServerReactor multiThreadEchoServerReactor=new MultiThreadEchoServerReactor();
        multiThreadEchoServerReactor.startService();
    }
}
