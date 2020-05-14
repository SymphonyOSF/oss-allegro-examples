/*
 * Copyright 2020 Symphony Communication Services, LLC.
 *
 * All Rights Reserved
 */

package com.symphony.s2.allegro.examples.ui;

import com.symphony.oss.allegro.api.IAllegroApi;
import com.symphony.oss.allegro.api.IAllegroMultiTenantApi;
import com.symphony.oss.allegro.api.IObjectPage;
import com.symphony.oss.allegro.api.request.VersionQuery;
import com.symphony.oss.commons.hash.Hash;
import com.symphony.oss.models.object.canon.IAbstractStoredApplicationObject;
import com.symphony.oss.models.object.canon.facade.IDeletedApplicationObject;

class ObjectVersionsView extends AbstractObjectView<IAbstractStoredApplicationObject>
{
  ObjectVersionsView(Hash baseHash, IAllegroMultiTenantApi accessApi, IAllegroApi userApi)
  {
    super(IAbstractStoredApplicationObject.class, baseHash, accessApi, userApi);
  }

  @Override
  protected IObjectPage<IAbstractStoredApplicationObject> fetchPage(IAllegroMultiTenantApi accessApi, Hash hash, String after,
      boolean scanForwards)
  {
    return accessApi.fetchObjectVersionsPage(new VersionQuery.Builder()
        .withBaseHash(hash)
//      .withMaxItems(2)
      .build()
      );
  }
  
  @Override
  void acceptDeletedObject(IDeletedApplicationObject deletedApplicationObject)
  {
    acceptStoredObject(deletedApplicationObject);
  }

  @Override
  Hash getPrimaryKey(IAbstractStoredApplicationObject storedObject)
  {
    return storedObject.getAbsoluteHash();
  }

  @Override
  String getSortKey(IAbstractStoredApplicationObject storedObject)
  {
    return storedObject.getSortKey() + "#" + storedObject.getAbsoluteHash();
  }
}
