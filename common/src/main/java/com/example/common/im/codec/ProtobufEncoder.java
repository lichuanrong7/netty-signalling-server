package com.example.common.im.codec;

import com.example.common.im.ProtoInstant;
import com.example.common.im.bean.msg.ProtoMsg;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ProtobufEncoder extends MessageToByteEncoder<ProtoMsg.Message> {

    @Override
    protected void encode(ChannelHandlerContext ctx, ProtoMsg.Message msg, ByteBuf out) throws Exception {
        out.writeShort(ProtoInstant.MAGIC_CODE);
        out.writeShort(ProtoInstant.VERSION_CODE);
        byte[] bytes = msg.toByteArray();//将对象转换为byte
        int length = bytes.length;//读取消息的长度
        out.writeInt(length);
        out.writeBytes(msg.toByteArray());
    }
}
