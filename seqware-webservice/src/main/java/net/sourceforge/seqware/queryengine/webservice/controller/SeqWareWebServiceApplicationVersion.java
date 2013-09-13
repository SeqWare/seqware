package net.sourceforge.seqware.queryengine.webservice.controller;


import org.restlet.Component;
import org.restlet.Context;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.Restlet;
import org.restlet.data.ChallengeScheme;
import org.restlet.data.Method;
import org.restlet.data.Protocol;
import org.restlet.data.Reference;
import org.restlet.data.Status;
import org.restlet.ext.freemarker.ContextTemplateLoader;
import org.restlet.ext.wadl.ApplicationInfo;
import org.restlet.ext.wadl.DocumentationInfo;
import org.restlet.ext.wadl.WadlApplication;
import org.restlet.routing.Router;
import org.restlet.security.ChallengeAuthenticator;

import freemarker.template.Configuration;
import net.sf.beanlib.hibernate.UnEnhancer;
import net.sourceforge.seqware.queryengine.webservice.security.SeqWareVerifier;
import net.sourceforge.seqware.queryengine.webservice.view.*;
import org.restlet.ext.wadl.*;

/**
 * <p>SeqWareWebServiceApplicationVersion class.</p>
 *
 * @author morgantaschuk
 * @version $Id: $Id
 */
public class SeqWareWebServiceApplicationVersion extends WadlApplication {

    private Configuration configuration = null;

    /**
     * <p>Getter for the field <code>configuration</code>.</p>
     *
     * @return a {@link freemarker.template.Configuration} object.
     */
    public Configuration getConfiguration() {
        return configuration;
    }

    /**
     * Creates a root Restlet that will receive all incoming calls.
     *
     * @return a {@link org.restlet.Restlet} object.
     */
    public synchronized Restlet createInboundRoot() {
        final Component component = new Component();
        component.getClients().add(Protocol.CLAP);
        configuration = new Configuration();
        configuration.setTemplateLoader(new ContextTemplateLoader(getContext(),
                "clap://system/templates"));

        ChallengeAuthenticator guard = getGuard();

        //String rootURL = ""; 
        //if (EnvUtil.getProperty("urlhack") != null) { rootURL = EnvUtil.getProperty("urlhack"); }
        
        // We don't want to use CGLIB since it is a huge memory hog
        //see for more information: http://beanlib.svn.sourceforge.net/viewvc/beanlib/trunk/beanlib-doc/faq.html
        UnEnhancer.setDefaultCheckCGLib(false);
        
        

        String version = "queryengine";
        //if (EnvUtil.getProperty("version") != null) { rootURL = EnvUtil.getProperty("version"); }

        // Create a router Restlet that routes each call to a
        // new instance of HelloWorldResource.
        Router router = new Router(getContext());
        router.setDefaultMatchingQuery(false);

        router.setRoutingMode(Router.MODE_LAST_MATCH);

        // I don't know if this is needed anymore
        getConnectorService().getClientProtocols().add(Protocol.FILE);

        Restlet slashRedirect = new OptionalSlashRedirect(getContext());
        // VERSION
        // this is used by various tools as a heartbeat to see if the application is alive
        router.attach("/version", VersionResource.class);

        return router;

    }

    private ChallengeAuthenticator getGuard() {
        // FIXME: double slash is an artifact of the groove proxy server
        // get the ROOT URL for various uses
        //String rootURL = "";
        //if (EnvUtil.getProperty("urlhack") != null) { rootURL = EnvUtil.getProperty("urlhack"); }
        // Guard the restlet with BASIC authentication.
        ChallengeAuthenticator guard = new ChallengeAuthenticator(null,
                ChallengeScheme.HTTP_BASIC, "testRealm");
        // Instantiates a Verifier of identifier/secret couples based on a
        // simple Map.
        SeqWareVerifier verifier = new SeqWareVerifier();
        guard.setVerifier(verifier);
        return guard;
    }

    /** {@inheritDoc} */
    @Override
    public ApplicationInfo getApplicationInfo(Request request, Response response) {
        ApplicationInfo result = super.getApplicationInfo(request, response);
        ResourcesInfo ri = new ResourcesInfo();
        ResourceInfo r = new ResourceInfo();
//        ri.setResources(null);
        
//        result.setResources(ri);

        DocumentationInfo docInfo = new DocumentationInfo(
                "SeqWare Web Service Application");
        docInfo.setTitle("First resource sample application.");
        result.setDocumentation(docInfo);
        

        return result;
    }

    /**
     * http://restlet-discuss.1400322.n2.nabble.com/Proper-handling-of-at-the-end-of-the-requested-URI-td5819896.html
     */
    protected static class OptionalSlashRedirect extends Restlet {

        public OptionalSlashRedirect(Context context) {
            super(context);
        }

        public OptionalSlashRedirect() {
        }

        public void handle(Request request, Response response) {
            super.handle(request, response);

            Method m = request.getMethod();
            if (m.equals(Method.GET) || m.equals(Method.HEAD)) {

                Reference ref = request.getOriginalRef().getTargetRef();
                String path = ref.getPath();
                if (path.endsWith("/")) {
                    path = path.substring(0, path.length() - 1);
                } else {
                    path = path + "/";
                }
                ref.setPath(path);

                response.redirectPermanent(ref);

            } else {
                response.setStatus(Status.CLIENT_ERROR_NOT_FOUND);
            }
        }
    }
}
