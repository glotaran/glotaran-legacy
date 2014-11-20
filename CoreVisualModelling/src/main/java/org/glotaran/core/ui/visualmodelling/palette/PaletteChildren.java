package org.glotaran.core.ui.visualmodelling.palette;

/**
 *
 * @author jsg210
 */
import java.util.ArrayList;
import org.openide.nodes.Index;
import org.openide.nodes.Node;
import org.openide.util.Utilities;

public class PaletteChildren extends Index.ArrayChildren {

    private Category category;
    private String[][] items = new String[][]{
        {"0", "Containers", "org/glotaran/core/ui/visualmodelling/resources/Dataset_container_48.png", "Dataset Container"},
        {"1", "Containers", "org/glotaran/core/ui/visualmodelling/resources/Model-icon-48.png", "Model"},
        {"2", "Containers", "org/glotaran/core/ui/visualmodelling/resources/Simulation_cont_48.png", "Simulation Input"},
        {"0", "Modelling", "org/glotaran/core/ui/visualmodelling/resources/Kinpar_32.png", "Kinetic Parameters"},
        {"1", "Modelling", "org/glotaran/core/ui/visualmodelling/resources/IRFpar_32.png", "IRF Parameters"},
        {"2", "Modelling", "org/glotaran/core/ui/visualmodelling/resources/Dispersion_32.png", "Dispersion"},
        {"3", "Modelling", "org/glotaran/core/ui/visualmodelling/resources/Weightpar_32.png", "Weight Parameters"},
        {"4", "Modelling", "org/glotaran/core/ui/visualmodelling/resources/Cohspecpar_32.png", "Cohspec Parameters"},
        {"5", "Modelling", "org/glotaran/core/ui/visualmodelling/resources/Cohspecpar_32.png", "Oscspec Parameters"},
        {"6", "Modelling", "org/glotaran/core/ui/visualmodelling/resources/Kmatr_32.png", "KMatrix"},
        {"0", "Model differences", "org/glotaran/core/ui/visualmodelling/resources/FreeParam_32.png", "FreeParam"},
        {"1", "Model differences", "org/glotaran/core/ui/visualmodelling/resources/ChageParam_32.png", "ChangeParam"},
        {"2", "Model differences", "org/glotaran/core/ui/visualmodelling/resources/AddParam_32.png", "AddParam"},
        {"3", "Model differences", "org/glotaran/core/ui/visualmodelling/resources/RemoveParam_32.png", "RemoveParam"},
        {"3", "Containers", "org/glotaran/core/ui/visualmodelling/resources/Output_cont_48.png", "Output Container"},};

    public PaletteChildren(Category Category) {
        this.category = Category;
    }

    @Override
    protected java.util.List<Node> initCollection() {
        ArrayList childrenNodes = new ArrayList(items.length);
        for (int i = 0; i < items.length; i++) {
            if (category.getName().equals(items[i][1])) {
                PaletteItem item = new PaletteItem();
                item.setNumber(new Integer(items[i][0]));
                item.setCategory(items[i][1]);
                item.setImage(Utilities.loadImage(items[i][2]));
                item.setImageLocation(items[i][2]);
                item.setName(items[i][3]);
                PaletteNode pn = new PaletteNode(item);
                pn.setDisplayName(item.getName());
                childrenNodes.add(pn);
            }
        }
        return childrenNodes;
    }
}
