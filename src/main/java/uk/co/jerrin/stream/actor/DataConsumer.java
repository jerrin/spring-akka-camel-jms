package uk.co.jerrin.stream.actor;

import java.util.Date;

import akka.camel.CamelMessage;
import akka.camel.javaapi.UntypedConsumerActor;

public class DataConsumer extends UntypedConsumerActor {

	@Override
	public String getEndpointUri() {
		return "jms:queue:data.stream";
	}

	@Override
	public void onReceive(Object message) throws Exception {
        if (message instanceof CamelMessage) {
            CamelMessage camelMessage = (CamelMessage) message;
            String msg = camelMessage.getBodyAs(String.class, getCamelContext());
            //System.out.println("Actor: " + getSelf().path().name() + " - Received: " + msg);
            System.out.println("Actor: " + getSelf().path().name() + " - Time: " + new Date() + " - Received: " + msg);
        } else {
            unhandled(message);
        }

	}

}
