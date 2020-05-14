/*
 * Copyright 2020 Symphony Communication Services, LLC.
 *
 * All Rights Reserved
 */

package com.symphony.s2.allegro.examples.ui;

import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.symphony.oss.allegro.api.IAllegroApi;
import com.symphony.oss.allegro.api.IAllegroMultiTenantApi;
import com.symphony.oss.commons.fault.CodingFault;
import com.symphony.oss.commons.hash.Hash;
import com.symphony.oss.models.object.canon.IAbstractStoredApplicationObject;

class ObjectVersionsViewProvider implements Consumer<IAbstractStoredApplicationObject>
{
  private final IAllegroMultiTenantApi accessApi_;
  private final IAllegroApi userApi_;
  private final LoadingCache<Hash, CacheEntry<ObjectVersionsView>> partitionCache_ = CacheBuilder.newBuilder()
      .maximumSize(1000)
      //.expireAfterWrite(10, TimeUnit.MINUTES)
      .build(
          new CacheLoader<Hash, CacheEntry<ObjectVersionsView>>()
          {
            @Override
            public CacheEntry<ObjectVersionsView> load(Hash baseHash)
            {
              try
              {
                return new CacheEntry<ObjectVersionsView>(new ObjectVersionsView(baseHash, accessApi_, userApi_));
              }
              catch(RuntimeException e)
              {
                return new CacheEntry<ObjectVersionsView>(e);
              }
            }
          });


  ObjectVersionsViewProvider(IAllegroMultiTenantApi accessApi, IAllegroApi userApi)
  {
    accessApi_ = accessApi;
    userApi_ = userApi;
  }
  
  ObjectVersionsView getIfPresent(Hash baseHash)
  {
    CacheEntry<ObjectVersionsView> item = partitionCache_.getIfPresent(baseHash);
    
    if(item == null)
      return null;
    
    return item.getObject();
  }
  
  ObjectVersionsView fetch(Hash baseHash)
  {
    try
    {
      return partitionCache_.get(baseHash).get();
    }
    catch (ExecutionException e)
    {
      throw new CodingFault(e);
    }
  }

  @Override
  public void accept(IAbstractStoredApplicationObject abstractStoredObject)
  {
    CacheEntry<ObjectVersionsView> ce = partitionCache_.getIfPresent(abstractStoredObject.getPartitionHash());
    
    if(ce != null && ce.getObject() != null)
      ce.getObject().accept(abstractStoredObject);
  }
}
