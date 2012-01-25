/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2010 Sun Microsystems, Inc. All rights reserved.
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

package com.acme;


import javax.ejb.*;
import javax.annotation.*;

import javax.naming.InitialContext;

import javax.management.j2ee.ManagementHome;
import javax.management.j2ee.Management;
import javax.rmi.PortableRemoteObject;

import com.sun.ejte.ccl.reporter.SimpleReporterAdapter;

public class Client {

    private static SimpleReporterAdapter stat = 
        new SimpleReporterAdapter("appserv-tests");

    private static String appName;

    public static void main(String args[]) {

	appName = args[0]; 
	stat.addDescription(appName);
	Client client = new Client(args);       
        client.doTest();	
        stat.printSummary(appName + "ID");
    }

    public Client(String[] args) {}

    public void doTest() {

	try {

	    // Ensure that MEJB is registered under all three of its JNDI names
	    System.out.println("Looking up MEJB Homes");
	    ManagementHome mh1Obj = (ManagementHome) new InitialContext().lookup("ejb/mgmt/MEJB");
	    ManagementHome mh2Obj = (ManagementHome) new InitialContext().lookup("java:global/mejb/MEJBBean");
	    ManagementHome mh3Obj = (ManagementHome) new InitialContext().lookup("java:global/mejb/MEJBBean!javax.management.j2ee.ManagementHome");
        addStatus("mejb relative lookup", (mh1Obj != null));
        addStatus("mejb global lookup", (mh2Obj != null));
        addStatus("mejb global lookup with explicit ManagedHome interface", (mh3Obj != null));

	    Hello hello = (Hello) new InitialContext().lookup("java:global/" + appName + "/SingletonBean");
        String response = hello.hello();
        addStatus("Singleton bean response", response.equals("hello, world!\n"));

	    try {
    		hello.testError();
            addStatus("Expected EJBException from Singleton.testError()", false);
	    	throw new RuntimeException("Expected EJBException");
	    } catch(EJBException e) {
            addStatus("Expected EJBException from Singleton.testError()", true);
	    }

        String injectionStatus = hello.testInjection();
        System.out.println("Injection tests in server response"+ injectionStatus);
        addStatus("Testing Injection in EJB Singleton" , injectionStatus.trim().equals(""));

	} catch(Exception e) {
	    stat.addStatus("local main", stat.DID_NOT_RUN);
	    e.printStackTrace();
	}
    }

    private void addStatus(String message, boolean result){
            stat.addStatus(message, (result ? stat.PASS: stat.FAIL));
    }


}