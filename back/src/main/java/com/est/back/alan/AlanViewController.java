package com.est.back.alan;

import com.est.back.user.User;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/recommendations")
public class AlanViewController {
    @GetMapping("/setting")
    public String setting(HttpSession session) {
        User user = (User) session.getAttribute("loggedInUser");
        if(user == null){
            return "redirect:/login";
        }
        return "recommendSetting";
    }

    @GetMapping("/place")
    public String recommend() {
        return "recommendPlace";
    }
}
