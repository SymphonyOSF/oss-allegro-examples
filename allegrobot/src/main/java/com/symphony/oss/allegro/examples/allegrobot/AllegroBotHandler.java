/*
 * Copyright 2021 Symphony Communication Services, LLC.
 *
 * All Rights Reserved
 */

package com.symphony.oss.allegro.examples.allegrobot;

import java.util.Date;

import com.symphony.oss.canon.runtime.exception.CanonException;
import com.symphony.oss.fugue.trace.ITraceContext;
import com.symphony.oss.models.allegro.canon.AllegroBotPathHandler;
import com.symphony.oss.models.allegro.canon.AllegroBotResponse;
import com.symphony.oss.models.allegro.canon.IAllegroBotRequest;
import com.symphony.oss.models.allegro.canon.IAllegroBotResponse;
import com.symphony.oss.models.allegro.canon.facade.ChatMessage;
import com.symphony.oss.models.allegro.canon.facade.IAbstractReceivedChatMessage;
import com.symphony.oss.models.allegro.canon.facade.IChatMessage;
import com.symphony.oss.models.allegro.canon.facade.IReceivedSocialMessage;
import com.symphony.oss.models.chat.canon.facade.ISocialMessage;

public class AllegroBotHandler extends AllegroBotPathHandler<String>
{


  public AllegroBotHandler(RequestAuthenticator authenticator)
  {
    super(authenticator);
  }

  @Override
  public IAllegroBotResponse handlePost(IAllegroBotRequest canonPayload, String canonAuth, ITraceContext canonTrace)
      throws CanonException
  {
    AllegroBotResponse.Builder builder = new AllegroBotResponse.Builder();

    for(IAbstractReceivedChatMessage message : canonPayload.getMessages())
    {
      if(message instanceof IReceivedSocialMessage)
      {
        IReceivedSocialMessage socialMessage = (IReceivedSocialMessage)message;
        
        if(socialMessage.getSocialMessage().getFrom().getId().equals(canonPayload.getUserId()))
        {
          // Ignore message from myself.
          continue;
        }
        
        String presentationML     = "<div data-format=\"PresentationML\" data-version=\"2.0\" class=\"wysiwyg\"><p>You said \"" +
      
            message.getText() + 
            "\" and the time is now <b>" + new Date() + "</b></p></div>";
    
        IChatMessage reply = new ChatMessage.Builder()
            .withMessageML(presentationML)
            .build();
        
        
        builder.withMessages(reply)
            ;
      }
    }
    
    return builder.build();
  }

}
