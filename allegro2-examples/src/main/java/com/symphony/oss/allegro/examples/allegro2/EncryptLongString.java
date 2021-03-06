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

import com.symphony.oss.models.core.canon.facade.IEncryptedRecord;

/**
 * Example to show how to encrypt and decrypt arbitrary String data.
 * 
 * @author Bruce Skingle
 *
 */
public class EncryptLongString extends Allegro2Example
{
  static final String CHARS_4096 = 
      "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Aliquam dapibus consectetur ullamcorper. Duis ipsum est, blandit vel tincidunt ut, imperdiet sit amet tellus. Nullam vitae ante nisi. Sed accumsan ligula ac felis cursus imperdiet. Morbi non purus leo. Nullam aliquet est non hendrerit hendrerit. In hac habitasse platea dictumst. Praesent sed ante id neque tincidunt hendrerit id placerat ante. In hac habitasse platea dictumst. Nulla varius eros vel nulla efficitur, non accumsan quam suscipit. In pretium vestibulum turpis vitae posuere. Phasellus vel nulla turpis. Mauris in ipsum vestibulum, facilisis ex vel, venenatis est.\n" + 
      "\n" + 
      "Phasellus cursus odio vitae mi molestie finibus. Proin congue euismod nisi. Etiam rutrum, enim sed sodales efficitur, orci leo ornare justo, ac sollicitudin ex ligula ut lorem. Phasellus gravida, magna non convallis sollicitudin, leo felis ornare magna, aliquam molestie tortor diam nec nibh. Donec libero risus, venenatis a lobortis non, scelerisque eu risus. Cras et mollis quam. Vestibulum sodales vel dolor id iaculis. Pellentesque et mauris vulputate, commodo enim semper, pulvinar ex. Sed sed quam volutpat turpis porttitor varius. Sed ac porta massa, at fermentum neque. Aliquam tincidunt enim ullamcorper ullamcorper sollicitudin. Ut elementum, ex at finibus pharetra, libero nulla sollicitudin purus, eget iaculis quam ligula ac mi. Nulla ac risus non justo tempus molestie. Aliquam eleifend dui eu erat eleifend vestibulum. Integer facilisis leo a nisl dignissim, sit amet vestibulum nunc hendrerit. Nunc ante lacus, feugiat ut quam nec, tincidunt iaculis nunc.\n" + 
      "\n" + 
      "Morbi eu metus venenatis, porttitor mauris nec, ultricies eros. Nam enim dolor, luctus a maximus quis, posuere ac dui. Etiam mattis sed enim quis consectetur. In congue ligula faucibus tempor imperdiet. Etiam neque arcu, ornare et mauris quis, pretium luctus turpis. Proin porta mauris diam, sit amet pretium metus scelerisque eget. Aliquam consectetur augue nulla, vitae vulputate lacus pretium in. Phasellus elementum mattis metus. Mauris pharetra est in congue posuere.\n" + 
      "\n" + 
      "Ut tempus tellus vitae nisl ornare ornare. Integer sed fringilla nisi. Morbi ut varius est. Ut facilisis pulvinar lectus feugiat pulvinar. Maecenas posuere ipsum ut elit posuere congue. Aliquam eu porta turpis, et dapibus lacus. Pellentesque justo purus, tincidunt a imperdiet non, fringilla eu nisl. Pellentesque convallis metus non aliquet dapibus. Morbi vulputate a purus id bibendum.\n" + 
      "\n" + 
      "Mauris vestibulum nisi vitae interdum commodo. Orci varius natoque penatibus et magnis dis parturient montes, nascetur ridiculus mus. Aliquam ac aliquam eros. Sed pulvinar quam in lorem maximus, ac aliquet nulla consectetur. Fusce sapien purus, sagittis non ipsum eu, eleifend aliquet sem. Donec scelerisque in velit ac eleifend. Morbi scelerisque magna commodo, efficitur lorem ac, molestie mi. Aenean dignissim tincidunt vehicula. Duis eleifend, mi vitae tincidunt commodo, elit lorem pharetra ante, non mattis nisl ex nec mi. Praesent ac metus ut ante accumsan volutpat. Praesent non libero rhoncus, pellentesque ex ut, tempor dolor. Donec cursus dignissim nunc non tempor. Pellentesque commodo mauris at gravida hendrerit. Vestibulum venenatis vitae magna ut tristique.\n" + 
      "\n" + 
      "Fusce odio lectus, egestas eu convallis sit amet, pretium et neque. Quisque lacinia a ex at consequat. Quisque ut cursus tellus, eu viverra lacus. Aenean vitae varius nibh, nec commodo leo. Pellentesque vitae dignissim augue, ac cursus lorem. Donec fermentum efficitur nunc, non imperdiet purus ultrices varius. Nulla tortor lacus, dignissim at turpis id, blandit suscipit turpis. Nunc vestibulum dui ac nisi consequat, sed accumsan lectus rhoncus. Orci varius natoque penatibus et magnis dis parturient montes, nascetur ridiculus mus. Nunc tincidunt magna dictum, scelerisque neque ac, accumsan erat. Quisque augue ex, ultricies id eros et, euismod suscipit nulla. Phasellus blandit mauris eget dui elementum, in imperdiet risus iaculis.\n" + 
      "\n" + 
      "Proin augue diam, semper et ex id, congue blandit dolor. Curabitur congue a risus quis feugiat. Aliquam erat vestibulum.";
  
  private EncryptLongString(String[] args)
  {
    super(args);
  }

  private void run()
  {
    for(int size = 1 ; size<10 ; size++)
    {
      StringBuilder s = new StringBuilder();
      
      for(int i=0 ; i<size ; i++)
        s.append(CHARS_4096);
      
      // The string we will encrypt.
      String clearText = s.toString();
      
      for(int run = 1 ; run<10 ; run++)
      {
       
        long encryptStart = System.currentTimeMillis();
        
        // We need to pass the threadId of the thread whose key will be used to encrypt the data and the clear text.
        // Note this payload is a UTF-8 String not arbitrary binary data.
        IEncryptedRecord encryptedRecord = allegro2Api_.newEncryptedRecordBuilder()
            .withThreadId(threadId_)
            .withPayload(clearText)
            .build();
        
        long encryptEnd = System.currentTimeMillis();
        
        
        long decryptStart = System.currentTimeMillis();
        // The encryptedRecord can be stored and later decrypted directly:
        String decryptedText = allegro2Api_.decrypt(encryptedRecord);
        long decryptEnd = System.currentTimeMillis();
        
    
        System.out.println("Encrypted " + clearText.length() + " characters of clear text in " + (encryptEnd - encryptStart) + "ms.");
        System.out.println("Decrypted " + encryptedRecord.getEncryptedPayload().asImmutableByteArray().length() + " characters of Base64 encoded cipher text in " + (decryptEnd - decryptStart) + "ms.");
      }
    }
  }
  
  /**
   * Main.
   * 
   * @param args Command line arguments.
   */
  public static void main(String[] args)
  {
    new EncryptLongString(args).run();
  }
}
