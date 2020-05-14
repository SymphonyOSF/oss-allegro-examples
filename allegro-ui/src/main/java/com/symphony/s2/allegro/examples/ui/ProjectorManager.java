/*
 *
 *
 * Copyright 2019 Symphony Communication Services, LLC.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.symphony.s2.allegro.examples.ui;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.symphony.oss.commons.fluent.BaseAbstractBuilder;
import com.symphony.oss.models.object.canon.IAbstractStoredApplicationObject;
import com.symphony.oss.models.object.canon.facade.IApplicationObjectPayload;

/**
 * Manager of Renderers.
 * 
 * @author Bruce Skingle
 */
public class ProjectorManager
{
  private final ImmutableMap<Class<?>, IProjector<?>> projectorMap_;
  private final ImmutableList<Class<?>>              projectorTypeList_;
  private final IProjector<Object>                    defaultConsumer_;
    
  ProjectorManager(AbstractBuilder<?,?> builder)
  {
    projectorMap_          = ImmutableMap.copyOf(builder.projectorMap_);
    projectorTypeList_     = ImmutableList.copyOf(builder.projectorTypeList_);
    defaultConsumer_      = builder.defaultConsumer_;
  }
  
  /**
   * Builder for RendererManager.
   * 
   * @author Bruce Skingle
   *
   */
  public static class Builder extends AbstractBuilder<Builder, ProjectorManager>
  {
    /**
     * Constructor.
     */
    public Builder()
    {
      super(Builder.class);
    }

    @Override
    protected ProjectorManager construct()
    {
      return new ProjectorManager(this);
    }
  }
  /**
   * AbstractBuilder.
   * 
   * @author Bruce Skingle
   *
   * @param <T> Concrete type of the builder for fluent methods.
   * @param <B> Concrete type of the built object for fluent methods.
   */
  public static abstract class AbstractBuilder<T extends AbstractBuilder<T,B>, B extends ProjectorManager> extends BaseAbstractBuilder<T,B>
  {
    private Map<Class<?>, IProjector<?>> projectorMap_      = new HashMap<>();
    private List<Class<?>>              projectorTypeList_ = new LinkedList<>();
    private IProjector<Object>           defaultConsumer_  = new IProjector<Object>()
      {
        @Override
        public Projection project(Object payload)
        {
          return new Projection.Error("No projector found for type " + payload.getClass());
        }
      };
    
    AbstractBuilder(Class<T> type)
    {
      super(type);
      
      withConsumer(IAbstractStoredApplicationObject.class, (object) ->
      {
        return new Projection.AttributeSet()
            .with(new Projection.AbsoluteHashAttribute(object.getAbsoluteHash()))
            .with(new Projection.BaseHashAttribute(object.getBaseHash()))
            .with(new Projection.PartitionHashAttribute(object.getPartitionHash()))
            ;
      });
    }
    
    protected IProjector<Object> getDefaultConsumer()
    {
      return defaultConsumer_;
    }
    
    protected <C> T withConsumer(Class<C> type, IProjector<C> projector)
    {
      projectorMap_.put(type, projector);
      projectorTypeList_.add(type);
      
      return self();
    }
    
    protected T withDefaultConsumer(IProjector<Object> defaultConsumer)
    {
      defaultConsumer_ = defaultConsumer;
      
      return self();
    }
  }

  /**
   * 
   * @return All projectors.
   */
  public ImmutableCollection<IProjector<?>> getConsumers()
  {
    return projectorMap_.values();
  }

  /**
   * Dispatch the given object to the most appropriate projector.
   * 
   * @param partitionObject An object to be consumed.
   * 
   * @return A Renderable object.
   */
  public Projection render(PartitionObject<?> partitionObject)
  {
    Projection result =  null;
    IApplicationObjectPayload payload = partitionObject.getPayloadUnchecked();
    
    if(payload != null)
    {
      result = consume(payload);
    }
    
    if(result == null)
      result = consume(partitionObject.getStoredObject());
    
    if(result == null)
      result = defaultConsumer_.project(partitionObject.getStoredObject());
    
    return result;
  }
  
  

  @SuppressWarnings({ "rawtypes", "unchecked" })
  private Projection consume(Object object)
  {
    Class<? extends Object> type = object.getClass();
    IProjector projector = projectorMap_.get(type);
    
    if(projector != null)
    {
      return projector.project(object);
    }
    
    Class<?> bestType = null;
    
    for(Class<?> t : projectorTypeList_)
    {
      if(!t.isAssignableFrom(type))
        continue;
      
      if(bestType == null || bestType.isAssignableFrom(t))
        bestType = t;
      
    }
    
    if(bestType == null)
      return null;
    
    return ((IProjector)projectorMap_.get(bestType)).project(object);
  }
}
