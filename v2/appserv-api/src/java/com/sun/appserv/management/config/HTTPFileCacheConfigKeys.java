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
 * $Header: /cvs/glassfish/appserv-api/src/java/com/sun/appserv/management/config/HTTPFileCacheConfigKeys.java,v 1.1 2006/12/02 06:03:07 llc Exp $
 * $Revision: 1.1 $
 * $Date: 2006/12/02 06:03:07 $
 */
package com.sun.appserv.management.config;


/**
	Keys for use with {@link HTTPServiceConfig#createHTTPFileCacheConfig}
	
	All Map values associated with a key are of class java.lang.String
	unless otherwise documented.
 */
public final class HTTPFileCacheConfigKeys
{
	private	HTTPFileCacheConfigKeys()	{}
	public static final String GLOBALLY_ENABLED_KEY				=	"GloballyEnabled";
	public static final String FILE_CACHING_ENABLED_KEY			=	"FileCachingEnabled";
	public static final String MAX_AGE_IN_SECONDS_KEY			=	"MaxAgeInSeconds";
	public static final String MEDIUM_FILE_SIZE_LIMIT_IN_BYTES_KEY	=	"MediumFileSizeLimitInBytes";
	public static final String MEDIUM_FILE_SPACE_IN_BYTES_KEY		=	"MediumFileSpaceInBytes";
	public static final String SMALL_FILE_SIZE_LIMIT_IN_BYTES_KEY	=	"SmallFileSizeLimitInBytes";
	public static final String SMALL_FILE_SPACE_IN_BYTES_KEY		=	"SmallFileSpaceInBytes";
	public static final String FILE_TRANSMISSION_ENABLED_KEY		=	"FileTransmissionEnabled";
	public static final String MAX_FILES_COUNT_KEY					=	"MaxFilesCount";
	public static final String HASH_INIT_SIZE_KEY					=	"HashInitSize";
}