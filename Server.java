import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;

public class Server {

	public static final int ST_READY = 220;
	public static final int ST_MDP = 331;
	public static final int ST_AUTH_FAIL = 332;
	public static final int ST_AUTH_OK = 430;
	public static final int ST_TRANSFERT_START = 126;
	public static final int ST_TRANSFERT_END = 226;
	public static final int ST_QUIT = 221;

	public static final int port = 7654;
	public ServerSocket serverSocket;
	public ArrayList<FTPRequestHandler> clients;
	public CSVReader csvReader;

	public Server() throws IOException {
		this.clients = new ArrayList<FTPRequestHandler>();
		this.csvReader = new CSVReader();
		/* TO REMOVE */
		csvReader.addUser("guest", "guest");
		serverSocket = new ServerSocket(port);
		System.out.println("Server opened on port "+port);
		this.acceptClients();
	}

	public void acceptClients() throws IOException {
		while (true) {
			FTPRequestHandler f = new FTPRequestHandler(serverSocket.accept(),
					this);
			clients.add(f);
			Thread t = new Thread(f);
			t.start();
		}
	}

	public void close() throws IOException {
		for (FTPRequestHandler f : clients)
			this.remove(f);
		serverSocket.close();
	}

	public void remove(FTPRequestHandler ftpRequestHandler) {
		this.clients.remove(ftpRequestHandler);
		ftpRequestHandler.closeConnection();
	}

	public static void main(String[] args) throws IOException {
		new Server();
	}

}
