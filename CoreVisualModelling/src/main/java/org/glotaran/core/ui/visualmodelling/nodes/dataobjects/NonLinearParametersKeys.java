/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.glotaran.core.ui.visualmodelling.nodes.dataobjects;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.glotaran.core.models.tgm.KinPar;
import org.glotaran.core.ui.visualmodelling.nodes.CohspecParametersSubNode;
import org.glotaran.core.ui.visualmodelling.nodes.ParametersSubNode;
import org.openide.nodes.Children;
import org.openide.nodes.Node;

/**
 *
 * @author slapten
 */
public class NonLinearParametersKeys extends Children.Keys {

    private List<NonLinearParameter> parameters;
    private String nodeType;

    public NonLinearParametersKeys(int paramNum) {
        nodeType = "nonlinearParameter";
        parameters = new ArrayList<NonLinearParameter>();
        for (int i = 0; i < paramNum; i++) {
            parameters.add(new NonLinearParameter());
        }
    }

    public NonLinearParametersKeys(List<KinPar> paramList) {
        nodeType = "nonlinearParameter";
        parameters = new ArrayList<NonLinearParameter>();
        if (paramList != null) {
            for (int i = 0; i < paramList.size(); i++) {
                parameters.add(new NonLinearParameter(paramList.get(i)));
            }
        }
    }
    
    public NonLinearParametersKeys(List<KinPar> paramList, String nodetype) {
        nodeType = nodetype;
        parameters = new ArrayList<NonLinearParameter>();
        if (paramList != null) {
            for (int i = 0; i < paramList.size(); i++) {
                parameters.add(new NonLinearParameter(paramList.get(i)));
            }
        }
    }

    @Override
    protected void addNotify() {
        setKeys(parameters);
    }

    @Override
    protected Node[] createNodes(Object key) {
        if (nodeType.equalsIgnoreCase("XPMCohSpecNode")) {
            return new Node[]{new CohspecParametersSubNode((NonLinearParameter) key, "XPM")};
        } else {
            return new Node[]{new ParametersSubNode((NonLinearParameter) key)};
        }
    }
    
    
    public void addObj(NonLinearParameter objToAdd){
        addObj(objToAdd,"nonlinearParameter");
    }

    public void addObj(NonLinearParameter objToAdd, String nodetype) {
        nodeType = nodetype;
        if (parameters != null) {
            parameters.add(objToAdd);
        } else {
            parameters = new ArrayList<NonLinearParameter>();
            parameters.add(objToAdd);
        }
        setKeys(parameters);
    }

    public void removeParams(int num) {
//remove num last components
        if (parameters != null) {
            if (parameters.size() < num) {
                parameters.clear();
                parameters.add(new NonLinearParameter());
            } else {
                for (int i = 0; i < num; i++) {
                    parameters.remove(parameters.size() - 1);
                }
            }
        }
        setKeys(parameters);
    }

    public void addDefaultObj(int numObj){
        addDefaultObj(numObj, "nonlinearParameter");
    }
            
    public void addDefaultObj(int numObj, String nodetype) {
        nodeType = nodetype;
        if (parameters != null) {
            for (int i = 0; i < numObj; i++) {
                parameters.add(new NonLinearParameter());
            }
        } else {
            parameters = new ArrayList<NonLinearParameter>();
            for (int i = 0; i < numObj; i++) {
                parameters.add(new NonLinearParameter());
            }
        }
        setKeys(parameters);
    }

    @Override
    public boolean remove(Node[] arg0) {
        for (int i = 0; i < arg0.length; i++) {
            ParametersSubNode node = (ParametersSubNode) arg0[i];
            parameters.remove(node.getDataObj());
            setKeys(parameters);
        }
        return true;
    }

    protected void backToParams() {
        setKeys(parameters);
    }    
    
    public void sortParams() {
        Collections.sort(parameters);                   
        setKeys(parameters);
        for (NonLinearParameter nlp : parameters) {            
            nlp.fire("start", null, nlp.getStart());
            nlp.fire("fixed", null, nlp.isFixed());            
        }
    }
}
