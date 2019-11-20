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

  private final Class supportType;
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


  @Override
  public final int hashCode() {
    return (7 + method.hashCode()) * 7 + target.hashCode();
  }

  @Override
  public final boolean equals(Object obj) {
    if (obj instanceof OmnipotentListener) {
      OmnipotentListener that = (OmnipotentListener) obj;

      /**
       * 同“事件监听器类”的不同“实例对象”下的同一个“监听器方法”是否允许重复注册，
       * 由“事件监听器类”的 hashCode 和equals 方法决定；
       *
       * 如果“事件监听器类”不重载 Object 类 hashCode 和 equals 方法，
       * 则同“事件监听器类”的不同“实例对象”下的同一个“监听器方法”是允许重复注册的；
       * 此时只防止同“事件监听器类”且同“实例对象”下的同一个“监听器方法”被多次注册；
       *
       * 如果“事件监听器类”重载了  Object 类 hashCode 和 equals 方法，
       * 则当同“事件监听器类”的两个不同“实例对象” 用 equals 方法判断相等时，
       * 这两个“实例对象”中的同一个“监听器方法”不会被重复注册（只有一个被注册）；
       *
       */
      return target.equals(that.target) && method.equals(that.method);
    }
    return false;
  }
}
