# im-exercise
从12天实践开始动手写一个IM系统

# day 1: Java NIO 实战

## 实战1：使用FileChannel复制文件

通过使用Channel通道，完成复制文件。

本环节的目标是掌握以下知识：Java NIO中ByteBuffer、Channel两个重要组件的使用。

接着是升级实战的案例，使用文件Channel通道的transferFrom方法，完成高效率的文件复制。

## 实战2：使用SocketChannel传输文件

本环节的目标是掌握以下知识：
+ 非阻塞客户端在发起连接后，需要不断的自旋，检测连接是否完成。
+ SocketChannel传输通道的read方法、write方法
+ 在SocketChannel传输通道关闭前，尽量发送一个结束标志给对方。

## 实战3：使用DatagramChannel传输数据

客户端使用DatagramChannel发送数据，服务端使用DatagramChannel接收数据。

本环节的目标是掌握以下知识：

+ 使用接收数据方法receive，使用发送数据方法send
+ DatagramChannel和SocketChannel两种通道，在发送、接收数据上的不同。

## 实战4：使用NIO实现Discard服务器

客户端功能：发送一个数据包到服务器端，然后关闭连接。 服务器端也很简单，收到客户端的数据，直接丢弃。

本环节的目标是掌握以下知识：
+ Selector选择器的注册，以及选择器的查询。
+ SelectionKey选择键方法的使用。
+ 根据SelectionKey方法的四种IO事件类型，完成对应的IO处理。
