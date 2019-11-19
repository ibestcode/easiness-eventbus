/**
 * Copyright 2019 the original author or authors.
 * <p>
 * Licensed to the IBESTCODE under one or more agreements.
 * The IBESTCODE licenses this file to you under the MIT license.
 * See the LICENSE file in the project root for more information.
 */

package cn.ibestcode.easiness.eventbus.listener;

import cn.ibestcode.easiness.eventbus.annotation.Synchronize;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

/**
 * @author WFSO (仵士杰)
 * create by WFSO (仵士杰) at 2019/11/19 19:15
 */
public class OmnipotentListenerResolver extends AbstractListenerResolver {
  public OmnipotentListenerResolver() {
  }

  public OmnipotentListenerResolver(Class<? extends Annotation> annotationClass) {
    super(annotationClass);
  }

  @Override
  protected Listener<?> generateListener(Object instance, Method method) {
    return method.isAnnotationPresent(Synchronize.class) ?
      new SynchronizedOmnipotentListener(instance, method)
      : new OmnipotentListener(instance, method);
  }
}
