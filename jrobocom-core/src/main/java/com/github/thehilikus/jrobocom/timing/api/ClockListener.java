package com.github.thehilikus.jrobocom.timing.api;

import java.util.EventListener;

import com.github.thehilikus.jrobocom.events.TickEvent;

/**
 * Callback interface
 * 
 */
public interface ClockListener extends EventListener {
    /**
     * Method that gets called every time the clock ticks
     * 
     * @param event tick event with info about the current pulse
     */
    public void update(TickEvent event);
}