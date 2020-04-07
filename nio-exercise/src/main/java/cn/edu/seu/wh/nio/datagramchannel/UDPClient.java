package cn.edu.seu.wh.nio.datagramchannel;

import cn.edu.seu.wh.common.utils.DataUtil;
import cn.edu.seu.wh.nio.config.FileConfigProperties;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.util.Scanner;

/**
 * @program:imexercise
 * @description:UDP CLient
 * @author: Huan Wang(https://github.com/njustwh2014)
 * @create:2020-04-07 10:29
 **/
public class UDPClient {

    private static Logger logger=Logger.getLogger(UDPClient.class);

    public static void startClient() throws IOException {

        // datagramChannel open
        DatagramChannel datagramChannel=DatagramChannel.open();
        // set datagramChannel nonblocking
        datagramChannel.configureBlocking(false);

        ByteBuffer buffer=ByteBuffer.allocate(FileConfigProperties.SEND_BUFFER_SIZE);

        Scanner sc=new Scanner(System.in);

        logger.info("UDP客户端启动成功");
        logger.info("请输入需要发送的内容: ");
        while(sc.hasNext()){

            String next=sc.next();
            buffer.put((DataUtil.getNow()+">>"+next).getBytes());
            buffer.flip();
            datagramChannel.send(buffer,new InetSocketAddress(FileConfigProperties.SOCKET_SERVER_IP,FileConfigProperties.SOCKET_SERVER_PORT));
            buffer.clear();
        }

        datagramChannel.close();
        logger.info("UDPClient关闭!");
    }

    public static void main(String[] args) throws IOException {
        UDPClient.startClient();
    }
}
