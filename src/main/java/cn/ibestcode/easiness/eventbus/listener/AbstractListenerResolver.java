/**
 * Copyright 2019 the original author or authors.
 * <p>
 * Licensed to the IBESTCODE under one or more agreements.
 * The IBESTCODE licenses this file to you under the MIT license.
 * See the LICENSE file in the project root for more information.
 */

package cn.ibestcode.easiness.eventbus.listener;

import cn.ibestcode.easiness.eventbus.annotation.EventListener;
import cn.ibestcode.easiness.eventbus.annotation.Subscribe;

import java.lang.annotation.*;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.util.*;

/**
 * @author WFSO (仵士杰)
 * create by WFSO (仵士杰) at 2019/11/19 19:25
 */
public abstract class AbstractListenerResolver implements ListenerResolver {

  private Class<? extends Annotation> subscribeAnnotation;
  private Class<? extends Annotation> listenerAnnotation;

  public AbstractListenerResolver() {
    this(Subscribe.class, EventListener.class);
  }

  public AbstractListenerResolver(Class<? extends Annotation> subscribeAnnotation,
                                  Class<? extends Annotation> listenerAnnotation) {
    if (subscribeAnnotation == null) {
      throw new NullPointerException("subscribeAnnotation can be Null");
    }
    if (listenerAnnotation == null) {
      throw new NullPointerException("listenerAnnotation can be Null");
    }
    this.subscribeAnnotation = subscribeAnnotation;
    this.listenerAnnotation = listenerAnnotation;
  }

  @Override
  public List<Listener> getListeners(Object instance) {
    if (instance == null) {
      return Collections.emptyList();
    }

    final List<Listener> listeners = new ArrayList<>();

    // 实现了 Listener 接口的
    if (instance instanceof Listener) {
      listeners.add((Listener) instance);
    }

    // 由 Subscribe 注解的方法
    if (isAnnotationPresent(instance.getClass(), listenerAnnotation)) {
      listeners.addAll(getListenersBySubscribeAnnotation(instance));
    }

    return listeners;
  }

  private List<Listener> getListenersBySubscribeAnnotation(Object instance) {
    List<Method> methods = getAnnotatedMethods(instance.getClass());
    if (methods == null || methods.isEmpty()) {
      return Collections.emptyList();
    }
    List<Listener> listeners = new ArrayList<>(methods.size());
    for (Method method : methods) {
      listeners.add(generateListener(instance, method));
    }
    return listeners;
  }

  protected abstract Listener generateListener(Object instance, Method method);


  private List<Method> getAnnotatedMethods(final Class type) {
    final List<Method> methods = new ArrayList<>();
    Class clazz = type;
    while (!Object.class.equals(clazz)) {
      Method[] currentClassMethods = clazz.getDeclaredMethods();
      for (final Method method : currentClassMethods) {
        if (isAnnotationPresent(method, subscribeAnnotation) && !method.isSynthetic()) {
          methods.add(method);
        }
      }
      // 获取其父类，以搜索更多的方法
      // 直到 Object 类为止
      clazz = clazz.getSuperclass();
    }
    return methods;
  }

  private static final List<Class<? extends Annotation>> metaAnnotations = Arrays.asList(
    Target.class, Retention.class, Inherited.class, Documented.class
  );

  private boolean isAnnotationPresent(AnnotatedElement element, Class<? extends Annotation> annotationClass) {
    Set<AnnotatedElement> processedClasses = new HashSet<>();
    Queue<AnnotatedElement> pendingQueue = new LinkedList<>();
    pendingQueue.offer(element);
    while ((element = pendingQueue.poll()) != null) {
      if (element.isAnnotationPresent(annotationClass)) {
        return true;
      }
      processedClasses.add(element);
      for (Annotation annotation : element.getAnnotations()) {
        Class<? extends Annotation> annotationType = annotation.annotationType();
        if (!metaAnnotations.contains(annotationType) && !processedClasses.contains(annotationType)) {
          pendingQueue.offer(annotationType);
        }

      }
    }
    return false;
  }
}
