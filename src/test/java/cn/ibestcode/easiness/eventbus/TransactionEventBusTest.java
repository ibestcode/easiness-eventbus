/**
 * Copyright 2019 the original author or authors.
 * <p>
 * Licensed to the IBESTCODE under one or more agreements.
 * The IBESTCODE licenses this file to you under the MIT license.
 * See the LICENSE file in the project root for more information.
 */

package cn.ibestcode.easiness.eventbus;

import cn.ibestcode.easiness.eventbus.event.LogEvent;
import cn.ibestcode.easiness.eventbus.event.ExceptionEvent;
import cn.ibestcode.easiness.eventbus.listener.TestListener;
import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

/**
 * @author WFSO (仵士杰)
 * create by WFSO (仵士杰) at 2019/11/19 19:16
 */
@RunWith(JUnit4.class)
@Slf4j
public class TransactionEventBusTest {
  private EventBus breadthEventBus = TransactionEventBus.getBreadthInstance();
  private EventBus depthEventBus = TransactionEventBus.getDepthInstance();

  @Before
  public void before() {
    breadthEventBus.register(new TestListener());
    depthEventBus.register(new TestListener());
  }

  @Test
  public void breadthEventBusTest() {
    try {
      breadthEventBus.post(new ExceptionEvent("test"));
      breadthEventBus.post(new LogEvent("test"));
    } catch (Throwable e) {
      log.warn(e.getMessage(), e);
    }
  }

  @Test
  public void depthEventBusTest() {
    try {
      depthEventBus.post(new LogEvent("test"));
      depthEventBus.post(new ExceptionEvent("test"));
    } catch (Throwable e) {
      log.warn(e.getMessage(), e);
    }
  }
}
