/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 * 
 * Copyright 1997-2007 Sun Microsystems, Inc. All rights reserved.
 * 
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common Development
 * and Distribution License("CDDL") (collectively, the "License").  You
 * may not use this file except in compliance with the License. You can obtain
 * a copy of the License at https://glassfish.dev.java.net/public/CDDL+GPL.html
 * or glassfish/bootstrap/legal/LICENSE.txt.  See the License for the specific
 * language governing permissions and limitations under the License.
 * 
 * When distributing the software, include this License Header Notice in each
 * file and include the License file at glassfish/bootstrap/legal/LICENSE.txt.
 * Sun designates this particular file as subject to the "Classpath" exception
 * as provided by Sun in the GPL Version 2 section of the License file that
 * accompanied this code.  If applicable, add the following below the License
 * Header, with the fields enclosed by brackets [] replaced by your own
 * identifying information: "Portions Copyrighted [year]
 * [name of copyright owner]"
 * 
 * Contributor(s):
 * 
 * If you wish your version of this file to be governed by only the CDDL or
 * only the GPL Version 2, indicate your decision by adding "[Contributor]
 * elects to include this software in this distribution under the [CDDL or GPL
 * Version 2] license."  If you don't indicate a single choice of license, a
 * recipient has the option to distribute your version of this file under
 * either the CDDL, the GPL Version 2 or to extend the choice of license to
 * its licensees as provided above.  However, if you add GPL Version 2 code
 * and therefore, elected the GPL Version 2 license, then the option applies
 * only if the new code is made subject to such option by the copyright
 * holder.
 */

package com.sun.enterprise.resource;

import com.sun.enterprise.ManagementObjectManager;

import com.sun.enterprise.server.ResourceDeployer;
import com.sun.enterprise.connectors.util.ResourcesUtil;
import com.sun.enterprise.config.serverbeans.Resources;
import com.sun.enterprise.config.serverbeans.ElementProperty;
import com.sun.enterprise.config.serverbeans.SecurityMap;
import com.sun.enterprise.connectors.ConnectorRuntime;
import com.sun.enterprise.connectors.ConnectorConnectionPool;
import com.sun.enterprise.deployment.ConnectionDefDescriptor;
import com.sun.logging.LogDomains;
import java.util.logging.*;

import java.util.Set;
import java.util.HashSet;
import java.util.Iterator;
import java.util.regex.*;

import com.sun.enterprise.connectors.ConnectorRuntimeException;
import com.sun.enterprise.connectors.ConnectorRegistry;
import com.sun.enterprise.deployment.ConnectorDescriptor;
import com.sun.enterprise.connectors.ConnectorDescriptorInfo;
import com.sun.enterprise.deployment.EnvironmentProperty;
import com.sun.enterprise.repository.IASJ2EEResourceFactoryImpl;
import com.sun.enterprise.util.i18n.StringManager;
import com.sun.enterprise.PoolManager;
import com.sun.enterprise.connectors.ConnectorConstants;
import com.sun.enterprise.connectors.util.ConnectionPoolObjectsUtils;
import com.sun.enterprise.connectors.util.SecurityMapUtils;                                           

/**
 *
 * @author    Srikanth P, Sivakumar Thyagarajan
 * @version
 */

