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

import com.symphony.oss.allegro2.mongo.api.Allegro2MongoApi;
import com.symphony.oss.allegro2.mongo.api.IAllegro2MongoApi;
import com.symphony.oss.models.allegro.canon.facade.Allegro2Configuration;
import com.symphony.oss.models.core.canon.facade.PodAndUserId;
import com.symphony.oss.models.core.canon.facade.ThreadId;

abstract class MongoExample
{
  protected final String            serviceAccount_;
  protected final String            podUrl_;
  protected final String            credentialFile_;
  protected final ThreadId          threadId_;
  protected final Long              ownerId_;

  protected final IAllegro2MongoApi allegro2MongoApi_;

  protected final String            mongoHost_;
  protected final String            mongoUser_;
  protected final String            mongoPassword_;
  protected final PodAndUserId      ownerUserId_;

  /**
   * Constructor.
   * 
   * @param args Command line arguments.
   */
  MongoExample(String[] args)
  {
    MongoExampleCommandLineHandler handler = new MongoExampleCommandLineHandler();
    
    handler.process(args);

    serviceAccount_ = handler.serviceAccount_;
    podUrl_         = handler.podUrl_;
    credentialFile_ = handler.credentialFile_;
    threadId_       = handler.threadId_;
    ownerId_        = handler.ownerId_;
    mongoHost_      = handler.mongoHost_;
    mongoUser_      = handler.mongoUser_;
    mongoPassword_  = handler.mongoPassword_;
  
    
    allegro2MongoApi_ = new Allegro2MongoApi.Builder()
        .withConfiguration(new Allegro2Configuration.Builder()
                .withPodUrl(podUrl_)
                .withUserName(serviceAccount_)
                .withRsaPemCredentialFile(credentialFile_)
                .build())
        .build();
    
    ownerUserId_ = ownerId_ == null ? allegro2MongoApi_.getUserId() : PodAndUserId.newBuilder().build(ownerId_);
  }
  
  protected abstract void run();
}
