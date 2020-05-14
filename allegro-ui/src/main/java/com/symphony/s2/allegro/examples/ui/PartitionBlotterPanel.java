/*
 * Copyright 2020 Symphony Communication Services, LLC.
 *
 * All Rights Reserved
 */

package com.symphony.s2.allegro.examples.ui;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.function.Consumer;

import javax.servlet.http.HttpServletRequest;

import org.joda.time.Instant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.symphony.oss.allegro.api.ConsumerManager;
import com.symphony.oss.allegro.api.IAllegroApi;
import com.symphony.oss.allegro.api.IAllegroMultiTenantApi;
import com.symphony.oss.allegro.api.request.UpsertFeedRequest;
import com.symphony.oss.commons.hash.Hash;
import com.symphony.oss.fugue.server.http.ui.servlet.UIHtmlWriter;
import com.symphony.oss.fugue.trace.NoOpTraceContext;
import com.symphony.oss.models.object.canon.IAbstractStoredApplicationObject;
import com.symphony.oss.models.object.canon.facade.IApplicationObjectHeader;
import com.symphony.oss.models.object.canon.facade.IApplicationObjectPayload;
import com.symphony.oss.models.object.canon.facade.IPartition;
import com.symphony.oss.models.object.canon.facade.IStoredApplicationObject;
import com.symphony.oss.models.object.canon.facade.SortKey;

class PartitionBlotterPanel extends RenderingPanel
{
  private static final Logger log_ = LoggerFactory.getLogger(PartitionBlotterPanel.class);
  
  private static final String                PANEL_NAME = "Partition Blotter";
  public static final String                 PANEL_ID   = "partitionBlotter";

  private final String                       feedName_;
  private final PartitionProvider            partitionProvider_;
  private final PartitionObjectsViewProvider partitionObjectsViewProvider_;
  
  PartitionBlotterPanel(ProjectorManager rendererManager, String feedName, PartitionProvider partitionProvider, PartitionObjectsViewProvider partitionObjectsViewProvider,
      IAllegroMultiTenantApi accessApi, IAllegroApi userApi)
  {
    super(PANEL_ID, PANEL_NAME, accessApi, userApi, rendererManager);
    
    feedName_                     = feedName;
    partitionProvider_            = partitionProvider;
    partitionObjectsViewProvider_ = partitionObjectsViewProvider;
  }
  
  @Override
  public void handleContent(HttpServletRequest req, UIHtmlWriter htmlOut)
  {
    UIHtmlWriter out = htmlOut; //new UIHtmlWriter(System.err);
    
    
    IPartition    partition     = null;
    Hash          partitionHash = getPartitionHash(req, out);
    
    try
    {
      if(partitionHash == null)
      {
        out.printError("No Partition Specified - navigate here from Partition Explorer");
        return;
      }
      
      partition = partitionProvider_.fetch(partitionHash);
    }
    catch(RuntimeException e)
    {
      out.printError(e);
      return;
    }
    
    userApi_.upsertFeed(new UpsertFeedRequest.Builder()
        .withName(feedName_)
        .withPartitionHashes(partitionHash)
        .build());
    

    out.printElement("i", "", "id", "error", "class", "error");
    
    out.printElement("h1", "Partition Blotter");

    printObject(out, "Partition Info", partition);
    
    out.printElement("h2", "Partition Data");
    out.print("Last Hearbeat: ");
    out.printElement("i", "NONE", "id", "heartbeat");
    out.printElement("p");
    
    out.openElement("table", "id", "blotter", "class", "w3-table w3-striped w3-bordered w3-border w3-hoverable w3-white");
    
    out.openElement("tr", "id", "header");
    out.printElement("td", "Sort Key");
    out.printElement("td", "Header Type");
    out.printElement("td", "Payload Type");
    out.printElement("td", "Payload");
    out.closeElement(); //tr 
    out.closeElement(); // table
    
    out.openElement("script");
    
    try(InputStreamReader in = new InputStreamReader(getClass().getResourceAsStream("/blotterFunc.js")))
    {
      char[] buf = new char[1024];
      int nbytes;
      
      while((nbytes = in.read(buf))>0)
        out.write(buf, 0, nbytes);
    }
    catch (IOException e)
    {
      log_.error("Failed to write functions", e);
    }
    
    
    PartitionObjectsView view = partitionObjectsViewProvider_.fetch(partitionHash);


    FeedHandler feedHandler = new FeedHandler(view, out);
    
    view.forEach((object) ->
    {
      feedHandler.doObjectUpserted(object);
    });
    out.closeElement(); // script
    out.flush();
    
    feedHandler.run();
    

    
  }
  
  class FeedHandler implements Runnable
  {
    private final PartitionObjectsView view_;
    private final UIHtmlWriter feedOut_;
    
