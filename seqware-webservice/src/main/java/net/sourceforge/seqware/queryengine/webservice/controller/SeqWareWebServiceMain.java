/**
 * 
 */
package net.sourceforge.seqware.queryengine.webservice.controller;


import org.restlet.Component;
import org.restlet.data.Protocol;

/**
 * <p>SeqWareWebServiceMain class.</p>
 *
 * @author boconnor
 * @version $Id: $Id
 */
public class SeqWareWebServiceMain {

    private static Component component = null;

    private SeqWareWebServiceMain() throws Exception {
        // Create a new Component.
        component = new Component();
//        JndiDatasourceCreator.create();
        // Add a new HTTP server listening on port 8181.
//            String port = EnvUtil.getProperty("port");
//            if (port != null && !"".equals(port)) {
//                component.getServers().add(Protocol.HTTP, Integer.parseInt(port));
//            } else {
        component.getServers().add(Protocol.HTTP, 8183);
//            }
        component.getClients().add(Protocol.WAR);
        component.getClients().add(Protocol.FILE);
        component.getClients().add(Protocol.CLAP);
        // Attach the sample application.
        component.getDefaultHost().attach(new SeqWareWebServiceApplication());

        // Start the component.
        component.start();
    }

    /**
     * <p>stop.</p>
     *
     * @throws java.lang.Exception if any.
     */
    public static void stop() throws Exception {
        component.stop();
        while (!component.isStopped()) {
            component.stop();
        }
    }

    /**
     * <p>main.</p>
     *
     * @param args an array of {@link java.lang.String} objects.
     */
    public static void main(String[] args) {
        try {
            new SeqWareWebServiceMain();
        } catch (Exception e) {
            // Something is wrong.
            e.printStackTrace();
        }
    }
}
