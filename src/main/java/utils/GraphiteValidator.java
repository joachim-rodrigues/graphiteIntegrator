package utils;


import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.regex.Pattern;

/**
 *
 * @author joachimrodrigues
 */
public class GraphiteValidator {

   
    /**
     *
     */
    final String ipPatern = "(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)";
    /**
     *
     */
    final String portPatern = "([0-9]{1,4}|[1-5][0-9]{4}|6[0-4][0-9]{3}|65[0-4][0-9]{2}|655[0-2][0-9]|6553[0-5])";

    /**
     *
     */
    public boolean validateIpFormat(String ip) {
        Pattern pattern = Pattern.compile(this.ipPatern);
        return pattern.matcher(ip).matches();
    }

    /**
     *
     */
    public boolean validatePortFormat(String port) {
        Pattern pattern = Pattern.compile(this.portPatern);
        return pattern.matcher(port).matches();
    }

    /**
     *
     */
    public boolean isListening(String ip, int port) {

        try {
            Socket socket = new Socket();
            socket.connect(new InetSocketAddress(ip, port), 5000);
            return true;
        } catch (IOException ex) {
            return false;
        }
    }

    /**
     *
     */
    public boolean isIpPresent(String ip) {
        if (ip.length() == 0) {
            return false;
        }
        return true;
    }

    /**
     *
     */
    public boolean isPortPresent(String port) {
        if (port.length() == 0) {
            return false;
        }
        return true;
    }

    /**
     *
     */
    public boolean isDescriptionPresent(String description) {
        if (description.length() == 0) {
            return false;
        }
        return true;
    }

    /**
     *
     */
    public boolean isDescriptionTooLong(String description) {
        if (description.length() > 100) {
            return true;
        }
        return false;
    }

}
