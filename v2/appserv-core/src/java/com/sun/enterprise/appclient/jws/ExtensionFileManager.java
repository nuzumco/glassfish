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

package com.sun.enterprise.appclient.jws;

import com.sun.logging.LogDomains;
import com.sun.enterprise.util.i18n.StringManager;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.Manifest;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Manages a data structure of all extension jars known to the app server.
 * <p>
 * This class builds a map of extension name to an
 * instance of the inner class Extension that records information about that
 * extension jar.  An Extension is created for every jar file in any of the 
 * directories specified by java.ext.dirs.  
 * <p>
 * Later, a caller can use the findExtensionTransitiveClosure method, passing
 * a jar file manifest's main attributes and receiving back a List of Extension objects representing
 * all extension jars referenced directly or indirectly from that jar.  
 * 
 * @author tjquinn
 */
public class ExtensionFileManager {
    
    /** the property name that points to extension directories */
    private static final String EXT_DIRS_PROPERTY_NAME = "java.ext.dirs";
    
    private Logger _logger=LogDomains.getLogger(NamingConventions.JWS_LOGGER);

    /** local strings manager */
    private StringManager localStrings = StringManager.getManager(getClass());
    
    /*
     *Stores File and version information for all extension jars in all
     *extension jar directories. (any directory listed in java.ext.dirs)
     */
    private Map<ExtensionKey, Extension> extensionFileInfo = null;

    /** Records directories specified in java.ext.dirs */
    private Vector<File> extensionFileDirs = null;
            
    /** Creates a new instance of ExtensionFileManager
     *@throws IOException in case of errors searching extension directories for jars
     */
    public ExtensionFileManager() throws IOException {
        extensionFileDirs = buildExtensionFileDirs();
        extensionFileInfo = buildExtensionFileEntries(extensionFileDirs);
    }
    
    /*
     *Returns the collection of extension
     *file info objects for the extension jars known to the app server.
     *@return Map from extension name to 
     *@throws IOException in case of error accessing a file as a jar file
     */
    public Map<ExtensionKey, Extension> getExtensionFileEntries() throws IOException {
        return extensionFileInfo;
    }

    /**
     *Constructs the collection of File objects, one for each extension directory.
     *@return Vector<File> containing a File for each extension directory
     */
    private Vector<File> buildExtensionFileDirs() {
        Vector<File> result = new Vector<File>();
        
        String extDirs = System.getProperty(EXT_DIRS_PROPERTY_NAME);
        StringTokenizer stkn = new StringTokenizer(extDirs, File.pathSeparator);

        while (stkn.hasMoreTokens()) {
            String extensionDirPath = stkn.nextToken();
            result.add(new File(extensionDirPath));
        }
        return result;
    }
    
    /**
     * Constructs the collection of extension files known to the app server.
     * @param dirs the directories in which to search for extension jar files
     * @return Map<ExtensionKey,Extension> mapping the extension name and spec version to the extension jar entry
     * @throws IOException in case of errors processing jars in the extension directories
     */
     private Map<ExtensionKey, Extension> buildExtensionFileEntries(Vector<File> dirs) throws IOException {

        /*
         *For each extension directory, collect all jar files
         *and add an entry (containing File and spec version string) for each 
         *file into the data structure.
         */
         Map<ExtensionKey,Extension> result = new HashMap<ExtensionKey,Extension>();
         
         for (int i = 0; i < dirs.size(); i++) {
            addExtJarsFromDirectory(result, i, dirs.get(i));
        }
        return result;
     }
     
