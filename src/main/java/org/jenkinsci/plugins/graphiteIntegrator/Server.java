/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jenkinsci.plugins.graphiteIntegrator;

/**
 *
 * @author joachimrodrigues
 */
public class Server {

    
    String ip;
    
    String port;
    
    String description;
            

    public String getIp() {
        return ip;
    }

    public String getPort() {
        return port;
    }

    public String getDescription() {
        return description;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public void setDescription(String description) {
        this.description = description;
    }
    
    
}

