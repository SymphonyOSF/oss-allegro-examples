/*
 * Copyright 2019 Symphony Communication Services, LLC.
 *
 * All Rights Reserved
 */
package com.symphony.oss.allegro.examples.allegrobot;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.symphony.oss.allegro.runtime.aws.AbstractAwsApiAssembly;
import com.symphony.oss.fugue.Fugue;
import com.symphony.oss.models.allegro.canon.AllegroModel;
import com.symphony.oss.models.chat.canon.ChatModel;
import com.symphony.oss.models.core.canon.CoreModel;

/**
 * API assembly.
 * 
 * @author Bruce Skingle
 *
 */
public class AwsApiAssembly  extends AbstractAwsApiAssembly
{
  private static final Logger      log_ = LoggerFactory.getLogger(AwsApiAssembly.class);
  
  private RequestAuthenticator requestAuthenticator_ = new RequestAuthenticator();
  
  protected AwsApiAssembly(Builder builder)
  {
    super(builder);
    
    log_.info("=====================================================================");
    log_.info("AllegroBot AwsApiAssembly");
    log_.info("=====================================================================");
    
    
    handlerContainer_
        .withHandler(new AllegroBotHandler(requestAuthenticator_))
        ;

    
    if(Fugue.isDeveloperMode())
      debug();
  }

  
  private void debug()
  {
  }

  /**
   * Builder.
   * 
   * @author Bruce Skingle
   *
   */
  public static class Builder extends AbstractAwsApiAssembly.AbstractBuilder<Builder, AwsApiAssembly>
  {
    /**
     * Constructor.
     */
    public Builder()
    {
      super(Builder.class);
      
      withModelFactories(CoreModel.FACTORIES);
      withModelFactories(AllegroModel.FACTORIES);
      withModelFactories(ChatModel.FACTORIES);
      withEnableStorage(false);
    }
    
//    public Builder withFeedHandlers(boolean feedHandlers)
//    {
//      feedHandlers_ = feedHandlers;
//      return self();
//    }
    
//    @Override
//    public void validate(FaultAccumulator faultAccumulator)
//    {
//      super.validate(faultAccumulator);
//    }

    @Override
    protected AwsApiAssembly construct()
    {
      return new AwsApiAssembly(this);
    }
  }
}
