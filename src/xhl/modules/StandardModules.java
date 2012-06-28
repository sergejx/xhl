package xhl.modules;

import xhl.core.Module;
import xhl.core.ModulesProvider;

import java.util.Set;

import static com.google.common.collect.Sets.newHashSet;

public class StandardModules implements ModulesProvider {

    Set<String> modules = newHashSet("arithmetic", "logic", "relations",
            "define");

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
