package cn.edu.seu.wh.nio.config;

import cn.edu.seu.wh.common.utils.ConfigProperties;

/**
 * @program:imexercise
 * @description:读取配置文件
 * @author: Huan Wang(https://github.com/njustwh2014)
 * @create:2020-04-06 15:23
 **/
public class FileConfigProperties extends ConfigProperties {

    public FileConfigProperties(String fileName) {
        super(fileName);
        super.loadFromFile();
    }

    static ConfigProperties singleton = new FileConfigProperties("/system.properties");




    public static final String FILE_SRC_PATH = singleton.getValue("file.src.path");

    public static final String FILE_DEST_PATH = singleton.getValue("file.dest.path");

    public static final String FILE_RESOURCE_SRC_PATH = singleton.getValue("file.resource.src.path");

    public static final String FILE_RESOURCE_DEST_PATH = singleton.getValue("file.resource.dest.path");


    //发送文件路径

    public static final String SOCKET_SEND_FILE = singleton.getValue("socket.send.file");
    public static final String SOCKET_RECEIVE_FILE = singleton.getValue("socket.receive.file");
    public static final String SOCKET_RECEIVE_PATH = singleton.getValue("socket.receive.path");


    public static final int SEND_BUFFER_SIZE = singleton.getIntValue("send.buffer.size");

    public static final int SERVER_BUFFER_SIZE = singleton.getIntValue("server.buffer.size");

    public static final String SOCKET_SERVER_IP = singleton.getValue("socket.server.ip");

    public static final int SOCKET_SERVER_PORT = singleton.getIntValue("socket.server.port");
}
