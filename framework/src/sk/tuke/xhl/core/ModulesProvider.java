/* XHL - Extensible Host Language
 * Copyright 2012 Sergej Chodarev
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package sk.tuke.xhl.core;

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
        private final ServiceLoader<ModulesProvider> loader;

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
