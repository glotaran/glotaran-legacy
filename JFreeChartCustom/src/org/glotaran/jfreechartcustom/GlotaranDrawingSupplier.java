/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.glotaran.jfreechartcustom;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Paint;
import java.awt.Stroke;
import org.jfree.chart.plot.DefaultDrawingSupplier;

/**
 *
 * @author slapten
 */
public class GlotaranDrawingSupplier extends DefaultDrawingSupplier {

    private final static long serialVersionUID = 1L;

    public GlotaranDrawingSupplier() {
        super(new Paint[]{
                    Color.BLACK,
                    Color.RED,
                    Color.BLUE,
                    Color.GREEN,
                    Color.MAGENTA,
                    Color.CYAN,
                    Color.YELLOW,
                    new Color(0, 139, 0),
                    Color.ORANGE,
                    new Color(150, 75, 0),
                    Color.GRAY,
                    new Color(148, 0, 211),
                    new Color(64, 224, 208),
                    new Color(128, 0, 0),
                    new Color(75, 0, 130)
                },
                DEFAULT_OUTLINE_PAINT_SEQUENCE,
                DEFAULT_STROKE_SEQUENCE,
                DEFAULT_OUTLINE_STROKE_SEQUENCE,
                DEFAULT_SHAPE_SEQUENCE);
    }

    public GlotaranDrawingSupplier(boolean state) {
        super(new Paint[]{
                    Color.BLACK,
                    Color.BLACK,
                    Color.RED,
                    Color.RED,
                    Color.BLUE,
                    Color.BLUE,
                    Color.GREEN,
                    Color.GREEN,
                    Color.MAGENTA,
                    Color.MAGENTA,
                    Color.CYAN,
                    Color.CYAN,
                    Color.YELLOW,
                    Color.YELLOW,
                    new Color(0, 139, 0),
                    new Color(0, 139, 0),
                    Color.ORANGE,
                    Color.ORANGE,
                    new Color(150, 75, 0),
                    new Color(150, 75, 0),
                    Color.GRAY,
                    Color.GRAY,
                    new Color(148, 0, 211),
                    new Color(148, 0, 211),
                    new Color(64, 224, 208),
                    new Color(64, 224, 208),
                    new Color(128, 0, 0),
                    new Color(128, 0, 0),
                    new Color(75, 0, 130),
                    new Color(75, 0, 130)
                },
                DEFAULT_OUTLINE_PAINT_SEQUENCE,
                new Stroke[]{
                    new BasicStroke(1.0f),
                    new BasicStroke(1.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 3.0f, new float[]{10.0f, 10.0f}, 0.0f)},
                DEFAULT_OUTLINE_STROKE_SEQUENCE,
                DEFAULT_SHAPE_SEQUENCE);
    }
}
