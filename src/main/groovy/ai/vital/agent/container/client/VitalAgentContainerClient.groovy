package ai.vital.agent.container.client

import javax.websocket.Endpoint
import javax.websocket.EndpointConfig
import javax.websocket.Session
import org.glassfish.tyrus.client.ClientManager
import ai.vital.agent.container.client.api.AgentMessageHandler
import ai.vital.vitalservice.VitalService
import javax.websocket.ClientEndpointConfig
import javax.websocket.CloseReason
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class VitalAgentContainerClient extends Endpoint {
	
	private final static Logger log = LoggerFactory.getLogger( VitalAgentContainerClient.class)
	
	private String endpointString = null
	
	private ClientManager client = null
	
	private boolean connected = false
	
	private Session session = null
	
	private EndpointConfig config = null
	
	private Closure onOpenClosure = null
	
	private Closure onCloseClosure = null
	
	private Closure onErrorClosure = null
	
	private AgentMessageHandler agentMessageHandler
		
	private VitalService vitalService = null
	
	private MessageHandler messageHandler
	
	
	public VitalAgentContainerClient(String endpoint, Closure onOpenClosure, Closure onCloseClosure, Closure onErrorClosure, AgentMessageHandler agentMessageHandler) {
		
		endpointString = endpoint
		
		this.onOpenClosure = onOpenClosure
		
		this.onCloseClosure = onCloseClosure
				
		this.onErrorClosure = onErrorClosure
		
		this.agentMessageHandler = agentMessageHandler
				
	}

	public void setAgentVitalService(VitalService vitalService) {
		
		this.vitalService = vitalService
		
		if (this.messageHandler != null) {
			
			this.messageHandler.setAgentVitalService(vitalService)
		}
	}
	
	public VitalService getAgentVitalService() {
		
		return this.vitalService
		
	}
	
	public void clearAgentVitalService() {
		
		this.vitalService = null
		
		if (this.messageHandler != null) {
			
			this.messageHandler.setAgentVitalService(null)
		}
	}
	
	public void connect() {
		
		try {
		
			client = ClientManager.createClient()
		
			client.connectToServer(this, ClientEndpointConfig.Builder.create().build(), new URI(endpointString))
	
			connected = true
		
		} catch (Exception ex) {
			
			log.error("Exception connecting: " + ex.localizedMessage)
			
		}		
	}
	
	public void disconnect() {
		
		try {
			
			closeConnection()
			
			client = null
			
			connected = false
			
		} catch (Exception ex) {
				
			log.error("Exception disconnecting: " + ex.localizedMessage)
				
		}
	}
	
	void closeConnection() {
		if (this.session != null) {
			try {
				this.session.close()
			} catch (Exception e) {
				log.error("Websocket close exception: " + e.localizedMessage)
			}
		}
	}
	
	
	public void sendTextMessage(String message) {
		
		if (session != null && session.isOpen()) {
			session.getAsyncRemote().sendText(message)
		}
	}
	
	@Override
	public void onOpen(Session session, EndpointConfig config) {
		
		this.session = session
		
		this.config = config
		
		setupMessageHandler(session)
		
		if(onOpenClosure) {
			onOpenClosure(session)
		}
	}
	
	
	@Override
	void onClose(Session session, CloseReason closeReason) {
	
		log.info("Websocket Closed")
		
		connected = false
		
		if(onCloseClosure) {
			onCloseClosure(session, closeReason)
		}
	}
	
	@Override
	public void onError(Session session, Throwable thr) {
	
		log.error("Error: " + thr.localizedMessage)
		
		if(onErrorClosure) {
			onErrorClosure(session, thr)
		}
		
	}
	
	private void setupMessageHandler(Session session) {
		
		messageHandler = new MessageHandler(session, this.agentMessageHandler)
		
		messageHandler.setAgentVitalService(this.vitalService)
		
		session.addMessageHandler(String.class, messageHandler)
	}
}


