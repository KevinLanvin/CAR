import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;

public class FTPRequestHandler implements Runnable {

	private Socket socket;
	private Server server;
	private PrintStream output;
	private BufferedReader input;
	private String client;
	private int state;

	public FTPRequestHandler(Socket socket, Server s) throws IOException {
		this.socket = socket;
		this.server = s;
		this.output = new PrintStream(socket.getOutputStream());
		this.input = new BufferedReader(new InputStreamReader(
				socket.getInputStream()));

	}

	public void run() {
		this.authenticate();
		while (!socket.isClosed())
			this.processRequest();
	}

	private void authenticate() {
		state = Server.ST_READY;
		this.write(state + " Ready to authenticate");
		this.processRequest();
	}

	private void write(String string) {
		output.println(string);
	}

	private String read() {
		try {
			return input.readLine();
		} catch (IOException e) {
			server.remove(this);
		}
		return "";
	}

	public void closeConnection() {
		try {
			state = Server.ST_QUIT;
			this.write(state + " Disconnection");
			socket.close();
		} catch (IOException e) {
			System.out.println("Client already disconnected");
		}
	}

	/* Dispatch Request to the right method */
	public void processRequest() {
		String msg = this.read();
		String[] cmds = msg.split(" ", 2);
		switch (cmds[0]) {
		case "USER":
			this.processUser(cmds[1]);
			break;
		case "PASS":
			this.processPass(cmds[1]);
			break;
		case "LIST":
			this.processList(cmds[1]);
			break;
		case "RETR":
			this.processRetr(cmds[1]);
			break;
		case "STOR":
			this.processStor(cmds[1]);
			break;
		case "QUIT":
			this.processQuit(cmds[1]);
			break;
		default:
			break;
		}
	}

	private void processUser(String string) {
		if (state != Server.ST_READY && state != Server.ST_AUTH_FAIL) {
			/* Already logged */
			return;
		}
		if (this.checkUser(string)) {
			state = Server.ST_MDP;
			this.write(state + " Type your password");
		} else {
			this.authenticationFailed();
		}
	}

	private void processPass(String string) {
		if (state != Server.ST_MDP) {
			/* Server not waiting for password */
			this.write("503 Bad Request");
			return;
		}
		if (string.equals("guest")) {
			state = Server.ST_AUTH_OK;
			this.write(state + " Logged as guest");
		} else {
			this.authenticationFailed();
		}
	}

	private void processList(String string) {

	}

	private void processRetr(String string) {

	}

	private void processStor(String string) {

	}

	private void processQuit(String string) {

	}

	private void authenticationFailed() {
		state = Server.ST_AUTH_FAIL;
		this.write(state + " User not found");
	}

	/* Checks if user is existing */
	private boolean checkUser(String string) {
		try {
			for (String s : server.csvReader.allUsers()){
				if (string.equals(s)){
					client = s;
					System.out.println("USER OK");
					return true;
				}
			}
		} catch (IOException e) {
			System.out.println("Error while getting users");
		}
		return false;
	}

	private boolean checkPassword(String password) {
		try {
			return server.csvReader.passwordOfUser(client).hashCode() == password.hashCode();
		} catch (IOException e) {
			System.out.println("Error : Can't read users file");
			return false;
		}
	}
}
