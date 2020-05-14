/*
 * Copyright 2020 Symphony Communication Services, LLC.
 *
 * All Rights Reserved
 */

package com.symphony.s2.allegro.examples.ui;

import java.util.Collection;

import javax.servlet.http.HttpServletRequest;

import com.symphony.oss.allegro.api.IAllegroApi;
import com.symphony.oss.allegro.api.IAllegroMultiTenantApi;
import com.symphony.oss.allegro.api.request.PartitionQuery;
import com.symphony.oss.commons.hash.Hash;
import com.symphony.oss.fugue.server.http.ui.servlet.UIHtmlWriter;
import com.symphony.oss.models.object.canon.IAbstractStoredApplicationObject;
import com.symphony.oss.models.object.canon.INamedUserIdObject;
import com.symphony.oss.models.object.canon.IUserIdObject;
import com.symphony.oss.models.object.canon.facade.IApplicationObjectHeader;
import com.symphony.oss.models.object.canon.facade.IApplicationObjectPayload;
import com.symphony.oss.models.object.canon.facade.IPartition;

class PartitionExplorerPanel extends ObjectPanel
{
  static final String PANEL_NAME = "Partition Explorer";
  static final String PANEL_ID = "partitionExplorer";
  
  
  private final PartitionBlotterPanel        partitionBlotterPanel_;
  private final PartitionProvider            partitionProvider_;
  private final PartitionObjectsViewProvider partitionObjectsViewProvider_;

  PartitionExplorerPanel(PartitionBlotterPanel partitionBlotterPanel, PartitionProvider partitionProvider, PartitionObjectsViewProvider partitionObjectsViewProvider, IAllegroMultiTenantApi accessApi, IAllegroApi userApi)
  {
    super(PANEL_ID, PANEL_NAME, accessApi, userApi);
    
    partitionBlotterPanel_        = partitionBlotterPanel;
    partitionProvider_            = partitionProvider;
    partitionObjectsViewProvider_ = partitionObjectsViewProvider;
  }
  
