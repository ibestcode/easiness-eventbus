/**
 * Copyright 2019 the original author or authors.
 * <p>
 * Licensed to the IBESTCODE under one or more agreements.
 * The IBESTCODE licenses this file to you under the MIT license.
 * See the LICENSE file in the project root for more information.
 */

package cn.ibestcode.easiness.eventbus.listener;

import java.lang.reflect.Method;

/**
 * @author WFSO (仵士杰)
 * create by WFSO (仵士杰) at 2019/11/18 19:55
 */
public final class SynchronizedOmnipotentListener extends OmnipotentListener {

  public SynchronizedOmnipotentListener(Object target, Method method) {
    super(target, method);
  }

  @Override
  public void handle(Object event) {
    synchronized (this) {
      super.handle(event);
    }
  }
}
