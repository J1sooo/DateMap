package com.est.back.user;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest; // HttpServletRequest 임포트
import jakarta.servlet.http.HttpServletResponse; // HttpServletResponse 임포트
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Controller
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;

    @GetMapping("/login")
    public String loginPage(HttpServletRequest request, Model model) {
       // 아이디 저장
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("savedUserId")) {
                    model.addAttribute("savedUserId", cookie.getValue());
                    break;
                }
            }
        }
        if (!model.containsAttribute("savedUserId")) {
            model.addAttribute("savedUserId", "");
        }
        return "login";
    }

    @PostMapping("/login")
    public String login(@ModelAttribute LoginRequestDto loginRequestDto,
                        @RequestParam(value = "rememberMe", defaultValue = "false") boolean rememberMe, // 아이디 저장 체크박스
                        Model model, HttpSession session,
                        HttpServletResponse response) {
        try {
            User user = userService.login(loginRequestDto);
            session.setAttribute("loggedInUser", user);

            // 아이디 저장 로직
            if (rememberMe) {
                Cookie cookie = new Cookie("savedUserId", loginRequestDto.getUsername());
                cookie.setMaxAge(60 * 60 * 24 * 30); // 30일 동안 유효
                cookie.setPath("/");
                response.addCookie(cookie);
            } else {
                Cookie cookie = new Cookie("savedUserId", null);
                cookie.setMaxAge(0);
                cookie.setPath("/");
                response.addCookie(cookie);
            }

            return "redirect:/"; // 메인페이지
        } catch (IllegalArgumentException e) {
            model.addAttribute("errorMessage", e.getMessage());
            model.addAttribute("savedUserId", loginRequestDto.getUsername());
            return "login";
        }
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }

    @GetMapping("/join") // 회원가입 html 페이지 반환
    public String joinPage(Model model) {
        if (!model.containsAttribute("joinRequestDto")) {
            model.addAttribute("joinRequestDto", new JoinRequestDto());
        }
        populateDateModelAttributes(model);
        return "join";
    }

    @PostMapping("/join")
    public String join(@Valid @ModelAttribute JoinRequestDto joinRequestDto, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            populateDateModelAttributes(model);
            return "join";
        }

        // 비밀번호 확인 로직
        if (!joinRequestDto.getPassword().equals(joinRequestDto.getPasswordCheck())) {
            bindingResult.rejectValue("passwordCheck", "passwordMismatch", "비밀번호가 일치하지 않습니다.");
            populateDateModelAttributes(model);
            return "join";
        }

        if ((joinRequestDto.getPreferArea() == null || joinRequestDto.getPreferArea().isEmpty()) &&
                (joinRequestDto.getPreferAreaDetail() == null || joinRequestDto.getPreferAreaDetail().isEmpty())) {
            bindingResult.rejectValue("preferArea", "minSelection", "선호 지역은 최소 1개 이상 선택해야 합니다.");
            populateDateModelAttributes(model);
            return "join";
        }

        try {
            userService.join(joinRequestDto);
            return "redirect:/login";
        } catch (IllegalArgumentException e) {
            model.addAttribute("errorMessage", e.getMessage());
            populateDateModelAttributes(model);
            return "join";
        }
    }
    private void populateDateModelAttributes(Model model) {
        int currentYear = LocalDate.now().getYear();
        List<Integer> years = new ArrayList<>();
        for (int i = 1900; i <= currentYear; i++) {
            years.add(i);
        }
        model.addAttribute("years", years);

        List<Integer> months = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12);
        List<Integer> days = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31);
        model.addAttribute("months", months);
        model.addAttribute("days", days);
    }
}