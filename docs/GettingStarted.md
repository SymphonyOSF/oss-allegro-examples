---
nav_order: 4
title: Home
---
# Getting Started
In order to run the Allegro examples you need to provide the following values:

+ The URL of your Symphony pod.
+ The name of the service account which you will use.
+ The name of a file containing the private RSA key to authenticate as that service account.
+ The URL of the object store server endpoint you will connect to.

The URL of a pod is typically https://companyname.symphony.com where __companyname__ is your company's name.

You (or your Symphony administrator) choose the name of your service account when you create it through the
Admin Console. Instructions for generating an RSA keypair for authentication and uploading the public key
to the Admin Console can be found at [developers.symphony.com](https://developers.symphony.com/restapi/docs/rsa-bot-authentication-workflow).  

For production pods, you do not need to specify the API URL (which is actually __api.symphony.com__), for pods in
non-production environments you need to specify the appropriate URL (__dev.api.symphony.com__, __qa.api.symphony.com__,
__uat.api.symphony.com__ etc)
