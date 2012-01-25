/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 1997-2010 Oracle and/or its affiliates. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common Development
 * and Distribution License("CDDL") (collectively, the "License").  You
 * may not use this file except in compliance with the License.  You can
 * obtain a copy of the License at
 * https://glassfish.dev.java.net/public/CDDL+GPL_1_1.html
 * or packager/legal/LICENSE.txt.  See the License for the specific
 * language governing permissions and limitations under the License.
 *
 * When distributing the software, include this License Header Notice in each
 * file and include the License file at packager/legal/LICENSE.txt.
 *
 * GPL Classpath Exception:
 * Oracle designates this particular file as subject to the "Classpath"
 * exception as provided by Oracle in the GPL Version 2 section of the License
 * file that accompanied this code.
 *
 * Modifications:
 * If applicable, add the following below the License Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyright [year] [name of copyright owner]"
 *
 * Contributor(s):
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

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;

import com.sun.appserv.test.util.results.SimpleReporterAdapter;

public class WebTest {
    private static int count = 0;
    private static final int EXPECTED_COUNT = 1;
    static final SimpleReporterAdapter stat = new SimpleReporterAdapter("appserv-tests", "keep-alive");

    public static void main(String args[]) {
        // The stat reporter writes out the test info and results
        // into the top-level quicklook directory during a run.
        stat.addDescription("Standalone keepAlive war test");
        String host = args[0];
        String portS = args[1];
        String contextRoot = args[2];
        int port = new Integer(portS);
        String name;
        try {
            goGet(host, port, "KeepAlive", contextRoot + "/test.jsp");

        } catch (Throwable t) {
        } finally {
            if (count != EXPECTED_COUNT) {
                stat.addStatus("web-keepAlive", SimpleReporterAdapter.FAIL);
            }
        }
        stat.printSummary();
    }

    private static void goGet(String host, int port,
        String result, String contextPath)
        throws Exception {
        Socket s = new Socket(host, port);
        OutputStream os = s.getOutputStream();
        System.out.println("GET " + contextPath + " HTTP/1.0");
        System.out.println("Connection: keep-alive");
        os.write(("GET " + contextPath + " HTTP/1.0\n").getBytes());
        os.write("Connection: keep-alive\n".getBytes());
        os.write("\n".getBytes());
        InputStream is = s.getInputStream();
        BufferedReader bis = new BufferedReader(new InputStreamReader(is));
        String line = null;
        int tripCount = 0;
        try {
            while ((line = bis.readLine()) != null) {
                System.out.println("from server: " + line);
                int index = line.indexOf("Connection:");
                if (index >= 0) {
                    index = line.indexOf(":");
                    String state = line.substring(index + 1).trim();
                    if ("keep-alive".equalsIgnoreCase(state)) {
                        System.out.println("found keep-alive");
                        stat.addStatus("web-keepalive ", SimpleReporterAdapter.PASS);
                        count++;
                    }
                }
                if (line.contains("KeepAlive:end")) {
                    if (++tripCount == 1) {
                        System.out.println("GET " + contextPath + " HTTP/1.0");
                        os.write(("GET " + contextPath + " HTTP/1.0\n\n").getBytes());
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new Exception("Test UNPREDICTED-FAILURE");
        }
    }
}