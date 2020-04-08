package cn.edu.seu.wh.nio.reactormodel.sinnglethread;

import cn.edu.seu.wh.common.utils.DataUtil;
import cn.edu.seu.wh.nio.config.FileConfigProperties;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Scanner;
import java.util.Set;

/**
 * @program:imexercise
 * @description:EchoClient
 * @author: Huan Wang(https://github.com/njustwh2014)
 * @create:2020-04-07 16:44
 **/
public class EchoClient {

    private static Logger logger=Logger.getLogger(EchoClient.class);

    public void start() throws IOException {
        InetSocketAddress inetSocketAddress=new InetSocketAddress(FileConfigProperties.SOCKET_SERVER_IP,FileConfigProperties.SOCKET_SERVER_PORT);

        SocketChannel socketChannel=SocketChannel.open(inetSocketAddress);

        socketChannel.configureBlocking(false);

        while(!socketChannel.finishConnect()){

        }
        logger.info("客户端启动成功！");

        //启动接收线程

        Processer processer=new Processer(socketChannel);

        processer.run();
    }


    private class Processer implements  Runnable {
        private SocketChannel socketChannel;
        private Selector readSelector,writeSelector;
        public Processer(SocketChannel s) throws IOException {
            readSelector=Selector.open();
            writeSelector=Selector.open();
            socketChannel=s;
            socketChannel.register(readSelector, SelectionKey.OP_READ );
            socketChannel.register(writeSelector,SelectionKey.OP_WRITE);
        }

        private void receivingThread(){
            while (!Thread.interrupted()){
                try{
                    readSelector.select();
                    Set<SelectionKey> selectionKeySet=readSelector.selectedKeys();
                    Iterator<SelectionKey> selectionKeyIterator=selectionKeySet.iterator();

                    while(selectionKeyIterator.hasNext()) {
                        SelectionKey selectionKey = selectionKeyIterator.next();
                        if (selectionKey.isReadable()) {
                            logger.info("接收数据中...");
                            // 若选择键的IO事件是“可读”事件,读取数据
                            SocketChannel socketChannel = (SocketChannel) selectionKey.channel();
                            ByteBuffer byteBuffer = ByteBuffer.allocate(1024);

                            int length = 0;
                            while ((length = socketChannel.read(byteBuffer)) > 0) {
                                byteBuffer.flip();
                                logger.info("server echo:" + new String(byteBuffer.array(), 0, length));
                                byteBuffer.clear();
                            }
                        }
                        //处理结束了, 这里不能关闭select key，需要重复使用
                        //selectionKey.cancel();
                    }
                    selectionKeySet.clear();

                }catch (IOException e){
                    logger.error(e.getMessage());
                }

            }
        }

        private void sendingThread(){
            while (!Thread.interrupted()){
                try{
                    writeSelector.select();
                    Set<SelectionKey> selectionKeySet=writeSelector.selectedKeys();
                    Iterator<SelectionKey> selectionKeyIterator=selectionKeySet.iterator();

                    while(selectionKeyIterator.hasNext()) {
                        SelectionKey selectionKey = selectionKeyIterator.next();
                        if (selectionKey.isWritable()) {
                            ByteBuffer buffer = ByteBuffer.allocate(FileConfigProperties.SERVER_BUFFER_SIZE);

                            Scanner sc = new Scanner(System.in);

                            logger.info("请输入发送内容：");
                            if (sc.hasNext()) {

                                //这个地方阻塞，可能导致回显不能及时出现
                                String next = sc.next();
                                SocketChannel socketChannel = (SocketChannel) selectionKey.channel();
                                buffer.put((DataUtil.getNow() + ">>" + next).getBytes());
                                buffer.flip();
                                socketChannel.write(buffer);
                                buffer.clear();
                            }
                        }
                        //处理结束了, 这里不能关闭select key，需要重复使用
                        //selectionKey.cancel();
                    }
                    selectionKeySet.clear();

                }catch (IOException e){
                    logger.error(e.getMessage());
                }

            }
        }

        @Override
        public void run() {
            new Thread(()->{
                this.sendingThread();
            }).start();

            new Thread(()->{
                this.receivingThread();
            }).start();

        }
    }

    public static void main(String[] args) throws IOException {
        EchoClient echoClient=new EchoClient();
        echoClient.start();
    }
}
