/**
 * 
 */
package com.github.thehilikus.jrobocom.events;

import java.util.EventListener;

import com.github.thehilikus.events.event_manager.SubscriptionManager;
import com.github.thehilikus.events.event_manager.api.EventDispatcher;
import com.github.thehilikus.events.event_manager.api.EventPublisher;
import com.github.thehilikus.jrobocom.security.GamePermission;

/**
 * Confirms each operation with a SecurityManager
 *
 * @author hilikus
 */
public class SecureSubscriptionManager extends SubscriptionManager {

    /* (non-Javadoc)
     * @see com.github.thehilikus.events.event_manager.SubscriptionManager#subscribe(com.github.thehilikus.events.event_manager.api.EventPublisher, java.util.EventListener)
     */
    @Override
    public <T extends EventListener> void subscribe(EventPublisher source, T listener) {
	checkPermission();
	super.subscribe(source, listener);
    }

    /* (non-Javadoc)
     * @see com.github.thehilikus.events.event_manager.SubscriptionManager#unsubscribe(com.github.thehilikus.events.event_manager.api.EventPublisher, java.util.EventListener)
     */
    @Override
    public <T extends EventListener> void unsubscribe(EventPublisher source, T listener) {
	checkPermission();
	super.unsubscribe(source, listener);
    }

    /* (non-Javadoc)
     * @see com.github.thehilikus.events.event_manager.SubscriptionManager#getEventDispatcher(com.github.thehilikus.events.event_manager.api.EventPublisher)
     */
    @Override
    public EventDispatcher getEventDispatcher(EventPublisher source) {
	checkPermission();
	return super.getEventDispatcher(source);
    }

    /* (non-Javadoc)
     * @see com.github.thehilikus.events.event_manager.SubscriptionManager#cleanPublisher(com.github.thehilikus.events.event_manager.api.EventPublisher)
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
