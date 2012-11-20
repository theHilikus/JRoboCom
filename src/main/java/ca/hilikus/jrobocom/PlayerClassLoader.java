package ca.hilikus.jrobocom;

import java.net.URL;
import java.net.URLClassLoader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PlayerClassLoader extends URLClassLoader {

    private static final Logger log = LoggerFactory.getLogger(PlayerClassLoader.class);
    private static final String GAME_PACKAGE = "ca.hilikus.jrobocom.";
    private static final String GAME_PLAYER_PACKAGE = "ca.hilikus.jrobocom.player";

    public PlayerClassLoader(URL[] urls) {
	super(urls);
    }

    /* (non-Javadoc)
     * @see java.lang.ClassLoader#loadClass(java.lang.String, boolean)
     */
    @Override
    protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
	log.trace("[loadClass] Asked to load {}", name);

	SecurityManager security = System.getSecurityManager();

	Class<?> clazz = findLoadedClass(name);
	if (clazz != null) {
	    // we know the class
	    return clazz;
	}

	if (security != null) {
	    int classPosition = name.lastIndexOf('.');
	    if (classPosition >= 0) {
		security.checkPackageAccess(name.substring(0, classPosition));
	    }
	}

	return super.loadClass(name, resolve);
    }

}
