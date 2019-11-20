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

/**
 * @author WFSO (仵士杰)
 * create by WFSO (仵士杰) at 2019/11/18 19:55
 */
public class TransactionEventBus extends AbstractEventBus {
  private TransactionEventBus(Dispatcher dispatcher) {
    super(dispatcher);
  }

  private TransactionEventBus(
    Class<? extends Annotation> subscribeAnnotation,
    Class<? extends Annotation> listenerAnnotation,
    Dispatcher dispatcher) {
    super(subscribeAnnotation, listenerAnnotation, dispatcher);
  }

  public static TransactionEventBus getDepthInstance(
    Class<? extends Annotation> subscribeAnnotation,
    Class<? extends Annotation> listenerAnnotation) {
    return new TransactionEventBus(
      subscribeAnnotation,
      listenerAnnotation,
      new DepthDispatcher()
    );
  }

  public static TransactionEventBus getBreadthInstance(
    Class<? extends Annotation> subscribeAnnotation,
    Class<? extends Annotation> listenerAnnotation
  ) {
    return new TransactionEventBus(
      subscribeAnnotation,
      listenerAnnotation,
      new BreadthDispatcher()
    );
  }

  public static TransactionEventBus getDepthInstance() {
    return new TransactionEventBus(new DepthDispatcher());
  }

  public static TransactionEventBus getBreadthInstance() {
    return new TransactionEventBus(new BreadthDispatcher());
  }
}
