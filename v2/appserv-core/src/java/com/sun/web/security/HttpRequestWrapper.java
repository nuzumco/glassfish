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
package com.sun.web.security;

import java.io.InputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.security.Principal;
import java.util.Iterator;
import java.util.Locale;

import javax.servlet.FilterChain; 
import javax.servlet.ServletInputStream;
import javax.servlet.ServletRequest;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

import org.apache.catalina.Connector;
import org.apache.catalina.Context;
import org.apache.catalina.Host;
import org.apache.catalina.HttpRequest;
import org.apache.catalina.Request;
import org.apache.catalina.Response;
import org.apache.catalina.ValveContext;
import org.apache.catalina.Wrapper;
import org.apache.tomcat.util.buf.MessageBytes;

class HttpRequestWrapper extends HttpServletRequestWrapper 
        implements HttpRequest {
        
    private HttpRequest httpRequest;         

    HttpRequestWrapper(HttpRequest request,
            HttpServletRequest servletRequest) {
        super(servletRequest);
        httpRequest = request;
    }
    
    // ----- HttpRequest Methods -----
    public void addCookie(Cookie cookie) {
        httpRequest.addCookie(cookie);
    }

    /* Delegate to HttpServletResponse
    public void addHeader(String name, String value) {
        httpRequest.addHeader(name, value);
    }
    */

    public void addHeader(String name, String value) {
        httpRequest.addHeader(name, value);
    }

    public void addLocale(Locale locale) {
        httpRequest.addLocale(locale);
    }

    public void addParameter(String name, String values[]) {
        httpRequest.addParameter(name, values);
    }

    public void clearCookies() {
        httpRequest.clearCookies();
    }

    public void clearHeaders() {
        httpRequest.clearHeaders();
    }

    public void clearLocales() {
        httpRequest.clearLocales();
    }

    public void clearParameters() {
        httpRequest.clearParameters();
    }

    public void setAuthType(String type) {
        httpRequest.setAuthType(type);
    }

    public MessageBytes getContextPathMB() {
        return httpRequest.getContextPathMB();
    }

    public void setContextPath(String path) {
        httpRequest.setContextPath(path);
    }

    public void setMethod(String method) {
        httpRequest.setMethod(method);
    }

    public void setQueryString(String query) {
        httpRequest.setQueryString(query);
    }

    public MessageBytes getPathInfoMB() {
        return httpRequest.getPathInfoMB();
    }

    public void setPathInfo(String path) {
        httpRequest.setPathInfo(path);
    }

    public MessageBytes getRequestPathMB() {
        return httpRequest.getRequestPathMB();
    }

    public void setRequestedSessionCookie(boolean flag) {
        httpRequest.setRequestedSessionCookie(flag);
    }

    public void setRequestedSessionId(String id) {
        httpRequest.setRequestedSessionId(id);
    }

    public void setRequestedSessionURL(boolean flag) {
        httpRequest.setRequestedSessionURL(flag);
    }

    public void setRequestURI(String uri) {
        httpRequest.setRequestURI(uri);
    }

    public void setDecodedRequestURI(String uri) {
        httpRequest.setDecodedRequestURI(uri);
    }

    public String getDecodedRequestURI() {
        return httpRequest.getDecodedRequestURI();
    }

    public MessageBytes getDecodedRequestURIMB() {
        return httpRequest.getDecodedRequestURIMB();
    }

    public MessageBytes getServletPathMB() {
        return httpRequest.getServletPathMB();
    }

    public void setServletPath(String path) {
        httpRequest.setServletPath(path);
    }

    public void setUserPrincipal(Principal principal) {
        httpRequest.setUserPrincipal(principal);
    }

    // ----- Request Methods -----
    public String getAuthorization() {
        return httpRequest.getAuthorization();
    }  

    public void setAuthorization(String authorization) {
        httpRequest.setAuthorization(authorization);
    }

    public Connector getConnector() {
        return httpRequest.getConnector();
    }

    public void setConnector(Connector connector) {
        httpRequest.setConnector(connector);
    }

    public Context getContext() {
        return httpRequest.getContext();
    }

    public void setContext(Context context) {
        httpRequest.setContext(context);
    }

    public FilterChain getFilterChain() {
        return httpRequest.getFilterChain();
    }

    public void setFilterChain(FilterChain filterChain) {
        httpRequest.setFilterChain(filterChain);
    }

    public Host getHost() {
        return httpRequest.getHost();
    }

    public void setHost(Host host) {
        httpRequest.setHost(host);
    }

    public String getInfo() {
        return httpRequest.getInfo();
    }

    public ServletRequest getRequest() {
        return httpRequest.getRequest();
    }

    public Response getResponse() {
        return httpRequest.getResponse();
    }

    public void setResponse(Response response) {
        httpRequest.setResponse(response);
    }

    public Socket getSocket() {
        return httpRequest.getSocket();
    }

    public void setSocket(Socket socket) {
        httpRequest.setSocket(socket);
    }

    public InputStream getStream() {
        return httpRequest.getStream();
    }

    public void setStream(InputStream stream) {
        httpRequest.setStream(stream);
    }

    public ValveContext getValveContext() {
        return httpRequest.getValveContext();
    }

    public void setValveContext(ValveContext valveContext) {
        httpRequest.setValveContext(valveContext);
    }

    public Wrapper getWrapper() {
        return httpRequest.getWrapper();
    }

    public void setWrapper(Wrapper wrapper) {
        httpRequest.setWrapper(wrapper);
    }

    public ServletInputStream createInputStream() throws IOException {
        return httpRequest.createInputStream();
    }

    public void finishRequest() throws IOException {
        httpRequest.finishRequest();
    }

    public Object getNote(String name) {
        return httpRequest.getNote(name);
    }

    public Iterator getNoteNames() {
        return httpRequest.getNoteNames();
    }

    public void recycle() {
        httpRequest.recycle();
    }

    public void removeNote(String name) {
        httpRequest.removeNote(name);
    }

    public void setContentLength(int length) {
        httpRequest.setContentLength(length);
    }

    public void setContentType(String type) {
        httpRequest.setContentType(type);
    }

    public void setNote(String name, Object value) {
        httpRequest.setNote(name, value);
    }

    public void setProtocol(String protocol) {
        httpRequest.setProtocol(protocol);
    }

    public void setRemoteAddr(String remote) {
        httpRequest.setRemoteAddr(remote);
    }

    public void setScheme(String scheme) {
        httpRequest.setScheme(scheme);
    }

    public void setSecure(boolean secure) {
        httpRequest.setSecure(secure);
    }

    public void setServerName(String name) {
        httpRequest.setServerName(name);
    }

    public void setServerPort(int port) {
        httpRequest.setServerPort(port);
    }

    public void setCheckRestrictedResources(boolean check) {
        httpRequest.setCheckRestrictedResources(check);
    }

    public boolean getCheckRestrictedResources() {
        return httpRequest.getCheckRestrictedResources();
    }

    public String getJrouteId() {
        return httpRequest.getJrouteId();
    }
}