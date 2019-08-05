/*
 * Copyright 2019 Symphony Communication Services, LLC.
 *
 * All Rights Reserved
 */

package com.symphony.s2.allegro.examples.helloworld;

import org.symphonyoss.s2.fugue.cmd.CommandLineHandler;

import com.symphony.oss.allegro.api.AllegroApi;
import com.symphony.oss.allegro.api.IAllegroApi;

/**
 * Simple Allegro example program which authenticates and then exits.
 * 
 * @author Bruce Skingle
 *
 */
public class HelloWorld extends CommandLineHandler implements Runnable
{
  private static final String ALLEGRO         = "ALLEGRO_";
  private static final String USER            = "USER";
  private static final String POD_URL         = "POD_URL";
  private static final String CREDENTIAL_FILE = "CREDENTIAL_FILE";
  
  private String              userName_;
  private String              podUrl_;
  private String              credentialFile_;
  
  private IAllegroApi         allegroApi_;

  /**
   * Constructor.
   */
  public HelloWorld()
  {
    withFlag('p',   POD_URL,          ALLEGRO + POD_URL,          String.class,   false, true,   (v) -> podUrl_               = v);
    withFlag('u',   USER,             ALLEGRO + USER,             String.class,   false, true,   (v) -> userName_             = v);
    withFlag('f',   CREDENTIAL_FILE,  ALLEGRO + CREDENTIAL_FILE,  String.class,   false, true,   (v) -> credentialFile_       = v);
  }
  
  @Override
  public void run()
  {
    allegroApi_ = new AllegroApi.Builder()
      .withPodUrl(podUrl_)
      .withUserName(userName_)
      .withRsaPemCredentialFile(credentialFile_)
      .build();
    
    System.out.println(allegroApi_.getSessioninfo());
  }
  
  /**
   * Main.
   * 
   * @param args Command line arguments.
   */
  public static void main(String[] args)
  {
    HelloWorld program = new HelloWorld();
    
    program.process(args);
    
    program.run();
  }
}
