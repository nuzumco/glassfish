/*
 * The contents of this file are subject to the terms 
 * of the Common Development and Distribution License 
 * (the License).  You may not use this file except in
 * compliance with the License.
 * 
 * You can obtain a copy of the license at 
 * https://glassfish.dev.java.net/public/CDDLv1.0.html or
 * glassfish/bootstrap/legal/CDDLv1.0.txt.
 * See the License for the specific language governing 
 * permissions and limitations under the License.
 * 
 * When distributing Covered Code, include this CDDL 
 * Header Notice in each file and include the License file 
 * at glassfish/bootstrap/legal/CDDLv1.0.txt.  
 * If applicable, add the following below the CDDL Header, 
 * with the fields enclosed by brackets [] replaced by
 * you own identifying information: 
 * "Portions Copyrighted [year] [name of copyright owner]"
 * 
 * Copyright 2006 Sun Microsystems, Inc. All rights reserved.
 */
 
/**
 *	This generated bean class EjbTimerService matches the DTD element ejb-timer-service
 *
 */

package com.sun.enterprise.config.serverbeans;

import org.w3c.dom.*;
import org.netbeans.modules.schema2beans.*;
import java.beans.*;
import java.util.*;
import java.io.Serializable;
import com.sun.enterprise.config.ConfigBean;
import com.sun.enterprise.config.ConfigException;
import com.sun.enterprise.config.StaleWriteConfigException;
import com.sun.enterprise.util.i18n.StringManager;

// BEGIN_NOI18N

public class EjbTimerService extends ConfigBean implements Serializable
{

	static Vector comparators = new Vector();
	private static final org.netbeans.modules.schema2beans.Version runtimeVersion = new org.netbeans.modules.schema2beans.Version(4, 2, 0);

	static public final String ELEMENT_PROPERTY = "ElementProperty";

	public EjbTimerService() {
		this(Common.USE_DEFAULT_VALUES);
	}

	public EjbTimerService(int options)
	{
		super(comparators, runtimeVersion);
		// Properties (see root bean comments for the bean graph)
		initPropertyTables(1);
		this.createProperty("property", ELEMENT_PROPERTY, 
			Common.TYPE_0_N | Common.TYPE_BEAN | Common.TYPE_KEY, 
			ElementProperty.class);
		this.createAttribute(ELEMENT_PROPERTY, "name", "Name", 
						AttrProp.CDATA | AttrProp.REQUIRED,
						null, null);
		this.createAttribute(ELEMENT_PROPERTY, "value", "Value", 
						AttrProp.CDATA | AttrProp.REQUIRED,
						null, null);
		this.initialize(options);
	}

	// Setting the default values of the properties
	void initialize(int options) {

	}

	// Get Method
	public ElementProperty getElementProperty(int index) {
		return (ElementProperty)this.getValue(ELEMENT_PROPERTY, index);
	}

	// This attribute is an array, possibly empty
	public void setElementProperty(ElementProperty[] value) {
		this.setValue(ELEMENT_PROPERTY, value);
	}

	// Getter Method
	public ElementProperty[] getElementProperty() {
		return (ElementProperty[])this.getValues(ELEMENT_PROPERTY);
	}

	// Return the number of properties
	public int sizeElementProperty() {
		return this.size(ELEMENT_PROPERTY);
	}

	// Add a new element returning its index in the list
	public int addElementProperty(ElementProperty value)
			throws ConfigException{
		return addElementProperty(value, true);
	}

	// Add a new element returning its index in the list with a boolean flag
	public int addElementProperty(ElementProperty value, boolean overwrite)
			throws ConfigException{
		ElementProperty old = getElementPropertyByName(value.getName());
		if(old != null) {
			throw new ConfigException(StringManager.getManager(EjbTimerService.class).getString("cannotAddDuplicate",  "ElementProperty"));
		}
		return this.addValue(ELEMENT_PROPERTY, value, overwrite);
	}

