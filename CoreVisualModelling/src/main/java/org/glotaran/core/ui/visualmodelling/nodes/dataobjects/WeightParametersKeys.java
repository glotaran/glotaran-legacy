/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.glotaran.core.ui.visualmodelling.nodes.dataobjects;

import java.util.ArrayList;
import java.util.List;
import org.glotaran.core.models.tgm.WeightPar;
import org.glotaran.core.ui.visualmodelling.nodes.WeighParametersSubNode;
import org.openide.nodes.Children;
import org.openide.nodes.Node;

/**
 *
 * @author lsp
 */
public class WeightParametersKeys extends Children.Keys {

    private List<WeightParameter> parameters;

    public WeightParametersKeys(int paramNum) {
        parameters = new ArrayList<WeightParameter>();
        for (int i = 0; i < paramNum; i++) {
            parameters.add(new WeightParameter());
        }
    }

    public WeightParametersKeys(List<WeightPar> weightpar) {
        parameters = new ArrayList<WeightParameter>();
        if (weightpar != null) {
            for (int i = 0; i < weightpar.size(); i++) {
                parameters.add(new WeightParameter(weightpar.get(i)));
            }
        }
    }

//    public NonLinearParametersKeys(List<KinPar> paramList){
//        parameters = new ArrayList<NonLinearParameter>();
//        if (paramList!=null){
//            for (int i = 0; i < paramList.size(); i++){
//               parameters.add(new NonLinearParameter(paramList.get(i)));
//            }
//        }
//    }
    @Override
    protected void addNotify() {
        setKeys(parameters);
    }

    @Override
    protected Node[] createNodes(Object key) {
        return new Node[]{new WeighParametersSubNode((WeightParameter) key)};
    }

    public void addObj(WeightParameter objToAdd) {
        if (parameters != null) {
            parameters.add(objToAdd);
        } else {
            parameters = new ArrayList<WeightParameter>();
            parameters.add(objToAdd);
        }
        setKeys(parameters);
    }

    public void removeParams(int num) {
//remove num last components
        if (parameters != null) {
            if (parameters.size() < num) {
                parameters.clear();
                parameters.add(new WeightParameter());
            } else {
                for (int i = 0; i < num; i++) {
                    parameters.remove(parameters.size() - 1);
                }
            }
        }
        setKeys(parameters);
    }

    public void addDefaultObj(int numObj) {
        if (parameters != null) {
            for (int i = 0; i < numObj; i++) {
                parameters.add(new WeightParameter());
            }
        } else {
            parameters = new ArrayList<WeightParameter>();
            for (int i = 0; i < numObj; i++) {
                parameters.add(new WeightParameter());
            }
        }
        setKeys(parameters);
    }

    @Override
    public boolean remove(Node[] arg0) {
        for (int i = 0; i < arg0.length; i++) {
            WeighParametersSubNode node = (WeighParametersSubNode) arg0[i];
            parameters.remove(node.getDataObj());
            setKeys(parameters);
        }
        return true;
    }

    protected void backToParams() {
        setKeys(parameters);
    }
}

