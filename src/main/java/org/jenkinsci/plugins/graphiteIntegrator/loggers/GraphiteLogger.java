
package org.jenkinsci.plugins.graphiteIntegrator.loggers;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * @author joachimrodrigues
 *
 */
public class GraphiteLogger {

    /**
     * 
     */
    PrintStream logger ;

    /**
     *
     * @param logger
     */
    public GraphiteLogger(PrintStream logger) {
        this.logger = logger;
    }
    
    
    /**
     *
     * @param graphiteHost
     * @param graphitePort
     * @param queue
     * @param metric
     * @throws UnknownHostException
     * @throws IOException
     */
    public void logToGraphite(String graphiteHost, String graphitePort, String queue, String metric) throws UnknownHostException, IOException  {
        
    	Socket conn = new Socket(graphiteHost, Integer.parseInt(graphitePort));
    			
        DataOutputStream dos = new DataOutputStream(conn.getOutputStream());
        String data = queue + " " + metric + "\n";
        dos.writeBytes(data);
        
    }

    
}
