package com.thinkgem.jeesite.modules.platform.socket;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.DefaultHttpHeaders;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketClientHandshakerFactory;
import io.netty.handler.codec.http.websocketx.WebSocketVersion;
import io.netty.handler.ssl.SslContext;

import java.net.URI;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.Timer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class WebSocketBase {

	protected Logger log = LoggerFactory.getLogger(getClass());

	private WebSocketService service = null;
	private Timer timerTask = null;
	private MoniterTask moniter = null;
	private EventLoopGroup group = null;
	private Bootstrap bootstrap = null;
	private Channel channel = null;
	private String url = null;
	private ChannelFuture future = null;
	private boolean isAlive = false;

	private Set<String> subscribChannel = new HashSet<String>();

	public WebSocketBase(String url, WebSocketService serivce) {
		this.url = url;
		this.service = serivce;
	}

	public void start() {
		if (url == null) {
			log.error(">>WebSocketClient start error  url can not be null");
			return;
		}
		if (service == null) {
			log.error(">>WebSocketClient start error  WebSocketService can not be null");
			return;
		}
		moniter = new MoniterTask(this);
		this.connect();
		timerTask = new Timer();
		// OKEX API 每30秒发送一次心跳数据：{'event':'ping'}
		timerTask.schedule(moniter, 1000, 29000);
	}

	public void setStatus(boolean flag) {
		this.isAlive = flag;
	}

	public void addChannel(String channel) {
		if (channel == null) {
			return;
		}
		String dataMsg = "{'event':'addChannel','channel':'" + channel
				+ "','binary':'true'}";
		this.sendMessage(dataMsg);
		subscribChannel.add(channel);
	}

	public void removeChannel(String channel) {
		if (channel == null) {
			return;
		}
		String dataMsg = "{'event':'removeChannel','channel':'" + channel
				+ "'}";
		this.sendMessage(dataMsg);
		subscribChannel.remove(channel);
	}

	private void connect() {
		try {
			final URI uri = new URI(url);
			if (uri == null) {
				return;
			}
			group = new NioEventLoopGroup(1);
			bootstrap = new Bootstrap();
			final SslContext sslCtx = SslContext.newClientContext();
			
			final WebSocketClientHandler handler = new WebSocketClientHandler(
					WebSocketClientHandshakerFactory.newHandshaker(uri,
							WebSocketVersion.V13, null, false,
							new DefaultHttpHeaders(), Integer.MAX_VALUE),
					service, moniter);
			
			bootstrap.group(group).option(ChannelOption.TCP_NODELAY, true)
					.channel(NioSocketChannel.class)
					.handler(new ChannelInitializer<SocketChannel>() {
						protected void initChannel(SocketChannel ch) {
							ChannelPipeline p = ch.pipeline();
							if (sslCtx != null) {
								p.addLast(sslCtx.newHandler(ch.alloc(),
										uri.getHost(), uri.getPort()));
							}
							p.addLast(new HttpClientCodec(),
									new HttpObjectAggregator(8192), handler);
						}
					});

			future = bootstrap.connect(uri.getHost(), uri.getPort());
			
			future.addListener(new ChannelFutureListener() {
				public void operationComplete(final ChannelFuture future)
						throws Exception {
				}
			});
			
			channel = future.sync().channel();
			handler.handshakeFuture().sync();
			this.setStatus(true);
		} catch (Exception e) {
			log.error(">> WebSocketClient start error! url = "+url, e);
			group.shutdownGracefully();
			this.setStatus(false);
		}
		
	}

	private void sendMessage(String message) {
		if (!isAlive) {
			log.error(">> WebSocket is not Alive addChannel error! message="+ message);
		}
		channel.writeAndFlush(new TextWebSocketFrame(message));
	}

	public void sentPing() {
		String dataMsg = "{'event':'ping'}";
		this.sendMessage(dataMsg);
	}

	public void reConnect() {
		try {
			this.group.shutdownGracefully();
			this.group = null;
			this.connect();
			if (future.isSuccess()) {
				this.setStatus(true);
				this.sentPing();
				Iterator<String> it = subscribChannel.iterator();
				while (it.hasNext()) {
					String channel = it.next();
					this.addChannel(channel);
				}
			}
		} catch (Exception e) {
			log.error(">> reConnect error:",e);
		}
	}

	public void setUrl(String url) {
		this.url = url;
	}

}
