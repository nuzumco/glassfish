package com.sun.enterprise.tools.verifier.tests.ejb.ias;

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

import com.sun.enterprise.tools.verifier.tests.ejb.EjbTest;
import java.util.*;
import com.sun.enterprise.deployment.EjbDescriptor;
import com.sun.enterprise.deployment.EjbSessionDescriptor;
import com.sun.enterprise.tools.verifier.*;
import com.sun.enterprise.tools.verifier.tests.*;

import com.sun.enterprise.tools.verifier.tests.ejb.EjbCheck;

import com.sun.enterprise.tools.common.dd.*;
import com.sun.enterprise.tools.common.dd.ejb.*;
import com.sun.enterprise.deployment.ResourceReferenceDescriptor;
import com.sun.enterprise.deployment.EjbMessageBeanDescriptor;


/** ejb [0,n]
 *    jms-max-messages-load ? [String]
 *
 * The jms-max-messages-load specifies the maximum number of messages to
 * load into a JMS Session.
 * It is valid only for MDBs
 * The value should be between 1 and MAX_INT
 * @author Irfan Ahmed
 */
public class ASEjbJMSMaxMessagesLoad extends EjbTest implements EjbCheck { 

    public Result check(EjbDescriptor descriptor) 
    {
	Result result = getInitializedResult();
	ComponentNameConstructor compName = new ComponentNameConstructor(descriptor);

        SunEjbJar ejbJar = descriptor.getEjbBundleDescriptor().getIasEjbObject();
        boolean oneFailed = false;
        
        if(ejbJar!=null)
        {
            Ejb ejbs[] = ejbJar.getEnterpriseBeans().getEjb();
            Ejb testCase = null;
            for(int i=0;i<ejbs.length;i++)
            {
                if(ejbs[i].getEjbName().equals(descriptor.getName()))
                {
                    testCase = ejbs[i];
                    break;
                }
            }
            
            String jmsMaxMsgs = testCase.getJmsMaxMessagesLoad();
            if(jmsMaxMsgs != null)
            {
                if(jmsMaxMsgs.length()==0)
                {
                    result.failed(smh.getLocalString(getClass().getName()+".failed",
                        "FAILED [AS-EJB ejb] : jms-max-messages-load cannot be an empty string value"));
                }
                else
                {
                    try
                    {
                        int value = Integer.valueOf(jmsMaxMsgs).intValue();
                        if(value<1 || value>Integer.MAX_VALUE)
                        {
                            result.failed(smh.getLocalString(getClass().getName()+".failed2",
                                "FAILED [AS-EJB ejb] : {0} is not a valid value for jms-max-messages-load. It should be " + '\n' + 
                                "between 0 and MAX_INT", new Object[]{jmsMaxMsgs}));
                        }
                        else
                        {
                            result.passed(smh.getLocalString(getClass().getName()+".passed",
                                "PASSED [AS-EJB ejb] : jms-max-messages-load is {0}", new Object[]{jmsMaxMsgs}));
                        }
                    }
                    catch(NumberFormatException nfex)
                    {
                        Verifier.debug(nfex);
                        result.failed(smh.getLocalString(getClass().getName()+".failed3",
                            "FAILED [AS-EJB ejb] : {0} value is not a number", new Object[]{jmsMaxMsgs}));
                    }
                }
            }
            else
            {
                if(descriptor instanceof EjbMessageBeanDescriptor)
                {
                    //<addition author="irfan@sun.com" [bug/rfe]-id="4724447" >
                    /*Change in message output ms->jms */
                    result.warning(smh.getLocalString(getClass().getName()+".warning",
                        "WARNING [AS-EJB ejb] : jms-max-messages-load should be defined for MDBs"));
                }
                else
                {
                    result.notApplicable(smh.getLocalString(getClass().getName()+".notApplicable",
                        "NOT APPLICABLE [AS-EJB ejb] : jms-max-messages-load element is not defined"));
                }
            }
        }
        else
        {
            result.addErrorDetails(smh.getLocalString
                 (getClass().getName() + ".notRun",
                  "NOT RUN [AS-EJB] : Could not create an SunEjbJar object"));
        }
        return result;
    }
}