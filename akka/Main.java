import java.util.HashSet;
import java.util.Set;

import akka.actor.ActorRef;
import akka.actor.ActorSelection;
import akka.actor.ActorSystem;
import akka.actor.Props;

public class Main {

	public static void main(String[] args) throws InterruptedException {
		ActorSystem syst = ActorSystem.create("NoobSyst");
		ActorRef noeud1, noeud2, noeud3, noeud4, noeud5, noeud6;

		noeud1 = syst.actorOf(Props.create(Noeud.class), "noeud1");
		noeud2 = syst.actorOf(Props.create(Noeud.class), "noeud2");
		noeud3 = syst.actorOf(Props.create(Noeud.class), "noeud3");
		noeud4 = syst.actorOf(Props.create(Noeud.class), "noeud4");
		noeud5 = syst.actorOf(Props.create(Noeud.class), "noeud5");
		noeud6 = syst.actorOf(Props.create(Noeud.class), "noeud6");

		Set<ActorRef> list = new HashSet<ActorRef>();
		list.add(noeud2);
		list.add(noeud5);
		noeud1.tell(new ConstructionMessage(list), ActorRef.noSender());

		Set<ActorRef> list2 = new HashSet<ActorRef>();
		list2.add(noeud3);
		list2.add(noeud4);
		list2.add(noeud1);
		noeud2.tell(new ConstructionMessage(list2), ActorRef.noSender());

		Set<ActorRef> list3 = new HashSet<ActorRef>();
		list3.add(noeud2);
		noeud3.tell(new ConstructionMessage(list3), ActorRef.noSender());
		noeud4.tell(new ConstructionMessage(list3), ActorRef.noSender());

		Set<ActorRef> list4 = new HashSet<ActorRef>();
		list4.add(noeud6);
		list4.add(noeud1);
		noeud5.tell(new ConstructionMessage(list4), ActorRef.noSender());

		Set<ActorRef> list5 = new HashSet<ActorRef>();
		list5.add(noeud5);
		noeud6.tell(new ConstructionMessage(list5), ActorRef.noSender());

		Thread.sleep(2000); // Parce qu'on n'a pas de wait pour les
							// communications non bloquantes

		
		ActorSystem clientSyst = ActorSystem.create("clientSyst");
		String url = "akka.tcp://NoobSyst@localhost:2552/user/noeud1";
		ActorSelection root = clientSyst.actorSelection(url);

		root.tell(new Message("Hello"), ActorRef.noSender());
		
		syst.shutdown();
	}

}
