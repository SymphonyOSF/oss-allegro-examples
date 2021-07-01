/*
 * Copyright 2021 Symphony Communication Services, LLC.
 *
 * All Rights Reserved
 */

package com.symphony.oss.allegro.examples.allegrobot;

import java.security.Key;
import java.security.PublicKey;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.eclipse.jetty.util.security.CertificateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.symphony.oss.canon.runtime.exception.CanonException;
import com.symphony.oss.canon.runtime.exception.NotAuthenticatedException;
import com.symphony.oss.canon.runtime.exception.ServerErrorException;
import com.symphony.oss.canon.runtime.http.IRequestAuthenticator;
import com.symphony.oss.canon.runtime.http.IRequestContext;
import com.symphony.oss.canon.runtime.jjwt.JwtBase;
import com.symphony.oss.models.allegro.canon.facade.AllegroBotCredential;
import com.symphony.oss.models.crypto.canon.PemPublicKey;
import com.symphony.oss.models.crypto.cipher.CipherSuiteUtils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwsHeader;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SigningKeyResolverAdapter;

public class RequestAuthenticator extends JwtBase implements IRequestAuthenticator<String>
{
  private static final Logger log_ = LoggerFactory.getLogger(RequestAuthenticator.class);
  
  private Map<String, Key>  keyMap_ = new HashMap<>();
  
  RequestAuthenticator()
  {
    loadKey("-----BEGIN PUBLIC KEY-----\n" + 
        "MIICIjANBgkqhkiG9w0BAQEFAAOCAg8AMIICCgKCAgEAmee3L8EPKvXgFQAVrLe7\n" + 
        "2jcJULrsFqktYYrQz+5kGCfAv3rnjiyRhdcVYQ1hPg4ksH/Kmu8M2DWurXFrfs9l\n" + 
        "Je1waY4AuOilIRxMbqoLvb/SySGpM2kSx7W3w7ZH7Vxp3wjSZKaGcaqVXgHpSWuU\n" + 
        "+5Dq8Q2/t1GzSpNsQ0csGN0OxnfwUx2nJ4M/t8hY+/f8Fk/TfP/ABVvcp2G8gg+f\n" + 
        "i5FVdtZcZiDGLWZm/GU0W/cyxsJPFLTWXPTWzORpNM2QNRePQnxSIJ5fM3+Fym7s\n" + 
        "O20OjKei0SLBY0CQ6SKHT9N6j8uSHqMkKhrL46L25Zh9/17qSVl/u8F5nKc9pt4b\n" + 
        "uq3Q22KfuQ2gU2wgGQZlU0EhkQ2akr7EDE+Q/r7hFZlivBEPjgtaUwQivZlgcbQC\n" + 
        "dafqOMQWGtMrehceOLlRgVPa3VvqZ8HwEa2xoNp/hNJqNOAsx3qaYlsWnRvUMLTw\n" + 
        "Vuc6MQfUtnxAFqRwBWyxuf8X/OAZdRGgPhdKuUcukIV2rkamf9O1G/hpdml+89Ty\n" + 
        "9qhu/kcA8ta7WinLyHO2WtRwteEhoR9isX4DyFcDxYnhcva+keKfLd4T36UXtbJ5\n" + 
        "UaVJOrLXnZQaRToKnvyoI9AcANWPAByU9fftPH5pozDsfxBASxC7tF80Ucc9pvbN\n" + 
        "nt3wwgT2V3vdIE6qMfwHeHMCAwEAAQ==\n" + 
        "-----END PUBLIC KEY-----");
    
    loadKey("-----BEGIN PUBLIC KEY-----\n" + 
        "MIICIjANBgkqhkiG9w0BAQEFAAOCAg8AMIICCgKCAgEAyfASpUvwBS8kUMmRufhw\n" + 
        "6YK/bK4IaCfwf4ErOJSU6fSSejbNzNMCsVGGXfU2IE9r9eNDKjvgKlSGYsw/z6E0\n" + 
        "4zMESBN6v1JFmbz6suFLCYMvJnRjYzu4GudgIkYEdL/8GIs5qqgqoU6J3pmG8PGl\n" + 
        "WFFAFZuEugCnhnDnhvrZTW6TUS9q1ExlFDr5+l3qgybvwDFmSTDTtMuRToZyGgXT\n" + 
        "8iIMzJidn5C0+ZqnOFKu2jk1pu5ynvUE7/Q5c48sKBpDPFhdBUnPn4gyvxX+YDd8\n" + 
        "hbB7SgKjiIAb2kyzPr+DnTQBqM428G7++TroOmngSQ9iD3HlzBuvoBGKd75YAvie\n" + 
        "5jxJ0nlKeTc2SexTgDJ7wAQHhUiEWnxlTwFB701oz2/jdb+CU3nIjD3gfyCPBdsg\n" + 
        "+4Ga3Hu28eJKvnm/gTmg/3j85FZyQeIH+I1+MZJ6T6LQZwfZCh8d8ANsSyWfoBoX\n" + 
        "R2Lo19Q3+RyFphb/jJhkVrVT689+/6NzKfZ+3hhUO0R8evoRTDdjgfPQZwihvzkT\n" + 
        "jT3g5TEVz00c8iN/We1bWWGjU6QN+zZrsmVsnCvCyc8REOmpDkFFabLEucElTLwk\n" + 
        "aFBXerrmg6N9N+IuzWV0g8QMUrZtASevLWkrZ/iKUUCRmp2lpXazYPRxm4mht6f8\n" + 
        "KT9MwG+UjgwJiF8de9iP14sCAwEAAQ==\n" + 
        "-----END PUBLIC KEY-----");
  }
  
  private void loadKey(String encodedPublicKey)
  {
    PublicKey publicKey = CipherSuiteUtils.publicKeyFromPem(PemPublicKey.newBuilder().build(encodedPublicKey));
    String keyId = AllegroBotCredential.generateKid(publicKey);
    
    keyMap_.put(keyId, publicKey);
  }

  @Override
  public String authenticate(IRequestContext context) throws CanonException
  {
    return authenticate(getToken(context));
  }

  @Override
  public String authenticate(HttpServletRequest context) throws CanonException
  {
    return authenticate(getToken(context));
  }

  private String authenticate(String token)
  {
    try
    {

      Jws<Claims> parsedJwt = Jwts.parserBuilder().setSigningKeyResolver(new SigningKeyResolver()).build()
          .parseClaimsJws(token);
      
      if(! SignatureAlgorithm.RS512.toString().equals(parsedJwt.getHeader().getAlgorithm()))
        throw new NotAuthenticatedException("Invalid JWT Token, unacceptable signature algorithm");
      
      Claims claims = parsedJwt.getBody();
      
      String userId = claims.get("sub").toString();
      
      log_.info("Request from " + userId );
      
      return userId;
    }
    catch(JwtException e)
    {
      throw new NotAuthenticatedException("Invalid JWT token: " + e.getLocalizedMessage());
    }
    catch(RuntimeException e)
    {
      throw new ServerErrorException("Unable to verify JWT token", e);
    }
  }
  
  class SigningKeyResolver extends SigningKeyResolverAdapter
  {
    @Override
    public Key resolveSigningKey(@SuppressWarnings("rawtypes") JwsHeader header, Claims claims)
    {
      String keyId = header.getKeyId();
      
      if(keyId != null)
      {
        Key key = keyMap_.get(keyId);
        
        if(key != null)
          return key;
      }
      
      throw new NotAuthenticatedException("Invalid JWT token");
    }
  }

}
