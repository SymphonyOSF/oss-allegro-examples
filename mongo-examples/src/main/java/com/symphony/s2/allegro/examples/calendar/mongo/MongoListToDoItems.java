/*
 * Copyright 2019 Symphony Communication Services, LLC.
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

package com.symphony.s2.allegro.examples.calendar.mongo;

import org.bson.Document;

import com.symphony.oss.models.core.canon.facade.IApplicationRecord;

/**
 * An example application which creates a ToDoItem, adding it to a current and absolute sequence.
 * 
 * @author Bruce Skingle
 *
 */
public class MongoListToDoItems extends MongoCalendarExample
{
  @Override
  public void run(CalendarContext context)
  {
    
    for(Document doc : context.todoItems_.find())
    {
      System.out.println("doc = " + doc);
      
      IApplicationRecord applicationRecord = context.allegro2MongoApi_.decrypt(doc);
      
      System.out.println("header = " + applicationRecord.getHeader());
      System.out.println("payload = " + applicationRecord.getPayload());
    }
  }

  /**
   * Main.
   * 
   * @param args Command line arguments.
   */
  public static void main(String[] args)
  {
    new MongoListToDoItems().run(args);
  }
}
