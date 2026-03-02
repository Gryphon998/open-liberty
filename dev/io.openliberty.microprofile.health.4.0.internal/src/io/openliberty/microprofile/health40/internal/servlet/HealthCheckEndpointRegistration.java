/*******************************************************************************
 * Copyright (c) 2026 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package io.openliberty.microprofile.health40.internal.servlet;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.ServletRegistration;
import javax.servlet.http.HttpServlet;

/**
 *
 */
public class HealthCheckEndpointRegistration implements ServletContextListener {

    /** {@inheritDoc} */
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        // TODO Auto-generated method stub
        //System.out.println("*************************************** In Context Initialized");

        ServletContext cxt = sce.getServletContext();

        registerHealthCheckEndpoint(cxt, "HealthCheckServlet", HealthCheckServlet.class, "");
        registerHealthCheckEndpoint(cxt, "HealthCheckLivenessServlet", HealthCheckLivenessServlet.class, "/live");
        registerHealthCheckEndpoint(cxt, "HealthCheckReadinessServlet", HealthCheckReadinessServlet.class, "/ready");
        registerHealthCheckEndpoint(cxt, "HealthCheckStartupServlet", HealthCheckStartupServlet.class, "/started");
    }

    private void registerHealthCheckEndpoint(ServletContext context, String healthCheckServletName, Class<? extends HttpServlet> healthCheckServletClass, String path) {
        try {
            ServletRegistration.Dynamic servlet = context.addServlet(healthCheckServletName, healthCheckServletClass);

            if (servlet != null) {
                servlet.addMapping(path);
                servlet.setLoadOnStartup(1);
                //System.out.println("Servlet " + healthCheckServletName + " successfully registered!");
            } else {
                //System.out.println("Servlet already registered!");
            }

        } catch (Exception e) {
            System.out.println("Exception: " + e);
        }

    }

    /** {@inheritDoc} */
    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        // TODO Auto-generated method stub
        //System.out.println("*************************************** In Context Destroyed");
    }

}
