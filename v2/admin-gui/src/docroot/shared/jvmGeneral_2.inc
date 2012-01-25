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
<!-- shared/jvmGeneral_2.jsf -->

#include "alertMsg.inc"
                <!-- Page Title -->
                <sun:title id="propertyContentPage" title="$resource{i18n.jvm.GeneralTitle}" 
                    helpText="$resource{i18n.jvm.GeneralPageHelp}">
                    <sun:button id="loadDefaults" style="margin-left: 8pt"  primary="#{false}"  text="$resource{i18n.button.LoadDefaults}" >    
                        <!command
                            getServerDefaultJvmAttributes(ConfigName="#{configName}", JavaHome=>$attribute{javaHome}, Options=>$attribute{javacOptions}, DebugEnabled=>$attribute{debugEnabled}, DebugOptions=>$attribute{debugOptions}, RmicOptions=>$attribute{rmicOptions}, BytecodePreprocessor=>$attribute{bytecodePreprocessor});
                            />
                    </sun:button>
                    <!-- Buttons  -->                    
                    <!facet pageButtonsTop>
                    <sun:panelGroup id="topButtons">
                        <sun:button id="saveButton" text="$resource{i18n.button.Save}" 
                                onClick="return guiValidate('#{reqMsg}','#{reqInt}','#{reqPort}'); " >  
                            <!command
                            prepareSuccessfulMsg();
                            saveServerJvmAttributes(
                            ConfigName="#{configName}",
                            JavaHome="#{javaHome}"
                            JavacOptions="#{javacOptions}"
                            DebugEnabled="#{debugEnabled}"
                            DebugOptions="#{debugOptions}"
                            RmicOptions="#{rmicOptions}"
                            BytecodePreprocessor="#{bytecodePreprocessor}"
                            );
                            />
                        </sun:button>
                    </sun:panelGroup>
                    </facet>                     
                </sun:title>
                    <!-- PropertySheet .... -->
                    <sun:propertySheet id="propertySheet">	
                        <!-- Text Field section -->               
                        <sun:propertySheetSection id="propertSectionTextField">
                            <sun:property id="javaHomeProp"  labelAlign="left" noWrap="#{true}" overlapLabel="#{false}" label="$resource{i18n.jvm.JavaHomeLabel}" helpText="$resource{i18n.jvm.JavaHomeHelp}">
                                <sun:textField id="JavaHome"  styleClass="required" columns="$int{55}" text="#{requestScope.javaHome}" required="#{true}"/> 
                            </sun:property>                       
                            <sun:property id="JavacOptionsProp"  labelAlign="left" noWrap="#{true}" overlapLabel="#{false}" label="$resource{i18n.jvm.JavacOptionsLabel}" helpText="$resource{i18n.jvm.JavacOptionsHelp}">                           
                                <sun:textField id="JavacOptions" columns="$int{55}" text="#{requestScope.javacOptions}"/>     
                            </sun:property>                        
                          <sun:property id="debugEnabledProp"  labelAlign="left" noWrap="#{true}" overlapLabel="#{false}" label="$resource{i18n.jvm.DebugLabel}" helpText="$resource{i18n.jvm.DebugHelp}">
                                <sun:checkbox label="$resource{i18n.common.Enabled}" selected="#{requestScope.debugEnabled}" />      
                            </sun:property>
                            <sun:property id="DebugOptionsProp"  labelAlign="left" noWrap="#{true}" overlapLabel="#{false}" label="$resource{i18n.jvm.DebugOptionsLabel}" helpText="$resource{i18n.jvm.DebugOptionsHelp}">
                                <sun:textField id="DebugOptions" columns="$int{55}" text="#{requestScope.debugOptions}" />      
                            </sun:property>
                            <sun:property id="RmicOptionsProp"  labelAlign="left" noWrap="#{true}" overlapLabel="#{false}" label="$resource{i18n.jvm.RMICompileOptionsLabel}" helpText="$resource{i18n.jvm.RMICompileOptionsHelp}">
                                <sun:textField id="RmicOptions" columns="$int{55}" text="#{requestScope.rmicOptions}"/>      
                            </sun:property> 
                            <sun:property id="bytecodePreprocessorProp"  labelAlign="left" noWrap="#{true}" overlapLabel="#{false}" label="$resource{i18n.jvm.BytecodePreprocessorLabel}" helpText="$resource{i18n.jvm.BytecodePreprocessorHelp}">
                                <sun:textArea id="bytecodePreprocessor" rows="$int{8}" columns="$int{55}" text="#{requestScope.bytecodePreprocessor}"/>      
                            </sun:property>                  
                            "<br /><br />
                        </sun:propertySheetSection>

                    </sun:propertySheet>    
	<sun:hidden id="helpKey" value="$attribute{helpKey}" />
            </sun:form>
        </sun:body>
    </sun:html>
</sun:page>