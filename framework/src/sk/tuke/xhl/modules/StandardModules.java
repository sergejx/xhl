package sk.tuke.xhl.modules;

import sk.tuke.xhl.core.Module;
import sk.tuke.xhl.core.ModulesProvider;

import java.util.Set;

import static com.google.common.collect.Sets.newHashSet;

public class StandardModules implements ModulesProvider {

    private final Set<String> modules = newHashSet(
            "arithmetic", "logic", "relations", "define");

    @Override
    public boolean hasModule(String name) {
        return modules.contains(name);
    }

    @Override
    public Module getModule(String name) {
        switch (name) {
            case "arithmetic":
                return new ArithmeticsModule();
            case "logic":
                return new LogicsModule();
            case "relations":
                return new RelationsModule();
            case "define":
                return new DefineModule();
            default:
                return null;
        }
    }
}
