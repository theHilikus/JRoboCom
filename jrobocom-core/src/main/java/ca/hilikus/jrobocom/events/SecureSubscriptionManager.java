/**
 * 
 */
package ca.hilikus.jrobocom.events;

import java.util.EventListener;

import ca.hilikus.events.event_manager.SubscriptionManager;
import ca.hilikus.events.event_manager.api.EventDispatcher;
import ca.hilikus.events.event_manager.api.EventPublisher;
import ca.hilikus.jrobocom.security.GamePermission;

/**
 * Confirms each operation with a SecurityManager
 *
 * @author hilikus
 */
public class SecureSubscriptionManager extends SubscriptionManager {

    /* (non-Javadoc)
     * @see ca.hilikus.events.event_manager.SubscriptionManager#subscribe(ca.hilikus.events.event_manager.api.EventPublisher, java.util.EventListener)
     */
    @Override
    public <T extends EventListener> void subscribe(EventPublisher source, T listener) {
	checkPermission();
	super.subscribe(source, listener);
    }

    /* (non-Javadoc)
     * @see ca.hilikus.events.event_manager.SubscriptionManager#unsubscribe(ca.hilikus.events.event_manager.api.EventPublisher, java.util.EventListener)
     */
    @Override
    public <T extends EventListener> void unsubscribe(EventPublisher source, T listener) {
	checkPermission();
	super.unsubscribe(source, listener);
    }

    /* (non-Javadoc)
     * @see ca.hilikus.events.event_manager.SubscriptionManager#getEventDispatcher(ca.hilikus.events.event_manager.api.EventPublisher)
     */
    @Override
    public EventDispatcher getEventDispatcher(EventPublisher source) {
	checkPermission();
	return super.getEventDispatcher(source);
    }

    /* (non-Javadoc)
     * @see ca.hilikus.events.event_manager.SubscriptionManager#cleanPublisher(ca.hilikus.events.event_manager.api.EventPublisher)
     */
    @Override
    public void unsubscribeAll(EventPublisher source) {
	checkPermission();
	super.unsubscribeAll(source);
    }

    private static void checkPermission() {
	SecurityManager sm = System.getSecurityManager();
	if (sm != null) {
	    sm.checkPermission(new GamePermission("eventsListener"));
	}

    }
}
