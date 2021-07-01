/*
 * Copyright 2019 Symphony Communication Services, LLC.
 *
 * All Rights Reserved
 */

package com.symphony.oss.allegro.examples.allegrobot;

import java.io.InputStream;
import java.io.OutputStream;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestStreamHandler;
import com.symphony.oss.allegro.runtime.aws.AwsLambdaHandlerContainer;
import com.symphony.oss.canon.runtime.IHandlerContainer;
import com.symphony.oss.canon.runtime.IHandlerContainerFactory;
import com.symphony.oss.canon.runtime.IModelRegistry;
import com.symphony.oss.fugue.container.FugueComponentContainer;
import com.symphony.oss.fugue.trace.ITraceContextTransactionFactory;

/**
 * Lambda implementation of the API.
 * 
 * @author Bruce Skingle
 *
 */
public class AwsLambdaApi  implements RequestStreamHandler, IHandlerContainerFactory
{
  private AwsLambdaHandlerContainer handler_;
  
  /**
   * Constructor.
   */
  public AwsLambdaApi()
  {
    FugueComponentContainer.Builder registry = new FugueComponentContainer.Builder();
    
    new AwsApiAssembly.Builder()
        .withHandlerContainerFactory(this)
        .withComponentRegistry(registry)
        .build();
    
    registry
      .build()
      .start();
  }

  @Override
  public void handleRequest(InputStream input, OutputStream output, Context context)
  {
    handler_.handleRequest(input, output, context);
  }

  @Override
  public synchronized IHandlerContainer createHandlerContainer(ITraceContextTransactionFactory traceFactory, IModelRegistry modelRegistry)
  {
    if(handler_ == null)
      handler_ = new AwsLambdaHandlerContainer(traceFactory, modelRegistry);
    
    return handler_;
  }
}