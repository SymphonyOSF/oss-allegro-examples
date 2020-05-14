/*
 * Copyright 2020 Symphony Communication Services, LLC.
 *
 * All Rights Reserved
 */

package com.symphony.s2.allegro.examples.ui;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

import com.symphony.oss.allegro.api.IAllegroApi;
import com.symphony.oss.allegro.api.IAllegroMultiTenantApi;
import com.symphony.oss.fugue.server.http.ui.servlet.UIHtmlWriter;

abstract class RenderingPanel extends AllegroUiPanel implements BiConsumer<UIHtmlWriter, PartitionObject>
{
  private final ProjectorManager              projectorManager_;
  private final Map<Class<? extends Projection>, BiConsumer<UIHtmlWriter, ?>> consumerMap_ = new HashMap<>();
  
  RenderingPanel(String id, String name, IAllegroMultiTenantApi accessApi, IAllegroApi userApi, ProjectorManager projectorManager)
  {
    super(id, name, accessApi, userApi);
    
    projectorManager_ = projectorManager;
    
    with(Projection.Error.class, (out, renderable) ->
    {
      out.println("showError('" + renderable.getMessage() + "');");
    });
    
    with(Projection.AttributeSet.class, (out, renderable) ->
    {
      //out.println("re('" + renderable.getMessage() + "');");
    });
  }
  
  <T extends Projection> RenderingPanel with(Class<T> type, BiConsumer<UIHtmlWriter, T> consumer)
  {
    consumerMap_.put(type, consumer);
    
    return this;
  }
  
  @SuppressWarnings("unchecked")
  private <T extends Projection> BiConsumer<UIHtmlWriter, Projection> getConsumer(Class<T> type)
  {
    return (BiConsumer<UIHtmlWriter, Projection>) consumerMap_.get(type);
  }

  @Override
  public void accept(UIHtmlWriter out, PartitionObject partitionObject)
  {
    Projection renderable = projectorManager_.render(partitionObject);
    BiConsumer<UIHtmlWriter, Projection> consumer = getConsumer(renderable.getClass());
    
    if(consumer == null)
    {
      out.println("showError('Unknown Renderable type " + renderable.getClass() + "');");
    }
    else
    {
      consumer.accept(out, renderable);
    }
  }
}