     /**
      *Create the collection of extension directories.
      *@return Vector of File objects, one for each directory.
      */
     /**
      * Adds entries for the extension files from one directory to the indicated Map.
      * @param extensionFilesMap map of each extension name to its Extension
      * @param extensionDirNumber the ordinal number of the directory being processed
      * @param extDirPath the current directory being processed
      * @throws IOException in case of error scanning for jar files
      */
     private void addExtJarsFromDirectory(Map<ExtensionKey, Extension> map, int extensionDirNumber, File extDir) throws IOException {
        File [] extJars = extDir.listFiles(new FilenameFilter() {
            public boolean accept(File dir, String name) {
                return name.endsWith(".jar");
            }
          });
        if (extJars != null) {
            for (File file : extJars) {
                Extension entry = buildExtensionForJar(file, extensionDirNumber);
                if (entry != null) {
                    map.put(entry.extensionKey, entry);
                }
            }
        }
     }
     
    /**
     * Creates an extension Extension for a jar file if the jar is in fact an extension.
     * @param jarFile a File object for the jar to use
     * @param extDirectoryNumber the ordinal number of the directory in java.ext.dirs being scanned
     * @return Extension for the jar if the jar has an extension name; null otherwise
     * @throws IOException in case of errors working with the file
     */
    private Extension buildExtensionForJar(File file, int extDirectoryNumber) throws IOException {
        Extension result = null;
        JarFile jarFile = null;
        try {
            jarFile = new JarFile(file);
            ExtensionKey key = getDefinedExtensionKey(jarFile);
            if (key != null) {
                result = new Extension(key, file, extDirectoryNumber);
            }
            return result;
        } finally {
            if (jarFile != null) {
                jarFile.close();
            }
        }
    }

    /**
     * Constructs a List of Extension objects corresponding to jars required to
     * satisfy an extension chain.
     * <p>
     * The transitive closure includes any extensions required by the
     * initial jar, its Class-Path jars, and any extensions required
     * by extensions.
     * @param anchorDir the directory relative to which Class-Path manifest entries are evaluated (if relative)
     * @param mainAttrs the main attributes from a jar file whose extensions are to be satisfied
     * @return List<Extension> containing an Extension object for each required jar
     * @throws IOException in case of errors building the extension jar file data structure
     */
    public Set<Extension> findExtensionTransitiveClosure(File anchorDir, Attributes mainAttrs) throws IOException {
        
        Set<Extension> result = new HashSet<Extension>();

        Vector<File> filesToProcess = new Vector<File>();
        
        filesToProcess.addAll(getClassPathJars(anchorDir, mainAttrs));
        
        Set<Extension> extensionsUsedByApp = getReferencedExtensions(mainAttrs);
        result.addAll(extensionsUsedByApp);
        filesToProcess.addAll(extensionsToFiles(extensionsUsedByApp));
        
        /**
         *Do not use the for/each construct next because the loop may add
         *elements to the vector and for/each would through a concurrent
         *modification exception.
         */
        for (int i = 0; i < filesToProcess.size(); i++) {
            File nextFile = filesToProcess.get(i);
            /*
             *The Class-Path entry might point to a directory.  If so, skip it
             *because directories do not support extensions.
             */
            if (nextFile.exists() && nextFile.isDirectory()) {
                continue;
            }
            
            try {
                JarFile nextJarFile = new JarFile(nextFile);
                try {
                    Attributes attrs = getMainAttrs(nextJarFile);
                    Set<Extension> newExtensions = getReferencedExtensions(attrs);
                    result.addAll(newExtensions);
                    filesToProcess.addAll(extensionsToFiles(newExtensions));
                } finally {
                    if (nextJarFile != null) {
                        nextJarFile.close();
                    }
                }
            } catch (Exception e) {
                _logger.log(Level.WARNING, localStrings.getString("jws.extension.error", nextFile.getAbsolutePath()), e);
            }
        }
        return result;
    }

    /**
     *Returns a Set of File objects corresponding to the supplied set of Extensions.
     *@param extensions set of Extension the files of which are of interest
     *@return set of File, one File for each Extension in the input set
     */
    private Set<File> extensionsToFiles(Set<Extension> extensions) {
        Set<File> result = new HashSet<File>();
        for (Extension e : extensions) {
            result.add(e.file);
        }
        return result;
    }
    
