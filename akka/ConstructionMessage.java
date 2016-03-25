import java.util.List;

import akka.actor.ActorRef;


public class ConstructionMessage extends Message{

	private static final long serialVersionUID = 1L;
	private List<ActorRef> children;
	private ActorRef father;
	
	public ConstructionMessage(ActorRef father,List<ActorRef> children) {
		this.father = father;
		this.children = children;
	}
	
	public List<ActorRef> getChildren(){
		return children;
	}
	
	public ActorRef getFather(){
		return father;
	}
	
	public void handleReceiveFor(Noeud n){
		n.setFather(father);
		n.addChildren(children);
	}
}
