package xhl.examples.entity;

import xhl.core.Language;
import xhl.core.LanguageProcessor;
import xhl.core.Module;
import xhl.modules.LogicsModule;
import xhl.modules.RelationsModule;

public class EntityLanguage implements Language {

    private static final EntityModule langModule = new EntityModule();
    private static final Module relModule = new RelationsModule();
    private static final Module logModule = new LogicsModule();

    @Override
    public Module[] getModules() {
        return new xhl.core.Module[] { langModule, relModule, logModule };
    }

    public xhl.examples.entity.Module getModule() {
        return langModule.getModule();
    }

    public static void main(String[] args) {
        if (args.length >= 1) {
            String filename = args[0];
            EntityLanguage lang = new EntityLanguage();
            LanguageProcessor.execute(lang, filename);
            xhl.examples.entity.Module m = lang.getModule();
            System.out.println(m);
            m.generate();
        } else
            System.out.println("Give file name as program argument!");
    }
}
