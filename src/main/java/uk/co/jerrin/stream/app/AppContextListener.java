package uk.co.jerrin.stream.app;

import org.apache.camel.CamelContext;
import org.apache.camel.component.jms.JmsComponent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import uk.co.jerrin.stream.actor.DataConsumer;
import uk.co.jerrin.stream.actor.DataPublisher;
import uk.co.jerrin.stream.actor.DataStreamer;
import akka.actor.Actor;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.actor.UntypedActorFactory;
import akka.camel.Camel;
import akka.camel.CamelExtension;
import akka.routing.RoundRobinRouter;

@Component
public class AppContextListener implements ApplicationListener<ContextRefreshedEvent> {

	public void onApplicationEvent(ContextRefreshedEvent event) {
		System.out.println("---------------------");
		
		ApplicationContext ctx = event.getApplicationContext();
		JmsComponent jmsComponent = (JmsComponent) ctx.getBean("jmsComponent");
		
		ActorSystem system = ActorSystem.create("data-stream");

		Camel camel = CamelExtension.get(system);
		CamelContext camelContext = camel.context();
		camelContext.addComponent("jms", jmsComponent);
		
		final ActorRef dataStreamPublisher = system.actorOf(new Props(DataPublisher.class).withRouter(new RoundRobinRouter(100)), "dataStreamPublisher");
		final ActorRef dataStreamConsumer = system.actorOf(new Props(DataConsumer.class).withRouter(new RoundRobinRouter(100)), "dataStreamConsumer");
		
		final ActorRef dataStreamer = system.actorOf(new Props(new UntypedActorFactory() {
			
			public Actor create() throws Exception {
				return new DataStreamer(dataStreamPublisher);
			}
		}), "dataStreamer");
		
	}

}
