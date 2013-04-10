package uk.co.jerrin.stream.actor;

import java.io.Serializable;
import java.util.Date;

import akka.actor.ActorRef;
import akka.actor.Terminated;
import akka.actor.UntypedActor;

public class DataStreamer extends UntypedActor {

	private ActorRef publisher;
	private ActorRef lastSender = getContext().system().deadLetters();

	public DataStreamer(ActorRef publisher) {
		super();
		this.publisher = publisher;
	}

	@SuppressWarnings("deprecation")
	@Override
	public void preStart() {

		getContext().watch(publisher);

		System.out.println("Streaming starts at: " + new Date());
		int noOfMsgs = 1000 * 1000;

		for (int i = 0; i < noOfMsgs; i++) {
			publisher.tell(new CustomCountMessage(i));
		}

		System.out.println("--------------- sent all messages ---------------");

		/*
		 * getContext() .system() .scheduler() .schedule(Duration.create(1,
		 * "second"), Duration.create(1, TimeUnit.MILLISECONDS), publisher, new
		 * CutomMessage(), getContext().dispatcher());
		 */
	}

	@Override
	public void onReceive(Object message) throws Exception {
		System.out.println(message);
		if (message.equals("kill")) {
			getContext().stop(publisher);
			lastSender = getSender();
		} else if (message instanceof Terminated) {
			final Terminated t = (Terminated) message;
			if (t.getActor() == publisher) {
				lastSender.tell("finished", getSelf());
			}
		} else {
			unhandled(message);
		}
	}
}

class CutomMessage implements Serializable {

	private static final long serialVersionUID = -337736011851000151L;

	@Override
	public String toString() {
		return String.valueOf(System.currentTimeMillis());
	}
}

class DetailedCutomMessage implements Serializable {

	private static final long serialVersionUID = -337736011851000151L;

	@Override
	public String toString() {
		return String.format("Timestamp: %s", System.currentTimeMillis());
	}
}

class CustomCountMessage implements Serializable {

	private int i;

	CustomCountMessage(int i) {
		this.i = i;
	}

	private static final long serialVersionUID = -337736011851000151L;

	@Override
	public String toString() {
		return String.valueOf(i);
	}
}
