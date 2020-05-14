/*
 * Copyright 2020 Symphony Communication Services, LLC.
 *
 * All Rights Reserved
 */

package com.symphony.s2.allegro.examples.ui;

import javax.servlet.http.HttpServletRequest;

import com.symphony.oss.allegro.api.IAllegroApi;
import com.symphony.oss.allegro.api.IAllegroMultiTenantApi;
import com.symphony.oss.fugue.server.http.ui.servlet.UIHtmlWriter;

class PodPanel extends AllegroUiPanel
{
  PodPanel(IAllegroMultiTenantApi accessApi, IAllegroApi userApi)
  {
    super("Pod", accessApi, userApi);
  }
  
  @Override
  public void handleContent(HttpServletRequest req, UIHtmlWriter out)
  {
    out.printElement("h1", "Pod Info");
    
    out.openElement("table", "class", "w3-table w3-striped w3-bordered w3-border w3-hoverable w3-white");
    
    out.openElement("tr");
    out.printElement("td", "Name");
    out.printElement("td", "Value");
    out.closeElement(); //tr
    
    print(out, "PodID", userApi_.getPodId());
    print(out, "UserID", userApi_.getUserId());
    print(out, "Display Name", userApi_.getSessioninfo().getDisplayName());
    print(out, "Company Name", userApi_.getSessioninfo().getCompany());
    
    out.closeElement(); // table
    
    printObject(out, "Session Info", userApi_.getSessioninfo());
    
    out.openElement("table", "class", "w3-table w3-striped w3-bordered w3-border w3-hoverable w3-white");
    
    out.printElement("h1", "Access Account Info");
    out.openElement("tr");
    out.printElement("td", "Name");
    out.printElement("td", "Value");
    out.closeElement(); //tr
    
    print(out, "UserID", accessApi_.getUserId());
    
    out.closeElement(); // table
  }
}
