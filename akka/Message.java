import java.io.Serializable;

public class Message implements Serializable {
	private static final long serialVersionUID = 1L;
	public String msg;

	public Message() {
	}

	public Message(String msg) {
		this.msg = msg;
	}

	public void handleReceiveFor(Noeud n) {
		n.forwardMessageToChildren(this);
	}
}
