package com.example.myproject.service;

import java.io.IOException;
import java.net.URI;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;


import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.exceptions.SpotifyWebApiException;
import se.michaelthelin.spotify.model_objects.credentials.AuthorizationCodeCredentials;
import se.michaelthelin.spotify.model_objects.specification.AlbumSimplified;
import se.michaelthelin.spotify.model_objects.specification.Paging;
import se.michaelthelin.spotify.model_objects.specification.Track;
import se.michaelthelin.spotify.requests.authorization.authorization_code.AuthorizationCodeRequest;
import se.michaelthelin.spotify.requests.authorization.authorization_code.AuthorizationCodeUriRequest;
import se.michaelthelin.spotify.requests.data.albums.GetAlbumsTracksRequest;
import se.michaelthelin.spotify.requests.data.artists.GetArtistsAlbumsRequest;
import se.michaelthelin.spotify.requests.data.artists.GetArtistsTopTracksRequest;
import se.michaelthelin.spotify.requests.data.search.simplified.SearchArtistsRequest;

import se.michaelthelin.spotify.model_objects.specification.TrackSimplified;

@Service
public class SpotifyService{
	@Autowired
	private SpotifyApi spotifyApi;
	
	public SpotifyService(@Autowired SpotifyApi spotifyApi) throws org.apache.hc.core5.http.ParseException, SpotifyWebApiException, IOException, ParseException {
		  this.spotifyApi = spotifyApi; // SpotifyApiクラスのインスタンスを取得する
		  // アクセストークンとリフレッシュトークンをコード内で取得する
		  AuthorizationCodeUriRequest authorizationCodeUriRequest = spotifyApi.authorizationCodeUri()
		    .scope("user-read-currently-playing,user-read-recently-played,user-read-playback-state,user-top-read,user-modify-playback-state")
		    .show_dialog(true)
		    .build();
		  URI uri = authorizationCodeUriRequest.execute();
		  System.out.println("URI: " + uri.toString());
		  // ブラウザでURIにアクセスして、認証コードを取得する
		  Scanner scanner = new Scanner(System.in);
		  System.out.print("Enter the code:");
		  String code = scanner.nextLine();
		  scanner.close();
		  // 認証コードからアクセストークンとリフレッシュトークンを取得する
		  AuthorizationCodeRequest authorizationCodeRequest = spotifyApi.authorizationCode(code)
		    .build();
		  AuthorizationCodeCredentials authorizationCodeCredentials = authorizationCodeRequest.execute(); // このメソッドで例外が発生する可能性がある
		String accessToken = authorizationCodeCredentials.getAccessToken();
		String refreshToken = authorizationCodeCredentials.getRefreshToken();
		// アクセストークンとリフレッシュトークンをセットする
		spotifyApi.setAccessToken(accessToken);
		spotifyApi.setRefreshToken(refreshToken);
	}
	
	
	
	
	// アーティストの名前からIDを取得
	public String getArtistIdByName(String name) {
		SearchArtistsRequest searchArtistsRequest = spotifyApi.searchArtists(name).build();
		try {
			return searchArtistsRequest.execute().getItems()[0].getId();
		}catch(Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	//アーティストIDからアルバムのIDリストを取得
	public List getAlbumIdsByArtistId(String artistId){
		List albumIds = new ArrayList<>();
		
		GetArtistsAlbumsRequest getArtistsAlbumsRequest = spotifyApi.getArtistsAlbums(artistId).build();
		try {
			Paging paging = getArtistsAlbumsRequest.execute();
            AlbumSimplified[] albums = (AlbumSimplified[]) paging.getItems();
            for (AlbumSimplified album : albums) {
                albumIds.add(album.getId());
            }
            return albumIds;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
	
	// アルバムIDからトラックの名前のリストを取得
	public List<String> getTrackNamesByAlbumId(String albumId) {
	    List<String> trackNames = new ArrayList<>();
	    // getArtistsTopTracksメソッドではなく、getAlbumTracksメソッドを使う
	    GetAlbumsTracksRequest getAlbumTracksRequest = spotifyApi.getAlbumsTracks(albumId).build();
	    try {
	        Paging paging = getAlbumTracksRequest.execute();
	        TrackSimplified[] tracks = (TrackSimplified[]) paging.getItems();
	        for (TrackSimplified track : tracks) {
	            trackNames.add(track.getName());
	        }
	        return trackNames;
	    } catch (Exception e) {
	        e.printStackTrace();
	        return null;
	    }
	}

	// アーティストの名前からトラックの名前のリストを取得するメソッド
	public List<String> getTrackNamesByArtist(String artist) {
	    List<String> trackNames = new ArrayList<>();
	    String artistId = getArtistIdByName(artist);
	    List<String> albumIds = getAlbumIdsByArtistId(artistId);
	    for (String albumId : albumIds) {
	        trackNames.addAll(getTrackNamesByAlbumId(albumId));
	    }
	    return trackNames;
	}

}
