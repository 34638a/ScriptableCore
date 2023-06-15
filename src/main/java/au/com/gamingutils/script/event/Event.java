package au.com.gamingutils.script.event;

import lombok.Getter;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Base class for events.
 * @param <E> Event type.
 */
public abstract class Event<E extends Event<?>> {
	@Getter
	private final String eventName = this.getClass().getName();
	private final AtomicBoolean dispatched = new AtomicBoolean();
	
	/**
	 * Check if the event has been dispatched.
	 * @return True if the event has been dispatched.
	 */
	public boolean isDispatched() {
		return dispatched.get();
	}
	
	/**
	 * Dispatch the event.
	 */
	@SuppressWarnings("unchecked")
	public E dispatch() {
		if (!this.dispatched.getAndSet(true)) {
			EventEngine.scheduleEvent(this);
		}
		return (E) this;
	}
}
