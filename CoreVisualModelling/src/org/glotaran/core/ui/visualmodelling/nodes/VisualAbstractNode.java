package org.glotaran.core.ui.visualmodelling.nodes;

public class VisualAbstractNode {

    private int id;
    private String name;
    private String category;

    public VisualAbstractNode(String name, String category, int id) {
        this.id = id;
        this.name = name;
        this.category = category;
    }

    public String getName() {
        return name;
    }

    public String getCategory() {
        return name;
    }

    public int getId() {
        return id;
    }
}
