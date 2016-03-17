package car.tp2;

import java.io.IOException;
import java.io.InputStream;
import java.net.SocketException;

import javax.ws.rs.GET;
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
	@Path("/file/{path}")
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

	/*
	 * PUT -> POST car il est impossible de créer un formulaire avec la méthode
	 * PUT.
	 */
/*AAAAA	@POST
	@Path("/put")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Produces("text/html")
	public String put(@MultipartForm FileUploadForm form) {
		try {
			/*
			 * Ici, récupérer le filename, les datas et tout envoyer au ftp avec ftp.stor();
			 * 
			 * Après plus d'une heure passée pour tenter de récupérer le nom du fichier,
			 * je suis contraint de rendre cette partie vide car il manque visiblement des
			 * librairies pour gérer les multipart forms.
			 * 
			 */
/*AAAAA			return "OK";
		} catch (IOException e) {
			e.printStackTrace();
			return "Rappelez-moi pourquoi on est obligés de le faire en java avec une API verbeuse, dans une version pas à jour avec des librairies qui manquent ?";
		}
	}
	
	/*
	 * Gérer toutes les autres requêtes de la même façon (parce que c'est toujours pareil).
	 * DELETE -> POST (car pas de delete dans les formulaires)
	 * MKDIR  -> POST aussi
	 */
	@GET
	@Path("/delete/{path}")
	public String delete(@PathParam("path") String path){
		try {
			ftp.dele(path);
			ftp.completePendingCommand();
			return path + " deleted";
		} catch (IOException e) {
			e.printStackTrace();
			return "HAHAHAHAHAHAHAHAHAHA non.";
		}
	}
	
	@GET
	@Path("/list/{path}")
	@Produces("text/html")
	public String list(@PathParam("path") String path){
		FTPFile[] files;
		try {
			files = ftp.listFiles(path);
			ftp.completePendingCommand();
			String res = "";
			for(FTPFile f : files)
				res += f.getName() + "<\br>";
			return res;
		} catch (IOException e) {
			e.printStackTrace();
			return "Erreur de la mort";
		}
		
	}
	
	@GET
	@Path("/mkdir/{path}")
	@Produces("text/html")
	public String mkdir(@PathParam("path") String path){
		try {
			String res = ftp.makeDirectory(path) ? path + " created" : "Non";
			ftp.completePendingCommand();
			return res;
		} catch (IOException e) {
			e.printStackTrace();
			return "nonon";
		}
	}
	
	/*
	 * Veuillez excuser ces commentaires acérés dûs à une perte de patience devant le côté
	 * laborieux d'un travail qui devrait pourtant être simple à réaliser.
	 */
}
