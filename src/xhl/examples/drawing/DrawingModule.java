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

    @Function
    public Color rgb(double r, double g, double b) {
        return new Color((int) r, (int) g, (int) b);
    }

    @Function
    public void setcolor(Color color) {
        this.color = color;
    }

    @Function
    public RectangularShape rectangle(double width, double height) {
        return new Rectangle2D.Double(0, 0, width, height);
    }

    @Function
    public RectangularShape ellipse(double width, double height) {
        return new Ellipse2D.Double(0, 0, width, height);
    }

    @Function
    public void draw(double x, double y, RectangularShape shape) {
        ColoredShape cs = new ColoredShape(shape, color);
        cs.setLocation(x, y);
        canvas.add(cs);
    }
}
