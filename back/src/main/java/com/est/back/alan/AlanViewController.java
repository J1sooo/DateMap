package com.est.back.alan;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/recommend")
public class AlanViewController {
    @GetMapping("/setting")
    public String setting() {
        return "aiRecommend/recommendSetting";
    }

    @GetMapping("/place")
    public String recommend() {
        return "aiRecommend/recommendPlace";
    }
}
