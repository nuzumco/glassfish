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


package com.sun.appserv.management.config;

import com.sun.appserv.management.base.XTypes;




/**
	 Configuration for the &lt;auth-realm&gt; element.
*/

public interface AuthRealmConfig extends PropertiesAccess, NamedConfigElement 
{
/** The j2eeType as returned by {@link com.sun.appserv.management.base.AMX#getJ2EEType}. */
	public static final String	J2EE_TYPE	= XTypes.AUTH_REALM_CONFIG;
	
	
	/**
	    Classname of the default implementing class.
	 */
	public static final String DEFAULT_REALM_CLASSNAME  =
	    "com.sun.enterprise.security.auth.realm.file.FileRealm";
	    
	public static final String KEY_FILE_PROPERTY_KEY    = PropertiesAccess.PROPERTY_PREFIX + "file";
	
	public static final String JAAS_CONTEXT_PROPERTY_KEY    = PropertiesAccess.PROPERTY_PREFIX + "jaas-context";
	
	/**
	    When using {@link #DEFAULT_REALM_CLASSNAME} implementation, append
	    a file name to this prefix and specify it via {@link #KEY_FILE_PROPERTY_KEY}.
	 */
	public static final String KEY_FILE_PREFIX         = "${com.sun.aas.instanceRoot}/config/";
	
	
	public String	getClassname();
	public void	setClassname( String value );

    /**
        <b>Supported only for the default implementation ({@link #DEFAULT_REALM_CLASSNAME})</b>.
     */
	public void			addUser( String user, String password, String[] groupList );
	
    /**
        <b>Supported only for the default implementation ({@link #DEFAULT_REALM_CLASSNAME})</b>.
     */
	public String[]		getGroupNames();
	
    /**
        <b>Supported only for the default implementation ({@link #DEFAULT_REALM_CLASSNAME})</b>.
     */
	public String[]		getUserGroupNames( String user );
	
    /**
        <b>Supported only for the default implementation ({@link #DEFAULT_REALM_CLASSNAME})</b>.
     */
	public String[]		getUserNames();
	
    /**
        <b>Supported only for the default implementation ({@link #DEFAULT_REALM_CLASSNAME})</b>.
     */
	public void			removeUser( String user );
	
    /**
        <b>Supported only for the default implementation ({@link #DEFAULT_REALM_CLASSNAME})</b>.
     */
	public void			updateUser( String user, String password, String[] groupList );





}