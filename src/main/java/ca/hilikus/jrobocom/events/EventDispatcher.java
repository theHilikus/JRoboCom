package ca.hilikus.jrobocom.events;

import java.util.EventListener;
import java.util.EventObject;

/**
 * Interface that listeners use to controls their registration
 * 
 * @param <T> the listening interface
 * @author hilikus
 */
public interface EventDispatcher<T extends EventListener> {

    /**
     * Adds a listener if it doesn't already exist. The event handler methods must have its first
     * argument as a descendent of {@link EventObject} <br>
     * The event handling methods must be declared directly in the listener and not in one of its
     * super-classes. This is a performance limitation
     * 
     * @param listener the object to notify
     */
    public void addListener(T listener);

    /**
     * @param listener the object to remove
     */
    public void removeListener(T listener);

    /**
     * removes all listeners
     */
    public void removeListeners();

}