package ai.vital.agent.container.client

import ai.vital.agent.container.client.api.AgentSearch
import ai.vital.agent.container.client.api.AgentSearchResults
import ai.vital.vitalsigns.model.GraphObject

class MessageResponse {
	
	MessageResponseTypeEnum messageResponseType = null
	
	// agent search case
	AgentSearch agentSearch = null
	
	AgentSearchResults agentSearchResults = null
	
	List<GraphObject> agentSearchResultsList
	
	
	// send a message case
	List<GraphObject> messageList = []
	
	// objects to save or update in service
	List<GraphObject> modifiedObjectList = []
	
	
}
