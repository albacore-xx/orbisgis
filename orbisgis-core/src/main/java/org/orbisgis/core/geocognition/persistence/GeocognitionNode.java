//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vJAXB 2.1.3 in JDK 1.6 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2009.04.25 at 03:03:22 PM CEST 
//


package org.orbisgis.core.geocognition.persistence;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element ref="{}geocognition-node" maxOccurs="unbounded"/>
 *         &lt;element ref="{}node-content"/>
 *       &lt;/sequence>
 *       &lt;attribute name="id" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="version" type="{http://www.w3.org/2001/XMLSchema}int" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "geocognitionNode",
    "nodeContent"
})
@XmlRootElement(name = "geocognition-node")
public class GeocognitionNode {

    @XmlElement(name = "geocognition-node", required = true)
    protected List<GeocognitionNode> geocognitionNode;
    @XmlElement(name = "node-content", required = true)
    protected NodeContent nodeContent;
    @XmlAttribute(required = true)
    protected String id;
    @XmlAttribute
    protected Integer version;

    /**
     * Gets the value of the geocognitionNode property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the geocognitionNode property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getGeocognitionNode().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link GeocognitionNode }
     * 
     * 
     */
    public List<GeocognitionNode> getGeocognitionNode() {
        if (geocognitionNode == null) {
            geocognitionNode = new ArrayList<GeocognitionNode>();
        }
        return this.geocognitionNode;
    }

    /**
     * Gets the value of the nodeContent property.
     * 
     * @return
     *     possible object is
     *     {@link NodeContent }
     *     
     */
    public NodeContent getNodeContent() {
        return nodeContent;
    }

    /**
     * Sets the value of the nodeContent property.
     * 
     * @param value
     *     allowed object is
     *     {@link NodeContent }
     *     
     */
    public void setNodeContent(NodeContent value) {
        this.nodeContent = value;
    }

    /**
     * Gets the value of the id property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getId() {
        return id;
    }

    /**
     * Sets the value of the id property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setId(String value) {
        this.id = value;
    }

    /**
     * Gets the value of the version property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getVersion() {
        return version;
    }

    /**
     * Sets the value of the version property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setVersion(Integer value) {
        this.version = value;
    }

}
