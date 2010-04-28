package xhl.core;

/**
 * XHL based language
 *
 * @author Sergej Chodarev
 */
public interface Language {
    /**
     * Get modules from which the language is composed.
     *
     * @return language modules
     */
    public Module[] getModules();
}
