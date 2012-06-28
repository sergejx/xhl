package xhl.core;

import java.util.ServiceLoader;

/**
 * Provider can load language modules by name.
 */
public interface ModulesProvider {
    /**
     * Can provider load specified module?
     *
     * @param name Module name
     * @return <code>true</code> if module can be loaded
     */
    public boolean hasModule(String name);

    /**
     * Get a module specified by name.
     *
     * @param name Module name
     * @return A new instance of specified module
     */
    public Module getModule(String name);

    /**
     * Helper class that allows to load modules from all available providers.
     */
    public static class ModulesLoader {
        private ServiceLoader<ModulesProvider> loader;

        /**
         * Make a new loader.
         */
        public ModulesLoader() {
            loader = ServiceLoader.load(ModulesProvider.class);
        }

        /**
         * Load specified module.
         *
         * @param name Module name
         * @return An instance of module or <code>null</code> if such a
         *         module is not provided by any provider.
         */
        public Module loadModule(String name) {
            for (ModulesProvider provider : loader)
                if (provider.hasModule(name))
                    return provider.getModule(name);
            return null;
        }
    }
}
