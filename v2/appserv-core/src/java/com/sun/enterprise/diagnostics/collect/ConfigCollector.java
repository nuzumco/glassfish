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
package com.sun.enterprise.diagnostics.collect;
import com.sun.logging.LogDomains;
import com.sun.enterprise.diagnostics.Data;
import com.sun.enterprise.diagnostics.Constants;
import com.sun.enterprise.diagnostics.ReportTarget;
import com.sun.enterprise.diagnostics.DiagnosticException;
import com.sun.enterprise.diagnostics.util.DiagnosticServiceHelper;
import com.sun.enterprise.diagnostics.util.XmlUtils;
import com.sun.enterprise.diagnostics.util.FileUtils;

import java.io.File;
import java.io.IOException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.parsers.ParserConfigurationException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.xml.sax.SAXException;
import org.w3c.dom.*;



/**
 * Responsible for capturing domain.xml, sun-acc.xml, server.policy, login.conf
 *
 * @author Manisha Umbarje
 */
public class ConfigCollector implements Collector {
    
    private static final String PASSWORD_REPLACEMENT ="****";
    private static final String PASSWORD = "password";
    
    private String repositoryDir ;
    private String reportDir ;
   
    private static Logger logger = 
    LogDomains.getLogger(LogDomains.ADMIN_LOGGER);
    
    /**
     * @param repositoryDir central/local cache repository
     * @param reportDir directory in which config information is collected.
     * @targetType type of the target for which report generation is invoked
     * instance
     */
    public ConfigCollector(String repositoryDir, String reportDir) {
        this.repositoryDir = repositoryDir;
        this.reportDir = reportDir;
      }
    
      
    /**
     * Capture config information
     * @throw DiagnosticException
     */
    public Data capture() throws DiagnosticException {
        WritableDataImpl dataImpl = new WritableDataImpl(DataType.CONFIG_DETAILS);
        
        dataImpl.addChild(captureXMLFile(Constants.DOMAIN_XML));
        dataImpl.addChild(captureFile(Constants.SERVER_POLICY));
        dataImpl.addChild(captureFile(Constants.LOGIN_CONF));
        dataImpl.addChild(captureFile(Constants.SUN_ACC));
        return dataImpl; 
     }//captureConfigFiles
    
    /**
     * Masks confidential information with **** and copies it to destination
     * @param fileName xml file to be captured.
     * @throw DiagnosticException
     */ 
    public Data captureXMLFile(String fileName) throws DiagnosticException {
        try {
            String xmlFileToModify = repositoryDir + fileName;
            String destFile = reportDir + fileName;
            String domainXMLDTD = 
                    DiagnosticServiceHelper.getInstallationRoot() + 
                    Constants.DOMAIN_XML_DTD;

            Document serverXml = XmlUtils.loadXML(xmlFileToModify,
                     domainXMLDTD);
            XmlUtils.attrSearchReplace(serverXml.getDocumentElement(),
                    PASSWORD, PASSWORD_REPLACEMENT);
            XmlUtils.copyXMLFile(serverXml, destFile);
            return new FileData(destFile, DataType.CONFIG_DETAILS);
            
        } catch (SAXException se) {
            logger.log(Level.WARNING, se.getMessage());
        } catch (IOException ie) {
            logger.log(Level.WARNING, ie.getMessage());
        } catch (TransformerConfigurationException tce) {
            logger.log(Level.WARNING, tce.getMessage());
        } catch (TransformerException te) {
            logger.log(Level.WARNING,te.getMessage());
        } catch (ParserConfigurationException pce) {
            logger.log(Level.WARNING,pce.getMessage());
        }
        return null;
    }
    
    /**
     * Copies file
     * @param fileName relative path of file to be copied
     * @throw DiagnosticException
     */
    public Data captureFile(String fileName) throws DiagnosticException {
        try {
             FileUtils.copyFile(repositoryDir + fileName,  
                    reportDir +fileName);
             return new FileData(reportDir + fileName, DataType.CONFIG_DETAILS);
        } catch(IOException ioe) {
            logger.log(Level.WARNING,ioe.getMessage());
        }
        return null;
   }
    
}