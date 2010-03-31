package xhl.examples.drawing;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JPanel;


public class Canvas extends JPanel {
    private final List<ColoredShape> shapes = new LinkedList<ColoredShape>();

    public Canvas() {
        super();
        setBackground(Color.WHITE);
    }

    public boolean add(ColoredShape shape) {
        return shapes.add(shape);
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setStroke(new BasicStroke(4));
        for (ColoredShape shape : shapes) {
            shape.paint(g2);
        }
    }
}
