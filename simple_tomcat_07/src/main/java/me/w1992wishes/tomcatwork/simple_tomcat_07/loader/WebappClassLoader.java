package me.w1992wishes.tomcatwork.simple_tomcat_07.loader;

import me.w1992wishes.tomcatwork.simple_tomcat_07.Lifecycle;
import me.w1992wishes.tomcatwork.simple_tomcat_07.LifecycleListener;
import me.w1992wishes.tomcatwork.simple_tomcat_07.exception.LifecycleException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.naming.NamingException;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.security.AccessControlException;
import java.security.AccessController;
import java.security.CodeSource;
import java.security.PrivilegedAction;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.jar.JarEntry;

/**
 * Created by w1992wishes
 * on 2018/2/8.
 */
public class WebappClassLoader extends URLClassLoader implements Reloader, Lifecycle {

    private static final Logger LOGGER = LoggerFactory.getLogger(WebappClassLoader.class);

    /**
     * Set of package names which are not allowed to be loaded from a webapp
     * class loader without delegating first.
     */
    private static final String[] packageTriggers = {
            "javax",                                     // Java extensions
            "org.xml.sax",                               // SAX 1 & 2
            "org.w3c.dom",                               // DOM 1 & 2
            "org.apache.xerces",                         // Xerces 1 & 2
            "org.apache.xalan"                           // Xalan
    };

    /**
     * The parent class loader.
     */
    private ClassLoader parent = null;

    /**
     * The system class loader.
     */
    private ClassLoader system = null;

    /**
     * The cache of ResourceEntry for classes and resources we have loaded,
     * keyed by resource name.
     */
    protected HashMap resourceEntries = new HashMap();

    /**
     * The list of not found resources.
     */
    protected HashMap notFoundResources = new HashMap();

    protected boolean delegate = false;

    /**
     * The list of local repositories, in the order they should be searched
     * for locally loaded classes or resources.
     */
    protected String[] repositories = new String[0];

    /**
     * Has this component been started?
     */
    protected boolean started = false;

    /**
     * Has external repositories.
     */
    protected boolean hasExternalRepositories = false;

    /**
     * Construct a new ClassLoader with no defined repositories and no
     * parent ClassLoader.
     */
    public WebappClassLoader() {
        super(new URL[0]);
        this.parent = getParent();
        system = getSystemClassLoader();

    }


    /**
     * Construct a new ClassLoader with no defined repositories and no
     * parent ClassLoader.
     */
    public WebappClassLoader(ClassLoader parent) {
        super(new URL[0], parent);
        this.parent = getParent();
        system = getSystemClassLoader();
    }

    /**
     * Return the "delegate first" flag for this class loader.
     */
    public boolean getDelegate() {
        return (this.delegate);
    }

    /**
     * Set the "delegate first" flag for this class loader.
     *
     * @param delegate The new "delegate first" flag
     */
    public void setDelegate(boolean delegate) {
        this.delegate = delegate;
    }

    @Override
    public void addRepository(String repository) {

    }

    @Override
    public String[] findRepositories() {
        return new String[0];
    }

    @Override
    public boolean modified() {
        return false;
    }

    @Override
    public void start() throws LifecycleException {

    }

    @Override
    public void stop() throws LifecycleException {

    }

    @Override
    public void addLifecycleListener(LifecycleListener listener) {

    }

    @Override
    public LifecycleListener[] findLifecycleListeners() {
        return new LifecycleListener[0];
    }

    @Override
    public void removeLifecycleListener(LifecycleListener listener) {

    }

