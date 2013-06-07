package com.areahomeschoolers.baconbits.client.content.calendar.event;

import com.google.gwt.event.shared.EventHandler;

/**
 * Handler interface for {@link DeleteEvent} events.
 * 
 * @param <T> the type being opened
 */
public interface TimeBlockClickHandler<T> extends EventHandler {

  /**
   * Called when {@link DeleteEvent} is fired.
   * 
   * @param event the {@link DeleteEvent} that was fired
   */
  void onTimeBlockClick(TimeBlockClickEvent<T> event);
}
