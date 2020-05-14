/*
 * Copyright 2020 Symphony Communication Services, LLC.
 *
 * All Rights Reserved
 */

package com.symphony.s2.allegro.examples.ui;

import com.symphony.oss.allegro.api.IAllegroApi;
import com.symphony.oss.models.object.canon.IAbstractStoredApplicationObject;
import com.symphony.oss.models.object.canon.facade.IApplicationObjectPayload;
import com.symphony.oss.models.object.canon.facade.IStoredApplicationObject;

class PartitionObject<T extends IAbstractStoredApplicationObject>
{
  private T                         storedObject_;
  private IApplicationObjectPayload payload_;
  private RuntimeException          payloadException_;

  PartitionObject(T abstracttoredObject, IAllegroApi userApi)
  {
    storedObject_ = abstracttoredObject;
    
    if(abstracttoredObject instanceof IStoredApplicationObject)
    {
      IStoredApplicationObject storedObject = (IStoredApplicationObject)abstracttoredObject;
      
      if(storedObject.getEncryptedPayload() == null)
      {
        payload_ = null;
        payloadException_ = null;
      }
      else
      {
        try
        {
          payload_ = userApi.decryptObject(storedObject);
          payloadException_ = null;
        }
        catch(RuntimeException e)
        {
          payload_ = null;
          payloadException_ = e;
        }
      }
    }
    else
    {
      payload_ = null;
      payloadException_ = null;
    }
  }

  T getStoredObject()
  {
    return storedObject_;
  }

  IApplicationObjectPayload getPayload()
  {
    if(payloadException_ != null)
      throw payloadException_;
    
    return payload_;
  }

  IApplicationObjectPayload getPayloadUnchecked()
  {
    return payload_;
  }
}
