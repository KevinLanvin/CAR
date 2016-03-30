import java.util.HashSet;
import java.util.Set;

import akka.actor.ActorRef;
import akka.actor.UntypedActor;

public class Noeud extends UntypedActor {
	private Set<ActorRef> neighboors;

	public Noeud() {
		super();
		this.neighboors = new HashSet<ActorRef>();
	}

	public void onReceive(Object msg) throws Exception {
		((Message) msg).handleReceiveFor(this);
	}

	public void forwardMessageToChildren(Message msg) {
		for (ActorRef child : getForwarders())
			child.tell(msg, getSelf());
		System.out.println(this.getSelf() + "   has received : " + msg.msg);
	}

	public void addNeighboors(Set<ActorRef> c) {
		neighboors.addAll(c);
	}

	private Set<ActorRef> getForwarders() {
		Set<ActorRef> forwarders = neighboors;
		forwarders.remove(getSender());
		return forwarders;
	}
}
