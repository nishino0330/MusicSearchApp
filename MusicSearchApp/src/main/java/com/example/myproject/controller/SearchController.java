package com.example.myproject.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.myproject.service.SpotifyService;

import org.springframework.ui.Model;

@Controller
public class SearchController {
    @Autowired
    private SpotifyService spotifyService;

    @GetMapping("/")
    public String showSearchForm(Model model) {
        return "index";
    }

    @GetMapping("/musicList")
    public String musicList(@RequestParam("artist") String artist, Model model) {
        // アーティスト名を使ってトラックのIDを取得
        List<String> trackNames =spotifyService.getTrackNamesByArtist(artist);
        
        // trackNamesがnullの場合、エラーメッセージを表示する
        if (trackNames == null) {
            // トラックが見つからなかった場合のメッセージをモデルに追加
            model.addAttribute("notFoundMessage", "トラックが見つかりませんでした");
            // エラーページに遷移せず、直接メッセージを表示する
            return "musicList";
        }

        // モデルにデータを追加
        model.addAttribute("artist", artist);
        model.addAttribute("trackNames", trackNames);

        // musicList.htmlに遷移
        return "musicList";
    }
}
