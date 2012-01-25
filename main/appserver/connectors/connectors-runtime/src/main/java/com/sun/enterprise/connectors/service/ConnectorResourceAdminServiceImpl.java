/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 1997-2011 Oracle and/or its affiliates. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common Development
 * and Distribution License("CDDL") (collectively, the "License").  You
 * may not use this file except in compliance with the License.  You can
 * obtain a copy of the License at
 * https://glassfish.dev.java.net/public/CDDL+GPL_1_1.html
 * or packager/legal/LICENSE.txt.  See the License for the specific
 * language governing permissions and limitations under the License.
 *
 * When distributing the software, include this License Header Notice in each
 * file and include the License file at packager/legal/LICENSE.txt.
 *
 * GPL Classpath Exception:
 * Oracle designates this particular file as subject to the "Classpath"
 * exception as provided by Oracle in the GPL Version 2 section of the License
 * file that accompanied this code.
 *
 * Modifications:
 * If applicable, add the following below the License Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyright [year] [name of copyright owner]"
 *
 * Contributor(s):
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

package com.sun.enterprise.connectors.service;

import com.sun.appserv.connectors.internal.api.*;
import org.glassfish.connectors.config.JdbcResource;
import com.sun.enterprise.connectors.ConnectorConnectionPool;
import com.sun.enterprise.connectors.ConnectorDescriptorInfo;
import com.sun.enterprise.connectors.ConnectorRuntime;
import com.sun.enterprise.connectors.naming.ConnectorResourceNamingEventNotifier;
import com.sun.appserv.connectors.internal.spi.ConnectorNamingEvent;
import com.sun.enterprise.connectors.naming.ConnectorNamingEventNotifier;
import com.sun.enterprise.connectors.util.ResourcesUtil;
import org.glassfish.resources.api.PoolInfo;
import org.glassfish.resources.api.ResourceInfo;
import org.glassfish.resources.naming.*;

import javax.naming.*;
import javax.sql.DataSource;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.Hashtable;
import java.util.logging.Logger;
import java.util.logging.Level;

/**
 * This is connector resource admin service. It creates and deletes the
 * connector resources.
 *
 * @author Srikanth P
 */
public class ConnectorResourceAdminServiceImpl extends ConnectorService {

    private ResourceNamingService namingService = _runtime.getResourceNamingService();
    /**
     * Default constructor
     */
    public ConnectorResourceAdminServiceImpl() {
        super();
    }