    public Class loadClass(String name, boolean resolve)
            throws ClassNotFoundException {

        Class clazz = null;

        // Don't load classes if class loader is stopped
        if (!started) {
            Thread.dumpStack();
            throw new ClassNotFoundException(name);
        }

        // (0) Check our previously loaded local class cache
        clazz = findLoadedClass0(name);
        if (clazz != null) {
            LOGGER.info("  Returning class from cache");
            if (resolve)
                resolveClass(clazz);
            return (clazz);
        }

        // (0.1) Check our previously loaded class cache
        clazz = findLoadedClass(name);
        if (clazz != null) {
            LOGGER.info("  Returning class from cache");
            if (resolve)
                resolveClass(clazz);
            return (clazz);
        }

        // (0.2) Try loading the class with the system class loader, to prevent
        //       the webapp from overriding J2SE classes
        try {
            clazz = system.loadClass(name);
            if (clazz != null) {
                if (resolve)
                    resolveClass(clazz);
                return (clazz);
            }
        } catch (ClassNotFoundException e) {
            // Ignore
        }

        boolean delegateLoad = delegate || filter(name);

        // (1) Delegate to our parent if requested
        if (delegateLoad) {
            LOGGER.info("  Delegating to parent classloader");
            ClassLoader loader = parent;
            if (loader == null)
                loader = system;
            try {
                clazz = loader.loadClass(name);
                if (clazz != null) {
                    LOGGER.info("  Loading class from parent");
                    if (resolve)
                        resolveClass(clazz);
                    return (clazz);
                }
            } catch (ClassNotFoundException e) {
                ;
            }
        }

        // (2) Search local repositories
        LOGGER.info("  Searching local repositories");
        try {
            clazz = findClass(name);
            if (clazz != null) {
                LOGGER.info("  Loading class from local repository");
                if (resolve)
                    resolveClass(clazz);
                return (clazz);
            }
        } catch (ClassNotFoundException e) {
            ;
        }

        // (3) Delegate to parent unconditionally
        if (!delegateLoad) {
           LOGGER.info("  Delegating to parent classloader");
            ClassLoader loader = parent;
            if (loader == null)
                loader = system;
            try {
                clazz = loader.loadClass(name);
                if (clazz != null) {
                    LOGGER.info("  Loading class from parent");
                    if (resolve)
                        resolveClass(clazz);
                    return (clazz);
                }
            } catch (ClassNotFoundException e) {
                ;
            }
        }

        // This class was not found
        throw new ClassNotFoundException(name);

    }

    /**
     * Finds the class with the given name if it has previously been
     * loaded and cached by this class loader, and return the Class object.
     * If this class has not been cached, return <code>null</code>.
     *
     * @param name Name of the resource to return
     */
    protected Class findLoadedClass0(String name) {

        ResourceEntry entry = (ResourceEntry) resourceEntries.get(name);
        if (entry != null) {
            return entry.loadedClass;
        }
        return (null);  // FIXME - findLoadedResource()

    }

    /**
     * Filter classes.
     *
     * @param name class name
     * @return true if the class should be filtered
     */
    protected boolean filter(String name) {
        if (name == null)
            return false;

        // Looking up the package
        String packageName = null;
        int pos = name.lastIndexOf('.');
        if (pos != -1)
            packageName = name.substring(0, pos);
        else
            return false;

        for (int i = 0; i < packageTriggers.length; i++) {
            if (packageName.startsWith(packageTriggers[i]))
                return true;
        }

        return false;
    }

    /**
     * Find the specified class in our local repositories, if possible.  If
     * not found, throw <code>ClassNotFoundException</code>.
     *
     * @param name Name of the class to be loaded
     *
     * @exception ClassNotFoundException if the class was not found
     */
    public Class findClass(String name) throws ClassNotFoundException {

        LOGGER.info("    findClass(" + name + ")");

        // Ask our superclass to locate this class, if possible
        // (throws ClassNotFoundException if it is not found)
        Class clazz = null;
        try {
            LOGGER.info("      findClassInternal(" + name + ")");
            try {
                clazz = findClassInternal(name);
            } catch(ClassNotFoundException cnfe) {
                if (!hasExternalRepositories) {
                    throw cnfe;
                }
            } catch(AccessControlException ace) {
                ace.printStackTrace();
                throw new ClassNotFoundException(name);
            } catch (RuntimeException e) {
                LOGGER.info("      -->RuntimeException Rethrown", e);
                throw e;
            }
            if ((clazz == null) && hasExternalRepositories) {
                try {
                    clazz = super.findClass(name);
                } catch(AccessControlException ace) {
                    throw new ClassNotFoundException(name);
                } catch (RuntimeException e) {
                    LOGGER.info("      -->RuntimeException Rethrown", e);
                    throw e;
                }
            }
            if (clazz == null) {
                LOGGER.info("    --> Returning ClassNotFoundException");
                throw new ClassNotFoundException(name);
            }
        } catch (ClassNotFoundException e) {
            LOGGER.info("    --> Passing on ClassNotFoundException", e);
            throw e;
        }

        // Return the class we have located
        LOGGER.info("      Returning class " + clazz);
        if ((clazz != null))
            LOGGER.info("      Loaded by " + clazz.getClassLoader());
        return (clazz);

    }

