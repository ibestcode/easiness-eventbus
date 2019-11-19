/**
 * Copyright 2019 the original author or authors.
 * <p>
 * Licensed to the IBESTCODE under one or more agreements.
 * The IBESTCODE licenses this file to you under the MIT license.
 * See the LICENSE file in the project root for more information.
 */

package cn.ibestcode.easiness.eventbus;

import cn.ibestcode.easiness.eventbus.dispatcher.Dispatcher;
import cn.ibestcode.easiness.eventbus.listener.Listener;
import cn.ibestcode.easiness.eventbus.listener.ListenerResolver;
import cn.ibestcode.easiness.eventbus.listener.OmnipotentListenerResolver;

import java.lang.annotation.Annotation;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * @author WFSO (仵士杰)
 * create by WFSO (仵士杰) at 2019/11/19 19:56
 */
public class AbstractEventBus implements EventBus {

  // 使用线程安全的容器类
  private final ConcurrentMap<Class<?>, CopyOnWriteArraySet<Listener<?>>> listeners = new ConcurrentHashMap<>();

  private final ListenerResolver listenerResolver;

  private final Dispatcher dispatcher;

  public AbstractEventBus(Dispatcher dispatcher) {
    this.listenerResolver = new OmnipotentListenerResolver();
    this.dispatcher = dispatcher;
  }

  public AbstractEventBus(Class<? extends Annotation> annotationClass, Dispatcher dispatcher) {
    this.listenerResolver = new OmnipotentListenerResolver(annotationClass);
    this.dispatcher = dispatcher;
  }

  @Override
  public void register(Object listener) {
    List<Listener<?>> listenerList = listenerResolver.getListeners(listener);
    Map<Class<?>, List<Listener<?>>> map = mergeToMap(listenerList);

    for (Map.Entry<Class<?>, List<Listener<?>>> entry : map.entrySet()) {
      Class<?> eventType = entry.getKey();
      List<Listener<?>> list = entry.getValue();

      CopyOnWriteArraySet<Listener<?>> listenerSet = listeners.get(eventType);

      if (listenerSet == null) {
        CopyOnWriteArraySet<Listener<?>> newSet = new CopyOnWriteArraySet<>();
        CopyOnWriteArraySet<Listener<?>> tmpSet = listeners.putIfAbsent(eventType, newSet);
        listenerSet = tmpSet == null ? newSet : tmpSet;
      }

      listenerSet.addAll(list);
    }

  }

  @Override
  public void unregister(Object listener) {
    List<Listener<?>> listenerList = listenerResolver.getListeners(listener);
    Map<Class<?>, List<Listener<?>>> map = mergeToMap(listenerList);

    for (Map.Entry<Class<?>, List<Listener<?>>> entry : map.entrySet()) {
      Class<?> eventType = entry.getKey();
      List<Listener<?>> list = entry.getValue();

      CopyOnWriteArraySet<Listener<?>> listenerSet = listeners.get(eventType);

      if (listenerSet == null || !listenerSet.removeAll(list)) {
        throw new IllegalArgumentException(
          "missing event subscriber for an annotated method. Is " + listener + " registered?");
      }

    }
  }

  @Override
  public void post(Object event) {
    List<Listener<?>> listeners = getListenersByEvent(event);
    dispatcher.dispatch(event,listeners);
  }

  private Map<Class<?>, List<Listener<?>>> mergeToMap(List<Listener<?>> listeners) {
    final Map<Class<?>, List<Listener<?>>> map = new HashMap<>();
    for (Listener listener : listeners) {
      if (!map.containsKey(listener.supports())) {
        map.put(listener.supports(), new ArrayList<>());
      }
      map.get(listener.supports()).add(listener);
    }
    return map;
  }

  private List<Listener<?>> getListenersByEvent(Object event) {
    List<Listener<?>> listenerList = new ArrayList<>();
    for (Class<?> eventType : getClassHierarchy(event.getClass())) {
      listenerList.addAll(listeners.get(eventType));
    }
    return listenerList;
  }

  // 获取类的承继结构
  private List<Class<?>> getClassHierarchy(Class<?> clazz) {
    List<Class<?>> classes = new ArrayList<>();
    while (!Object.class.equals(clazz)) {
      classes.add(clazz);
      // move to the upper class in the hierarchy in search for more methods
      clazz = clazz.getSuperclass();
    }
    return classes;
  }

}
