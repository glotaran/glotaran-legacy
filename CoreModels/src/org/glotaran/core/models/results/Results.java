//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vhudson-jaxb-ri-2.2-147 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2010.03.11 at 04:25:06 PM CET 
//
package org.glotaran.core.models.results;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
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
 *         &lt;element name="datasets" type="{http://glotaran.org/schema/AnalysisResultsSchema}Dataset" maxOccurs="unbounded"/>
 *         &lt;element name="summary" type="{http://glotaran.org/schema/AnalysisResultsSchema}Summary"/>
 *         &lt;element name="datasetRelations" type="{http://glotaran.org/schema/AnalysisResultsSchema}DatasetRelation" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "datasets",
    "summary",
    "datasetRelations"
})
@XmlRootElement(name = "results")
public class Results {

    @XmlElement(required = true, nillable = true)
    protected List<Dataset> datasets;
    @XmlElement(required = true)
    protected Summary summary;
    @XmlElement(nillable = true)
    protected List<DatasetRelation> datasetRelations;

    /**
     * Gets the value of the datasets property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the datasets property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getDatasets().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Dataset }
     * 
     * 
     */
    public List<Dataset> getDatasets() {
        if (datasets == null) {
            datasets = new ArrayList<Dataset>();
        }
        return this.datasets;
    }

    /**
     * Gets the value of the summary property.
     * 
     * @return
     *     possible object is
     *     {@link Summary }
     *     
     */
    public Summary getSummary() {
        return summary;
    }

    /**
     * Sets the value of the summary property.
     * 
     * @param value
     *     allowed object is
     *     {@link Summary }
     *     
     */
    public void setSummary(Summary value) {
        this.summary = value;
    }

    /**
     * Gets the value of the datasetRelations property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the datasetRelations property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getDatasetRelations().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link DatasetRelation }
     * 
     * 
     */
    public List<DatasetRelation> getDatasetRelations() {
        if (datasetRelations == null) {
            datasetRelations = new ArrayList<DatasetRelation>();
        }
        return this.datasetRelations;
    }
}
