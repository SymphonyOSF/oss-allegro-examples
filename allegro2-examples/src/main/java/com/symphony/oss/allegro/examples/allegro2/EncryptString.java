/*
 * Copyright 2021 Symphony Communication Services, LLC.
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

package com.symphony.oss.allegro.examples.allegro2;

import com.symphony.oss.commons.immutable.ImmutableByteArray;
import com.symphony.oss.models.core.canon.facade.IEncryptedRecord;

/**
 * Example to show how to encrypt and decrypt arbitrary String data.
 * 
 * @author Bruce Skingle
 *
 */
public class EncryptString extends Allegro2Example
{
  private EncryptString(String[] args)
  {
    super(args);
  }

  private void run()
  {
    // The string we will encrypt.
    String clearText = "Hello World";
    
    // We need to pass the threadId of the thread whose key will be used to encrypt the data and the clear text.
    // Note this payload is a UTF-8 String not arbitrary binary data.
    IEncryptedRecord encryptedRecord = allegro2Api_.newEncryptedRecordBuilder()
        .withThreadId(threadId_)
        .withPayload(clearText)
        .build();
    
    // The resulting JSON object contains the cipher text plus key coordinates needed to decrypt as a JSON object.
    System.out.println(encryptedRecord);
    
    // The encryptedRecord can be stored and later decrypted directly:
    String decryptedText = allegro2Api_.decrypt(encryptedRecord);
    
    System.out.println(decryptedText);
    
    // If you don't like JSON you can store the threadId, rotationId and cipher text some other way and pass them
    // separately to decrypt like this.
    ImmutableByteArray decryptedText2 = allegro2Api_.decrypt(encryptedRecord.getThreadId(), encryptedRecord.getRotationId(), encryptedRecord.getEncryptedPayload());
    
    // The return value is an ImmutableByteArray but that class has a toString() method which converts the data back
    // to a UTF-8 string.
    System.out.println(decryptedText2);
  }
  
  /**
   * Main.
   * 
   * @param args Command line arguments.
   */
  public static void main(String[] args)
  {
    new EncryptString(args).run();
  }
}
