package com.github.thehilikus.jrobocom.security;

import java.net.SocketPermission;
import java.security.Permission;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.thehilikus.events.event_manager.GenericEventDispatcher;
import com.github.thehilikus.jrobocom.Player;
import com.github.thehilikus.jrobocom.player.Bank;

/**
 * The security manager used to block code
 * 
 * @author hilikus
 */
public class GameSecurityManager extends SecurityManager {

    private static final String GAME_PACKAGE = "com.github.thehilikus.jrobocom.";

    private static final Logger log = LoggerFactory.getLogger(GameSecurityManager.class);

    private boolean isPlayer() {
	Class<?>[] context = getClassContext();
	/*for (Class<?> level : context) {
	    if (Bank.class.isAssignableFrom(level)) {
		return true;
	    }
	}

	return false;
	*/
	
	//if a user's bank called it
	return context[3] != Bank.class && Bank.class.isAssignableFrom(context[3]); // 
    }

    @Override
    public void checkPackageAccess(String pkg) {
	log.trace("[checkPackageAccess] Checking package access for {}", pkg);
	super.checkPackageAccess(pkg);
	if (isPlayer()) {
	    if (pkg.startsWith(GAME_PACKAGE)) {
		if (!"com.github.thehilikus.jrobocom.player".equals(pkg) && !"com.github.thehilikus.jrobocom.robot.api".equals(pkg)) {
		    throw new SecurityException("No access to game packages");
		}
	    }
	}
    }

    @Override
    public void checkPackageDefinition(String pkg) {
	log.trace("[checkPackageDefinition] Check creating new classes in {}", pkg);
	super.checkPackageDefinition(pkg);

	if (isPlayer()) {
	    if (pkg.startsWith(GAME_PACKAGE)) {
		throw new SecurityException("Cannot create classes in game packages");
	    }
	}
    }

    @Override
    public void checkRead(String file) {
	log.trace("[checkRead] Check read access for {}", file);
	if (isPlayer()) {
	    // TODO: this should be less restrictive super.checkRead(file);
	    // throw new AccessControlException("Cannot read other resources");
	}
    }

    @Override
    public void checkCreateClassLoader() {
	log.trace("[checkCreateClassLoader] Tring to create class loader");
	if (isPlayer()) {
	    super.checkCreateClassLoader();
	}
    }

    @Override
    public void checkPermission(Permission perm) {
	log.trace("[checkPermission] Checking permission {}", perm);
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
	log.trace("[checkGamePermission] Checking game permission {}", perm);
	if (isPlayer()) {
	    throw new SecurityException("Player cannot connect a bank to a robot. Only the game can do that");
	}

    }

    @SuppressWarnings("unused")
    private boolean isPlayerThread() {
	return Player.PLAYERS_GROUP.parentOf(getThreadGroup());
    }

    @Override
    public void checkDelete(String file) {
	log.trace("[checkDelete] Trying to delete file {}", file);
	if (isPlayer()) {
	    super.checkDelete(file);
	}
    }

    @Override
    public void checkMemberAccess(Class<?> clazz, int which) {
	log.trace("[checkMemberAccess] Trying to use reflection on {}", clazz);
	if (isPlayer() && !getClassContext()[3].equals(GenericEventDispatcher.class)) {
	    throw new SecurityException("Cannot use reflection");
	}
    }

}
