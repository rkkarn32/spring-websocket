package org.ramesh.websocketdemo;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

public class TextHandler extends TextWebSocketHandler{
	Logger log = LoggerFactory.getLogger(TextHandler.class);
	private Map<String, WebSocketSession> sessions = new HashMap<>();
	boolean casting ;
	long msgCount;

//	@Autowired
//	private SimpMessagingTemplate brokerMessagingTemplate;
	
	@Override
	public void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
		this.sessions.put(session.getId(),session);
		log.info("Message received: {}",message.getPayload());
		if(!casting) {
			casting=true;
			new Thread(()-> {
				while(casting) {
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					broadcast();
				}
			} ).run();;
		}
	}
	
	public boolean broadcast() {
//		this.brokerMessagingTemplate.convertAndSend("/logs", "Message");
		for(String key :this.sessions.keySet()) {
			try {
				if(this.sessions.get(key).isOpen()) {
					this.sessions.get(key).sendMessage(new TextMessage("Broad casting message "+msgCount++));
				}else {
					this.log.error("Connection {} is closed, Removing from stack");
					this.sessions.remove(key);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return true;
	}
}
