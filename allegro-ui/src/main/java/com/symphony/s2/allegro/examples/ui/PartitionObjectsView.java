/*
 * Copyright 2020 Symphony Communication Services, LLC.
 *
 * All Rights Reserved
 */

package com.symphony.s2.allegro.examples.ui;

import com.symphony.oss.allegro.api.IAllegroApi;
import com.symphony.oss.allegro.api.IAllegroMultiTenantApi;
import com.symphony.oss.allegro.api.IObjectPage;
import com.symphony.oss.allegro.api.request.PartitionQuery;
import com.symphony.oss.commons.hash.Hash;
import com.symphony.oss.models.object.canon.IAbstractStoredApplicationObject;
import com.symphony.oss.models.object.canon.facade.IStoredApplicationObject;

class PartitionObjectsView extends AbstractObjectView<IStoredApplicationObject>
{
  PartitionObjectsView(Hash partitionHash, IAllegroMultiTenantApi accessApi, IAllegroApi userApi)
  {
    super(IStoredApplicationObject.class, partitionHash, accessApi, userApi);
  }

  @Override
  protected IObjectPage<IStoredApplicationObject> fetchPage(IAllegroMultiTenantApi accessApi, Hash hash, String after, boolean scanForwards)
  {
    return accessApi.fetchPartitionObjectPage(new PartitionQuery.Builder()
        .withHash(hash)
        .withAfter(after)
        .withScanForwards(scanForwards)
//        .withMaxItems(2)
        .build()
        );
  }

  @Override
  Hash getPrimaryKey(IAbstractStoredApplicationObject storedObject)
  {
    return storedObject.getBaseHash();
  }

  @Override
  String getSortKey(IAbstractStoredApplicationObject storedObject)
  {
    return storedObject.getSortKey().toString();
  }
}