    /**
     *Returns a Set of Extensions that are referenced by the jar file whose
     *main attributes are passed.
     *@param mainAttrs the main attributes from a jar file's manifest
     *@return Set of Extension objects corresponding to the extensions referenced by the attributes
     *@throws IOException if an extension jar is required but not found
     */
    private Set<Extension> getReferencedExtensions(Attributes mainAttrs) throws IOException {
        Map<ExtensionKey,Extension> result = new HashMap<ExtensionKey, ExtensionFileManager.Extension>();
        Set<ExtensionKey> extensionKeys = getReferencedExtensionKeys(mainAttrs);

        for (ExtensionKey key : extensionKeys) {
            if ( ! result.containsKey(key)) {
                Extension extension = extensionFileInfo.get(key);

                /*
                 *Add this extension only if it does not already appear 
                 *in the result collection.  In that case, also add the
                 *file to the collection of files to be processed.
                 */
                if (extension != null) {
                    result.put(key, extension);
                } else {
                    throw new IOException("Jar file requires the extension " + key + " but it is not in the known extensions " + extensionFileInfo);
                }
            }
        }
        return new HashSet<Extension>(result.values());
    }
    
    /**
     *Returns the main attributes (if any) object from a jar file.
     *@param jarFile the JarFile of interest
     *@return Attributes object for the jar file's main attributes.
     *@throws IOException in case of error getting the Jar file's manifest
     */
    private Attributes getMainAttrs(JarFile jarFile) throws IOException {
        Attributes result = null;
        
        Manifest mf = jarFile.getManifest();
        if (mf != null) {
            result = mf.getMainAttributes();
        }
        return result;
    }
    
    /**
     *Returns the Files corresponding to the Class-Path entries (if any) in a
     *Jar file's main attributes.
     *@param anchorDir the directory to which relative Class-Path entries are resolved
     *@param mainAttrs the jar file's main attributes (which would contain Class-Path entries if there are any)
     */
    private List<File> getClassPathJars(File anchorDir, Attributes mainAttrs) {
        List<File> result = new LinkedList<File>();
        String classPathList = mainAttrs.getValue(Attributes.Name.CLASS_PATH);
        if (classPathList != null) {
            StringTokenizer stkn = new StringTokenizer(classPathList, " ");
            while (stkn.hasMoreTokens()) {
                String classPathJarPath = stkn.nextToken();
                File classPathJarFile = new File(classPathJarPath);
                if ( ! classPathJarFile.isAbsolute()) {
                    classPathJarFile = new File(anchorDir, classPathJarPath);
                }
                result.add(classPathJarFile);
            }
        }
        return result;
    }

    /**
     *Returns the ExtensionKey for the extension which the specified JarFile provides (if any).
     *@param jarFile the JarFile which may be an extension jar
     *@returns the ExtensionKey for the extension if this jar is one; null otherwise
     *@throws IOException in case of error getting the jar file's main attributes
     */
    private ExtensionKey getDefinedExtensionKey(JarFile jarFile) throws IOException {
        ExtensionKey result = null;
        
        Attributes mainAttrs = getMainAttrs(jarFile);
        if (mainAttrs != null) {
            String extName = mainAttrs.getValue(Attributes.Name.EXTENSION_NAME);
            if (extName != null) {
                String specVersion = mainAttrs.getValue(Attributes.Name.SPECIFICATION_VERSION);
                result = new ExtensionKey(extName, specVersion);
            }
        }
        
        return result;
    }
    
