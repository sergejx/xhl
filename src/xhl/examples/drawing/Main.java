package xhl.examples.drawing;

import java.io.FileNotFoundException;
import java.io.IOException;

import javax.swing.JFrame;

public class Main extends JFrame {

    private final DrawingLanguage language;

    public Main() {
        super("XHL drawing");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(450, 350);
        language = new DrawingLanguage();
        add(language.getCanvas());
    }

    private void run(String filename) throws IOException, FileNotFoundException {
        language.process(filename);
        setVisible(true);
    }

    public static void main(String[] args) throws FileNotFoundException,
            IOException {
        if (args.length >= 1)
            new Main().run(args[0]);
        else
            System.out.println("Give file name as program argument!");
    }

}
