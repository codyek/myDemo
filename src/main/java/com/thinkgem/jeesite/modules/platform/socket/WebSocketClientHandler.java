package com.thinkgem.jeesite.modules.platform.socket;
import java.io.IOException;
import java.util.zip.DataFormatException;
import java.util.zip.Inflater;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import io.netty.handler.codec.http.websocketx.CloseWebSocketFrame;
import io.netty.handler.codec.http.websocketx.PongWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketClientHandshaker;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import io.netty.util.CharsetUtil;

public class WebSocketClientHandler extends SimpleChannelInboundHandler<Object> {

	protected Logger log = LoggerFactory.getLogger(getClass());
	
    private final WebSocketClientHandshaker handshaker;
    private ChannelPromise handshakeFuture;
    private MoniterTask moniter;
    private WebSocketService service ;
    public WebSocketClientHandler(WebSocketClientHandshaker handshaker,WebSocketService service,MoniterTask moniter) {
        this.handshaker = handshaker;
        this.service = service;
        this.moniter = moniter;
    }

    public ChannelFuture handshakeFuture() {
        return handshakeFuture;
    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) {
        handshakeFuture = ctx.newPromise();
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        handshaker.handshake(ctx.channel());
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        log.info(">> WebSocket Client disconnected!");
    }

    @Override
    public void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
    	moniter.updateTime();
    	//log.info("channelRead0-------");
        Channel ch = ctx.channel();
        if (!handshaker.isHandshakeComplete()) {
            handshaker.finishHandshake(ch, (FullHttpResponse) msg);
            log.info(">>WebSocket Client connected!");
            handshakeFuture.setSuccess();
            return;
        }

        if (msg instanceof FullHttpResponse) {
            FullHttpResponse response = (FullHttpResponse) msg;
            throw new IllegalStateException(
                    "Unexpected FullHttpResponse (getStatus=" + response.getStatus() +
                            ", content=" + response.content().toString(CharsetUtil.UTF_8) + ')');
        }

        WebSocketFrame frame = (WebSocketFrame) msg;
        if (frame instanceof TextWebSocketFrame) {
        	 TextWebSocketFrame textFrame = (TextWebSocketFrame) frame;
        	 service.onReceive(textFrame.text());
        } else if (frame instanceof BinaryWebSocketFrame) {
        	BinaryWebSocketFrame binaryFrame=(BinaryWebSocketFrame)frame;
        	service.onReceive(decodeByteBuff(binaryFrame.content()));
        }else if (frame instanceof PongWebSocketFrame) {
            log.info("WebSocket Client received pong");
        } else if (frame instanceof CloseWebSocketFrame) {
            log.info("WebSocket Client received closing");
            ch.close();
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        if (!handshakeFuture.isDone()) {
            handshakeFuture.setFailure(cause);
        }
        ctx.close();
    }
    public  String decodeByteBuff(ByteBuf buf) throws IOException, DataFormatException {
    
    	   byte[] temp = new byte[buf.readableBytes()];
    	   ByteBufInputStream bis = new ByteBufInputStream(buf);
		   bis.read(temp);
		   bis.close();
		   Inflater decompresser = new Inflater(true);
		   decompresser.setInput(temp, 0, temp.length);
		   StringBuilder sb = new StringBuilder();
		   byte[] result = new byte[1024];
		   while (!decompresser.finished()) {
				int resultLength = decompresser.inflate(result);
				sb.append(new String(result, 0, resultLength, "UTF-8"));
		   }
		   decompresser.end();
           return sb.toString();
	}
}
