//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vhudson-jaxb-ri-2.2-147 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2011.06.04 at 11:14:03 PM CEST 
//


package org.glotaran.core.models.tgm;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for WeightParPanelModel complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="WeightParPanelModel">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="weightpar" type="{http://www.nat.vu.nl/~jsnel/Schema/TgmSchema}WeightPar" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="poisson" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "WeightParPanelModel", propOrder = {
    "weightpar",
    "poisson"
})
public class WeightParPanelModel {

    @XmlElement(nillable = true)
    protected List<WeightPar> weightpar;
    @XmlElement(required = true, type = Boolean.class, defaultValue = "false", nillable = true)
    protected Boolean poisson;

    /**
     * Gets the value of the weightpar property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the weightpar property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getWeightpar().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link WeightPar }
     * 
     * 
     */
    public List<WeightPar> getWeightpar() {
        if (weightpar == null) {
            weightpar = new ArrayList<WeightPar>();
        }
        return this.weightpar;
    }

    /**
     * Gets the value of the poisson property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isPoisson() {
        return poisson;
    }

    /**
     * Sets the value of the poisson property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setPoisson(Boolean value) {
        this.poisson = value;
    }

}
