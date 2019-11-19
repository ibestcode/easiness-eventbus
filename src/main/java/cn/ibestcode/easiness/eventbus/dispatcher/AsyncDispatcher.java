/**
 * Copyright 2019 the original author or authors.
 * <p>
 * Licensed to the IBESTCODE under one or more agreements.
 * The IBESTCODE licenses this file to you under the MIT license.
 * See the LICENSE file in the project root for more information.
 */

package cn.ibestcode.easiness.eventbus.dispatcher;

import cn.ibestcode.easiness.eventbus.listener.Listener;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * @author WFSO (仵士杰)
 * create by WFSO (仵士杰) at 2019/11/19 19:19
 */
@Slf4j
public class AsyncDispatcher implements Dispatcher {

  private final Executor executor;

  public AsyncDispatcher() {
    this(Executors.newScheduledThreadPool(10));
  }

  public AsyncDispatcher(Executor executor) {
    this.executor = executor;
  }

  @Override
  public void dispatch(Object event, List<Listener<?>> listeners) {
    for (final Listener listener : listeners) {
      executor.execute(
        new Runnable() {
          @Override
          public void run() {
            try {
              listener.handle(event);
            } catch (Throwable e) {
              log.error("Event Execute Fail: " + e.getMessage(), e);
            }
          }
        }
      );
    }
  }
}
