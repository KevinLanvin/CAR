package server;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.nio.file.DirectoryIteratorException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FTPRequestHandler implements Runnable {

	public Socket socket, dataSocket;
	public Server server;
	public File currentDir;
	public File rootDir;
	public PrintStream output;
	public BufferedReader input;
	public String client;
	public int state;

	public FTPRequestHandler(Socket socket, Server s) throws IOException {
		this.socket = socket;
		this.server = s;
		this.output = new PrintStream(socket.getOutputStream());
		this.input = new BufferedReader(new InputStreamReader(
				socket.getInputStream()));
		this.currentDir = this.rootDir = new File(".");
	}

	public void run() {
		this.authenticate();
		while (!socket.isClosed())
			this.processRequest();
	}

	public void authenticate() {
		state = Server.ST_READY;
		this.write(state + " Ready to authenticate");
		this.processRequest();
	}

	public void write(String string) {
		output.println(string);
	}

	public String read() {
		try {
			return input.readLine();
		} catch (IOException e) {
			server.remove(this);
		}
		return "";
	}

	public void readDataIntoFile(File f) throws IOException {
		int b;
		FileOutputStream out = new FileOutputStream(f);
		while ((b = dataSocket.getInputStream().read()) != -1)
			out.write(b);
		out.close();
	}

	public void writeDataFromFile(InputStreamReader fileReader)
			throws IOException {
		int c;
		while ((c = fileReader.read()) != -1)
			dataSocket.getOutputStream().write(c);
		dataSocket.getOutputStream().flush();
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

	/* Dispatch request to the right method */
	public void processRequest() {
		String msg = this.read();
		System.out.println(msg);
		String[] cmds = msg.split(" ", 2);
		switch (cmds[0]) {
		case "USER":
			this.processUser(cmds[1]);
			break;
		case "PASS":
			this.processPass(cmds[1]);
			break;
		case "LIST":
			this.processList();
			break;
		case "RETR":
			this.processRetr(cmds[1]);
			break;
		case "STOR":
			this.processStor(cmds[1]);
			break;
		case "MKD":
			this.processMkdir(cmds[1]);
		case "SYST":
			this.processSyst();
			break;
		case "PORT":
			this.processPort(cmds[1]);
			break;
		case "CWD":
			this.processCWD(cmds[1]);
		case "QUIT":
			this.processQuit();
			break;
		default:
			break;
		}
	}

	public void processUser(String string) {
		if (state != Server.ST_READY && state != Server.ST_AUTH_FAIL) {
			/* Server not waiting for user */
			state = Server.ST_BAD_REQUEST;
			this.write(state+" Bad Request");
			return;
		}
		if (this.checkUser(string)) {
			state = Server.ST_MDP;
			this.write(state + " Type your password");
		} else {
			this.authenticationFailed();
		}
	}

	public void processPass(String string) {
		if (state != Server.ST_MDP) {
			/* Server not waiting for password */
			state = Server.ST_BAD_REQUEST;
			this.write(state+" Bad Request");
			return;
		}
		if (this.checkPassword(string)) {
			state = Server.ST_AUTH_OK;
			this.write(state + " Logged as guest");
		} else {
			this.authenticationFailed();
		}
	}

	public void processList() {
		state = Server.ST_Transfert_INCOMING;
		this.write(state + " Listing the files");
		try {
			Path dir = Paths.get("./users/files");
			DirectoryStream<Path> stream = Files.newDirectoryStream(dir);
			for (Path file : stream) {
				dataSocket.getOutputStream().write(
						(file.getFileName() + "\015\012").getBytes());
			}
			dataSocket.close();
		} catch (IOException | DirectoryIteratorException x) {
			// IOException can never be thrown by the iteration.
			// In this snippet, it can only be thrown by newDirectoryStream.
			System.err.println(x);
		}
		state = Server.ST_TRANSFERT_END;
		this.write(state + " Listing done");
	}

	public void processRetr(String string) {
		InputStreamReader fileReader;
		try {
			fileReader = new InputStreamReader(new FileInputStream(
					"./users/files/" + string));
		} catch (FileNotFoundException e) {
			state = Server.ST_FILE_NOT_FOUND;
			this.write(state + " File does not exist");
			return;
		}
		state = Server.ST_Transfert_INCOMING;
		this.write(state + " Transfering datas");
		try {
			this.writeDataFromFile(fileReader);
			state = Server.ST_TRANSFERT_END;
			this.write(state + " File downloaded");
			dataSocket.close();
		} catch (IOException e) {
			state = Server.ST_CONNECTION_BROKEN;
			this.write(state + " Connection broken");
		}

	}

	public void processStor(String string) {
		File file = new File("./users/files/" + string);
		state = Server.ST_Transfert_INCOMING;
		this.write(state + " Waiting for datas");
		try {
			this.readDataIntoFile(file);
			state = Server.ST_TRANSFERT_END;
			this.write(state + " File copied");
			dataSocket.close();
		} catch (IOException e) {
			state = Server.ST_CONNECTION_BROKEN;
			this.write(state + " Connection broken");
		}
	}

	public void processPort(String string) {
		String[] numbers = string.split(",");
		int port = (Integer.parseInt(numbers[4]) * 256)
				+ Integer.parseInt(numbers[5]);
		try {
			dataSocket = new Socket(socket.getInetAddress(), port);
			state = Server.ST_PORT_ACCEPTED;
			this.write(state + " Data connection created");
		} catch (IOException e) {
			state = Server.ST_CONNECTION_FAILED;
			this.write(state + " No connection established");
			return;
		}
	}
	
	public void processMkdir(String dirname){
		File dir= new File(currentDir.getAbsolutePath()+"/"+dirname);
		dir.mkdir();
	}

	public void processCWD(String dir){
		File tempDir = currentDir;
		String[]dirNames = dir.split("/");
		for(String s : dirNames){
			switch(s){
				case ".." :
					if(!currentDir.equals(rootDir))
						currentDir=currentDir.getParentFile();
					else {
						state = Server.ST_FILE_NOT_FOUND;
						this.write(state + " Dir not found");
						currentDir=tempDir;
						return;
					}
					break;
				case ".":
					break;
				default:
					currentDir = new File(currentDir.getAbsolutePath()+s);
			}
		}
		state = Server.ST_CWD_OK;
		this.write(state + " Ok");
	}
	
	public void processSyst() {
		state = Server.ST_SYST;
		this.write(state + " UNIX Type: AT");
	}

	public void processQuit() {
		state = Server.ST_QUIT;
		this.write(state + " Bye");
		try {
			socket.close();
		} catch (IOException e) {
		}
	}

	public void authenticationFailed() {
		state = Server.ST_AUTH_FAIL;
		this.write(state + " Authentication failed");
	}

	/* Checks if user is existing */
	public boolean checkUser(String string) {
		try {
			for (String s : server.csvReader.allUsers()) {
				if (string.equals(s)) {
					client = s;
					return true;
				}
			}
		} catch (IOException e) {
			System.out.println("Error while getting users");
		}
		return false;
	}

	/* Checks if the given password matches with current user */
	public boolean checkPassword(String password) {
		try {
			return server.csvReader.passwordOfUser(client) == password
					.hashCode();
		} catch (IOException e) {
			System.out.println("Error : Can't read users file");
			return false;
		}
	}
}