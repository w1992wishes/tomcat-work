package me.w1992wishes.tomcatwork.simple_tomcat_07.loader;

/**
 * Created by w1992wishes
 * on 2018/2/8.
 */
public interface Reloader {

    /**
     * Add a new repository to the set of places this ClassLoader can look for
     * classes to be loaded.
     *
     * @param repository Name of a source of classes to be loaded, such as a
     *  directory pathname, a JAR file pathname, or a ZIP file pathname
     *
     * @exception IllegalArgumentException if the specified repository is
     *  invalid or does not exist
     */
    void addRepository(String repository);


    /**
     * Return a String array of the current repositories for this class
     * loader.  If there are no repositories, a zero-length array is
     * returned.
     */
    String[] findRepositories();


    /**
     * Have one or more classes or resources been modified so that a reload
     * is appropriate?
     */
    boolean modified();

}
