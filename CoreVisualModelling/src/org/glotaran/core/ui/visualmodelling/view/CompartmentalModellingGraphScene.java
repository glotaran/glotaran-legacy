/*
 * The contents of this file are subject to the terms of the Common Development
 * and Distribution License (the License). You may not use this file except in
 * compliance with the License.
 *
 * You can obtain a copy of the License at http://www.netbeans.org/cddl.html
 * or http://www.netbeans.org/cddl.txt.
 *
 * When distributing Covered Code, include this CDDL Header Notice in each file
 * and include the License file at http://www.netbeans.org/cddl.txt.
 * If applicable, add the following below the CDDL Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2007 Sun
 * Microsystems, Inc. All Rights Reserved.
 */
package org.glotaran.core.ui.visualmodelling.view;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import org.glotaran.core.models.tgm.Tgm;
import org.glotaran.core.ui.visualmodelling.menu.SceneMainMenu;
import org.glotaran.gtafilesupport.GtaDataObject;
import org.glotaran.tgmfilesupport.TgmDataObject;
import org.netbeans.api.visual.action.ActionFactory;
import org.netbeans.api.visual.action.WidgetAction;
import org.netbeans.api.visual.graph.GraphScene;
import org.netbeans.api.visual.router.Router;
import org.netbeans.api.visual.router.RouterFactory;
import org.netbeans.api.visual.widget.LayerWidget;
import org.netbeans.api.visual.widget.Widget;
import org.openide.util.ImageUtilities;

/**
 * @author Alex
 */
public class CompartmentalModellingGraphScene extends GraphScene<Object, Object> implements PropertyChangeListener {

    private LayerWidget mainLayer = new LayerWidget(this);
    private LayerWidget connectionLayer = new LayerWidget(this);
    private LayerWidget interractionLayer = new LayerWidget(this);
    private LayerWidget backgroundLayer = new LayerWidget(this);
    private Router router = RouterFactory.createFreeRouter();  
    private WidgetAction moveControlPointAction = ActionFactory.createFreeMoveControlPointAction();  
    private WidgetAction resizeAction = ActionFactory.createAlignWithResizeAction(mainLayer, interractionLayer, null);
    private WidgetAction moveAction;    
    private GtaDataObject dobj = null;

    public CompartmentalModellingGraphScene() {
        addChild(mainLayer);
        addChild(connectionLayer);
        addChild(interractionLayer);
        getActions().addAction(ActionFactory.createRectangularSelectAction(this, backgroundLayer));
        getActions().addAction(ActionFactory.createPopupMenuAction(new SceneMainMenu(this)));        
        setToolTipText("Drag components from the palette onto this design pane");
        initGrids();        
    }

    public CompartmentalModellingGraphScene(TgmDataObject tgmDObj) {
        addChild(mainLayer);
        addChild(connectionLayer);
        addChild(interractionLayer);
        getActions().addAction(ActionFactory.createRectangularSelectAction(this, backgroundLayer));
        getActions().addAction(ActionFactory.createPopupMenuAction(new SceneMainMenu(this)));
        setToolTipText("Drag components from the palette onto this design pane");
        initGrids();
    }

    public GtaDataObject getDobj() {
        return dobj;
    }

    public void setDobj(GtaDataObject dobj) {
        this.dobj = dobj;
    }


    public final void initGrids() {
        Image sourceImage = ImageUtilities.loadImage("org/glotaran/core/ui/visualmodelling/resources/paper_grid17.png"); // NOI18N
        int width = sourceImage.getWidth(null);
        int height = sourceImage.getHeight(null);
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics = image.createGraphics();
        graphics.drawImage(sourceImage, 0, 0, null);
        graphics.dispose();
        TexturePaint PAINT_BACKGROUND = new TexturePaint(image, new Rectangle(0, 0, width, height));
        setBackground(PAINT_BACKGROUND);
        repaint();
        revalidate(false);
        validate();
    }

    public WidgetAction getMoveAction() {
        return moveAction;
    }

    public WidgetAction getResizeAction() {
        return resizeAction;
    }

    public WidgetAction getMoveControlPointAction() {
        return moveControlPointAction;
    }

    @Override
    protected Widget attachNodeWidget(Object node) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    protected Widget attachEdgeWidget(Object edge) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    protected void attachEdgeSourceAnchor(Object edge, Object oldSourceNode, Object sourceNode) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    protected void attachEdgeTargetAnchor(Object edge, Object oldTargetNode, Object targetNode) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}

