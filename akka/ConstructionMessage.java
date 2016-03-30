import java.util.Set;

import akka.actor.ActorRef;

public class ConstructionMessage extends Message {

	private static final long serialVersionUID = 1L;
	private Set<ActorRef> neighboors;
	private ActorRef father;

	public ConstructionMessage(Set<ActorRef> neighboors) {
		this.neighboors = neighboors;
	}

	public Set<ActorRef> getNeighboors() {
		return neighboors;
	}

	public ActorRef getFather() {
		return father;
	}

	public void handleReceiveFor(Noeud n) {
		n.addNeighboors(neighboors);
	}
}
