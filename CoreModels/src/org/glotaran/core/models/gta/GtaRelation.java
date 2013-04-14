//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vhudson-jaxb-ri-2.2-147 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2010.04.09 at 03:30:53 PM CEST 
//


package org.glotaran.core.models.gta;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for GtaRelation complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="GtaRelation">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="what1" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="what2" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="index1" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="index2" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="dataset1" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="dataset2" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="relation" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="start" type="{http://www.w3.org/2001/XMLSchema}double"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "GtaRelation", propOrder = {
    "what1",
    "what2",
    "index1",
    "index2",
    "dataset1",
    "dataset2",
    "relation",
    "start"
})
public class GtaRelation {

    @XmlElement(required = true)
    protected String what1;
    @XmlElement(required = true)
    protected String what2;
    protected int index1;
    protected int index2;
    protected int dataset1;
    protected int dataset2;
    @XmlElement(required = true, nillable = true)
    protected String relation;
    @XmlElement(required = true, type = Double.class, nillable = true)
    protected Double start;

    /**
     * Gets the value of the what1 property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getWhat1() {
        return what1;
    }

    /**
     * Sets the value of the what1 property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setWhat1(String value) {
        this.what1 = value;
    }

    /**
     * Gets the value of the what2 property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getWhat2() {
        return what2;
    }

    /**
     * Sets the value of the what2 property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setWhat2(String value) {
        this.what2 = value;
    }

    /**
     * Gets the value of the index1 property.
     * 
     */
    public int getIndex1() {
        return index1;
    }

    /**
     * Sets the value of the index1 property.
     * 
     */
    public void setIndex1(int value) {
        this.index1 = value;
    }

    /**
     * Gets the value of the index2 property.
     * 
     */
    public int getIndex2() {
        return index2;
    }

    /**
     * Sets the value of the index2 property.
     * 
     */
    public void setIndex2(int value) {
        this.index2 = value;
    }

    /**
     * Gets the value of the dataset1 property.
     * 
     */
    public int getDataset1() {
        return dataset1;
    }

    /**
     * Sets the value of the dataset1 property.
     * 
     */
    public void setDataset1(int value) {
        this.dataset1 = value;
    }

    /**
     * Gets the value of the dataset2 property.
     * 
     */
    public int getDataset2() {
        return dataset2;
    }

    /**
     * Sets the value of the dataset2 property.
     * 
     */
    public void setDataset2(int value) {
        this.dataset2 = value;
    }

    /**
     * Gets the value of the relation property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRelation() {
        return relation;
    }

    /**
     * Sets the value of the relation property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRelation(String value) {
        this.relation = value;
    }

    /**
     * Gets the value of the start property.
     * 
     * @return
     *     possible object is
     *     {@link Double }
     *     
     */
    public Double getStart() {
        return start;
    }

    /**
     * Sets the value of the start property.
     * 
     * @param value
     *     allowed object is
     *     {@link Double }
     *     
     */
    public void setStart(Double value) {
        this.start = value;
    }

}
