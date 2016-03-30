package car.tp2;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.SocketException;

import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPClientConfig;
import org.apache.commons.net.ftp.FTPFile;

@Path("/ftp")
public class FTPResource {
	FTPClient ftp;
	FTPClientConfig config;

	public FTPResource() throws SocketException, IOException {
		super();
		ftp = new FTPClient();
		config = new FTPClientConfig(FTPClientConfig.SYST_UNIX);
		config.setUnparseableEntries(true);
		ftp.configure(config);
		ftp.connect("localhost", 7654);
		ftp.login("guest", "guest");
	}

	@GET
	@Produces("text/html")
	public String index() {
		return "<h1>Welcome to the best REST service the world has ever seen !</h1>";
	}

	@GET
	@Produces("application/octet-stream")
	@Path("/file{path : (/.*)*}")
	public Response get(@PathParam("path") String path) {
		try {
			byte[] buffer = new byte[10000000];
			InputStream in = ftp.retrieveFileStream(path);
			ftp.completePendingCommand();
			in.read(buffer);
			return Response.ok(buffer).build();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	@POST
	@Path("/put")
	@Produces("text/html")
	public String put(@FormParam("path") String path,
			@FormParam("file") File file) {
		try {
			ftp.storeFile(path, new FileInputStream(file));
			return "OK";
		} catch (IOException e) {
			e.printStackTrace();
			return "Nope";
		}
	}

	@GET
	@Path("/delete{path : (/.*)*}")
	public String delete(@PathParam("path") String path) {
		System.out.println("Delete " + path);
		try {
			ftp.dele(path);
			return path + " deleted";
		} catch (IOException e) {
			e.printStackTrace();
			return "HAHAHAHAHAHAHAHAHAHA non.";
		}
	}

	@GET
	@Path("/list{path : (/.*)*}")
	@Produces("text/html")
	public String list(@PathParam("path") String path) {
		FTPFile[] files;
		try {
			ftp.changeWorkingDirectory(path);
			files = ftp.listFiles();
			String res = "<a href= \"http://localhost:8080/rest/tp2/ftp/list/"
					+ path + "/..\">..</a><br />";
			for (FTPFile f : files)
				res += parse(f.toString(), path) + "<br />";
			return res;
		} catch (IOException e) {
			e.printStackTrace();
			return "Erreur de la mort";
		}

	}

	private String parse(String name, String path) {
		String[] words = name.split(" ");
		if (words[0].equals("f"))
			return "<a href= \"http://localhost:8080/rest/tp2/ftp/file/"
					+ path
					+ words[1]
					+ "\">"
					+ words[1]
					+ "</a> (<a href=\"http://localhost:8080/rest/tp2/ftp/delete/"
					+ path + words[1] + "\">delete</a>)";
		if (words[0].equals("d"))
			return "<a href= \"http://localhost:8080/rest/tp2/ftp/list/" + path
					+ words[1] + "/\">" + words[1] + "</a>";
		return "Mauvais formatage des données";
	}

	@GET
	@Path("/mkdir{path : (/.*)*}")
	@Produces("text/html")
	public String mkdir(@PathParam("path") String path) {
		try {
			String res = ftp.makeDirectory(path) ? path + " created" : "Non";
			return res;
		} catch (IOException e) {
			e.printStackTrace();
			return "nonon";
		}
	}

}
