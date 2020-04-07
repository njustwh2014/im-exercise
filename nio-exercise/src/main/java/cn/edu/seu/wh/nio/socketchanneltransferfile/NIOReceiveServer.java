package cn.edu.seu.wh.nio.socketchanneltransferfile;

import cn.edu.seu.wh.common.utils.IOUtil;
import cn.edu.seu.wh.nio.config.FileConfigProperties;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * @program:imexercise
 * @description:NIO服务器
 * @author: Huan Wang(https://github.com/njustwh2014)
 * @create:2020-04-07 10:49
 **/
public class NIOReceiveServer {
    private static Logger logger=Logger.getLogger(NIOReceiveServer.class);
    private Charset charset = Charset.forName("UTF-8");
    /**
     * @program:imexercise
     * @description:内部类，服务端保存的客户端对象，对应一个客户端文件
     * @author: Huan Wang(https://github.com/njustwh2014)
     * @create:2020-04-07 10:49
     **/
    static class Client{
        //文件名称
        String fileName;
        //文件长度
        long fileLength;
        // 开始传输时间
        long startTime;
        // 客户端地址
        InetSocketAddress address;
        // 输出的文件通道
        FileChannel outChannel;
    }


    private ByteBuffer byteBuffer=ByteBuffer.allocate(FileConfigProperties.SERVER_BUFFER_SIZE);

    //使用Map保存每个文件传输通道对应于CLient
    Map<SocketChannel,Client> map=new HashMap<>();


    public void startServer() throws IOException {
        // 1. 获取选择器
        Selector selector=Selector.open();
        // 2. 获取ServerSocketChannel
        ServerSocketChannel serverSocketChannel=ServerSocketChannel.open();
        // 3. 设置ServerSocketChannel为非阻塞
        serverSocketChannel.configureBlocking(false);

        // 4. serverSocketChannel设置监听端口
        serverSocketChannel.bind(new InetSocketAddress(FileConfigProperties.SOCKET_SERVER_PORT));

        logger.info("服务器开启...");

        // 5. 在selector上注册serverSocketChannel的accept IO事件
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

        // 6. 轮询
        while(selector.select()>0){
            // 7. 获取SelectionKey集合
            Iterator<SelectionKey> selectionKeyIterator=selector.selectedKeys().iterator();

            while(selectionKeyIterator.hasNext()){
                // 8. 获取单个seletionKey 并处理
                SelectionKey key=selectionKeyIterator.next();
                // 9. 判断key具体是什么事件
                if(key.isAcceptable()){
                    // 10. 新连接事件
                    ServerSocketChannel server=(ServerSocketChannel)key.channel();

                    SocketChannel socketChannel=server.accept();

                    socketChannel.configureBlocking(false);

                    SelectionKey selectionKey=socketChannel.register(selector,SelectionKey.OP_READ);

                    //为每个通道，创建一个Client对象

                    Client client=new Client();
                    client.address=(InetSocketAddress)socketChannel.getRemoteAddress();
                    map.put(socketChannel,client);
                    logger.info(socketChannel.getRemoteAddress()+" 连接成功！");
                }else if(key.isReadable()){
                    // 11. 可读事件
                    processData(key);
                }
                selectionKeyIterator.remove();
            }
        }
    }

    /**
    * @Description: 处理客户端传来的数据
    * @Param: [key]
    * @return: void
    * @thorws:
    * @Author: Mr.Wang
    * @Date: 2020/4/7
    */
    private void processData(SelectionKey key) {
        SocketChannel socketChannel=(SocketChannel)key.channel();
        Client client=map.get(socketChannel);

        int num=0;

        try{
            byteBuffer.clear();

            while((num=socketChannel.read(byteBuffer))>0){
                byteBuffer.flip();

                // 首先传入文件名称
                if(null==client.fileName){
                    String fileName= charset.decode(byteBuffer).toString();
                    String destPath= IOUtil.getResourcePath(FileConfigProperties.SOCKET_RECEIVE_PATH);
                    File directory=new File(destPath);
                    if(!directory.exists()){
                        directory.mkdir();
                    }
                    client.fileName=fileName;
                    String fullName=directory.getAbsolutePath()+File.separatorChar+fileName;
                    logger.info("NIO 接收文件目标:"+fullName);

                    File file=new File(fullName);
                    FileChannel fileChannel=new FileOutputStream(file).getChannel();

                    client.outChannel=fileChannel;
                }else if(0==client.fileLength){
                    //紧接着，客户端发来文件长度
                    long fileLength=byteBuffer.getLong();
                    client.fileLength=fileLength;
                    client.startTime=System.currentTimeMillis();
                    logger.info("传输开始！");
                }else{
                    //接收客户端发送的文件
                    client.outChannel.write(byteBuffer);
                }
                byteBuffer.clear();
            }
            key.cancel();
        }catch (IOException e){
            key.cancel();
            logger.error(e.getMessage());
            return ;
        }

        if(num==-1){
            IOUtil.closeQuietly(client.outChannel);
            logger.info("文件传输完毕!");
            key.cancel();
            long endTime=System.currentTimeMillis();
            logger.info("文件接收成功，File Name: "+client.fileName+" Size: "+client.fileLength+" 耗时（ms）："+ (endTime-client.startTime));
        }
    }

    public static void main(String[] args) throws IOException {
        NIOReceiveServer nioReceiveServer=new NIOReceiveServer();
        nioReceiveServer.startServer();
    }
}
