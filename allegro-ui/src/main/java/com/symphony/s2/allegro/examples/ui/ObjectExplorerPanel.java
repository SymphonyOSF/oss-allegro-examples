/*
 * Copyright 2020 Symphony Communication Services, LLC.
 *
 * All Rights Reserved
 */

package com.symphony.s2.allegro.examples.ui;

import javax.servlet.http.HttpServletRequest;

import com.symphony.oss.allegro.api.IAllegroApi;
import com.symphony.oss.allegro.api.IAllegroMultiTenantApi;
import com.symphony.oss.canon.runtime.exception.PermissionDeniedException;
import com.symphony.oss.commons.hash.Hash;
import com.symphony.oss.fugue.server.http.ui.servlet.UIHtmlWriter;
import com.symphony.oss.models.object.canon.IAbstractStoredApplicationObject;
import com.symphony.oss.models.object.canon.facade.IApplicationObjectPayload;
import com.symphony.oss.models.object.canon.facade.IStoredApplicationObject;

class ObjectExplorerPanel extends AllegroUiPanel
{
  private static final String PANEL_NAME = "Object Explorer";
  public static final String PANEL_ID = "objectExplorer";

  ObjectExplorerPanel(IAllegroMultiTenantApi accessApi, IAllegroApi userApi)
  {
    super(PANEL_ID, PANEL_NAME, accessApi, userApi);
  }
  
  @Override
  public void handleContent(HttpServletRequest req, UIHtmlWriter out)
  {
    Hash                              partitionHash = getPartitionHash(req, out);
    Hash                              absoluteHash = getAbsoluteHash(req, out);
    String                            sortKey = req.getParameter(SORT_KEY);
    IAbstractStoredApplicationObject  object = null;

    try
    {
      if(absoluteHash != null)
      {
        object = accessApi_.fetchAbsolute(absoluteHash);
        partitionHash = object.getPartitionHash();
        sortKey = object.getSortKey().asString();
      }
      else if(partitionHash != null && sortKey != null)
      {
        object = accessApi_.fetchObject(partitionHash, sortKey);
        absoluteHash = object.getAbsoluteHash();
      }
    }
    catch(PermissionDeniedException e)
    {
      out.printError("Object Access Denied", e);
    }
    
    out.printElement("h1", "Object Explorer");
    
    
    out.openElement("table", "class", "w3-table w3-striped w3-bordered w3-border w3-hoverable w3-white");
    
    out.openElement("tr");
    out.printElement("td", "Absolute Hash");
    out.openElement("td");
    out.openElement("form", "method", "POST");
    out.printTextInput(ABSOLUTE_HASH, absoluteHash == null ? "" : absoluteHash.toStringBase64(), "size", "64", CLASS, CODE_CLASS);
    out.printElement("input", null, "type", "submit", "value", "Search");
    out.closeElement(); // form
    out.closeElement(); // td
    out.closeElement(); //tr 
    
    out.openElement("tr");
    out.printElement("td", "Partition Hash");
    out.openElement("td");
    out.openElement("form", "method", "POST");
    out.printTextInput(PARTITION_HASH, partitionHash == null ? "" : partitionHash.toStringBase64(), "size", "64", CLASS, CODE_CLASS);
    out.printElement("br");
    out.printTextInput(SORT_KEY, sortKey == null ? "" : sortKey, "size", "64");
    out.printElement("input", null, "type", "submit", "value", "Search");
    out.closeElement(); // form
    out.closeElement(); // td
    out.closeElement(); //tr 
    
    out.closeElement(); // table
    

    if(object != null)
    {
      printSummary(out, object);
      
      printObject(out, "Stored Application Object", object);
      
      if(object instanceof IStoredApplicationObject)
      {
        IStoredApplicationObject storedObject = (IStoredApplicationObject) object;

        if(storedObject.getHeader() != null)
        {
          printObject(out, "Header", storedObject.getHeader());
        }
        
        if(storedObject.getEncryptedPayload() != null)
        {
          try
          {
            IApplicationObjectPayload payload = userApi_.decryptObject(storedObject);
            printObject(out, "Decrypted Payload", payload);
          }
          catch(RuntimeException e)
          {
            out.printElement("h2", "Encrypted Payload");
            out.printError(e);
          }
        }
      }
    }
  }

  private void printSummary(UIHtmlWriter out, IAbstractStoredApplicationObject object)
  {

    out.printElement("h2", "Object Summary");
    
    out.openElement("table", "class", "w3-table w3-striped w3-bordered w3-border w3-hoverable w3-white");

    out.openElement("tr");
    out.printElement("td", "Absolute Hash");
    out.printElement("td", object.getAbsoluteHash().toString(), CLASS, CODE_CLASS);
    out.closeElement();

    out.openElement("tr");
    out.printElement("td", "Base Hash");
    out.openElement("td", CLASS, CODE_CLASS);
    out.printElement("a", object.getBaseHash().toStringBase64(), "href", 
        getPath(ObjectVersionsPanel.PANEL_ID) + "?" + BASE_HASH + "=" + object.getBaseHash().toStringUrlSafeBase64());
    out.closeElement();
    out.closeElement();

    out.openElement("tr");
    out.printElement("td", "Partition Hash");
    out.openElement("td", CLASS, CODE_CLASS);
    out.printElement("a", object.getPartitionHash().toStringBase64(), "href", 
        getPath(PartitionExplorerPanel.PANEL_ID) + "?" + PARTITION_HASH + "=" + object.getPartitionHash().toStringUrlSafeBase64());
    out.closeElement();
    out.closeElement();
    

    out.openElement("tr");
    out.printElement("td", "Sort Key");
    out.printElement("td", object.getSortKey());
    out.closeElement();

    out.closeElement(); // table
  }
}
