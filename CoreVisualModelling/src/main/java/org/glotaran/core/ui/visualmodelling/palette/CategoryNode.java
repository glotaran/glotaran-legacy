/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.glotaran.core.ui.visualmodelling.palette;

import org.openide.nodes.AbstractNode;
import org.openide.util.lookup.Lookups;

public class CategoryNode extends AbstractNode {

    /** Creates a new instance of CategoryNode */
    public CategoryNode(Category category) {
        super(new PaletteChildren(category), Lookups.singleton(category));
        setDisplayName(category.getName());
    }
}
