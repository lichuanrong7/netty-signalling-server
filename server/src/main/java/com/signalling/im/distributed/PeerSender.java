package com.example.im.distributed;

import com.example.common.entity.ImNode;
import com.example.common.im.bean.Notification;
import com.example.common.im.bean.User;
import com.example.common.im.bean.msg.ProtoMsg;
import com.example.common.im.codec.ProtobufDecoder;
import com.example.common.im.codec.ProtobufEncoder;
import com.example.common.util.JsonUtil;
import com.example.im.builder.NotificationMsgBuilder;
import com.example.im.handler.ImNodeExceptionHandler;
import com.example.im.handler.ImNodeHeartBeatClientHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.concurrent.GenericFutureListener;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;
import java.util.concurrent.TimeUnit;

@Slf4j
@Data
public class PeerSender {

    private Channel channel;
    private ImNode node;

    /**
     * 唯一标记
     */
    private boolean connectFlag = false;
    private User user;
    GenericFutureListener<ChannelFuture> closeListener = (ChannelFuture f) -> {
        log.info("The distributed connection has been disconnected……{}", node.toString());
        channel = null;
        connectFlag = false;
    };

    private GenericFutureListener<ChannelFuture> connectedListener = (ChannelFuture f) -> {
        final EventLoop eventLoop = f.channel().eventLoop();
        if(!f.isSuccess()){
            log.info("Connection failed! Prepare to try reconnection after 10 seconds!");
            eventLoop.schedule(() -> PeerSender.this.doConnect(), 10, TimeUnit.SECONDS);
            connectFlag = false;
        } else {
            connectFlag = true;

            log.info(new Date() + "Distributed node connected successfully:{}", node.toString());

            channel = f.channel();
            channel.closeFuture().addListener(closeListener);

            /**
             * 发送链接成功的通知
             */
            Notification<ImNode> notification=new Notification<>(ImWorker.getInst().getLocalNodeInfo());
            notification.setType(Notification.CONNECT_FINISHED);
            String json= JsonUtil.pojoToJson(notification);
            ProtoMsg.Message pkg = NotificationMsgBuilder.buildNotification(json);
            writeAndFlush(pkg);
        }
    };

    private Bootstrap b;
    private EventLoopGroup g;

    public PeerSender(ImNode n){
        this.node = n;
        /**
         * 客户端的是Bootstrap，服务端的则是 ServerBootstrap。
         * 都是AbstractBootstrap的子类。
         **/
        b = new Bootstrap();
        /**
         * 通过nio方式来接收连接和处理连接
         */
        g = new NioEventLoopGroup();
    }

    /**
     * 重连
     */
    public void doConnect(){
        String host = node.getHost();
        int port = node.getPort();
        try{
            if (b != null && b.group() == null) {
                b.group(g);
                b.channel(NioSocketChannel.class);
                b.option(ChannelOption.SO_KEEPALIVE,true);
                b.option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT);
                b.remoteAddress(host,port);

                //设置通道初始化
                b.handler(
                        new ChannelInitializer<SocketChannel>() {
                            @Override
                            protected void initChannel(SocketChannel ch) throws Exception {
                                ch.pipeline().addLast("decoder", new ProtobufDecoder());
                                ch.pipeline().addLast("encoder", new ProtobufEncoder());
                                ch.pipeline().addLast("imNodeHeartBeatClientHandler",new ImNodeHeartBeatClientHandler());
                                ch.pipeline().addLast("exceptionHandler",new ImNodeExceptionHandler());
                            }
                        }
                );

                log.info(new Date() + "Start connecting distributed nodes:{}", node.toString());
                ChannelFuture f = b.connect();
                f.addListener(connectedListener);

            } else  if(b.group() != null){
                log.info(new Date() + "Start connecting the distributed nodes again", node.toString());
                ChannelFuture f = b.connect();
                f.addListener(connectedListener);
            }
        } catch (Exception e){
            log.info("Client connection failure!" + e.getMessage());
        }
    }

    public void stopConnecting(){
        g.shutdownGracefully();
        connectFlag = false;
    }


    public void writeAndFlush(Object pkg){
        if(connectFlag == false){
            log.error("Distributed node not connected:", node.toString());
            return;
        }
        channel.writeAndFlush(pkg);
    }
}