public class ConnectorConnectionPoolDeployer extends GlobalResourceDeployer
        implements ResourceDeployer {

    private static final String QUEUE_CF = "javax.jms.QueueConnectionFactory";
    private static final String TOPIC_CF = "javax.jms.TopicConnectionFactory";
    private static final String UNIVERSAL_CF = "javax.jms.ConnectionFactory";
    
    static Logger _logger = LogDomains.getLogger(LogDomains.CORE_LOGGER);

    private static StringManager localStrings = 
        StringManager.getManager( ConnectorConnectionPoolDeployer.class);
    /**
     * ResourceManager callback to indicate resource-deployment
     * Since 8.1 PE/SE/EE, this is a no-op
     *
     * @param resource The resource to be undeployed.
     * @throws Exception if there is an error undeploying the resource.
     */
    public synchronized void deployResource(Object resource) throws Exception {
        _logger.fine("ConnectorConnectionPoolDeployer : deployResource ");

        final com.sun.enterprise.config.serverbeans.ConnectorConnectionPool 
        domainCcp = 
        (com.sun.enterprise.config.serverbeans.ConnectorConnectionPool)resource;

        // If the user is trying to modify the default pool, 
        // redirect call to redeployResource
        if (ConnectionPoolObjectsUtils.isPoolSystemPool(domainCcp)){
        	this.redeployResource(resource);
        	return;
        }
        	
       
        final ConnectorConnectionPool ccp = 
            getConnectorConnectionPool(domainCcp);
        final String defName = domainCcp.getConnectionDefinitionName();
        final ConnectorRuntime crt = ConnectorRuntime.getRuntime();
        
        if (domainCcp.isEnabled()) {
            if (UNIVERSAL_CF.equals(defName) || QUEUE_CF.equals(defName) || TOPIC_CF.equals(defName)) {
            //registers the jsr77 object for the mail resource deployed
            final ManagementObjectManager mgr = 
                getAppServerSwitchObject().getManagementObjectManager();
            mgr.registerJMSResource(domainCcp.getName(), defName, null, null, 
                    getPropNamesAsStrArr(domainCcp.getElementProperty()), 
                    getPropValuesAsStrArr(domainCcp.getElementProperty()));
            }
            
        } else {
                _logger.log(Level.INFO, "core.resource_disabled",
                        new Object[] {domainCcp.getName(),
                        IASJ2EEResourceFactoryImpl.CONNECTOR_CONN_POOL_TYPE});
        }

        _logger.log(Level.FINE,
                   "Calling backend to add connectorConnectionPool",
                   domainCcp.getResourceAdapterName());
        crt.createConnectorConnectionPool(ccp,
            defName, domainCcp.getResourceAdapterName(),
            domainCcp.getElementProperty(),
            domainCcp.getSecurityMap());
       _logger.log(Level.FINE,
                   "Added connectorConnectionPool in backend",
                   domainCcp.getResourceAdapterName());

    }
    
    /**
     * Undeploys the connector connection pool resource. 
     *
     * @param resource The resource to be undeployed.
     * @throws Exception if there is an error undeploying the resource.
     */
    public synchronized void undeployResource(Object resource) 
    throws Exception {
        _logger.fine("ConnectorConnectionPoolDeployer : undeployResource : " );
        final com.sun.enterprise.config.serverbeans.ConnectorConnectionPool 
        domainCcp = 
        (com.sun.enterprise.config.serverbeans.ConnectorConnectionPool)resource;
        final String poolName = domainCcp.getName();
        final ConnectorRuntime crt = ConnectorRuntime.getRuntime();
        final String defName = domainCcp.getConnectionDefinitionName();
        
        _logger.log(Level.FINE,
                 "Calling backend to delete ConnectorConnectionPool",poolName);
        crt.deleteConnectorConnectionPool(poolName);
        _logger.log(Level.FINE,
                   "Deleted ConnectorConnectionPool in backend",poolName);
        
        //unregister the managed object
        if (QUEUE_CF.equals(defName) || TOPIC_CF.equals(defName)) {
            //registers the jsr77 object for the mail resource deployed
            final ManagementObjectManager mgr = 
                getAppServerSwitchObject().getManagementObjectManager();
            mgr.unregisterJMSResource(domainCcp.getName());
        }
    }

    public synchronized void redeployResource(Object resource) 
               throws Exception {
        //Connector connection pool reconfiguration or
        //change in security maps 
        com.sun.enterprise.config.serverbeans.ConnectorConnectionPool 
        domainCcp = 
        (com.sun.enterprise.config.serverbeans.ConnectorConnectionPool)resource;
        SecurityMap[] securityMaps = domainCcp.getSecurityMap();      
        String poolName = domainCcp.getName();
        ConnectorRuntime crt = ConnectorRuntime.getRuntime();
        
        //Since 8.1 PE/SE/EE, only if pool has already been deployed in this 
        //server-instance earlier, reconfig this pool
        if (!crt.isConnectorConnectionPoolDeployed(poolName)) {
            _logger.fine("The connector connection pool " + poolName
                            + " is either not referred or not yet created in "
                            + "this server instance and pool and hence "
                            + "redeployment is ignored");
            return;
        }
        

        String rarName = domainCcp.getResourceAdapterName();
        String connDefName = domainCcp.getConnectionDefinitionName();
        ElementProperty[] props = domainCcp.getElementProperty();
        ConnectorConnectionPool ccp = getConnectorConnectionPool(domainCcp);
        populateConnectorConnectionPool( ccp, connDefName, rarName, props, 
	        securityMaps);
	
        boolean poolRecreateRequired = false;
        try {	
            _logger.fine("Calling reconfigure pool");
                poolRecreateRequired = crt.reconfigureConnectorConnectionPool( ccp, 
                new HashSet());  
        } catch (ConnectorRuntimeException cre ) {
            cre.printStackTrace();
        }
        
        if (poolRecreateRequired){
            _logger.fine("Pool recreation required");    
            crt.recreateConnectorConnectionPool( ccp );
            _logger.fine("Pool recreation done");    
        }
    }

    public synchronized void disableResource(Object resource) 
               throws Exception {
    }

    public synchronized void enableResource(Object resource) 
               throws Exception {
    }

    public Object getResource(String name, Resources rbeans) 
               throws Exception {

        Object res = rbeans.getConnectorConnectionPoolByName(name);
        if (res == null) {
            Exception ex = new Exception("No such resource");
            _logger.log(Level.SEVERE,"no_resource",name);
            _logger.log(Level.SEVERE,"",ex);
            throw ex;
        }
        return res;
    }

    private ConnectorConnectionPool getConnectorConnectionPool(
            com.sun.enterprise.config.serverbeans.ConnectorConnectionPool
                    domainCcp) throws Exception {
        ConnectorConnectionPool ccp =
                new ConnectorConnectionPool(domainCcp.getName());
        ccp.setSteadyPoolSize(domainCcp.getSteadyPoolSize());
        ccp.setMaxPoolSize(domainCcp.getMaxPoolSize());
        ccp.setMaxWaitTimeInMillis(domainCcp.getMaxWaitTimeInMillis());
        ccp.setPoolResizeQuantity(domainCcp.getPoolResizeQuantity());
        ccp.setIdleTimeoutInSeconds(domainCcp.getIdleTimeoutInSeconds());
        ccp.setFailAllConnections(domainCcp.isFailAllConnections());
        ccp.setAuthCredentialsDefinedInPool(
                isAuthCredentialsDefinedInPool(domainCcp));
        //The line below will change for 9.0. We will get this from
        //the domain.xml
        ccp.setConnectionValidationRequired(domainCcp.isIsConnectionValidationRequired());

        String txSupport = domainCcp.getTransactionSupport();
        int txSupportIntVal = parseTransactionSupportString(txSupport);

        if (txSupportIntVal == -1) {
            //if transaction-support attribute is null load the value
            //from the ra.xml
            if (_logger.isLoggable(Level.FINE)) {
                _logger.fine("Got transaction-support attr null from domain.xml");
            }
            txSupportIntVal = ConnectionPoolObjectsUtils.getTransactionSupportFromRaXml(
                    domainCcp.getResourceAdapterName());

        } else {
            //We got some valid transaction-support attribute value
            //so go figure if it is valid.
            //The tx support is valid if it is less-than/equal-to
            //the value specified in the ra.xml
            if (!isTxSupportConfigurationSane(txSupportIntVal,
                    domainCcp.getResourceAdapterName())) {

                String i18nMsg = localStrings.getString(
                        "ccp_deployer.incorrect_tx_support");
                ConnectorRuntimeException cre = new
                        ConnectorRuntimeException(i18nMsg);

                _logger.log(Level.SEVERE, "rardeployment.incorrect_tx_support",
                        ccp.getName());
                throw cre;
            }
        }
        if (_logger.isLoggable(Level.FINE)) {
            _logger.fine("setting txSupportVal to " + txSupportIntVal +
                    " in pool " + domainCcp.getName());
        }
        ccp.setTransactionSupport(txSupportIntVal);

        //Always for ccp	
        ccp.setNonComponent(false);
        ccp.setNonTransactional(false);
        ccp.setConnectionLeakTracingTimeout(domainCcp.getConnectionLeakTimeoutInSeconds());
        ccp.setConnectionReclaim(domainCcp.isConnectionLeakReclaim());

        ccp.setMatchConnections(domainCcp.isMatchConnections());
        ccp.setAssociateWithThread(domainCcp.isAssociateWithThread());
        
        boolean lazyConnectionEnlistment = domainCcp.isLazyConnectionEnlistment();
        boolean lazyConnectionAssociation = domainCcp.isLazyConnectionAssociation();

        if (lazyConnectionAssociation) {
            if (lazyConnectionEnlistment) {
                ccp.setLazyConnectionAssoc(true);
                ccp.setLazyConnectionEnlist(true);
            } else {
                _logger.log(Level.SEVERE,
                        "conn_pool_obj_utils.lazy_enlist-lazy_assoc-invalid-combination",
                        domainCcp.getName());
                String i18nMsg = localStrings.getString(
                        "cpou.lazy_enlist-lazy_assoc-invalid-combination",  domainCcp.getName());
                throw new RuntimeException(i18nMsg);
            }
        } else {
            ccp.setLazyConnectionAssoc(lazyConnectionAssociation);
            ccp.setLazyConnectionEnlist(lazyConnectionEnlistment);
        }

        ccp.setMaxConnectionUsage(domainCcp.getMaxConnectionUsageCount());
        ccp.setValidateAtmostOncePeriod(
                domainCcp.getValidateAtmostOncePeriodInSeconds());

        ccp.setConCreationRetryAttempts(
                domainCcp.getConnectionCreationRetryAttempts());
        ccp.setConCreationRetryInterval(
                domainCcp.getConnectionCreationRetryIntervalInSeconds());

        //IMPORTANT
        //Here all properties that will be checked by the
        //convertElementPropertyToPoolProperty method need to be set to
        //their default values
        convertElementPropertyToPoolProperty(ccp, domainCcp);
        return ccp;
    }
    
    private void populateConnectorConnectionPool( ConnectorConnectionPool ccp,
        String connectionDefinitionName, String rarName, 
	ElementProperty[] props, SecurityMap[] securityMaps) 
	throws ConnectorRuntimeException {
    
	ConnectorRegistry _registry = ConnectorRegistry.getInstance();
        ConnectorDescriptor connectorDescriptor = _registry.getDescriptor(rarName);
        if (connectorDescriptor == null) {
            ConnectorRuntimeException cre = new ConnectorRuntimeException(
                            "Failed to get connection pool object");
            _logger.log(Level.SEVERE,
                 "rardeployment.connector_descriptor_notfound_registry",rarName);
            _logger.log(Level.SEVERE,"",cre);
            throw cre; 
        }
        Set connectionDefs =  
             connectorDescriptor.getOutboundResourceAdapter().getConnectionDefs();
        ConnectionDefDescriptor cdd = null;
        Iterator it = connectionDefs.iterator();
        while(it.hasNext()) {
          cdd = (ConnectionDefDescriptor)it.next();
          if(connectionDefinitionName.equals(cdd.getConnectionFactoryIntf()))
              break;

        }
        ConnectorDescriptorInfo cdi = new ConnectorDescriptorInfo();

        cdi.setRarName(rarName);
        cdi.setResourceAdapterClassName(
                    connectorDescriptor.getResourceAdapterClass());
        cdi.setConnectionDefinitionName(cdd.getConnectionFactoryIntf());
        cdi.setManagedConnectionFactoryClass(
                    cdd.getManagedConnectionFactoryImpl());
        cdi.setConnectionFactoryClass(cdd.getConnectionFactoryImpl());
        cdi.setConnectionFactoryInterface(cdd.getConnectionFactoryIntf());
        cdi.setConnectionClass(cdd.getConnectionImpl());
        cdi.setConnectionInterface(cdd.getConnectionIntf());
        Set mergedProps = mergeProps(props, cdd.getConfigProperties());
        cdi.setMCFConfigProperties(mergedProps);
        cdi.setResourceAdapterConfigProperties(
                    connectorDescriptor.getConfigProperties());
        ccp.setConnectorDescriptorInfo( cdi );
        ccp.setSecurityMaps(SecurityMapUtils.getConnectorSecurityMaps(securityMaps));
             
    }

    private Set mergeProps(ElementProperty[] props, Set defaultMCFProps) {
        HashSet mergedSet = new HashSet();

        Object[] defaultProps = ( defaultMCFProps == null ) ? 
	    new Object[0] :
	    defaultMCFProps.toArray();

        for (int i =0; i< defaultProps.length; i++) {
	     mergedSet.add(defaultProps[i]);
        }

        for (int i =0; i< props.length; i++) {
	     if ( props[i] != null ) {
	         EnvironmentProperty ep = new EnvironmentProperty(
	            		props[i].getName(),props[i].getValue(),null);
	         if (defaultMCFProps.contains(ep)) {
	             mergedSet.remove(ep);
	         }
	         mergedSet.add(ep);
	     }
        }

        return mergedSet;
    }

    private boolean isTxSupportConfigurationSane(int txSupport, String raName) {
        int raXmlTxSupport = ConnectorConstants.UNDEFINED_TRANSACTION_INT; 
	
	try {
            raXmlTxSupport = ConnectionPoolObjectsUtils.getTransactionSupportFromRaXml( raName ) ;
        } catch (Exception e) {
	    _logger.log(Level.WARNING, 
	        (e.getMessage() != null ? e.getMessage() : "  " ));
	}
        if (_logger.isLoggable(Level.FINE) ) {
            _logger.log(Level.FINE,"isTxSupportConfigSane:: txSupport => " 
	        + txSupport + "  raXmlTxSupport => " + raXmlTxSupport);
	}

        return (txSupport <= raXmlTxSupport);
       
    }

    
    private int parseTransactionSupportString( String txSupport ) {
        return ConnectionPoolObjectsUtils.parseTransactionSupportString( txSupport );
    }

    /**
     * The idea is to convert the ElementProperty values coming from the admin
     * connection pool to standard pool attributes thereby making it
     * easy in case of a reconfig
     */
    public void convertElementPropertyToPoolProperty(ConnectorConnectionPool ccp,
                                                     com.sun.enterprise.config.serverbeans.ConnectorConnectionPool domainCcp) {
        ElementProperty[] elemProps = domainCcp.getElementProperty();
        if (elemProps == null) {
            return;
        }
        for (ElementProperty ep : elemProps) {
            if (ep != null) {
                if ("MATCHCONNECTIONS".equals(ep.getName().toUpperCase())) {
                    //the foreach loop seems to handle change in the underlying datastructure.
                    domainCcp.removeElementProperty(ep);
                    if (_logger.isLoggable(Level.FINE)) {
                        _logger.fine(" ConnectorConnectionPoolDeployer::  Setting matchConnections");
                    }
                    ccp.setMatchConnections(toBoolean(ep.getValue(), true));
                } else if ("LAZYCONNECTIONASSOCIATION".equals(ep.getName().toUpperCase())) {
                    ConnectionPoolObjectsUtils.setLazyEnlistAndLazyAssocProperties(ep.getValue(), domainCcp, ccp);
                    boolean assoc = toBoolean(ep.getValue(), false);
                    _logger.log(Level.FINE, "ccp_deployer.lazy_con_assoc_value",
                            new Object[]{ccp.getName(), String.valueOf(assoc)});
                } else if ("LAZYCONNECTIONENLISTMENT".equals(ep.getName().toUpperCase())) {
                    boolean enlist = toBoolean(ep.getValue(), false);
                    ccp.setLazyConnectionEnlist(enlist);
                    _logger.log(Level.FINE, "ccp_deployer.lazy_con_enlist_value",
                            new Object[]{ccp.getName(), String.valueOf(enlist)});
                }
            }
        }
    }

    private boolean toBoolean(Object prop, boolean defaultVal) {
        if (prop == null) {
            return defaultVal;
        }

        return Boolean.valueOf((String) prop);
    }

    private boolean isAuthCredentialsDefinedInPool(
            com.sun.enterprise.config.serverbeans.ConnectorConnectionPool domainCcp) {
        ElementProperty[] elemProps = domainCcp.getElementProperty();
        if ( elemProps == null ) {
            return false;
        }

        for( int i =0; i < elemProps.length; i++ ) {
            ElementProperty ep = elemProps[i];
            if (ep.getName().equalsIgnoreCase("UserName") ||
                ep.getName().equalsIgnoreCase("User") ||
                ep.getName().equalsIgnoreCase("Password")) {
                return true;
            }
        }
        return false;
    }
}