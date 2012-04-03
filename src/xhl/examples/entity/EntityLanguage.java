package xhl.examples.entity;

import xhl.core.Language;
import xhl.core.LanguageProcessor;
import xhl.core.Module;

public class EntityLanguage implements Language {

    private static final EntityModule langModule = new EntityModule();

    @Override
    public Module[] getModules() {
        return new xhl.core.Module[] { langModule };
    }

    public xhl.examples.entity.Module getModule() {
        return langModule.getModule();
    }

    /**
     * @param args
     */
    public static void main(String[] args) {
        if (args.length >= 1) {
            String filename = args[0];
            EntityLanguage lang = new EntityLanguage();
            LanguageProcessor.execute(lang, filename);
            xhl.examples.entity.Module m = lang.getModule();
            System.out.println(m);
        } else
            System.out.println("Give file name as program argument!");
    }
}
