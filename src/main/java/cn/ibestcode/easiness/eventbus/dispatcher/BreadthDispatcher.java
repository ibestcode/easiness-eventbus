/**
 * Copyright 2019 the original author or authors.
 * <p>
 * Licensed to the IBESTCODE under one or more agreements.
 * The IBESTCODE licenses this file to you under the MIT license.
 * See the LICENSE file in the project root for more information.
 */

package cn.ibestcode.easiness.eventbus.dispatcher;

import cn.ibestcode.easiness.eventbus.listener.Listener;

import java.util.ArrayDeque;
import java.util.List;
import java.util.Queue;

/**
 * @author WFSO (仵士杰)
 * create by WFSO (仵士杰) at 2019/11/18 19:55
 */
public class BreadthDispatcher implements Dispatcher {

  /**
   * Per-thread queue of events to dispatch.
   */
  private final ThreadLocal<Queue<EventWithListener>> queue =
    new ThreadLocal<Queue<EventWithListener>>() {
      @Override
      protected Queue<EventWithListener> initialValue() {
        return new ArrayDeque<>();
      }
    };

  /**
   * Per-thread dispatch state, used to avoid reentrant event dispatching.
   */
  private final ThreadLocal<Boolean> dispatching =
    new ThreadLocal<Boolean>() {
      @Override
      protected Boolean initialValue() {
        return false;
      }
    };

  @Override
  public void dispatch(Object event, List<Listener<?>> listeners) {
    if (event == null) {
      throw new NullPointerException("the event can't be null");
    }
    if (listeners == null) {
      throw new NullPointerException("the listeners can't be null");
    }

    Queue<EventWithListener> queueForThread = queue.get();
    for (Listener listener : listeners) {
      queueForThread.offer(new EventWithListener(event, listener));
    }

    if (!dispatching.get()) {
      dispatching.set(true);
      try {
        EventWithListener eventWithListener;
        while ((eventWithListener = queueForThread.poll()) != null) {
          eventWithListener.listener.handle(eventWithListener.event);
        }
      } finally {
        dispatching.remove();
        queue.remove();
      }
    }
  }

  private static class EventWithListener {
    private final Object event;
    private final Listener listener;

    public EventWithListener(Object event, Listener listener) {
      this.event = event;
      this.listener = listener;
    }
  }
}
