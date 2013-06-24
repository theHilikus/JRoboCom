package com.github.thehilikus.jrobocom.security;

import java.security.BasicPermission;

/**
 * A permission token to allow sensitive actions
 * 
 * @author hilikus
 */
public class GamePermission extends BasicPermission {

    private static final long serialVersionUID = 5987272177083971254L;

    /**
     * Constructs a permission with a particular name
     * 
     * @param name the name of the permission
     */
    public GamePermission(String name) {
	super(name);
    }

}
