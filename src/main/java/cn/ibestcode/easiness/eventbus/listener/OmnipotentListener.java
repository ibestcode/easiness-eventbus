/**
 * Copyright 2019 the original author or authors.
 * <p>
 * Licensed to the IBESTCODE under one or more agreements.
 * The IBESTCODE licenses this file to you under the MIT license.
 * See the LICENSE file in the project root for more information.
 */

package cn.ibestcode.easiness.eventbus.listener;

import cn.ibestcode.easiness.eventbus.exception.EventBusException;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

/**
 * @author WFSO (仵士杰)
 * create by WFSO (仵士杰) at 2019/11/18 19:55
 */
public class OmnipotentListener implements Listener {

  private final Class<?> supportType;
  private final Object target;
  private final Method method;

  public OmnipotentListener(Object target, Method method) {
    this.target = target;
    this.method = method;

    this.supportType = getMethodArgumentType(method);

    assertPublicMethod(method);
  }

  protected Class getMethodArgumentType(Method method) {
    Class[] paramTypes = method.getParameterTypes();
    if (paramTypes.length != 1) {
      throw new IllegalArgumentException("Method " + method + " has @Subscribe annotation but has " + paramTypes.length + " parameters."
        + "Subscriber methods must have exactly 1 parameter.");
    }
    return paramTypes[0];
  }

  private void assertPublicMethod(Method method) {
    int modifiers = method.getModifiers();
    if (!Modifier.isPublic(modifiers)) {
      throw new IllegalArgumentException("Event handler method [" + method + "] must be public.");
    }
  }

  @Override
  public Class<?> supports() {
    return supportType;
  }

  @Override
  public void handle(Object event) {
    try {
      method.invoke(target, event);
    } catch (IllegalAccessException e) {
      throw new EventBusException("Method became inaccessible: " + event, e);
    } catch (InvocationTargetException e) {
      if (e.getCause() instanceof Error) {
        throw (Error) e.getCause();
      }
      if (e.getCause() instanceof RuntimeException) {
        throw (RuntimeException) e.getCause();
      }
      throw new EventBusException("EventBus InvocationTargetException: " + event, e.getCause());
    }
  }
}
