package sk.tuke.xhl.examples.drawing;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RectangularShape;

public class ColoredShape {
    private final RectangularShape shape;
    private final Color color;

    public ColoredShape(RectangularShape shape, Color color) {
        this.shape = (RectangularShape) shape.clone();
        this.color = color;
    }

    public void setLocation(double x, double y) {
        Rectangle2D old = shape.getFrame();
        shape.setFrame(x, y, old.getWidth(), old.getHeight());
    }

    public void paint(Graphics2D g) {
        Color oldColor = g.getColor();
        g.setColor(color);
        g.draw(shape);
        g.setColor(oldColor);
    }
}
