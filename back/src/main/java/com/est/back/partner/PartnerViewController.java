package com.est.back.partner;

import com.est.back.user.User;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PartnerViewController {
    @GetMapping("/partners/setting")
    public String viewPartnerSetting(HttpSession session) {
        User user = (User) session.getAttribute("loggedInUser");
        if(user == null){
            return "redirect:/login";
        }
        return "datesetting";
    }

}
