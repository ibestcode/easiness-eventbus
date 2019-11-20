/**
 * Copyright 2019 the original author or authors.
 * <p>
 * Licensed to the IBESTCODE under one or more agreements.
 * The IBESTCODE licenses this file to you under the MIT license.
 * See the LICENSE file in the project root for more information.
 */

package cn.ibestcode.easiness.eventbus;

import cn.ibestcode.easiness.eventbus.dispatcher.*;

import java.lang.annotation.Annotation;
import java.util.concurrent.Executors;

/**
 * @author WFSO (仵士杰)
 * create by WFSO (仵士杰) at 2019/11/18 19:55
 */
public class AsyncEventBus extends AbstractEventBus {

  private AsyncEventBus(int poolSize) {
    super(new AsyncDispatcher(Executors.newScheduledThreadPool(poolSize)));
  }

  private AsyncEventBus(
    Class<? extends Annotation> subscribeAnnotation,
    Class<? extends Annotation> listenerAnnotation,
    int poolSize) {
    super(
      subscribeAnnotation,
      listenerAnnotation,
      new AsyncDispatcher(Executors.newScheduledThreadPool(poolSize))
    );
  }

  public static AsyncEventBus getInstance() {
    return new AsyncEventBus(10);
  }

  public static AsyncEventBus getInstance(int poolSize) {
    return new AsyncEventBus(poolSize);
  }

  public static AsyncEventBus getInstance(
    Class<? extends Annotation> subscribeAnnotation,
    Class<? extends Annotation> listenerAnnotation,
    int poolSize) {
    return new AsyncEventBus(
      subscribeAnnotation,
      listenerAnnotation,
      poolSize
    );
  }
}