    protected boolean validate(String name) {

        if (name == null)
            return false;
        if (name.startsWith("java."))
            return false;

        return true;

    }

    protected Class findClassInternal(String name)
            throws ClassNotFoundException {

        if (!validate(name))
            throw new ClassNotFoundException(name);

        String tempPath = name.replace('.', '/');
        String classPath = tempPath + ".class";

        ResourceEntry entry = null;

        entry = findResourceInternal(name, classPath);

        if ((entry == null) || (entry.binaryContent == null))
            throw new ClassNotFoundException(name);

        Class clazz = entry.loadedClass;
        if (clazz != null)
            return clazz;

        // Looking up the package
        String packageName = null;
        int pos = name.lastIndexOf('.');
        if (pos != -1)
            packageName = name.substring(0, pos);

        Package pkg = null;

        if (packageName != null) {

            pkg = getPackage(packageName);

            // Define the package (if null)
            if (pkg == null) {
                if (entry.manifest == null) {
                    definePackage(packageName, null, null, null, null, null,
                            null, null);
                } else {
                    definePackage(packageName, entry.manifest, entry.codeBase);
                }
            }

        }

        // Create the code source object
        CodeSource codeSource = new CodeSource(entry.codeBase, entry.certificates);

        if (entry.loadedClass == null) {
            synchronized (this) {
                if (entry.loadedClass == null) {
                    clazz = defineClass(name, entry.binaryContent, 0, entry.binaryContent.length, codeSource);
                    entry.loadedClass = clazz;
                } else {
                    clazz = entry.loadedClass;
                }
            }
        } else {
            clazz = entry.loadedClass;
        }

        return clazz;

    }

    protected ResourceEntry findResourceInternal(String name, String path) {

        if (!started) {
            LOGGER.error("Lifecycle error : CL stopped");
            return null;
        }

        if ((name == null) || (path == null))
            return null;

        ResourceEntry entry = (ResourceEntry) resourceEntries.get(name);
        if (entry != null)
            return entry;

        int contentLength = -1;
        InputStream binaryStream = null;

        int repositoriesLength = repositories.length;

        int i;

        Resource resource = null;

        for (i = 0; (entry == null) && (i < repositoriesLength); i++) {

            String fullPath = repositories[i] + path;


            // Note : Not getting an exception here means the resource was
            // found

            entry = new ResourceEntry();
            entry.codeBase = entry.source;

            if (resource != null) {

                try {
                    binaryStream = resource.streamContent();
                } catch (IOException e) {
                    return null;
                }


            }

            if ((entry == null) && (notFoundResources.containsKey(name)))
                return null;

            JarEntry jarEntry = null;

            if (entry == null) {
                synchronized (notFoundResources) {
                    notFoundResources.put(name, name);
                }
                return null;
            }

            if (binaryStream != null) {

                byte[] binaryContent = new byte[contentLength];

                try {
                    int pos = 0;
                    while (true) {
                        int n = binaryStream.read(binaryContent, pos,
                                binaryContent.length - pos);
                        if (n <= 0)
                            break;
                        pos += n;
                    }
                    binaryStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    return null;
                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }

                entry.binaryContent = binaryContent;

                // The certificates are only available after the JarEntry
                // associated input stream has been fully read
                if (jarEntry != null) {
                    entry.certificates = jarEntry.getCertificates();
                }

            }
            // Add the entry in the local resource repository
            synchronized (resourceEntries) {
                // Ensures that all the threads which may be in a race to load
                // a particular class all end up with the same ResourceEntry
                // instance
                ResourceEntry entry2 = (ResourceEntry) resourceEntries.get(name);
                if (entry2 == null) {
                    resourceEntries.put(name, entry);
                } else {
                    entry = entry2;
                }
            }

        }
        return entry;
    }
}
