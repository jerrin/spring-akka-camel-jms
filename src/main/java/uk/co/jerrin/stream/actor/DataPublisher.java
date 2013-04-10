package uk.co.jerrin.stream.actor;

import akka.camel.javaapi.UntypedProducerActor;

public class DataPublisher extends UntypedProducerActor {

	@Override
	public String getEndpointUri() {
		return "jms:queue:data.stream";
	}

	@Override
	public boolean isOneway() {
		return true;
	}

	@Override
	public Object onTransformOutgoingMessage(Object message) {
        //System.out.println("Actor: " + getSelf().path().name() + " - Sending: " + message);
		return super.onTransformOutgoingMessage(message);
	}
	
	

}
