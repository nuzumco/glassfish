/*
 * The contents of this file are subject to the terms 
 * of the Common Development and Distribution License 
 * (the "License").  You may not use this file except 
 * in compliance with the License.
 * 
 * You can obtain a copy of the license at 
 * glassfish/bootstrap/legal/CDDLv1.0.txt or 
 * https://glassfish.dev.java.net/public/CDDLv1.0.html. 
 * See the License for the specific language governing 
 * permissions and limitations under the License.
 * 
 * When distributing Covered Code, include this CDDL 
 * HEADER in each file and include the License file at 
 * glassfish/bootstrap/legal/CDDLv1.0.txt.  If applicable, 
 * add the following below this CDDL HEADER, with the 
 * fields enclosed by brackets "[]" replaced with your 
 * own identifying information: Portions Copyright [yyyy] 
 * [name of copyright owner]
 */

//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vhudson-jaxb-1973 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2005.04.20 at 08:27:00 IST 
//


package com.sun.persistence.api.deployment;

import javax.xml.bind.annotation.AccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import com.sun.persistence.api.deployment.Adapter1;
import com.sun.persistence.api.deployment.DescriptorNode;

@XmlAccessorType(value = AccessType.FIELD)
@XmlType(name = "discriminator-column", namespace = "http://java.sun.com/xml/ns/persistence_ORM")
public class DiscriminatorColumnDescriptor
    extends DescriptorNode
{

    @XmlElement(defaultValue = "", name = "name", namespace = "http://java.sun.com/xml/ns/persistence_ORM", type = String.class)
    protected String name;
    @XmlElement(defaultValue = "false", name = "nullable", namespace = "http://java.sun.com/xml/ns/persistence_ORM", type = Boolean.class)
    protected Boolean nullable;
    @XmlElement(defaultValue = "", name = "column-definition", namespace = "http://java.sun.com/xml/ns/persistence_ORM", type = String.class)
    protected String columnDefinition;
    @XmlElement(defaultValue = "10", name = "length", namespace = "http://java.sun.com/xml/ns/persistence_ORM", type = String.class)
    @XmlJavaTypeAdapter(value = Adapter1 .class)
    protected Integer length;

    /**
     * Gets the value of the name property.
     * 
     * @return
     *     possible object is
     *     {@link java.lang.String}
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the value of the name property.
     * 
     * @param value
     *     allowed object is
     *     {@link java.lang.String}
     */
    public void setName(String value) {
        this.name = value;
    }

    public boolean isSetName() {
        return (this.name!= null);
    }

    public void unsetName() {
        this.name = null;
    }

    /**
     * Gets the value of the nullable property.
     * 
     * @return
     *     possible object is
     *     {@link java.lang.Boolean}
     */
    public Boolean isNullable() {
        return nullable;
    }

    /**
     * Sets the value of the nullable property.
     * 
     * @param value
     *     allowed object is
     *     {@link java.lang.Boolean}
     */
    public void setNullable(Boolean value) {
        this.nullable = value;
    }

    public boolean isSetNullable() {
        return (this.nullable!= null);
    }

    public void unsetNullable() {
        this.nullable = null;
    }

    /**
     * Gets the value of the columnDefinition property.
     * 
     * @return
     *     possible object is
     *     {@link java.lang.String}
     */
    public String getColumnDefinition() {
        return columnDefinition;
    }

    /**
     * Sets the value of the columnDefinition property.
     * 
     * @param value
     *     allowed object is
     *     {@link java.lang.String}
     */
    public void setColumnDefinition(String value) {
        this.columnDefinition = value;
    }

    public boolean isSetColumnDefinition() {
        return (this.columnDefinition!= null);
    }

    public void unsetColumnDefinition() {
        this.columnDefinition = null;
    }

    /**
     * Gets the value of the length property.
     * 
     * @return
     *     possible object is
     *     {@link java.lang.String}
     */
    public Integer getLength() {
        return length;
    }

    /**
     * Sets the value of the length property.
     * 
     * @param value
     *     allowed object is
     *     {@link java.lang.String}
     */
    public void setLength(Integer value) {
        this.length = value;
    }

    public boolean isSetLength() {
        return (this.length!= null);
    }

    public void unsetLength() {
        this.length = null;
    }

}