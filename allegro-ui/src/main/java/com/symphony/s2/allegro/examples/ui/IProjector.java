/*
 * Copyright 2020 Symphony Communication Services, LLC.
 *
 * All Rights Reserved
 */

package com.symphony.s2.allegro.examples.ui;

@FunctionalInterface
public interface IProjector<T>
{
  Projection project(T payload);
}
