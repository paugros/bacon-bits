package com.areahomeschoolers.baconbits.client.content.calendar.event;

import com.google.gwt.event.shared.EventHandler;

/**
 * Handler interface for {@link MouseOverEvent} events.
 * 
 * @param <T> the type being hovered
 */
public interface MouseOverHandler<T> extends EventHandler {

  /**
   * Called when {@link MouseOverEvent} is fired.
   * 
   * @param event the {@link MouseOverEvent} that was fired
   */
  void onMouseOver(MouseOverEvent<T> event);
}
