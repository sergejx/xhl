package xhl.examples.drawing;

import xhl.core.Language;
import xhl.core.Module;
import xhl.modules.ArithmeticsModule;
import xhl.modules.DefineModule;

public class DrawingLanguage implements Language {

    private final Canvas canvas = new Canvas();

    public Canvas getCanvas() {
        return canvas;
    }

    @Override
    public Module[] getModules() {
        Module arithm = new ArithmeticsModule();
        Module def = new DefineModule();
        Module draw = new DrawingModule(canvas);
        return new Module[] { arithm, def, draw };
    }
}