    /**
     * Creates the connector resource on a given connection pool
     *
     * @param jndiName     JNDI name of the resource to be created
     * @param poolInfo     PoolName to which the connector resource belongs.
     * @param resourceType Resource type Unused.
     * @throws ConnectorRuntimeException If the resouce creation fails.
     */
    public void createConnectorResource(ResourceInfo resourceInfo, PoolInfo poolInfo,
                                        String resourceType) throws ConnectorRuntimeException {

        try {
            ConnectorConnectionPool ccp = null;
            String jndiNameForPool = ConnectorAdminServiceUtils.
                    getReservePrefixedJNDINameForPool(poolInfo);
            try {
                ccp = (ConnectorConnectionPool) namingService.lookup(poolInfo, jndiNameForPool);
            } catch (NamingException ne) {
                //Probably the pool is not yet initialized (lazy-loading), try doing a lookup
                try {
                    checkAndLoadPool(poolInfo);
                    ccp = (ConnectorConnectionPool) namingService.lookup(poolInfo, jndiNameForPool);
                } catch (NamingException e) {
                    Object params[] = new Object[]{poolInfo, e};
                    _logger.log(Level.SEVERE, "unable.to.lookup.pool", params);
                }
            }

            if(ccp == null){
                ccp = (ConnectorConnectionPool) namingService.lookup(poolInfo, jndiNameForPool);
            }
            ConnectorDescriptorInfo cdi = ccp.getConnectorDescriptorInfo();

            javax.naming.Reference ref=new  javax.naming.Reference(
                   cdi.getConnectionFactoryClass(), 
                   "com.sun.enterprise.resource.naming.ConnectorObjectFactory",
                   null);
            RefAddr addr = new SerializableObjectRefAddr(PoolInfo.class.getName(), poolInfo);
            ref.add(addr);
            addr = new StringRefAddr("rarName", cdi.getRarName() );
            ref.add(addr);
            RefAddr resAddr = new SerializableObjectRefAddr(ResourceInfo.class.getName(), resourceInfo);
            ref.add(resAddr);

            try{
                namingService.publishObject(resourceInfo, ref, true);
                _registry.addResourceInfo(resourceInfo);
            }catch(NamingException ne){
                ConnectorRuntimeException cre = new ConnectorRuntimeException(ne.getMessage());
                cre.initCause(ne);
                Object params[] = new Object[]{resourceInfo, cre};
                _logger.log(Level.SEVERE, "rardeployment.resource_jndi_bind_failure", params);
                throw cre;
            }

/*

            ConnectorObjectFactory cof = new ConnectorObjectFactory(jndiName, ccp.getConnectorDescriptorInfo().
                    getConnectionFactoryClass(), cdi.getRarName(), poolName);

            _runtime.getNamingManager().publishObject(jndiName, cof, true);
*/

            //To notify that a connector resource rebind has happened.
            ConnectorResourceNamingEventNotifier.getInstance().
                    notifyListeners(new ConnectorNamingEvent(resourceInfo.toString(),
                            ConnectorNamingEvent.EVENT_OBJECT_REBIND));

        } catch (NamingException ne) {
            ConnectorRuntimeException cre = new ConnectorRuntimeException(ne.getMessage());
            cre.initCause(ne);
            Object params[] = new Object[]{resourceInfo, cre};
            _logger.log(Level.SEVERE, "rardeployment.jndi_lookup_failed", params);
            throw cre;
        }
    }

    /**
     * Deletes the connector resource.
     *
     * @param resourceInfo JNDI name of the resource to delete.
     * @throws ConnectorRuntimeException if connector resource deletion fails.
     */
    public void deleteConnectorResource(ResourceInfo resourceInfo)
            throws ConnectorRuntimeException {

        try {
            namingService.unpublishObject(resourceInfo, resourceInfo.getName());
        } catch (NamingException ne) {
            /* TODO for System RAR (not needed as proxy will always be present ?)
            ResourcesUtil resUtil = ResourcesUtil.createInstance();
            if (resUtil.resourceBelongsToSystemRar(jndiName)) {
                return;
            }
            */
            if (ne instanceof NameNotFoundException) {
                if(_logger.isLoggable(Level.FINE)) {
                    _logger.log(Level.FINE, "rardeployment.connectorresource_removal_from_jndi_error", resourceInfo);
                    _logger.log(Level.FINE, "", ne);
                }
                return;
            }
            ConnectorRuntimeException cre = new ConnectorRuntimeException
                    ("Failed to delete connector resource from jndi");
            cre.initCause(ne);
            _logger.log(Level.SEVERE, "rardeployment.connectorresource_removal_from_jndi_error", resourceInfo);
            _logger.log(Level.SEVERE, "", cre);
            throw cre;
        }finally{
            _registry.removeResourceInfo(resourceInfo);
        }
    }

    /**
     * Gets Connector Resource Rebind Event notifier.
     *
     * @return ConnectorNamingEventNotifier
     */
    public ConnectorNamingEventNotifier getResourceRebindEventNotifier() {
        return ConnectorResourceNamingEventNotifier.getInstance();
    }


