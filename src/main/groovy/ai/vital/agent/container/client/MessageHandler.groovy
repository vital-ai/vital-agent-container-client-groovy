package ai.vital.agent.container.client

import javax.websocket.Session
import javax.websocket.Endpoint
import javax.websocket.EndpointConfig
import org.glassfish.tyrus.client.ClientManager
import ai.vital.agent.container.client.api.AgentMessageHandler
import ai.vital.agent.container.client.api.AgentSearch
import ai.vital.agent.container.client.api.AgentSearchResults
import ai.vital.agent.container.client.api.AgentServiceAPI
import ai.vital.vitalservice.VitalService
import ai.vital.vitalsigns.model.GraphObject
import javax.websocket.ClientEndpointConfig
import javax.websocket.CloseReason
import org.slf4j.Logger
import org.slf4j.LoggerFactory


class MessageHandler implements javax.websocket.MessageHandler.Whole<String> {
	
		private final static Logger log = LoggerFactory.getLogger( MessageHandler.class)
		
		private final Session session = null
	
		private AgentMessageHandler messageHandler = null
		
		private VitalService vitalService = null
		
		MessageHandler(Session session, AgentMessageHandler messageHandler) {
			
			this.session = session
			
			this.messageHandler = messageHandler
		}
	
		public setAgentVitalService(VitalService vitalService) {
			
			this.vitalService = vitalService
		}
		
		
		@Override
		void onMessage(String message) {
			
			log.info("Received Message: " + message)
			
			// converting string into graph object list
			List<GraphObject> messageList = null
			
			// handle API cases and produce a MessageResponse
			
			Boolean agent_search = false
			Boolean agent_send_message = false
			Boolean agent_upsert_objects = false
			
			if(agent_search) {
				
				
				AgentSearch agentSearch = new AgentSearch()
				
				AgentSearchResults agentSearchResults = AgentServiceAPI.handleAgentSearch(vitalService, agentSearch)
				
				// send json string
				String agent_search_results = ""
				
				
				if (session != null && session.isOpen()) {
					session.getAsyncRemote().sendText(agent_search_results)
				}
				
				MessageResponse messageResponse = new MessageResponse()
				
				messageResponse.messageResponseType = MessageResponseTypeEnum.AGENT_SEARCH
				
				if(messageHandler) {
	
					// agent search is already handled so this should just handle 
					// any additional logging or other functionality
					messageHandler.handleAgentMessage(session, message, messageList, messageResponse)
									
				}
				
				return
			}
			
			
			if(agent_send_message) {
				
				
				MessageResponse messageResponse = new MessageResponse()
				
				messageResponse.messageResponseType = MessageResponseTypeEnum.AGENT_SEND_MESSAGE
				
				if(messageHandler) {
	
					// handler will send the message
					messageHandler.handleAgentMessage(session, message, messageList, messageResponse)
									
				}
				
				return
				
				
			}
			
			// service objects
			if(agent_upsert_objects) {
				
				
				MessageResponse messageResponse = new MessageResponse()
				
				messageResponse.messageResponseType = MessageResponseTypeEnum.AGENT_UPSERT
				
				if(messageHandler) {
	
					// handler will test objects and decide if should be upserted or not
					messageHandler.handleAgentMessage(session, message, messageList, messageResponse)
									
				}
				
				return
				
				
			}
			
			// TODO
			// agent_query_objects (from service)
			// agent_get_objects (from service)
			
			
			
			
			// not API case
			
			MessageResponse messageResponse = new MessageResponse()
			
			messageResponse.messageResponseType = MessageResponseTypeEnum.AGENT_PASSTHRU
			
			
			if(messageHandler) {

				messageHandler.handleAgentMessage(session, message, messageList, messageResponse)
								
			}
			
		}
}
	
