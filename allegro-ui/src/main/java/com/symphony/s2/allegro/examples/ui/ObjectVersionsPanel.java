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

class ObjectVersionsPanel extends ObjectPanel
{
  static final String PANEL_NAME = "Object Versions";
  static final String PANEL_ID = "objectVersions";
  
//
//  private final PartitionProvider            partitionProvider_;
  private final ObjectVersionsViewProvider objectVersionsViewProvider_;

  ObjectVersionsPanel(ObjectVersionsViewProvider objectVersionsViewProvider, IAllegroMultiTenantApi accessApi, IAllegroApi userApi)
  {
    super(PANEL_ID, PANEL_NAME, accessApi, userApi);
    
    objectVersionsViewProvider_ = objectVersionsViewProvider;
  }
  
  @Override
  public void handleContent(HttpServletRequest req, UIHtmlWriter out)
  {
    Hash                              partitionHash = getPartitionHash(req, out);
    Hash                              baseHash = getBaseHash(req, out);
    String                            sortKey = req.getParameter(SORT_KEY);
    boolean                           loadAfter = "true".equalsIgnoreCase(req.getParameter(LOAD_AFTER));
    IAbstractStoredApplicationObject  currentObject = null;

    try
    {
      if(baseHash != null)
      {
        currentObject = accessApi_.fetchCurrent(baseHash);
        partitionHash = currentObject.getPartitionHash();
        sortKey = currentObject.getSortKey().asString();
      }
      else if(partitionHash != null && sortKey != null)
      {
        currentObject = accessApi_.fetchObject(partitionHash, sortKey);
        baseHash = currentObject.getBaseHash();
      }
    }
    catch(PermissionDeniedException e)
    {
      out.printError("Object Access Denied", e);
    }
    
    out.printElement("h1", "Object Versions");
    
    
    out.openElement("table", "class", "w3-table w3-striped w3-bordered w3-border w3-hoverable w3-white");

    out.openElement("tr");
    out.printElement("td", "Base Hash");
    out.openElement("td");
    out.openElement("form", "method", "POST");
    out.printTextInput(BASE_HASH, baseHash == null ? "" : baseHash.toStringBase64(), "size", "64", CLASS, CODE_CLASS);
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
    

    

    if(currentObject != null)
    {
      printSummary(out, currentObject);
      
      printObject(out, "Stored Application Object", currentObject);
      
      if(currentObject instanceof IStoredApplicationObject)
      {
        IStoredApplicationObject storedObject = (IStoredApplicationObject) currentObject;

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

      out.printElement("h2", "Object Versions");
      
      
      out.openElement("table", "class", "w3-table w3-striped w3-bordered w3-border w3-hoverable w3-white");
      
      out.openElement("tr");
      out.printElement("td", "Absolute Hash");
      out.printElement("td", "Base Hash");
      out.printElement("td", "Created Date");
      out.printElement("td", "Header Type");
      out.printElement("td", "Payload Type");
      out.closeElement(); //tr 
      
      ObjectVersionsView view = objectVersionsViewProvider_.fetch(baseHash);
      
      if(loadAfter)
        view.loadAfter();
      
      view.forEach((object) ->
      {
        out.openElement("tr");
        
        printStoredObject(out, object.getStoredObject());
        
        if(object.getStoredObject() instanceof IStoredApplicationObject)
          printHeader(out, ((IStoredApplicationObject)object.getStoredObject()).getHeader());
        else
          printHeader(out, null);
        
        try
        {
          printPayload(out, object.getPayload());
        }
        catch(RuntimeException e)
        {
          printPayloadException(out, e);
        }
  
        out.closeElement(); //tr
      });
      
      if(view.hasMoreAfter())
      {
        out.openElement("tr");
        out.openElement("td", "colspan", "5", "style", "text-align:center");
        out.printElement("a", "Load More", "href", "?" + PARTITION_HASH + "=" + currentObject.getPartitionHash().toStringUrlSafeBase64() + "&" + LOAD_AFTER + "=true");
        out.closeElement(); //td
        out.closeElement(); //tr
      }
  
      out.closeElement(); // table
    }
  }

  private void printSummary(UIHtmlWriter out, IAbstractStoredApplicationObject object)
  {
    out.printElement("h2", "Object Summary");
    
    out.openElement("table", "class", "w3-table w3-striped w3-bordered w3-border w3-hoverable w3-white");

    out.openElement("tr");
    out.printElement("td", "Absolute Hash");
    out.printElement("td", object.getAbsoluteHash(), CLASS, CODE_CLASS);
    out.closeElement();

    out.openElement("tr");
    out.printElement("td", "Partition Hash");
    out.openElement("td");
    out.printElement("a", object.getPartitionHash().toStringBase64(), "href", 
        getPath(PartitionExplorerPanel.PANEL_ID) + "?" + PARTITION_HASH + "=" + object.getPartitionHash().toStringUrlSafeBase64(), CLASS, CODE_CLASS);
    out.closeElement();
    out.closeElement();
    

    out.openElement("tr");
    out.printElement("td", "Sort Key");
    out.printElement("td", object.getSortKey());
    out.closeElement();

    out.closeElement(); // table
  }
}
