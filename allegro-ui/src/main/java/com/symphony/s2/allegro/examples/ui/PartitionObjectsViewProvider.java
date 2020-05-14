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

class PartitionObjectsViewProvider implements Consumer<IAbstractStoredApplicationObject>
{
  private final IAllegroMultiTenantApi accessApi_;
  private final IAllegroApi userApi_;
  private final LoadingCache<Hash, CacheEntry<PartitionObjectsView>> partitionCache_ = CacheBuilder.newBuilder()
      .maximumSize(1000)
      //.expireAfterWrite(10, TimeUnit.MINUTES)
      .build(
          new CacheLoader<Hash, CacheEntry<PartitionObjectsView>>()
          {
            @Override
            public CacheEntry<PartitionObjectsView> load(Hash partitionHash)
            {
              try
              {
                return new CacheEntry<PartitionObjectsView>(new PartitionObjectsView(partitionHash, accessApi_, userApi_));
              }
              catch(RuntimeException e)
              {
                return new CacheEntry<PartitionObjectsView>(e);
              }
            }
          });


  PartitionObjectsViewProvider(IAllegroMultiTenantApi accessApi, IAllegroApi userApi)
  {
    accessApi_ = accessApi;
    userApi_ = userApi;
  }
  
  PartitionObjectsView getIfPresent(Hash partitionHash)
  {
    CacheEntry<PartitionObjectsView> item = partitionCache_.getIfPresent(partitionHash);
    
    if(item == null)
      return null;
    
    return item.getObject();
  }
  
  PartitionObjectsView fetch(Hash partitionHash)
  {
    try
    {
      return partitionCache_.get(partitionHash).get();
    }
    catch (ExecutionException e)
    {
      throw new CodingFault(e);
    }
  }

  @Override
  public void accept(IAbstractStoredApplicationObject abstractStoredObject)
  {
    CacheEntry<PartitionObjectsView> ce = partitionCache_.getIfPresent(abstractStoredObject.getPartitionHash());
    
    if(ce != null && ce.getObject() != null)
      ce.getObject().accept(abstractStoredObject);
  }
}
