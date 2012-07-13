package xhl.examples.entity;

import xhl.core.LanguageProcessor;

public class EntityExecutor {

    public static void main(String[] args) {
        if (args.length >= 1) {
            String filename = args[0];
            EntityModule langModule = new EntityModule();
            LanguageProcessor.execute(langModule, filename);
            xhl.examples.entity.Module m = langModule.getModule();
            System.out.println(m);
            m.generate();
        } else
            System.out.println("Give file name as program argument!");
    }
}