	//
	// Remove an element using its reference
	// Returns the index the element had in the list
	//
	public int removeElementProperty(ElementProperty value){
		return this.removeValue(ELEMENT_PROPERTY, value);
	}

	//
	// Remove an element using its reference
	// Returns the index the element had in the list
	// with boolean overwrite
	//
	public int removeElementProperty(ElementProperty value, boolean overwrite)
			throws StaleWriteConfigException{
		return this.removeValue(ELEMENT_PROPERTY, value, overwrite);
	}

	public ElementProperty getElementPropertyByName(String id) {
	 if (null != id) { id = id.trim(); }
	ElementProperty[] o = getElementProperty();
	 if (o == null) return null;

	 for (int i=0; i < o.length; i++) {
	     if(o[i].getAttributeValue(Common.convertName(ServerTags.NAME)).equals(id)) {
	         return o[i];
	     }
	 }

		return null;
		
	}
	/**
	* Getter for MinimumDeliveryIntervalInMillis of the Element ejb-timer-service
	* @return  the MinimumDeliveryIntervalInMillis of the Element ejb-timer-service
	*/
	public String getMinimumDeliveryIntervalInMillis() {
		return getAttributeValue(ServerTags.MINIMUM_DELIVERY_INTERVAL_IN_MILLIS);
	}
	/**
	* Modify  the MinimumDeliveryIntervalInMillis of the Element ejb-timer-service
	* @param v the new value
	* @throws StaleWriteConfigException if overwrite is false and file changed on disk
	*/
	public void setMinimumDeliveryIntervalInMillis(String v, boolean overwrite) throws StaleWriteConfigException {
		setAttributeValue(ServerTags.MINIMUM_DELIVERY_INTERVAL_IN_MILLIS, v, overwrite);
	}
	/**
	* Modify  the MinimumDeliveryIntervalInMillis of the Element ejb-timer-service
	* @param v the new value
	*/
	public void setMinimumDeliveryIntervalInMillis(String v) {
		setAttributeValue(ServerTags.MINIMUM_DELIVERY_INTERVAL_IN_MILLIS, v);
	}
	/**
	* Get the default value of MinimumDeliveryIntervalInMillis from dtd
	*/
	public static String getDefaultMinimumDeliveryIntervalInMillis() {
		return "7000".trim();
	}
	/**
	* Getter for MaxRedeliveries of the Element ejb-timer-service
	* @return  the MaxRedeliveries of the Element ejb-timer-service
	*/
	public String getMaxRedeliveries() {
		return getAttributeValue(ServerTags.MAX_REDELIVERIES);
	}
	/**
	* Modify  the MaxRedeliveries of the Element ejb-timer-service
	* @param v the new value
	* @throws StaleWriteConfigException if overwrite is false and file changed on disk
	*/
	public void setMaxRedeliveries(String v, boolean overwrite) throws StaleWriteConfigException {
		setAttributeValue(ServerTags.MAX_REDELIVERIES, v, overwrite);
	}
	/**
	* Modify  the MaxRedeliveries of the Element ejb-timer-service
	* @param v the new value
	*/
	public void setMaxRedeliveries(String v) {
		setAttributeValue(ServerTags.MAX_REDELIVERIES, v);
	}
	/**
	* Get the default value of MaxRedeliveries from dtd
	*/
	public static String getDefaultMaxRedeliveries() {
		return "1".trim();
	}
	/**
	* Getter for TimerDatasource of the Element ejb-timer-service
	* @return  the TimerDatasource of the Element ejb-timer-service
	*/
	public String getTimerDatasource() {
			return getAttributeValue(ServerTags.TIMER_DATASOURCE);
	}
	/**
	* Modify  the TimerDatasource of the Element ejb-timer-service
	* @param v the new value
	* @throws StaleWriteConfigException if overwrite is false and file changed on disk
	*/
	public void setTimerDatasource(String v, boolean overwrite) throws StaleWriteConfigException {
		setAttributeValue(ServerTags.TIMER_DATASOURCE, v, overwrite);
	}
	/**
	* Modify  the TimerDatasource of the Element ejb-timer-service
	* @param v the new value
	*/
	public void setTimerDatasource(String v) {
		setAttributeValue(ServerTags.TIMER_DATASOURCE, v);
	}
	/**
	* Getter for RedeliveryIntervalInternalInMillis of the Element ejb-timer-service
	* @return  the RedeliveryIntervalInternalInMillis of the Element ejb-timer-service
	*/
	public String getRedeliveryIntervalInternalInMillis() {
		return getAttributeValue(ServerTags.REDELIVERY_INTERVAL_INTERNAL_IN_MILLIS);
	}
	/**
	* Modify  the RedeliveryIntervalInternalInMillis of the Element ejb-timer-service
	* @param v the new value
	* @throws StaleWriteConfigException if overwrite is false and file changed on disk
	*/
	public void setRedeliveryIntervalInternalInMillis(String v, boolean overwrite) throws StaleWriteConfigException {
		setAttributeValue(ServerTags.REDELIVERY_INTERVAL_INTERNAL_IN_MILLIS, v, overwrite);
	}
	/**
	* Modify  the RedeliveryIntervalInternalInMillis of the Element ejb-timer-service
	* @param v the new value
	*/
	public void setRedeliveryIntervalInternalInMillis(String v) {
		setAttributeValue(ServerTags.REDELIVERY_INTERVAL_INTERNAL_IN_MILLIS, v);
	}
	/**
	* Get the default value of RedeliveryIntervalInternalInMillis from dtd
	*/
	public static String getDefaultRedeliveryIntervalInternalInMillis() {
		return "5000".trim();
	}
	/**
	 * Create a new bean using it's default constructor.
	 * This does not add it to any bean graph.
	 */
	public ElementProperty newElementProperty() {
		return new ElementProperty();
	}

