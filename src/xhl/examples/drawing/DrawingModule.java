package xhl.examples.drawing;

import java.awt.Color;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RectangularShape;

import xhl.core.GenericModule;

public class DrawingModule extends GenericModule {

    private final Canvas canvas;
    private Color color = Color.BLACK;

    public DrawingModule(Canvas canvas) {
        this.canvas = canvas;
    }

    @Element
    public Color rgb(double r, double g, double b) {
        return new Color((int) r, (int) g, (int) b);
    }

    @Element
    public void setcolor(Color color) {
        this.color = color;
    }

    @Element
    public RectangularShape rectangle(double width, double height) {
        return new Rectangle2D.Double(0, 0, width, height);
    }

    @Element
    public RectangularShape ellipse(double width, double height) {
        return new Ellipse2D.Double(0, 0, width, height);
    }

    @Element
    public void draw(double x, double y, RectangularShape shape) {
        ColoredShape cs = new ColoredShape(shape, color);
        cs.setLocation(x, y);
        canvas.add(cs);
    }
}
