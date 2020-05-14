/*
 * Copyright 2020 Symphony Communication Services, LLC.
 *
 * All Rights Reserved
 */

package com.symphony.s2.allegro.examples.ui;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.symphony.oss.allegro.api.IAllegroMultiTenantApi;
import com.symphony.oss.allegro.api.request.PartitionQuery;
import com.symphony.oss.commons.fault.CodingFault;
import com.symphony.oss.commons.hash.Hash;
import com.symphony.oss.models.object.canon.facade.IPartition;

class PartitionProvider implements Iterable<IPartition>
{
  private final IAllegroMultiTenantApi accessApi_;
  
  private LoadingCache<Hash, CacheEntry<IPartition>> partitionCache_ = CacheBuilder.newBuilder()
      .maximumSize(1000)
      //.expireAfterWrite(10, TimeUnit.MINUTES)
      .build(
          new CacheLoader<Hash, CacheEntry<IPartition>>()
          {
            @Override
            public CacheEntry<IPartition> load(Hash partitionHash)
            {
              try
              {
                IPartition partition = accessApi_.fetchPartition(new PartitionQuery.Builder()
                    .withHash(partitionHash)
                    .build());
                
                return new CacheEntry<IPartition>(partition);
              }
              catch(RuntimeException e)
              {
                return new CacheEntry<IPartition>(e);
              }
            }
          });

  PartitionProvider(IAllegroMultiTenantApi accessApi)
  {
    accessApi_ = accessApi;
  }
  
  IPartition fetch(Hash partitionHash)
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
  
  Collection<IPartition> getAll()
  {
    List<IPartition> result = new LinkedList<>();
    
    for(CacheEntry<IPartition> entry : partitionCache_.asMap().values())
    {
      if(entry.getObject() != null)
        result.add(entry.getObject());
    }
    
    return result;
  }

  @Override
  public Iterator<IPartition> iterator()
  {
    return getAll().iterator();
  }
}
