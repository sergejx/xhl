package sk.tuke.xhl.examples.drawing;

import java.io.IOException;

import javax.swing.JFrame;

import sk.tuke.xhl.core.LanguageProcessor;

public class Main extends JFrame {

    private final DrawingModule module;

    public Main() {
        super("XHL drawing");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(450, 350);
        module = new DrawingModule();
        add(module.getCanvas());
    }

    private void run(String filename) {
        LanguageProcessor.execute(module, filename);
        setVisible(true);
    }

    public static void main(String[] args) throws IOException {
        if (args.length >= 1)
            new Main().run(args[0]);
        else
            System.out.println("Give file name as program argument!");
    }

}
