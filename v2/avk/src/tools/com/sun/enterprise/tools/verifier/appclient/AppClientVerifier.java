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
package com.sun.enterprise.tools.verifier.appclient;

import java.io.File;
import java.io.IOException;

import com.sun.enterprise.deployment.ApplicationClientDescriptor;
import com.sun.enterprise.deployment.Descriptor;
import com.sun.enterprise.tools.verifier.BaseVerifier;
import com.sun.enterprise.tools.verifier.FrameworkContext;
import com.sun.enterprise.tools.verifier.SpecVersionMapper;
import com.sun.enterprise.tools.verifier.apiscan.classfile.ClassFileLoaderFactory;
import com.sun.enterprise.tools.verifier.apiscan.packaging.ClassPathBuilder;
import com.sun.enterprise.tools.verifier.apiscan.stdapis.AppClientClosureCompiler;
import com.sun.enterprise.util.io.FileUtils;

/**
 * @author Vikas Awasthi
 */
public class AppClientVerifier extends BaseVerifier {

    private ApplicationClientDescriptor appclientd = null;
    private String classPath;//this is lazily populated in getClassPath()
    private boolean isASMode = false;

    public AppClientVerifier(FrameworkContext frameworkContext,
                             ApplicationClientDescriptor appClientDescriptor) {
        this.frameworkContext = frameworkContext;
        this.appclientd = appClientDescriptor;
        this.isASMode = !frameworkContext.isPortabilityMode();
    }

    /**
     * Responsible for running application client based verifier tests on the the web archive.
     * Called from runVerifier in {@link BaseVerifier} class.
     *
     * @throws Exception
     */
    public void verify() throws Exception {
        if (areTestsNotRequired(frameworkContext.isAppClient()) &&
                areTestsNotRequired(frameworkContext.isWebServicesClient()) &&
                areTestsNotRequired(frameworkContext.isPersistenceUnits()))
            return;

        preVerification();
        createClosureCompiler();//this can be moved up to base verifier in future.
        verify(appclientd, new AppClientCheckMgrImpl(frameworkContext));
    }

    public Descriptor getDescriptor() {
        return appclientd;
    }

    protected ClassLoader createClassLoader()
            throws IOException {
        return appclientd.getClassLoader();
    }

    protected String getArchiveUri() {
        return FileUtils.makeFriendlyFileName(appclientd.getModuleDescriptor().getArchiveUri());
    }

    protected String[] getDDString() {
        String dd[] = {"META-INF/sun-application-client.xml", // NOI18N
                       "META-INF/application-client.xml"}; // NOI18N
        return dd;
    }

    /**
     * Creates and returns the class path associated with the client jar.
     * Uses the exploded location of the archive for generating the classpath.
     *
     * @return entire classpath string
     * @throws IOException
     */
    protected String getClassPath() throws IOException {
        if (classPath != null) return classPath;

        if(isASMode)
            return (classPath = getClassPath(frameworkContext.getClassPath()));

        String cp;
        if (!appclientd.getModuleDescriptor().isStandalone()) {
            //take the cp from the enclosing ear file
            String ear_uri = frameworkContext.getExplodedArchivePath();
            File ear = new File(ear_uri);
            assert(ear.isDirectory());
            String earCP = ClassPathBuilder.buildClassPathForEar(ear);
            String libdir = appclientd.getApplication().getLibraryDirectory();
            if (libdir!=null) {
                earCP = getLibdirClasspath(ear_uri, libdir) + earCP;
            }
            String module_uri = appclientd.getModuleDescriptor().getArchiveUri();//this is a relative path
            File module = new File(module_uri);
            assert(module.isFile() && !module.isAbsolute());
            // exploder creates the directory replacing all dots by '_'
            File explodedModuleDir = new File(ear_uri,
                    FileUtils.makeFriendlyFileName(module_uri));
            String moduleCP = ClassPathBuilder.buildClassPathForJar(
                    explodedModuleDir);
            cp = moduleCP + File.pathSeparator + earCP;
        } else {
            String module_uri = frameworkContext.getExplodedArchivePath();//this is an absolute path
            File module = new File(module_uri);
            assert(module.isDirectory() && module.isAbsolute());
            cp = ClassPathBuilder.buildClassPathForJar(module);
        }
        return (classPath = cp);
    }

    /**
     * creates the ClosureCompiler for the client jar and sets it to the
     * verifier context. This is used to compute the closure on the classes used
     * in the client jar.
     *
     * @throws IOException
     */
    protected void createClosureCompiler() throws IOException {
        String specVer = SpecVersionMapper.getAppClientVersion(
                frameworkContext.getJavaEEVersion());
        Object arg = (isASMode)?appclientd.getClassLoader():(Object)getClassPath();
        AppClientClosureCompiler cc = new AppClientClosureCompiler(specVer,
                ClassFileLoaderFactory.newInstance(new Object[]{arg}));
        context.setClosureCompiler(cc);
    }

}