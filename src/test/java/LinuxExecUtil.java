import ch.ethz.ssh2.Connection;
import ch.ethz.ssh2.SCPClient;
import ch.ethz.ssh2.Session;
import ch.ethz.ssh2.StreamGobbler;

import java.io.*;

public class LinuxExecUtil {

    private Connection conn;

    public Connection connect(final String host, final int port, final String user, final String password) throws IOException {
        conn = new Connection(host, port);
        conn.connect();
        boolean isAuthenticated = conn.authenticateWithPassword(user, password);
        if (isAuthenticated == false) {
            throw new IOException("Authentication failed.");
        }
        return conn;
    }

    public Connection connect(String host, String user, String password, String keyPath) throws IOException {
        conn = new Connection(host);
        conn.connect();
        File KeyFile = new File(keyPath);
        boolean isAuthenticated = conn.authenticateWithPublicKey(user, KeyFile, password);
        if (isAuthenticated == false) {
            throw new IOException("Authentication failed.");
        }
        return conn;
    }

    public void execute(final String commandText){
        try {
            System.out.println("exec : " + commandText);
            Session sess = conn.openSession();
            sess.execCommand(commandText);
            System.out.println("Here is some information about the remote host:");
            InputStream standardOut = new StreamGobbler(sess.getStdout());
            BufferedReader br = new BufferedReader(new InputStreamReader(standardOut));
            while (true) {
                String line = br.readLine();
                if (line == null) {
                    break;
                }
                System.out.println(line);
            }
            System.out.println("ExitCode: " + sess.getExitStatus());
            sess.close();
        }
        catch (IOException e)
        {
            e.printStackTrace(System.err);
            System.exit(2);
        }
    }

    public void upload(final String localFile, final String remoteTargetDirectory) throws IOException {
        SCPClient clt = conn.createSCPClient();
        clt.put(localFile, remoteTargetDirectory);
    }

    public void close(){
        if( conn != null )
            conn.close();
    }
}
