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

package com.sun.enterprise.connectors.system;

import java.util.logging.Logger;
import com.sun.logging.LogDomains;
import com.sun.enterprise.util.i18n.StringManager;


/** 
 * Represents one of the MQ address list elements.
 *
 * @author Binod P.G
 */
public class MQUrl {

    static Logger logger = LogDomains.getLogger(LogDomains.RSR_LOGGER);
    private String host = null;
    private String port = null;
    private String scheme = "mq";
    private String service = "";
    private String id = null;

    /**
     * Constructs the MQUrl with the id. Id is actually 
     * the name of JmsHost element in the domain.xml.
     *
     * @param id Logical name of the MQUrl
     */
    public MQUrl(String id) {
        this.id = id;
    }

    /**
     * Sets the host name of the Url.
     *
     * @param host Host Name of the Url.
     */
    public void setHost(String host) {
        this.host = host;
    }

    /**
     * Sets the port number of the Url.
     *
     * @param port Port number of the Url.
     */
    public void setPort(String port) {
        this.port = port;
    }

    /**
     * Sets the Scheme of MQ connection for this Url.
     * Eg> mq, mtcp, mqssl ...
     *
     * @param scheme scheme of the connection.
     */
    public void setScheme(String scheme) {
        this.scheme = scheme;
    }

    /**
     * Sets the type of service offered by MQ broker.
     * Eg> jms, jmsssl etc.
     *
     * @param service Name of service.
     */
    public void setService(String service) {
        this.service = service;
    }

    /**
     * String representation of the Url.
     * i.e> scheme://host:port/service
     * Eg> mq://javasoft12:7676/jmsssl
     *
     * @returns String representation of Url.
     */
    public String toString() {
        if ( host.equals("")) {
            return "";
        }

        if ( port.equals("") && service.equals("")) {
           return scheme + "://" + host;
        }

        if (service.equals("")) {
           return scheme + "://" + host + ":" + port + "/";
        }

        return scheme + "://" + host + ":" + port + "/" + service;
    }

    /**
     * Two MQUrls are identified by their id (name).
     *
     * @param obj another MQUrl object.
     * @returns a boolean indicating whether MQUrls are equal.
     */
    public boolean equals(Object obj) {
        if (obj instanceof MQUrl) {
            return this.id.equals(((MQUrl)obj).id); 
        } else {
            return false;
        }
    }

    /**
     * Hashcode of MQurl is the same as the Hashcode of its name.
     *
     * @return  hashcode of MQUrl
     */
    public int hashCode() {
        return id.hashCode();
    }
}