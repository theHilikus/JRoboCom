package ca.hilikus.jrobocom.security;

import java.net.SocketPermission;
import java.security.AccessControlException;
import java.security.Permission;

import ca.hilikus.jrobocom.Player;
import ca.hilikus.jrobocom.player.Bank;

/**
 * The security manager used to block code
 * 
 * @author hilikus
 */
public class GameSecurityManager extends SecurityManager {

    private static final String GAME_PACKAGE = "ca.hilikus.jrobocom.";

    private boolean isPlayer() {
	Class<?>[] context = getClassContext();
	for (Class<?> level : context) {
	    if (Bank.class.isAssignableFrom(level)) {
		return true;
	    }
	}

	return false;
    }

    @Override
    public void checkPackageAccess(String pkg) {
	super.checkPackageAccess(pkg);
	if (isPlayer()) {
	    if (pkg.startsWith(GAME_PACKAGE)) {
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
	    if (pkg.startsWith(GAME_PACKAGE)) {
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
	if (perm instanceof GamePermission) {
	    checkGamePermission((GamePermission) perm);
	} else {
	    if (isPlayer()) {
		if (perm instanceof SocketPermission) {
		    throw new SecurityException("Cannot use sockets");
		} else if (perm instanceof RuntimePermission) {
		    throw new SecurityException("No Runtime Permissions");
		}
		super.checkPermission(perm);
	    }
	}
    }

    private void checkGamePermission(GamePermission perm) {
	if ("connectBank".equals(perm.getName()) && isPlayerThread()) {
	    throw new SecurityException("Player cannot connect a bank to a robot. Only the game can do that");
	}

    }

    private boolean isPlayerThread() {
	return Player.PLAYERS_GROUP.parentOf(getThreadGroup());
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