	/**
	* get the xpath representation for this element
	* returns something like abc[@name='value'] or abc
	* depending on the type of the bean
	*/
	protected String getRelativeXPath() {
	    String ret = null;
	    ret = "ejb-timer-service";
	    return (null != ret ? ret.trim() : null);
	}

	/*
	* generic method to get default value from dtd
	*/
	public static String getDefaultAttributeValue(String attr) {
		if(attr == null) return null;
		attr = attr.trim();
		if(attr.equals(ServerTags.MINIMUM_DELIVERY_INTERVAL_IN_MILLIS)) return "7000".trim();
		if(attr.equals(ServerTags.MAX_REDELIVERIES)) return "1".trim();
		if(attr.equals(ServerTags.REDELIVERY_INTERVAL_INTERNAL_IN_MILLIS)) return "5000".trim();
	return null;
	}
	//
	public static void addComparator(org.netbeans.modules.schema2beans.BeanComparator c) {
		comparators.add(c);
	}

	//
	public static void removeComparator(org.netbeans.modules.schema2beans.BeanComparator c) {
		comparators.remove(c);
	}
	public void validate() throws org.netbeans.modules.schema2beans.ValidateException {
	}

	// Dump the content of this bean returning it as a String
	public void dump(StringBuffer str, String indent){
		String s;
		Object o;
		org.netbeans.modules.schema2beans.BaseBean n;
		str.append(indent);
		str.append("ElementProperty["+this.sizeElementProperty()+"]");	// NOI18N
		for(int i=0; i<this.sizeElementProperty(); i++)
		{
			str.append(indent+"\t");
			str.append("#"+i+":");
			n = (org.netbeans.modules.schema2beans.BaseBean) this.getElementProperty(i);
			if (n != null)
				n.dump(str, indent + "\t");	// NOI18N
			else
				str.append(indent+"\tnull");	// NOI18N
			this.dumpAttributes(ELEMENT_PROPERTY, i, str, indent);
		}

	}
	public String dumpBeanNode(){
		StringBuffer str = new StringBuffer();
		str.append("EjbTimerService\n");	// NOI18N
		this.dump(str, "\n  ");	// NOI18N
		return str.toString();
	}}

// END_NOI18N