    public FeedHandler(PartitionObjectsView view, UIHtmlWriter feedOut)
    {
      view_ = view;
      feedOut_ = feedOut;
    }

    @Override
    public void run()
    {
      view_.addListener(this);
      try
      {
        while(true)
        {

          feedOut_.openElement("script");
          feedOut_.println("heartBeat('" + Instant.now() + "')");
          feedOut_.closeElement(); // script
          feedOut_.flush();
          if(feedOut_.checkError())
          {
            break;
          }
          try
          {
            Thread.sleep(20000);
          }
          catch (InterruptedException e)
          {
            feedOut_.printError(e);
            break;
          }
        }
      }
      finally
      {
        view_.removeListener(this);
      }
    }

    public void objectDeleted(Hash hash)
    {
      feedOut_.openElement("script");
      feedOut_.println("deletePayload('" + hash.toStringBase64() + "');");
      feedOut_.closeElement(); // script
      feedOut_.flush();
    }

    public void objectUpserted(PartitionObject partitionObject)
    {
      feedOut_.openElement("script");
      doObjectUpserted(partitionObject);
      feedOut_.closeElement(); // script
      feedOut_.flush();
    }
    

    public void doObjectUpserted(PartitionObject partitionObject)
    {
      accept(feedOut_, partitionObject);
//      feedOut_.openElement("script");
//      accept
//      Renderable renderable = rendererManager_.render(partitionObject);
//      
//      if(renderable)
//      try
//      {
//        IApplicationObjectPayload payload = partitionObject.getPayload();
//        
//        if(payload == null)
//          rendererManager_.render(feedOut_, partitionObject.getStoredObject());
//        else
//          rendererManager_.render(feedOut_, payload);
//        
//        feedOut_.println("upsertPayload('" + partitionObject.getStoredObject().getBaseHash() + "', ['" + 
//            s.getSortKey() + "', '" +
//            s.getSortKey() + "', '" +
//
//            p.getCanonType() + "', '" +
//            p.getCanonType() + "']);");
//      }
//      catch(RuntimeException e)
//      {
//        rendererManager_.render(feedOut_, partitionObject.getStoredObject());
//        feedOut_.println("upsertPayload('" + partitionObject.getStoredObject().getBaseHash() + "', ['" + 
//            s.getSortKey() + "', '" +
//            e.getClass().getSimpleName() + ": " + e.getLocalizedMessage() + "']);");
////        feedOut_.println("upsertException(" + partitionObject.getStoredObject() + ", '" +
////            e.getClass().getSimpleName() + ": " + e.getLocalizedMessage() + "');");
//      }
      
      
      
//      if(partitionObject.getPayload() == null)
//      printStoredObject(out, object.getStoredObject());
//      
//      printHeader(out, object.getStoredObject().getHeader());
//      
//      try
//      {
//        printPayload(out, object.getPayload());
//      }
//      catch(RuntimeException e)
//      {
//        printPayloadException(out, e);
//      }

//      feedOut_.closeElement(); //script
    }

  }

//  private void printPayload(UIHtmlWriter out, IApplicationObjectPayload payload)
//  {
//    if(payload == null)
//    {
//      out.printElement("td"); 
//    }
//    else
//    {
//      out.printElement("td", payload.getCanonType());
//    }
//  }
//
//  private void printPayloadException(UIHtmlWriter out, RuntimeException e)
//  {
//    out.printElement("td", e.getClass().getSimpleName() + ": " + e.getLocalizedMessage());
//  }
//
//  private void printHeader(UIHtmlWriter out, IApplicationObjectHeader header)
//  {
//    if(header == null)
//    {
//      out.printElement("td"); 
//    }
//    else
//    {
//      out.printElement("td", header.getCanonType());
//    }
//  }
//
//  private void printStoredObject(UIHtmlWriter out, IStoredApplicationObject object)
//  {
//    out.openElement("td");
//    out.printElement("a", object.getAbsoluteHash().toStringBase64(), "href", 
//        getPath(ObjectExplorerPanel.PANEL_ID) + "?" + ABSOLUTE_HASH + "=" + object.getAbsoluteHash().toStringUrlSafeBase64(), CLASS, CODE_CLASS);
//    out.closeElement();// td
//
//    out.openElement("td");
//    out.printElement("a", object.getBaseHash().toStringBase64(), "href", 
//        getPath(ObjectVersionsPanel.PANEL_ID) + "?" + BASE_HASH + "=" + object.getBaseHash().toStringUrlSafeBase64(), CLASS, CODE_CLASS);
//    out.closeElement();// td
//    
//    out.printElement("td", object.getCreatedDate());
//  }
}
