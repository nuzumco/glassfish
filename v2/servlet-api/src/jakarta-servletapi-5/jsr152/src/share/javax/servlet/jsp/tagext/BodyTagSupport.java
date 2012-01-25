/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 * 
 * Copyright 1997-2007 Sun Microsystems, Inc. All rights reserved.
 * 
 * Portions Copyright Apache Software Foundation.
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
package javax.servlet.jsp.tagext;

import javax.servlet.jsp.*;
import javax.servlet.jsp.tagext.*;

import javax.servlet.*;

import java.io.Writer;

import java.util.Hashtable;

/**
 * A base class for defining tag handlers implementing BodyTag.
 *
 * <p>
 * The BodyTagSupport class implements the BodyTag interface and adds
 * additional convenience methods including getter methods for the
 * bodyContent property and methods to get at the previous out JspWriter.
 *
 * <p>
 * Many tag handlers will extend BodyTagSupport and only redefine a
 * few methods.
 */

public class BodyTagSupport extends TagSupport implements BodyTag {

    /**
     * Default constructor, all subclasses are required to only define
     * a public constructor with the same signature, and to call the
     * superclass constructor.
     *
     * This constructor is called by the code generated by the JSP
     * translator.
     */

    public BodyTagSupport() {
	super();
    }

    /**
     * Default processing of the start tag returning EVAL_BODY_BUFFERED.
     *
     * @return EVAL_BODY_BUFFERED
     * @throws JspException if an error occurred while processing this tag
     * @see BodyTag#doStartTag
     */
 
    public int doStartTag() throws JspException {
        return EVAL_BODY_BUFFERED;
    }


    /**
     * Default processing of the end tag returning EVAL_PAGE.
     *
     * @return EVAL_PAGE
     * @throws JspException if an error occurred while processing this tag
     * @see Tag#doEndTag
     */

    public int doEndTag() throws JspException {
	return super.doEndTag();
    }


    // Actions related to body evaluation

    /**
     * Prepare for evaluation of the body: stash the bodyContent away.
     *
     * @param b the BodyContent
     * @see #doAfterBody
     * @see #doInitBody()
     * @see BodyTag#setBodyContent
     */

    public void setBodyContent(BodyContent b) {
	this.bodyContent = b;
    }


    /**
     * Prepare for evaluation of the body just before the first body evaluation:
     * no action.
     *
     * @throws JspException if an error occurred while processing this tag
     * @see #setBodyContent
     * @see #doAfterBody
     * @see BodyTag#doInitBody
     */

    public void doInitBody() throws JspException {
    }


    /**
     * After the body evaluation: do not reevaluate and continue with the page.
     * By default nothing is done with the bodyContent data (if any).
     *
     * @return SKIP_BODY
     * @throws JspException if an error occurred while processing this tag
     * @see #doInitBody
     * @see BodyTag#doAfterBody
     */

    public int doAfterBody() throws JspException {
 	return SKIP_BODY;
    }


    /**
     * Release state.
     *
     * @see Tag#release
     */

    public void release() {
	bodyContent = null;

	super.release();
    }

    /**
     * Get current bodyContent.
     *
     * @return the body content.
     */
    
    public BodyContent getBodyContent() {
	return bodyContent;
    }


    /**
     * Get surrounding out JspWriter.
     *
     * @return the enclosing JspWriter, from the bodyContent.
     */

    public JspWriter getPreviousOut() {
	return bodyContent.getEnclosingWriter();
    }

    // protected fields

    /**
     * The current BodyContent for this BodyTag.
     */
    protected BodyContent   bodyContent;
}