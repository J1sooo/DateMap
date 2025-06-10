package com.est.back.partner;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PartnerViewController {

    @GetMapping("/partner-setting")
    public String viewPartnerSetting() {

        return "datesetting";
    }

}
