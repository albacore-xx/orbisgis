//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vJAXB 2.1.3 in JDK 1.6 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2009.04.14 at 03:50:39 PM CEST 
//


package org.orbisgis.core.renderer.legend.carto.persistence;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import org.orbisgis.core.renderer.symbol.collection.persistence.SymbolType;


/**
 * <p>Java class for proportional-legend-type complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="proportional-legend-type">
 *   &lt;complexContent>
 *     &lt;extension base="{org.orbisgis.legend}legend-type">
 *       &lt;sequence>
 *         &lt;element name="sample-symbol" type="{org.orbisgis.symbol}symbol-type"/>
 *       &lt;/sequence>
 *       &lt;attribute name="max-size" use="required" type="{http://www.w3.org/2001/XMLSchema}int" />
 *       &lt;attribute name="method" use="required" type="{http://www.w3.org/2001/XMLSchema}int" />
 *       &lt;attribute name="field-name" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "proportional-legend-type", propOrder = {
    "sampleSymbol"
})
public class ProportionalLegendType
    extends LegendType
{

    @XmlElement(name = "sample-symbol", required = true)
    protected SymbolType sampleSymbol;
    @XmlAttribute(name = "max-size", required = true)
    protected int maxSize;
    @XmlAttribute(required = true)
    protected int method;
    @XmlAttribute(name = "field-name", required = true)
    protected String fieldName;

    /**
     * Gets the value of the sampleSymbol property.
     * 
     * @return
     *     possible object is
     *     {@link SymbolType }
     *     
     */
    public SymbolType getSampleSymbol() {
        return sampleSymbol;
    }

    /**
     * Sets the value of the sampleSymbol property.
     * 
     * @param value
     *     allowed object is
     *     {@link SymbolType }
     *     
     */
    public void setSampleSymbol(SymbolType value) {
        this.sampleSymbol = value;
    }

    /**
     * Gets the value of the maxSize property.
     * 
     */
    public int getMaxSize() {
        return maxSize;
    }

    /**
     * Sets the value of the maxSize property.
     * 
     */
    public void setMaxSize(int value) {
        this.maxSize = value;
    }

    /**
     * Gets the value of the method property.
     * 
     */
    public int getMethod() {
        return method;
    }

    /**
     * Sets the value of the method property.
     * 
     */
    public void setMethod(int value) {
        this.method = value;
    }

    /**
     * Gets the value of the fieldName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFieldName() {
        return fieldName;
    }

    /**
     * Sets the value of the fieldName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFieldName(String value) {
        this.fieldName = value;
    }

}
