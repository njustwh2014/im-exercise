package cn.edu.seu.wh.nio.reactormodel.sinnglethread;

import org.apache.log4j.Logger;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;

/**
 * @program:imexercise
 * @description:单线程Echo Handler
 * @author: Huan Wang(https://github.com/njustwh2014)
 * @create:2020-04-07 16:26
 **/
public class EchoHandler implements Runnable {
    private static Logger logger=Logger.getLogger(EchoHandler.class);

    private final SocketChannel socketChannel;
    private final SelectionKey selectionKey;
    private final ByteBuffer byteBuffer=ByteBuffer.allocate(1024);

    private static final  int RECEIVING=0,SENDING=1;

    private int state=RECEIVING;

    public EchoHandler(SocketChannel s, Selector selector) throws IOException {
        socketChannel=s;
        socketChannel.configureBlocking(false);

        //先取得选择键，稍后再设置感兴趣的IO事件
        selectionKey=socketChannel.register(selector,0);

        //将handler自身attch到selectionKey中
        selectionKey.attach(this);

        //注册Read就绪为感兴趣事件
        selectionKey.interestOps(SelectionKey.OP_READ);

        // NIO中的Selector封装了底层的系统调用，
        // 其中wakeup用于唤醒阻塞在select方法上的线程，它的实现很简单，
        // 在linux上就是创建一个管道并加入poll的fd集合，
        // wakeup就是往管道里写一个字节，
        // 那么阻塞的poll方法有数据可读就立即返回。
        // 证明这一点很简单，strace即可知道：
        selector.wakeup();
    }

    @Override
    public void run() {
        logger.info("echo handler run...");
        try{
            if(state==SENDING){
                //发送状态，buffer数据写入通道
                socketChannel.write(byteBuffer);

                logger.info("成功返回数据："+new String(byteBuffer.array(),0,byteBuffer.limit()));
                //写完后，准备开始从通道读，buffer切换为写入模式
                byteBuffer.clear();


                //写完后，注册read就绪事件
                selectionKey.interestOps(SelectionKey.OP_READ);

                //写完后，进入接收状态
                state=RECEIVING;
            }else if(state==RECEIVING){
                //接收状态，从通道读
                int length=0;
                while((length=socketChannel.read(byteBuffer))>0){
                    logger.info("接收到数据："+new String(byteBuffer.array(),0,length));
                }
                //读完后，准备开始写入通道
                byteBuffer.flip();
                //读完后，注册write就绪事件
                selectionKey.interestOps(SelectionKey.OP_WRITE);
                //读完后，进入发送状态
                state=SENDING;
            }
            //处理结束了，这里不能关闭SelectionKey，需要重复使用
            //sk.cancel();
        }catch (IOException e){
            logger.error(e.getMessage());
        }

    }
}
