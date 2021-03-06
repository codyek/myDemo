package com.thinkgem.jeesite.modules.platform.socket.bitmex;

import java.net.URI;
import javax.websocket.ClientEndpoint;
import javax.websocket.CloseReason;
import javax.websocket.ContainerProvider;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.WebSocketContainer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ClientEndpoint
public class WebsocketClientEndpoint {
	private Logger log = LoggerFactory.getLogger(getClass());

	public Session userSession = null;
    private MessageHandler messageHandler;

    public WebsocketClientEndpoint(URI endpointURI) {
        try {
            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            container.setDefaultMaxBinaryMessageBufferSize(512000);
            container.setDefaultMaxTextMessageBufferSize(512000);  // 500k
            container.connectToServer(this, endpointURI);
        } catch (Exception e) {
        	log.error(">> WebsocketClientEndpoint init error ",e);
            throw new RuntimeException(e);
        }
    }

    /**
     * Callback hook for Connection open events.
     *
     * @param userSession the userSession which is opened.
     */
    @OnOpen
    public void onOpen(Session userSession) {
    	log.info(">> opening Mex websocket");
        this.userSession = userSession;
    }

    /**
     * Callback hook for Connection close events.
     *
     * @param userSession the userSession which is getting closed.
     * @param reason the reason for connection close
     */
    @OnClose
    public void onClose(Session userSession, CloseReason reason) {
    	//log.error(">> closing Mex websocket" +" userSession="+userSession+"  ,reason="+reason);
    	log.error(">> closing Mex websocket" +" reason="+reason);
        this.userSession = null;
    }
    
    /**
     * Callback hook for Message Events. This method will be invoked when a client send a message.
     *
     * @param message The text message
     */
    @OnMessage
    public void onMessage(String message) {
        if (this.messageHandler != null) {
            this.messageHandler.handleMessage(message);
        }
    }

    /**
     * register message handler
     *
     * @param msgHandler
     */
    public void addMessageHandler(MessageHandler msgHandler) {
        this.messageHandler = msgHandler;
    }

    /**
     * Send a message.
     *
     * @param message
     */
    public void sendMessage(String message) {
    	if(null != this.userSession){
    		if(null != this.userSession.getAsyncRemote()){
    			this.userSession.getAsyncRemote().sendText(message);
        	}
    	}
    }

    /**
     * Message handler.
     */
    public static interface MessageHandler {

        public void handleMessage(String message);
    }
}
