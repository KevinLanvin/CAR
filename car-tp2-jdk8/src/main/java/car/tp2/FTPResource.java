package car.tp2;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.SocketException;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.StreamingOutput;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPClientConfig;

@Path("/ftp")
public class FTPResource {
	FTPClient ftp;
	FTPClientConfig config;
	
	public FTPResource() throws SocketException, IOException{
		ftp = new FTPClient();
		config = new FTPClientConfig(FTPClientConfig.SYST_UNIX);
		ftp.configure(config);
		ftp.connect("localhost", 7654);
		ftp.login("guest","guest");
	}
	
	@GET
	@Produces("text/html")
	public String index(){
		return "<h1>Welcome to the best REST service the world has ever seen !</h1>";
	}
	
	@GET
	@Produces("application/octet-stream")
	@Path("/file/{path}")
	public StreamingOutput get( @PathParam("path") String path){
		StreamingOutput out = null;
		try {
			final InputStream in = ftp.retrieveFileStream(path);
			out = new StreamingOutput() {	
				public void write(OutputStream os) throws IOException,
						WebApplicationException {
					int b;
					while((b=in.read()) != -1)
						os.write(b);
				}
			};
		} catch (IOException e) {
			e.printStackTrace();
		}
		return out;
	}
	
	
}