  @Override
  public void handleContent(HttpServletRequest req, UIHtmlWriter out)
  {
    IPartition    partition     = null;
    Hash          partitionHash = getPartitionHash(req, out);
    String        partitionName = req.getParameter(PARTITION_NAME);
    IUserIdObject partitionId   = getPartitionId(req, out);
    boolean       loadAfter = "true".equalsIgnoreCase(req.getParameter(LOAD_AFTER));
    
    try
    {
      if(partitionHash != null)
      {
        partition = partitionProvider_.fetch(partitionHash);
      }
      else if(partitionId != null)
      {
        partition = partitionProvider_.fetch(partitionId.getHash());
      }
      else if(partitionName != null)
      {
        partition = partitionProvider_.fetch(new PartitionQuery.Builder()
            .withName(partitionName)
            .build().getHash(accessApi_.getUserId()));
      }
    }
    catch(RuntimeException e)
    {
      out.printError(e);
    }
    
    if(partition != null)
    {
      partitionHash = partition.getId().getHash();
      partitionId = partition.getId();
      
      if(partitionId instanceof INamedUserIdObject)
      {
        partitionName = ((INamedUserIdObject) partitionId).getName();
      }
    }
    
    out.printElement("h1", "Partition Explorer");
    
    
    out.openElement("table", "class", "w3-table w3-striped w3-bordered w3-border w3-hoverable w3-white");
    
    out.openElement("tr");
    out.printElement("td", "Partition Hash");
    out.openElement("td");
    out.openElement("form", "method", "POST", "action", getPath());
    out.printTextInput(PARTITION_HASH, partitionHash == null ? "" : partitionHash.toStringBase64(), "size", "64", CLASS, CODE_CLASS);
    out.printElement("input", null, "type", "submit", "value", "Search");
    out.closeElement(); // form
    out.closeElement(); // td
    out.closeElement(); //tr 
    
    out.openElement("tr");
    out.printElement("td", "Partition Name");
    out.openElement("td");
    out.openElement("form", "method", "POST", "action", getPath());
    out.printTextInput(PARTITION_NAME, partitionName == null ? "" : partitionName, "size", "30");
    out.printElement("input", null, "type", "submit", "value", "Search");
    out.closeElement(); // form
    out.closeElement(); // td
    out.closeElement(); //tr
    
    out.openElement("tr");
    out.printElement("td", "Partition ID Object");
    out.openElement("td");
    out.openElement("form", "method", "POST", "action", getPath());
    out.printElement("textarea", partitionId == null ? "" : partitionId.toString(), "name", PARTITION_ID, "rows", "8", "cols", "64");
    out.printElement("input", null, "type", "submit", "value", "Search");
    out.closeElement(); // form
    out.closeElement(); // td
    out.closeElement(); // tr
    
    Collection<IPartition> allPartitions = partitionProvider_.getAll();
    
    if(!allPartitions.isEmpty())
    {      
      out.openElement("tr");
      out.printElement("td", "Recent Partitions");
      out.openElement("td");
      
      out.openElement("table", "class", "w3-table w3-bordered w3-border w3-white"); // table2
      
      out.openElement("tr"); // tr2
      out.printElement("td", "Hash");
      out.printElement("td", "Owner");
      out.printElement("td", "Name");
      out.closeElement(); // tr2
      
      for(IPartition p : partitionProvider_)
      {
        IUserIdObject pId = p.getId();
        Hash pHash = pId.getHash();
        String pName = pId instanceof INamedUserIdObject ? ((INamedUserIdObject)pId).getName() : pId.getCanonType();
        
        out.openElement("tr"); // tr2
        out.openElement("td");
        out.printElement("a", pHash.toStringBase64(), "href", "?" + PARTITION_HASH + "=" + pHash.toStringUrlSafeBase64(), CLASS, CODE_CLASS);
        out.closeElement(); // td
        out.printElement("td", pId.getUserId());
        out.printElement("td", pName);
        out.closeElement(); // tr2
      }
      
      out.closeElement(); // table2
      out.closeElement(); // td
      out.closeElement(); // tr
    }
    
    out.closeElement(); // table
    
    if(partition != null)
    {
      printObject(out, "Partition Info", partition);
      
      if(partitionBlotterPanel_ != null)
      {
        out.printElement("a", "Open Blotter", "href", 
          getPath(PartitionBlotterPanel.PANEL_ID) + "?" + PARTITION_HASH + "=" + partitionHash.toStringUrlSafeBase64());
      }
      
      
      
      
      out.printElement("h2", "Partition Data");
      
      
      out.openElement("table", "class", "w3-table w3-striped w3-bordered w3-border w3-hoverable w3-white");
      
      out.openElement("tr");
      out.printElement("td", "Absolute Hash");
      out.printElement("td", "Base Hash");
      out.printElement("td", "Created Date");
      out.printElement("td", "Header Type");
      out.printElement("td", "Payload Type");
      out.closeElement(); //tr 
      
      PartitionObjectsView view = partitionObjectsViewProvider_.fetch(partitionHash);
      
      if(loadAfter)
        view.loadAfter();
      
      view.forEach((object) ->
      {
        out.openElement("tr");
        
        printStoredObject(out, object.getStoredObject());
        
        printHeader(out, object.getStoredObject().getHeader());
        
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
        out.printElement("a", "Load More", "href", "?" + PARTITION_HASH + "=" + partitionHash.toStringUrlSafeBase64() + "&" + LOAD_AFTER + "=true");
        out.closeElement(); //td
        out.closeElement(); //tr
      }
//      accessApi_.fetchPartitionObjects(new FetchPartitionObjectsRequest.Builder()
//          .withQuery(new PartitionQuery.Builder()
//              .withHash(partitionHash)
//              .build()
//              )
//          .withConsumerManager(new ConsumerManager.Builder()
//              .withConsumer(IStoredApplicationObject.class, (object, trace) ->
//              {
//                out.openElement("tr");
//                
//                printStoredObject(out, object);
//                
//                printHeader(out, object.getHeader());
//                
//                if(object.getEncryptedPayload() == null)
//                {
//                  printPayload(out, null);
//                }
//                else
//                {
//                  try
//                  {
//                    IApplicationObjectPayload payload = userApi_.decryptObject(object);
//                    printPayload(out, payload);
//                  }
//                  catch(RuntimeException e)
//                  {
//                    printPayloadException(out, e);
//                  }
//                }
//
//                out.closeElement(); //tr
//              })
//              .build()
//              )
//          .build()
//          );
      

      out.closeElement(); // table
    }
  }
}
