/*
 * Copyright 2020 Symphony Communication Services, LLC.
 *
 * All Rights Reserved
 */

package com.symphony.s2.allegro.examples.ui;

class CacheEntry<E>
{
  private final E object_;
  private final RuntimeException exception_;
  
  CacheEntry(E object)
  {
    object_ = object;
    exception_ = null;
  }

  CacheEntry(RuntimeException exception)
  {
    object_ = null;
    exception_ = exception;
  }
  
  @Override
  public String toString()
  {
    if(object_ == null)
      return exception_.getClass().getSimpleName() + ": " + exception_.getLocalizedMessage();
    
    return object_.toString();
  }

  E get()
  {
    if(object_ == null)
      throw exception_;
    
    return object_;
  }

  E getObject()
  {
    return object_;
  }
}
