package com.atex.plugins.textmining.extraggo;

import com.polopoly.application.*;
import com.polopoly.application.config.ConfigurationRuntimeException;
import com.polopoly.application.servlet.ApplicationServletUtil;
import com.polopoly.cm.client.CmClientBase;
import com.polopoly.cm.client.DiskCacheSettings;
import com.polopoly.cm.client.HttpCmClientHelper;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.util.logging.Level;
import java.util.logging.Logger;


public class TextminingServletContextListener implements ServletContextListener {

    private static final Logger LOGGER = Logger.getLogger(TextminingServletContextListener.class.getName());

    private Application application;

    @Override
    public void contextInitialized(final ServletContextEvent sce) {
        try {
            ServletContext servletContext = sce.getServletContext();

            application = new StandardApplication(ApplicationServletUtil.getApplicationName(servletContext));
            application.setManagedBeanRegistry(ApplicationServletUtil.getManagedBeanRegistry());

            ConnectionProperties connectionProperties = ApplicationServletUtil.getConnectionProperties(servletContext);
            CmClientBase cmClient = HttpCmClientHelper.createAndAddToApplication(application, connectionProperties);

            application.readConnectionProperties(connectionProperties);

            final DiskCacheSettings diskCacheSettings = new DiskCacheSettings();
            diskCacheSettings.setContentCacheDisabled(true);
            diskCacheSettings.setFilesCacheDisabled(true);
            cmClient.setDiskCacheSettings(diskCacheSettings);

            PacemakerComponent pacemaker = new PacemakerComponent();
            PacemakerSettings pacemakerSettings = new PacemakerSettings();
            pacemakerSettings.setEnabled(true);
            pacemakerSettings.setInterval(5.0);
            pacemaker.setPacemakerSettings(pacemakerSettings);
            application.addApplicationComponent(pacemaker);

            ApplicationServletUtil.initApplication(servletContext, application);
            ApplicationServletUtil.setApplication(servletContext, application);

        } catch (IllegalApplicationStateException e) {
            throw new RuntimeException("This is a programming error, should never happen.", e);
        } catch (ConnectionPropertiesException e) {
            LOGGER.log(Level.SEVERE, "Could not get, read or apply connection properties.", e);
        } catch (ConfigurationRuntimeException e) {
            LOGGER.log(Level.SEVERE, "Could not read or apply configuration.", e);
        }
    }

    @Override
    public void contextDestroyed(final ServletContextEvent sce) {
        ServletContext sc = sce.getServletContext();

        ApplicationServletUtil.setApplication(sc, null);
        if (application != null) {
            application.destroy();
        }

        LegacyDaemonThreadsStopper.stopStaticDaemons();
    }
}