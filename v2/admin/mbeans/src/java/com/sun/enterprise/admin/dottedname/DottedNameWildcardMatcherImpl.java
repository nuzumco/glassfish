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
 * $Header: /cvs/glassfish/admin/mbeans/src/java/com/sun/enterprise/admin/dottedname/DottedNameWildcardMatcherImpl.java,v 1.3 2005/12/25 03:42:05 tcfujii Exp $
 * $Revision: 1.3 $
 * $Date: 2005/12/25 03:42:05 $
 */


package com.sun.enterprise.admin.dottedname;

import java.util.HashSet;
import java.util.Set;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.regex.Pattern;

import javax.management.ObjectName;


/*
	Resolve a wildcarded dotted name.
 */
 
public class DottedNameWildcardMatcherImpl implements DottedNameWildcardMatcher
{
	final Set		mSearchSet;
	
		public
	DottedNameWildcardMatcherImpl( final Set	searchSet )
	{
		mSearchSet		= searchSet;
	}
	
	
	/*
		Return the set of all dotted names that match the specified wildcarded dotted name
	 */
		Set
	resolveAll( final String wildcardedName, final Iterator iter )
	{
		final HashSet	resolvedSet	= new HashSet();
		
		final Pattern	pattern	= Pattern.compile( wildcardedName );
		
		while ( iter.hasNext() )
		{
			final String	candidate	= (String)iter.next();
			
			if ( pattern.matcher( candidate ).matches() )
			{
				resolvedSet.add( candidate );
			}
		}
		
		return( resolvedSet );
	}

	/*
		IMPORTANT: the wildcard format must be that of java.util.regex
		
		@param dottedNameString	a string using java.util.regex format
	 */
		public Set
	matchDottedNames( String dottedNameString )
	{
		Set	resolvedSet	= null;
		
		if ( dottedNameString.equals( ".*" ) )
		{
			// optimization; match all
			resolvedSet	= new HashSet();
			resolvedSet.addAll( mSearchSet );
		}
		else
		{
			resolvedSet	= resolveAll( dottedNameString, mSearchSet.iterator() );
		}
		
		return( resolvedSet );
	}
}



