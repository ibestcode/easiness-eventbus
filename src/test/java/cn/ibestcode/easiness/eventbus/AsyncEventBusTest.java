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
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

/**
 * @author WFSO (仵士杰)
 * create by WFSO (仵士杰) at 2019/11/19 19:09
 */
@RunWith(JUnit4.class)
public class AsyncEventBusTest {

  private EventBus eventBus = AsyncEventBus.getInstance();

  @Before
  public void before() {
    eventBus.register(new TestListener());
  }

  @Test
  public void test() {
    eventBus.post(new ExceptionEvent("test"));
    eventBus.post(new ExceptionEvent("test"));
    eventBus.post(new LogEvent("test"));
    eventBus.post(new LogEvent("test"));
    try {
      Thread.sleep(1000);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }
}
