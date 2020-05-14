/*
 * Copyright 2020 Symphony Communication Services, LLC.
 *
 * All Rights Reserved
 */

package com.symphony.s2.allegro.examples.ui;

import com.symphony.oss.allegro.api.IAllegroApi;
import com.symphony.oss.allegro.api.IAllegroMultiTenantApi;
import com.symphony.oss.fugue.server.http.ui.servlet.UIHtmlWriter;
import com.symphony.oss.models.object.canon.IAbstractStoredApplicationObject;
import com.symphony.oss.models.object.canon.facade.IApplicationObjectHeader;
import com.symphony.oss.models.object.canon.facade.IApplicationObjectPayload;

abstract class ObjectPanel extends AllegroUiPanel
{

  ObjectPanel(String name, IAllegroMultiTenantApi accessApi, IAllegroApi userApi)
  {
    super(name, accessApi, userApi);
  }

  ObjectPanel(String id, String name, IAllegroMultiTenantApi accessApi, IAllegroApi userApi)
  {
    super(id, name, accessApi, userApi);
  }

  void printPayload(UIHtmlWriter out, IApplicationObjectPayload payload)
  {
    if(payload == null)
    {
      out.printElement("td"); 
    }
    else
    {
      out.printElement("td", payload.getCanonType());
    }
  }

  void printPayloadException(UIHtmlWriter out, RuntimeException e)
  {
    out.printElement("td", e.getClass().getSimpleName() + ": " + e.getLocalizedMessage());
  }

  void printHeader(UIHtmlWriter out, IApplicationObjectHeader header)
  {
    if(header == null)
    {
      out.printElement("td"); 
    }
    else
    {
      out.printElement("td", header.getCanonType());
    }
  }

  void printStoredObject(UIHtmlWriter out, IAbstractStoredApplicationObject object)
  {
    out.openElement("td");
    out.printElement("a", object.getAbsoluteHash().toStringBase64(), "href", 
        getPath(ObjectExplorerPanel.PANEL_ID) + "?" + ABSOLUTE_HASH + "=" + object.getAbsoluteHash().toStringUrlSafeBase64(), CLASS, CODE_CLASS);
    out.closeElement();// td

    out.openElement("td");
    out.printElement("a", object.getBaseHash().toStringBase64(), "href", 
        getPath(ObjectVersionsPanel.PANEL_ID) + "?" + BASE_HASH + "=" + object.getBaseHash().toStringUrlSafeBase64(), CLASS, CODE_CLASS);
    out.closeElement();// td
    
    out.printElement("td", object.getCreatedDate());
  }
}
