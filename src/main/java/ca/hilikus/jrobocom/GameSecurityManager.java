package ca.hilikus.jrobocom;

import java.net.SocketPermission;
import java.security.AccessControlException;
import java.security.Permission;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.hilikus.jrobocom.player.Bank;

/**
 * The security manager used to block code
 * 
 * @author hilikus
 */
public class GameSecurityManager extends SecurityManager {

    private static final Logger log = LoggerFactory.getLogger(GameSecurityManager.class);

    private boolean isPlayer() {
	Class<?>[] context = getClassContext();
	for (Class<?> level : context) {
	    if (Bank.class.isAssignableFrom(level)) {
		return true;
	    }
	}
	// return Player.PLAYERS_GROUP.parentOf(getThreadGroup());
	return false;
    }

    @Override
    public void checkPackageAccess(String pkg) {
	super.checkPackageAccess(pkg);
	if (isPlayer()) {
	    if (pkg.startsWith("ca.hilikus.jrobocom")) {
		if (!"ca.hilikus.jrobocom.player".equals(pkg) && !"ca.hilikus.jrobocom.robot.api".equals(pkg)) {
		    throw new SecurityException("No access to game packages");
		}
	    }
	}
    }

    @Override
    public void checkPackageDefinition(String pkg) {
	super.checkPackageDefinition(pkg);

	if (isPlayer()) {
	    if (pkg.startsWith("ca.hilikus.jrobocom")) {
		throw new SecurityException("Cannot create classes in game packages");
	    }
	}
    }

    @Override
    public void checkRead(String file) {
	if (isPlayer()) {
	    throw new AccessControlException("Cannot read other resources");
	}
    }

    @Override
    public void checkCreateClassLoader() {
	if (isPlayer()) {
	    super.checkCreateClassLoader();
	}
    }

    @Override
    public void checkPermission(Permission perm) {
	if (isPlayer()) {
	    if (perm instanceof SocketPermission) {
		throw new SecurityException("Cannot use sockets");
	    }
	    super.checkPermission(perm);
	}
    }

    @Override
    public void checkDelete(String file) {
	if (isPlayer()) {
	    super.checkDelete(file);
	}
    }

    @Override
    public void checkMemberAccess(Class<?> clazz, int which) {
	if (isPlayer()) {
	    throw new SecurityException("Cannot use reflection");
	}
    }


}
