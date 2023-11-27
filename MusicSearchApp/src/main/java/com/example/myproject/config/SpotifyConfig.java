package com.example.myproject.config;

import java.net.URI;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import se.michaelthelin.spotify.SpotifyApi;

@Configuration
public class SpotifyConfig {
  @Value("${spotify.client.id}")
  private String clientId;
  
  @Value("${spotify.client.secret}")
  private String clientSecret;
  
  @Value("${spotify.redirect.url}")
  private URI redirectUri;
  
  @Bean
  public SpotifyApi getSpotifyApi() {
    return new SpotifyApi.Builder()
      .setClientId(clientId) // Client IDを設定する
      .setClientSecret(clientSecret) // Client Secretを設定する
      .setRedirectUri(redirectUri) // リダイレクトURIを設定する
      .build();
  }
}