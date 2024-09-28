package ai.vital.agent.container.client.api

import ai.vital.agent.container.client.MessageResponse
import ai.vital.vitalsigns.model.GraphObject
import javax.websocket.Session

abstract class AgentMessageHandler {
	
	
	abstract void handleAgentMessage(Session sesson, String message, List<GraphObject> messageList, MessageResponse messageResponse)
	
	
}
