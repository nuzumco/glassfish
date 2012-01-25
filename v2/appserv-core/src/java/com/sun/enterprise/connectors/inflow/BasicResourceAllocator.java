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
package com.sun.enterprise.connectors.inflow;

import com.sun.enterprise.resource.PoolingException;
import com.sun.enterprise.resource.AbstractConnectorAllocator;
import com.sun.enterprise.resource.ResourceAllocator;
import com.sun.enterprise.resource.ResourceHandle;
import com.sun.enterprise.resource.ResourceSpec;
import com.sun.enterprise.resource.XAResourceWrapper;
import com.sun.logging.LogDomains;

import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.resource.ResourceException;
import javax.transaction.xa.XAResource;

public final class BasicResourceAllocator extends AbstractConnectorAllocator {

    private static final Logger logger = 
    LogDomains.getLogger(LogDomains.RSR_LOGGER);

    private static final String JMS_RESOURCE_FACTORY = "JMS";

    public BasicResourceAllocator () {}

    public ResourceHandle createResource()
        throws PoolingException {
        throw new UnsupportedOperationException();
    }

    public ResourceHandle createResource(XAResource xaResource)
         throws PoolingException {

        ResourceHandle resourceHandle = null;
        ResourceSpec spec =
                new ResourceSpec(JMS_RESOURCE_FACTORY,
                                 ResourceSpec.JMS);
        
        if (xaResource != null) {
            
            logger.logp(Level.FINEST, 
                    "BasicResourceAllocator", "createResource",
                    "NOT NULL", xaResource);
            
            try {
                resourceHandle = new ResourceHandle(
                        null,  //no object present
		        spec,
                        this, null);
                
                if (logger.isLoggable(Level.FINEST)) {
                    xaResource = new XAResourceWrapper(xaResource);
                }
                    
                resourceHandle.fillInResourceObjects(null, xaResource);

            } catch (Exception e) {
                throw (PoolingException) (new PoolingException()).initCause(e);
            }
        } else {
            logger.logp(Level.FINEST, 
                    "BasicResourceAllocator", "createResource",
                    "NULL", xaResource);
        }

        return resourceHandle;
    }
    

    public void closeUserConnection(ResourceHandle resourceHandle) 
        throws PoolingException {
        throw new UnsupportedOperationException();
    }


    public boolean matchConnection(ResourceHandle resourceHandle) {
        return false;
    }

    public boolean supportsReauthentication() {
        return false;
    }

    public void cleanup(ResourceHandle resourceHandle) 
        throws PoolingException {
        throw new UnsupportedOperationException();
    }

    public Set getInvalidConnections(Set connectionSet) throws ResourceException {
        throw new UnsupportedOperationException();
    }

    public boolean isConnectionValid( ResourceHandle resource ) {
        throw new UnsupportedOperationException();
    }

}