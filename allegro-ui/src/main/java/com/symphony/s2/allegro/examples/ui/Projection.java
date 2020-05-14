/*
 * Copyright 2020 Symphony Communication Services, LLC.
 *
 * All Rights Reserved
 */

package com.symphony.s2.allegro.examples.ui;

import java.util.HashMap;
import java.util.Map;

import com.symphony.oss.commons.hash.Hash;

public class Projection
{
  public static class Error extends Projection
  {
    private String message_;

    public Error(String message)
    {
      message_ = message;
    }

    public String getMessage()
    {
      return message_;
    }
  }
  
  public static class AbstractAttribute<T> extends Projection
  {
    private String name_;
    private T value_;
    
    public AbstractAttribute(String name, T value)
    {
      name_ = name;
      value_ = value;
    }

    public String getName()
    {
      return name_;
    }

    public T getValue()
    {
      return value_;
    }
  }
  
  public static class Attribute extends AbstractAttribute<String>
  {
    public Attribute(String name, String value)
    {
      super(name, value);
    }
  }
  
  public static class AbsoluteHashAttribute extends AbstractAttribute<Hash>
  {
    public AbsoluteHashAttribute(Hash value)
    {
      super("Absolute Hash", value);
    }
  }
  
  public static class BaseHashAttribute extends AbstractAttribute<Hash>
  {
    public BaseHashAttribute(Hash value)
    {
      super("Base Hash", value);
    }
  }
  
  public static class PartitionHashAttribute extends AbstractAttribute<Hash>
  {
    public PartitionHashAttribute(Hash value)
    {
      super("Partition Hash", value);
    }
  }
  
  public static class AttributeSet extends Projection
  {
    private Map<String, AbstractAttribute<?>> attributeMap_ = new HashMap<>();
    
    public AttributeSet with(AbstractAttribute<?> attribute)
    {
      attributeMap_.put(attribute.getName(), attribute);
      
      return this;
    }
    
    public AttributeSet with(String name, String value)
    {
      attributeMap_.put(name, new Attribute(name, value));
      
      return this;
    }

    public Map<String, AbstractAttribute<?>> getAttributeMap()
    {
      return attributeMap_;
    }
  }
}
