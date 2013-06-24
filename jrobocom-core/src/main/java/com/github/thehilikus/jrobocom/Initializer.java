package com.github.thehilikus.jrobocom;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.thehilikus.jrobocom.exceptions.PlayerException;

/**
 * Creates each component and connects them
 * 
 * @author hilikus
 * 
 */
public final class Initializer {

    private static final Logger log = LoggerFactory.getLogger(Initializer.class);


    /**
     * @param args
     */
    public static void main(String[] args) {

	List<String> playersData = null;
	if (args.length > 0) {
	    log.info("[main] Initializing with {} main args: {}", args.length, Arrays.toString(args));
	    playersData = new ArrayList<>(Arrays.asList(args));
	    start(playersData);
	} 

    }

    private static void start(List<String> playersData) {
	try {

	    List<Player> players = Player.loadPlayers(playersData);
	    Session session = new Session(players);
	    session.start();

	} catch (PlayerException exc) {
	    log.error("[main]", exc);
	}
    }

}
