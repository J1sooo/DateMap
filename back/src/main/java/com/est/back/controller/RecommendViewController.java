package com.est.back.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class RecommendViewController {

    @GetMapping("/recommend-setting")
    public String viewPartnerSetting() {

        return "recommendSetting";
    }

}