import java.util.ArrayList;
import java.util.List;

import akka.actor.ActorRef;
import akka.actor.UntypedActor;


public class Noeud extends UntypedActor {
	private List<ActorRef> children;
	private ActorRef father;
	
	public Noeud(){
		super();
		this.children= new ArrayList<ActorRef>();
	}
	
	public void onReceive(Object msg) throws Exception {
		((Message)msg).handleReceiveFor(this);
	}
	
	public void forwardMessageToChildren(Message msg){
		for (ActorRef child : getForwarders())
			child.tell(msg, getSelf());
	}
	
	public void addChildren(List<ActorRef> children){
		children.addAll(children);
	}

	public void setFather(ActorRef father){
		this.father=father;
	}
	
	private List<ActorRef> getForwarders(){
		List<ActorRef> forwarders = children;
		if(father!=null)
			forwarders.add(father);
		forwarders.remove(getSender());
		return forwarders;
	}
}
