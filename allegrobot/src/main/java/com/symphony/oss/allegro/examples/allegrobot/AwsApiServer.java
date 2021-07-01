/*
 * Copyright 2018 Symphony Communication Services, LLC.
 *
 * All Rights Reserved
 */

package com.symphony.oss.allegro.examples.allegrobot;

import org.slf4j.LoggerFactory;

import com.symphony.oss.canon.runtime.ModelServlet;
import com.symphony.oss.fugue.server.http.FugueHttpServerAssembly;
import com.symphony.s2.authc.model.MultiTenantService;

/**
 * Server implementation of the API.
 * 
 * @author Bruce Skingle
 *
 */
public class AwsApiServer extends FugueHttpServerAssembly
{
  /**
   * Constructor.
   */
  public AwsApiServer()
  {
    super(MultiTenantService.EXAMPLE);
    
    new AwsApiAssembly.Builder()
      .withHandlerContainerFactory((traceFactory, modelRegistry) -> new ModelServlet(traceFactory, modelRegistry))
      .withComponentRegistry(getRegistry())
      .build();
  }
  
  /**
   * Main.
   * 
   * @param argv Command line arguments, ignored.
   */
  public static void main(String[] argv)
  {
    try
    {
      AwsApiServer assembly = new AwsApiServer();
      //assembly.setKeyStorePassword("changeit");
     // assembly.setKeyStorePath("/Users/bruce/keys/keystore");
      assembly.getServer().start().join();
    }
    catch (RuntimeException | InterruptedException e)
    {
      LoggerFactory.getLogger(AwsApiServer.class).error("Failed", e);
      System.exit(1);
    }
  }
}