    /**
     *Returns the ExtensionKeys for the extension jars referenced by the specified main attributes
     *@param mainAttrs the main attributes from a jar file that may refer to extension jars
     *@return Set of ExtensionKey, one key or each distinct extension jar that is referenced
     */
    private Set<ExtensionKey> getReferencedExtensionKeys(Attributes mainAttrs) {
        Set<ExtensionKey> result = new HashSet<ExtensionKey>();
        
        if (mainAttrs != null) {
            String extensionList = mainAttrs.getValue(Attributes.Name.EXTENSION_LIST);
            if (extensionList != null) {
                StringTokenizer stkn = new StringTokenizer(extensionList, " ");
                while (stkn.hasMoreTokens()) {
                    /*
                     *For each extension jar in this jar's list, create a new
                     *ExtensionKey using the name and spec version.
                     */
                    String token = stkn.nextToken().trim();
                    String extName = mainAttrs.getValue(token + "-" + Attributes.Name.EXTENSION_NAME);
                    String specVersion = mainAttrs.getValue(token + "-" + Attributes.Name.SPECIFICATION_VERSION);
                    ExtensionKey key = new ExtensionKey(extName, specVersion);
                    result.add(key);
                }
            }
        }
        return result;
    }
            
    /**
     * The key for identifying extension jar Extension objects in the Map.  The key
     * needs to include both the extension name and the specification version.
     * Note that the spec version defaults to the empty string.
     */
    public class ExtensionKey {
        private String extensionName = null;
        
        private String specificationVersion = null;
        
        /**
         * Creates a new instance of ExtensionKey.
         * @param extensionName the extension name of interest (cannot be null)
         * @param specificationVersion the spec version of interest
         */
        public ExtensionKey(String extensionName, String specificationVersion) {
            assert extensionName != null : "extensionName is null";
            this.extensionName = extensionName;
            this.specificationVersion = (specificationVersion != null) ? specificationVersion : "";
        }
        
        public boolean equals(Object other) {
            boolean result = false;
            if (other != null) {
                if (other == this) {
                    result = true;
                } else {
                    if (other instanceof ExtensionKey) {
                        ExtensionKey otherEntryKey = (ExtensionKey) other;
                        result = extensionName.equals(otherEntryKey.extensionName) &&
                                 specificationVersion.equals(otherEntryKey.specificationVersion);
                    }
                }
            }
            return result;
        }

        public int hashCode() {
            int result = 17;
            result = 37 * result + extensionName.hashCode();
            result = 37 * result + specificationVersion.hashCode();
            return result;
        }
        
        public String toString() {
            return "Name=" + extensionName + ", spec version = " + specificationVersion;
        }
    }
    
    /**
     *Records information about an extension jar file known to the app server.
     */
    public class Extension {
        
        private ExtensionKey extensionKey;
        
        private File file = null;
        
        /** in case the same extension appears in more than one extension directory */
        private int extDirectoryNumber = -1;
        
        public Extension(ExtensionKey extensionKey, File file, int extDirectoryNumber) {
            assert extensionKey != null : "extensionKey is null";
            assert file != null : "file is null";
            
            this.extensionKey = extensionKey;
            this.file = file;
            this.extDirectoryNumber = extDirectoryNumber;
        }
        
        public boolean equals(Object other) {
            boolean result = false;
            if (other != null) {
                if (other == this) {
                    result = true;
                } else {
                    if (other instanceof Extension) {
                        Extension otherEntry = (Extension) other;
                        result = extensionKey.equals(otherEntry.extensionKey) &&
                                file.equals(otherEntry.file) &&
                                extDirectoryNumber == otherEntry.extDirectoryNumber;
                    }
                }
            }
            return result;
        }
        
        public int hashCode() {
            int result = 17;
            result = result * 37 + extensionKey.hashCode();
            result = result * 37 + file.hashCode();
            result = result * 37 + extDirectoryNumber;
            return result;
        }
        
        public int getExtDirectoryNumber() {
            return extDirectoryNumber;
        }
        
        public File getFile() {
            return file;
        }
        
        public String toString() {
            return extensionKey.toString() + ", file = " + file.getAbsolutePath() + ", in ext dir " + extDirectoryNumber + "(" + extensionFileDirs.get(extDirectoryNumber).getAbsolutePath();
        }
    }
}