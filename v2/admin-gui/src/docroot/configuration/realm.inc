<!--
 DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 
 Copyright 1997-2007 Sun Microsystems, Inc. All rights reserved.
 
 The contents of this file are subject to the terms of either the GNU
 General Public License Version 2 only ("GPL") or the Common Development
 and Distribution License("CDDL") (collectively, the "License").  You
 may not use this file except in compliance with the License. You can obtain
 a copy of the License at https://glassfish.dev.java.net/public/CDDL+GPL.html
 or glassfish/bootstrap/legal/LICENSE.txt.  See the License for the specific
 language governing permissions and limitations under the License.
 
 When distributing the software, include this License Header Notice in each
 file and include the License file at glassfish/bootstrap/legal/LICENSE.txt.
 Sun designates this particular file as subject to the "Classpath" exception
 as provided by Sun in the GPL Version 2 section of the License file that
 accompanied this code.  If applicable, add the following below the License
 Header, with the fields enclosed by brackets [] replaced by your own
 identifying information: "Portions Copyrighted [year]
 [name of copyright owner]"
 
 Contributor(s):
 
 If you wish your version of this file to be governed by only the CDDL or
 only the GPL Version 2, indicate your decision by adding "[Contributor]
 elects to include this software in this distribution under the [CDDL or GPL
 Version 2] license."  If you don't indicate a single choice of license, a
 recipient has the option to distribute your version of this file under
 either the CDDL, the GPL Version 2 or to extend the choice of license to
 its licensees as provided above.  However, if you add GPL Version 2 code
 and therefore, elected the GPL Version 2 license, then the option applies
 only if the new code is made subject to such option by the copyright
 holder.
-->
<!--  configuration/realm.inc -->

<event>
    <!beforeEncode
        # need to put back the required image if there is any error in processing and the page is refreshed.
        # we had turned off the requirement in initPage before.
        setComponentRequired(id="form1:fileSection:jaax:jaax" required="$boolean{true}" );
        setComponentRequired(id="form1:fileSection:keyFile:keyFile" required="$boolean{true}" );
        setComponentRequired(id="form1:ldapSection:jaax:jaax" required="$boolean{true}" );
        setComponentRequired(id="form1:ldapSection:directory:directory" required="$boolean{true}" );
        setComponentRequired(id="form1:ldapSection:baseDn:baseDn" required="$boolean{true}" );
        setComponentRequired(id="form1:solarisSection:jaax:jaax" required="$boolean{true}" );
        setComponentRequired(id="form1:jdbcSection:jaax:jaax" required="$boolean{true}" );
        setComponentRequired(id="form1:jdbcSection:jndi:jndi" required="$boolean{true}" );
        setComponentRequired(id="form1:jdbcSection:userTable:userTable" required="$boolean{true}" );
        setComponentRequired(id="form1:jdbcSection:userName:userName" required="$boolean{true}" );
        setComponentRequired(id="form1:jdbcSection:pswd:pswd" required="$boolean{true}" );
        setComponentRequired(id="form1:jdbcSection:groupTable:groupTable" required="$boolean{true}" );
        setComponentRequired(id="form1:jdbcSection:groupName:groupName" required="$boolean{true}" );
    />
</event>

<sun:html id="html">
    <sun:head id="propertyhead" title="#{pageTitle}">
        <sun:script url="../js/restart.js" />
        <sun:script url="../js/selectElements.js" />
    </sun:head>

    <sun:body id="body" 
        onLoad="initClassname('#{myOption}'); 
            synchronizeRestartRequired('#{requestScope.restartRequired}', '#{sessionScope.restartRequired}')">
        <sun:form id="form1">

#include treeBreadcrumbs.inc

<!-- since this file is shared by both the security realm under config and node agent, we need to include the node agent tab here.
   the nodeAgentTab has its own control to whether display the tab or not.
-->
#include "nodeAgent/nodeAgentTabs.inc"

#include "alertMsg.inc"
        <!-- there seems to be a bug with the radio button component where if we switch from 'input' to 'predefine',
             the selected value will not get updated.  It will always stay as 'input'.
             So i work around the problem by using a hidden field
          -->
        <sun:hidden id="option"  value="#{myOption}" />

        <sun:title id="propertyContentPage" title="#{pageTitle}" helpText="#{pageTitleHelp}">
            <sun:button id="manageUsersButton" rendered="#{showUserButton}" immediate="#{true}" style="margin-left: 8pt"  text="$resource{i18n.button.ManageUsers}" >
                <!command
                    redirect(page="manageUsers.jsf?name=#{name}&configName=#{configName}");
                />
            </sun:button>

            <!facet pageButtonsTop>
#include "configuration/realmButtons.inc"            
            </facet>   
            </sun:title>

#include "configuration/realmAttrs.inc" 
#include "propertyTable.inc"

        <sun:title id="title2">
            <!facet pageButtonsBottom>
#include "configuration/realmButtons.inc" 
            </facet> 
        </sun:title>    

        <sun:hidden id="helpKey" value="#{helpKey}" />
    </sun:form>
    "<br/><br/>

    </sun:body>
        
#include "configuration/realmjs.inc"
#include "changeButtonsJS.inc"

</sun:html>
 