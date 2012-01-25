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
 
/*
 * $Header: /cvs/glassfish/admin/mbeanapi-impl/tests/com/sun/enterprise/management/ext/wsmgmt/WebServiceConfigTest.java,v 1.6 2006/03/09 20:30:57 llc Exp $
 * $Revision: 1.6 $
 * $Date: 2006/03/09 20:30:57 $
 */
package com.sun.enterprise.management.ext.wsmgmt;

import java.util.Set;
import java.util.Iterator;
import java.io.IOException;
import java.util.Map;
import java.util.List;
import java.util.HashMap;


import javax.management.ObjectName;
import javax.management.AttributeList;
import javax.management.MBeanServerConnection;
import javax.management.NotCompliantMBeanException;

import com.sun.appserv.management.j2ee.J2EEDomain;


import com.sun.appserv.management.util.jmx.MBeanServerConnectionConnectionSource;

import com.sun.enterprise.management.support.AMXNonConfigImplBase;
import com.sun.enterprise.management.support.QueryMgrImpl;
import com.sun.appserv.management.ext.wsmgmt.WebServiceMgr;
import com.sun.appserv.management.util.misc.ExceptionUtil;

import com.sun.appserv.management.config.WebServiceEndpointConfig;
import com.sun.appserv.management.config.TransformationRuleConfig;
import com.sun.appserv.management.base.XTypes;
import com.sun.appserv.management.base.Util;

import com.sun.enterprise.management.AMXTestBase;
import com.sun.enterprise.management.Capabilities;

/**
 */
public final class WebServiceConfigTest extends AMXTestBase
{

    public  WebServiceConfigTest() throws IOException {
    }
    
	    public static Capabilities
	getCapabilities()
	{
	    return getOfflineCapableCapabilities( false );
	}
	
    public void testConfigMBeans() {
        assert (getDomainRoot().getWebServiceMgr() != null);

       final Set<WebServiceEndpointConfig>   s   =
        getDomainRoot().getQueryMgr().queryJ2EETypeSet(
                              XTypes.WEB_SERVICE_ENDPOINT_CONFIG);

       for( final WebServiceEndpointConfig wsc : s )
       {
            String oldSize = wsc.getMaxHistorySize();
            System.out.println("Old Max History size is " + oldSize);
            System.out.println("Setting Max History size to 1 " );
            wsc.setMaxHistorySize("1");
            System.out.println("New Max History size is  " 
                + wsc.getMaxHistorySize());
            assert( "1".equals(wsc.getMaxHistorySize()));
            System.out.println("Resetting Max History size to " + oldSize );
            wsc.setMaxHistorySize(oldSize);
            System.out.println("Config value is " + wsc.getMonitoringLevel());

            Map m = wsc.getTransformationRuleConfigMap();

            System.out.println("Transformation rules found " + m.size());

           Iterator itr = m.values().iterator();
           while  ( itr.hasNext()) {
                TransformationRuleConfig tc = (TransformationRuleConfig)
                itr.next();
                System.out.println("rule name is " + tc.getName());
           }
           System.out.println("Getting tranformation rules in order ");
            List l = wsc.getTransformationRuleConfigList();

            System.out.println("Transformation rules found " + l.size());

           Iterator litr = l.iterator();
           while  ( litr.hasNext()) {
                TransformationRuleConfig tc = (TransformationRuleConfig)
                litr.next();
                System.out.println("rule name is " + tc.getName());
           }
       }
       assert(true);
    }    
}