    /**
     * Look up the JNDI name with appropriate suffix.
     * Suffix can be either __pm or __nontx.
     *
     * @param resourceInfo resource-name
     * @return Object - from jndi
     * @throws NamingException - when unable to get the object form jndi
     */
    public Object lookup(ResourceInfo resourceInfo) throws NamingException {

        Hashtable env = null;
        String jndiName = resourceInfo.getName();
        String suffix = ConnectorsUtil.getValidSuffix(jndiName);

        //To pass suffix that will be used by connector runtime during lookup
        if(suffix != null){
            env = new Hashtable();
            env.put(ConnectorConstants.JNDI_SUFFIX_PROPERTY, suffix);
            jndiName = jndiName.substring(0, jndiName.lastIndexOf(suffix));
        }
        ResourceInfo actualResourceInfo = new ResourceInfo(jndiName, resourceInfo.getApplicationName(),
                resourceInfo.getModuleName());
        return namingService.lookup(actualResourceInfo, actualResourceInfo.getName(), env);
    }

    /**
     * Get a wrapper datasource specified by the jdbcjndi name
     * This API is intended to be used in the DAS. The motivation for having this
     * API is to provide the CMP backend/ JPA-Java2DB a means of acquiring a connection during
     * the codegen phase. If a user is trying to deploy an JPA-Java2DB app on a remote server,
     * without this API, a resource reference has to be present both in the DAS
     * and the server instance. This makes the deployment more complex for the
     * user since a resource needs to be forcibly created in the DAS Too.
     * This API will mitigate this need.
     *
     * @param jndiName the jndi name of the resource
     * @return DataSource representing the resource.
     */
    public Object lookupDataSourceInDAS(ResourceInfo resourceInfo) throws ConnectorRuntimeException{
        MyDataSource myDS = new MyDataSource();
        myDS.setResourceInfo(resourceInfo);
        return myDS;
    }

    class MyDataSource implements DataSource {
        private ResourceInfo resourceInfo;
        private PrintWriter logWriter;
        private int loginTimeout;

        public void setResourceInfo(ResourceInfo resourceInfo) throws ConnectorRuntimeException{
            validateResource(resourceInfo);
            this.resourceInfo = resourceInfo;
        }

        private void validateResource(ResourceInfo resourceInfo) throws ConnectorRuntimeException {
            ResourcesUtil resourcesUtil = ResourcesUtil.createInstance();
            String jndiName = resourceInfo.getName();
            String suffix = ConnectorsUtil.getValidSuffix(jndiName);

            if(suffix != null){
                //Typically, resource is created without suffix. Try without suffix.
                String tmpJndiName = jndiName.substring(0, jndiName.lastIndexOf(suffix));
                if(resourcesUtil.getResource(tmpJndiName, resourceInfo.getApplicationName(),
                        resourceInfo.getModuleName(), JdbcResource.class) != null){
                    return;
                }
            }

            if(resourcesUtil.getResource(resourceInfo, JdbcResource.class) == null){
                throw new ConnectorRuntimeException("Invalid resource : " + resourceInfo);
            }
        }

        public Connection getConnection() throws SQLException {
            return ConnectorRuntime.getRuntime().getConnection(resourceInfo);
        }

        public Connection getConnection(String username, String password) throws SQLException {
            return ConnectorRuntime.getRuntime().getConnection(resourceInfo, username, password);
        }

        public PrintWriter getLogWriter() throws SQLException {
            return logWriter;
        }

        public void setLogWriter(PrintWriter out) throws SQLException {
           this.logWriter = out;
        }

        public void setLoginTimeout(int seconds) throws SQLException {
           loginTimeout = seconds;
        }

        public int getLoginTimeout() throws SQLException {
            return loginTimeout;
        }
        public boolean isWrapperFor(Class<?> iface) throws SQLException{
           throw new SQLException("Not supported operation");
        }
        public <T> T unwrap(Class<T> iface) throws SQLException{
           throw new SQLException("Not supported operation");
        }

        public Logger getParentLogger() throws SQLFeatureNotSupportedException {
            throw new SQLFeatureNotSupportedException("Not supported operation");
        }
    }
}