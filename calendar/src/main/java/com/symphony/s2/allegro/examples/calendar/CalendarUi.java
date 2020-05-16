/*
 *
 *
 * Copyright 2020 Symphony Communication Services, LLC.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.symphony.s2.allegro.examples.calendar;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.symphony.oss.allegro.examples.calendar.canon.CalendarModel;
import com.symphony.oss.allegro.examples.calendar.canon.IToDoItem;
import com.symphony.oss.allegro.ui.AllegroUi;
import com.symphony.oss.allegro.ui.Projection;
import com.symphony.oss.fugue.server.http.ui.FugueHttpUiServer;

public class CalendarUi extends AllegroUi
{
  private static final Logger log_ = LoggerFactory.getLogger(AllegroUi.class);
  
  CalendarUi(AllegroUi.AbstractBuilder<?,?> builder)
  {
    super(builder);
  }
  
  static class Builder extends AllegroUi.AbstractBuilder<Builder, CalendarUi>
  {
    public Builder()
    {
      super(Builder.class);
    }

    @Override
    protected CalendarUi construct()
    {
      return new CalendarUi(this);
    }
  }
  
  /**
   * Main.
   * 
   * @param argv Command line arguments.
   */
  public static void main(String[] argv)
  {
    try
    {
      FugueHttpUiServer server = new CalendarUi.Builder()
          .withApplicationName("CalendarUi")
          .withCommandLine(argv)
          .withProjector(IToDoItem.class, (projection, object) ->
          {
            projection
                .with(Projection.ATTRIBUTE_PAYLOAD, object.getDescription())
                ;
          })
          .withFactories(CalendarModel.FACTORIES)
          .build()
          .getHttpServer()
          ;
      
      server.start();
  
      try
      {
        server.join();
      }
      finally
      {
        server.stop();
      }
    }
    catch (RuntimeException | InterruptedException e)
    {
      log_.error("Failed", e);
      System.exit(1);
    }
  }
}
