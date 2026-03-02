///*******************************************************************************
// * Copyright (c) 2026 IBM Corporation and others.
// * All rights reserved. This program and the accompanying materials
// * are made available under the terms of the Eclipse Public License 2.0
// * which accompanies this distribution, and is available at
// * http://www.eclipse.org/legal/epl-2.0/
// *
// * SPDX-License-Identifier: EPL-2.0
// *******************************************************************************/
//package io.openliberty.microprofile.health40.internal;
//
//import java.util.Dictionary;
//import java.util.Hashtable;
//import java.util.Map;
//
//import org.osgi.framework.BundleContext;
//import org.osgi.framework.ServiceRegistration;
//import org.osgi.service.component.ComponentContext;
//import org.osgi.service.component.annotations.Activate;
//import org.osgi.service.component.annotations.Component;
//import org.osgi.service.component.annotations.ConfigurationPolicy;
//import org.osgi.service.component.annotations.Deactivate;
//import org.osgi.service.component.annotations.Modified;
//import org.osgi.service.component.annotations.Reference;
//import org.osgi.service.component.annotations.ReferencePolicy;
//import org.osgi.service.component.annotations.ReferencePolicyOption;
//
//import com.ibm.wsspi.wab.configure.WABConfiguration;
//
////import io.openliberty.microprofile.health40.internal.HealthCheckEndpointManager.WABConfigManager;
//
///**
// *
// */
//@Component(configurationPid = "io.openliberty.microprofile.health", configurationPolicy = ConfigurationPolicy.OPTIONAL, immediate = true, property = { "service.vendor=IBM" })
//public class HealthCheckEndpointManager {
//
//    private static final String HEALTH_CHECK_CONTEXT_PATH_VAR_NAME = "healthCheckContextPath";
//    private static final String HEALTH_CHECK_CONTEXT_PATH = "/health";
//
//    private static final String HEALTH_CHECK_ENDPOINTS_ENABLED = "enableEndpoints";
//    private static final String ENV_HEALTH_CHECK_ENDPOINTS_ENABLED = "MP_HEALTH_ENABLE_ENDPOINTS";
//
//    private final WABConfigManager publicWabConfigMgr;
//
//    ComponentContext componentContext;
//
//    private HealthCheck40Service healthCheckService;
//
//    private volatile boolean endpointsCurrentlyEnabled = false;
//
//    public HealthCheckEndpointManager() {
//        publicWabConfigMgr = new WABConfigManager(HEALTH_CHECK_CONTEXT_PATH_VAR_NAME, HEALTH_CHECK_CONTEXT_PATH);
//    }
//
//    @Reference(service = HealthCheck40Service.class, policy = ReferencePolicy.DYNAMIC, policyOption = ReferencePolicyOption.GREEDY)
//    protected void setHealthCheckService(HealthCheck40Service service) {
//        this.healthCheckService = service;
//    }
//
//    protected void unsetHealthCheckService(HealthCheck40Service service) {
//        if (this.healthCheckService == service) {
//            this.healthCheckService = null;
//        }
//    }
//
//    @Activate
//    protected void activate(ComponentContext cc, Map<String, Object> properties) {
//        System.out.println("In Activate!");
//        componentContext = cc;
//        boolean isEnabled = areHealthCheckEndpointsEnabled(properties);
////        System.out.println("Starting Health Check endpoints enabled: " + isEnabled);
//        publicWabConfigMgr.manageEndpoints(cc, isEnabled);
//        endpointsCurrentlyEnabled = isEnabled;
//    }
//
//    @Deactivate
//    protected void deactivate() {
//        System.out.println("In Deactivate!");
//
//        //publicWabConfigMgr.deactivate();
//    }
//
//    @Modified
//    protected void modified(Map<String, Object> properties) {
//        System.out.println("In Modified!");
//        String enabled = parseServerConfig(properties);
//
//        boolean result;
//        if (healthCheckService != null && !healthCheckService.isFileHealthCheckingEnabled()) {
//            if (enabled != null) {
//                System.out.println("WARNING: Health endpoint enable/disable configuration is set, but file health check is not enabled. Endpoints will always be registered.");
//            }
//            result = true;
//        } else {
//            result = (enabled == null) || Boolean.parseBoolean(enabled);
//        }
//
//        //System.out.println("Modified Health endpoints configuration: " +"prop[" + HEALTH_CHECK_ENDPOINTS_ENABLED + "]=" + enabled + ", " +"result=" + result);
//
//        // Only re-register if the state has changed
//        if (result != endpointsCurrentlyEnabled) {
//            publicWabConfigMgr.manageEndpoints(componentContext, result);
//            endpointsCurrentlyEnabled = result;
//        } else {
//            System.out.println("Endpoints already in desired state (" + result + "), skipping re-registration");
//        }
//    }
//
//    private boolean areHealthCheckEndpointsEnabled(Map<String, Object> properties) {
//        String enableEndpoints = parseServerConfig(properties);
//        String envEnabled = System.getenv(ENV_HEALTH_CHECK_ENDPOINTS_ENABLED);
//        String enabled = (enableEndpoints != null) ? enableEndpoints : envEnabled;
//
//        // If file health check is NOT enabled
//        if (healthCheckService != null && !healthCheckService.isFileHealthCheckingEnabled()) {
//            if (enabled != null) {
//                System.out.println("WARNING: Health endpoint enable/disable configuration is set, but file health check is not enabled. Endpoints will always be registered.");
//            }
//            return true;
//        }
//
//        // If file health check IS enabled, use the configuration
//        boolean result = (enabled == null) || Boolean.parseBoolean(enabled);
//
//        //System.out.println("Health endpoints configuration: " +"prop[" + HEALTH_CHECK_ENDPOINTS_ENABLED + "]=" + enabled + ", " + "result=" + result);
//        return result;
//    }
//
//    private String parseServerConfig(Map<String, Object> properties) {
//        //System.out.println("PROPERTIES: " + properties);
//        return (String) properties.get(HEALTH_CHECK_ENDPOINTS_ENABLED);
//    }
//
//    final static class WABConfigManager {
//        private final String contextPath;
//        private final String contextName;
//        ServiceRegistration<WABConfiguration> wabConfigReg;
//
//        public WABConfigManager(String contextName, String contextPath) {
//            this.contextName = contextName;
//            this.contextPath = contextPath;
//        }
//
//        public final void deactivate() {
//            System.out.println("In WAB Deactivate!! wabConfigReg=" + wabConfigReg);
//            if (wabConfigReg != null) {
//                try {
//                    wabConfigReg.unregister();
//                    deactivateEvent();
//                } catch (IllegalStateException e) {
//                    // BundleContext is no longer valid, service already unregistered
//                    System.out.println("BundleContext no longer valid during unregister, service already cleaned up");
//                } finally {
//                    wabConfigReg = null;
//                }
//            }
//        }
//
//        public void manageEndpoints(ComponentContext context, boolean enabled) {
//            if (enabled) {
//                System.out.println("Registering MpHealth Context root : " + enabled + " WAB config=" + wabConfigReg);
//                // Push WAB configuration to the WAB installer to start the WAB
//                pushConfiguration(context, contextPath);
//            } else {
//                System.out.println("De-Registering MpHealth Context root : " + enabled + " WAB config=" + wabConfigReg);
//                // Instead of unregistering, just mark it as disabled via properties
//                disableEndpoints();
//                //deactivate();
//            }
//        }
//
//        private void disableEndpoints() {
//            if (wabConfigReg != null) {
//                try {
//                    // Set a property to indicate disabled state without unregistering
//                    final Dictionary<String, String> props = new Hashtable<String, String>();
//                    props.put(WABConfiguration.CONTEXT_NAME, contextName);
//                    props.put(WABConfiguration.CONTEXT_PATH, ""); // Empty path disables
//                    wabConfigReg.setProperties(props);
//                    System.out.println("Disabled endpoints via properties update");
//                } catch (IllegalStateException e) {
//                    // Context invalid
//                }
//            }
//        }
//
//        private final void pushConfiguration(ComponentContext context, String path) {
//            final BundleContext bundleContext = context.getBundleContext();
//            final Dictionary<String, String> props = new Hashtable<String, String>();
//            props.put(WABConfiguration.CONTEXT_NAME, contextName);
//            props.put(WABConfiguration.CONTEXT_PATH, path);
//            if (wabConfigReg == null) {
//                wabConfigReg = bundleContext.registerService(WABConfiguration.class, new WABConfiguration() {
//                }, props);
//                registerEvent(props);
//            } else {
//                wabConfigReg.setProperties(props);
//                modifiedEvent(props);
//            }
//        }
//
//        public void registerEvent(Dictionary<String, String> props) {
//            System.out.println("Registered web app bundle: " + toString() + " props=" + props + " }");
//        }
//
//        public void modifiedEvent(Dictionary<String, String> props) {
//            System.out.println("Modified web app bundle: " + toString() + " props= " + props + " }");
//        }
//
//        public void deactivateEvent() {
//            System.out.println("Unregistered web app bundle: " + toString());
//        }
//
//        @Override
//        public String toString() {
//            return " WABConfigManager [contextPath=" + contextPath + ", contextName=" + contextName + ", wabConfigReg="
//                   + wabConfigReg + "]";
//        }
//    }
//
//}
