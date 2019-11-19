/**
 * Copyright 2019 the original author or authors.
 * <p>
 * Licensed to the IBESTCODE under one or more agreements.
 * The IBESTCODE licenses this file to you under the MIT license.
 * See the LICENSE file in the project root for more information.
 */

package cn.ibestcode.easiness.eventbus.listener;

import cn.ibestcode.easiness.eventbus.annotation.Subscribe;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author WFSO (仵士杰)
 * create by WFSO (仵士杰) at 2019/11/19 19:25
 */
public abstract class AbstractListenerResolver implements ListenerResolver {

  private Class<? extends Annotation> annotationClass;

  public AbstractListenerResolver() {
    this(Subscribe.class);
  }

  public AbstractListenerResolver(Class<? extends Annotation> annotationClass) {
    if (annotationClass == null) {
      throw new NullPointerException("annotationClass can be Null");
    }
    this.annotationClass = annotationClass;
  }

  @Override
  public List<Listener<?>> getListeners(Object instance) {
    if (instance == null) {
      return Collections.emptyList();
    }

    final List<Listener<?>> listeners = new ArrayList<>();

    // 实现了 Listener 接口的
    if (instance instanceof Listener) {
      listeners.add((Listener<?>) instance);
    }

    // 由 Subscribe 注解的方法
    listeners.addAll(getListenersBySubscribeAnnotation(instance));

    return listeners;
  }

  private List<Listener<?>> getListenersBySubscribeAnnotation(Object instance) {
    List<Method> methods = getAnnotatedMethods(instance.getClass());
    if (methods == null || methods.isEmpty()) {
      return Collections.emptyList();
    }
    List<Listener<?>> listeners = new ArrayList<>(methods.size());
    for (Method method : methods) {
      listeners.add(generateListener(instance, method));
    }
    return listeners;
  }

  protected abstract Listener<?> generateListener(Object instance, Method method);


  private List<Method> getAnnotatedMethods(final Class<?> type) {
    final List<Method> methods = new ArrayList<>();
    Class<?> clazz = type;
    while (!Object.class.equals(clazz)) {
      Method[] currentClassMethods = clazz.getDeclaredMethods();
      for (final Method method : currentClassMethods) {
        if (method.isAnnotationPresent(annotationClass) && !method.isSynthetic()) {
          methods.add(method);
        }
      }
      // move to the upper class in the hierarchy in search for more methods
      clazz = clazz.getSuperclass();
    }
    return methods;
  }
}
