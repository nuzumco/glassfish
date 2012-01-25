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

import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.resource.ResourceException;
import javax.resource.spi.ConnectionRequestInfo;
import javax.resource.spi.ManagedConnection;
import javax.resource.spi.ManagedConnectionFactory;
import javax.resource.spi.ValidatingManagedConnectionFactory;
import javax.security.auth.Subject;

import com.sun.enterprise.PoolManager;
import com.sun.enterprise.deployment.ConnectorDescriptor;
import com.sun.logging.LogDomains;


/**
 * An abstract implementation of the <code>ResourceAllocator</code> interface
 * that houses all the common implementation(s) of the various connector allocators.
 * All resource allocators except <code>BasicResourceAllocator</code> extend this 
 * abstract implementation
 * @author Sivakumar Thyagarajan
 */
public abstract class AbstractConnectorAllocator 
                            implements ResourceAllocator {

    protected PoolManager poolMgr;
    protected ResourceSpec spec;
    protected ConnectionRequestInfo reqInfo;
    protected Subject subject;
    protected ManagedConnectionFactory mcf;
    protected ConnectorDescriptor desc;
    protected ClientSecurityInfo info;
    
    protected final static Logger _logger = LogDomains.getLogger(LogDomains.RSR_LOGGER);

    public AbstractConnectorAllocator() {
    }

    public AbstractConnectorAllocator(PoolManager poolMgr,
                    ManagedConnectionFactory mcf,
                    ResourceSpec spec,
                    Subject subject,
                    ConnectionRequestInfo reqInfo,
                    ClientSecurityInfo info,
                    ConnectorDescriptor desc) {
        this.poolMgr = poolMgr;
        this.mcf = mcf;
        this.spec = spec;
        this.subject = subject;
        this.reqInfo = reqInfo;
        this.info = info;
        this.desc = desc;
        
    }

    public Set getInvalidConnections(Set connectionSet)
                                throws ResourceException {
        if(mcf instanceof ValidatingManagedConnectionFactory){
            return ((ValidatingManagedConnectionFactory)this.mcf).
                                    getInvalidConnections(connectionSet);
        }
        return null;
    }

    public boolean isConnectionValid( ResourceHandle h ) 
    {
         HashSet conn = new HashSet();
         conn.add( h.getResource() );
         Set invalids = null;
         try {
             invalids = getInvalidConnections( conn );
         } catch( ResourceException re ) {
             //ignore and continue??
         }
         
	 if ( (invalids != null && invalids.size() > 0)  ||
	         h.hasConnectionErrorOccurred() ) {
	     return false;
	 } 

	 return true;
    }

    public void destroyResource(ResourceHandle resourceHandle)
        throws PoolingException {
        throw new UnsupportedOperationException();
    }

    public void fillInResourceObjects(ResourceHandle resourceHandle)
        throws PoolingException {
        throw new UnsupportedOperationException();
    }

    public boolean supportsReauthentication() {
        return this.desc.supportsReauthentication();
    }

    public boolean isTransactional() {
        return true;
    }

    public void cleanup(ResourceHandle h) throws PoolingException {
        try {
            ManagedConnection mc = (ManagedConnection) h.getResource();
            mc.cleanup();
        } catch (Exception ex) {
            _logger.log(Level.WARNING, "managed_con.cleanup-failed", ex);
            throw new PoolingException(ex.toString(), ex);
        }
    }

    public boolean matchConnection(ResourceHandle h) {
        Set set = new HashSet();
        set.add(h.getResource());
        try {
            ManagedConnection mc = 
                mcf.matchManagedConnections(set, subject, reqInfo);
            return (mc != null);
        } catch (ResourceException ex) {
            return false;
        }
    }

    public void closeUserConnection(ResourceHandle resource) throws PoolingException {
    
        try {
            ManagedConnection mc = (ManagedConnection) resource.getResource();
            mc.cleanup();
        } catch (ResourceException ex) {
            throw new PoolingException(ex);
        }
    }

    public boolean shareableWithinComponent() {
        return false;
    }

    public Object getSharedConnection(ResourceHandle h) 
                                    throws PoolingException {
        throw new UnsupportedOperationException();
    }

}