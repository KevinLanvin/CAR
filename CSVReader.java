import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

public class CSVReader {

	File usersFile;

	public CSVReader() {
		this.usersFile = new File("./users/users.csv");
	}

	public List<String> allUsers() throws IOException {
		List<String> result = new ArrayList<String>();
		FileReader fr;
		try {
			fr = new FileReader(usersFile);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return result;
		}
		BufferedReader br = new BufferedReader(fr);

		for (String line = br.readLine(); line != null; line = br.readLine()) {
			result.add(line.split(",")[0]);
		}
		br.close();
		fr.close();
		return result;
	}
	
	public void addUser(String user, String password){
		try {
			PrintStream ps = new PrintStream(usersFile);
			ps.println(user + ',' + password.hashCode());
			ps.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	
	public String passwordOfUser(String s) throws IOException{
		FileReader fr;
		try {
			fr = new FileReader(usersFile);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return "ERROR";
		}
		BufferedReader br = new BufferedReader(fr);
		
		for (String line = br.readLine(); line != null; line = br.readLine()) {
			if(line.split(",")[0].equals(s)){
				br.close();
				fr.close();
				return line.split(",")[1];
			}
		}
		br.close();
		fr.close();
		return "Not Found";
	}
}
