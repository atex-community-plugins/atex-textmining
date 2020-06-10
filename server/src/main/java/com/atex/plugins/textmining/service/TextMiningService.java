package com.atex.plugins.textmining.service;

import com.atex.plugins.textmining.TextMiningClient;
import com.atex.plugins.textmining.TextMiningConfigPolicy;
import com.atex.plugins.textmining.calais.api.CalaisMiningClient;
import com.atex.plugins.textmining.extraggo.api.ExtraggoMiningClient;
import com.atex.plugins.textmining.google.GoogleMiningClient;

import com.polopoly.application.Application;
import com.polopoly.application.IllegalApplicationStateException;
import com.polopoly.application.servlet.ApplicationServletUtil;
import com.polopoly.cm.ExternalContentId;
import com.polopoly.cm.client.CMException;
import com.polopoly.cm.client.CmClient;
import com.polopoly.cm.policy.PolicyCMServer;
import com.polopoly.metadata.Annotation;
import com.polopoly.metadata.Dimension;
import com.polopoly.textmining.Document;

import org.apache.commons.lang.StringUtils;
import org.jsoup.Jsoup;

import javax.servlet.ServletContext;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import static com.atex.plugins.textmining.TextMiningConfigPolicy.CONFIG_EXTERNAL_ID;

@Path("metadata")
public class TextMiningService {

    private static Logger LOG = Logger.getLogger(TextMiningService.class.getName());

    Pattern htmlPattern = Pattern.compile(".*\\<[^>]+>.*", Pattern.DOTALL);

    private static TextMiningClient textMiningClient;

    private static final Object lock = new Object();
    private static Integer lastConfigVersion = 0;

    @Context
    private ServletContext servletContext;

    public TextMiningClient getTextMiningClient() throws CMException {
        PolicyCMServer cmServer = getCmServer();
        if (cmServer != null) {
            final TextMiningConfigPolicy policy = (TextMiningConfigPolicy) cmServer.getPolicy(new ExternalContentId(CONFIG_EXTERNAL_ID));
            if (policy != null) {
                synchronized (lock) {
                        String provider = policy.getConfig().getProviderName();
                           switch (provider){
                            case "google":
                                setTextMiningClient(new GoogleMiningClient(policy.getConfig()));
                                break;
                            case "calais":
                                setTextMiningClient(new CalaisMiningClient(policy.getConfig()));
                                break;
                            case "extraggo":
                                setTextMiningClient(new ExtraggoMiningClient(policy.getConfig()));
                                break;
                            default:
                                throw new CMException("No provider found");
                        }
                    }

            } else {
                throw new CMException("Content " + CONFIG_EXTERNAL_ID + " not found");
            }
        } else {
            throw new CMException("CmClient is not set");
        }
        return textMiningClient;
    }

    public static void setTextMiningClient(TextMiningClient textMiningClient) {
        TextMiningService.textMiningClient = textMiningClient;
    }

    @GET
    @Produces({"text/plain"})
    public String description() {
        return textMiningClient.getDescription();
    }

    @GET
    @Produces({"text/plain"})
    @Path("configured")
    public String configured() {
        try {
            return String.valueOf(getTextMiningClient().isConfigured());
        } catch (Exception e) {
            LOG.log(Level.WARNING, "Textmining not configured due to error", e);
        }
        return String.valueOf(false);
    }

    @POST
    @Consumes({"application/xml", "application/json"})
    @Produces({"application/xml", "application/json"})
    @Path("annotation")
    public Annotation suggestCategorization(Document document) {

        if (document == null || StringUtils.isBlank(document.body) || document.plan == null) {
            throw new WebApplicationException(Response.Status.BAD_REQUEST);
        }

        try {
            if (htmlPattern.matcher(document.body).matches()) {
                document.body = Jsoup.parse(document.body).text();
            }
            TextMiningClient client = getTextMiningClient();
            if (client != null) {
                return client.analyzeText(document, getCmServer());
            } else {
                throw new CMException("unable to make textMiningClient");
            }

        } catch (Exception e) {
            LOG.log (Level.WARNING, "Exception calling service", e);
            throw new WebApplicationException(e, Response.Status.INTERNAL_SERVER_ERROR);
        }

    }

    private PolicyCMServer getCmServer() {
        try {
            Application app = ApplicationServletUtil.getApplication(servletContext);
            CmClient cmClient = app.getPreferredApplicationComponent(CmClient.class);
            return cmClient.getPolicyCMServer();
        } catch (IllegalApplicationStateException e) {
            LOG.severe("unable to get policy cmserver");
            return null;
        }
    }


    @POST
    @Consumes({"application/xml", "application/json"})
    @Path("add-entity")
    public void addEntity(Dimension dimensionWithEntityChain) {
        if (dimensionWithEntityChain == null || dimensionWithEntityChain.getId() == null || dimensionWithEntityChain.getEntities().size() != 1) {
            throw new WebApplicationException(Response.Status.BAD_REQUEST);
        }
    }

}