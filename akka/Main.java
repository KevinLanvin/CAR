import java.util.ArrayList;

import akka.actor.ActorPath;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;


public class Main {

	
	public static void main(String[] args) {
		ActorSystem syst = ActorSystem.create("NoobSyst");
		ActorRef noeud1, noeud2, noeud3, noeud4, noeud5, noeud6;
		
		noeud1 = syst.actorOf(Props.create(Noeud.class),"noeud1");
		noeud2 = syst.actorOf(Props.create(Noeud.class),"noeud2");
		noeud3 = syst.actorOf(Props.create(Noeud.class),"noeud3");
		noeud4 = syst.actorOf(Props.create(Noeud.class),"noeud4");
		noeud5 = syst.actorOf(Props.create(Noeud.class),"noeud5");
		noeud6 = syst.actorOf(Props.create(Noeud.class),"noeud6");
		
		ActorRef[] tab = {noeud2,noeud5};
		noeud1.tell(new ConstructionMessage(null,new ArrayList<ActorRef>(tab)), ActorRef.noSender());
		noeud1.tell(new ConstructionMessage(noeud5), ActorRef.noSender());
		noeud2.tell(new ConstructionMessage(noeud3), ActorRef.noSender());
		noeud2.tell(new ConstructionMessage(noeud4), ActorRef.noSender());
		noeud5.tell(new ConstructionMessage(noeud6), ActorRef.noSender());
		
		
		
		
		syst.shutdown();
	}

}
