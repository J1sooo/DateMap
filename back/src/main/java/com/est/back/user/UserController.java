package com.est.back.user;

import com.est.back.user.dto.JoinRequestDto;
import com.est.back.user.dto.LoginRequestDto;
import com.est.back.user.dto.UserUpdateRequestDto;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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
        model.addAttribute("currentPage", "/login");
        return "login";
    }

    @PostMapping("/login")
    public String login(@ModelAttribute LoginRequestDto loginRequestDto,
                        @RequestParam(value = "rememberMe", defaultValue = "false") boolean rememberMe,
                        Model model, HttpSession session,
                        HttpServletResponse response) {
        try {
            User user = userService.login(loginRequestDto);
            session.setAttribute("loggedInUser", user);
            session.setAttribute("usn", user.getUsn());

            if (rememberMe) {
                Cookie cookie = new Cookie("savedUserId", loginRequestDto.getUsername());
                cookie.setMaxAge(60 * 60 * 24 * 30);
                cookie.setPath("/");
                response.addCookie(cookie);
            } else {
                Cookie cookie = new Cookie("savedUserId", null);
                cookie.setMaxAge(0);
                cookie.setPath("/");
                response.addCookie(cookie);
            }

            return "redirect:/main";
        } catch (IllegalArgumentException e) {
            model.addAttribute("errorMessage", e.getMessage());
            model.addAttribute("savedUserId", loginRequestDto.getUsername());
            model.addAttribute("currentPage", "/login");
            return "login";
        }
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/main";
    }

    @GetMapping("/join")
    public String joinPage(Model model) {
        if (!model.containsAttribute("joinRequestDto")) {
            model.addAttribute("joinRequestDto", new JoinRequestDto());
        }
        populateDateModelAttributes(model);
        model.addAttribute("currentPage", "/join");
        return "join";
    }

    @PostMapping("/join")
    public String join(@Valid @ModelAttribute JoinRequestDto joinRequestDto, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            populateDateModelAttributes(model);
            model.addAttribute("currentPage", "/join");
            return "join";
        }

        if (!joinRequestDto.getPassword().equals(joinRequestDto.getPasswordCheck())) {
            bindingResult.rejectValue("passwordCheck", "passwordMismatch", "비밀번호가 일치하지 않습니다.");
            populateDateModelAttributes(model);
            model.addAttribute("currentPage", "/join");
            return "join";
        }

        if ((joinRequestDto.getPreferArea() == null || joinRequestDto.getPreferArea().isEmpty()) &&
                (joinRequestDto.getPreferAreaDetail() == null || joinRequestDto.getPreferAreaDetail().isEmpty())) {
            bindingResult.rejectValue("preferArea", "minSelection", "선호 지역은 최소 1개 이상 선택해야 합니다.");
            populateDateModelAttributes(model);
            model.addAttribute("currentPage", "/join");
            return "join";
        }

        try {
            userService.join(joinRequestDto);
            return "redirect:/login";
        } catch (IllegalArgumentException e) {
            model.addAttribute("errorMessage", e.getMessage());
            populateDateModelAttributes(model);
            model.addAttribute("currentPage", "/join");
            return "join";
        }
    }

    // 회원 정보 수정
    @GetMapping("/profile/edit")
    public String editProfilePage(HttpSession session, Model model, RedirectAttributes redirectAttributes) {
        User loggedInUser = (User) session.getAttribute("loggedInUser");
        if (loggedInUser == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "로그인이 필요합니다.");
            return "redirect:/login";
        }

        try {
            User user = userService.getUserInfo(loggedInUser.getUsn());

            UserUpdateRequestDto userProfileUpdateDto = new UserUpdateRequestDto();
            userProfileUpdateDto.setUsn(user.getUsn());
            userProfileUpdateDto.setUserId(user.getUserId());
            userProfileUpdateDto.setNickName(user.getNickName());
            userProfileUpdateDto.setEmail(user.getEmail());
            userProfileUpdateDto.setGender(user.getGender());
            if (user.getDateOfBirth() != null) {
                userProfileUpdateDto.setBirthYear(user.getDateOfBirth().getYear());
                userProfileUpdateDto.setBirthMonth(user.getDateOfBirth().getMonthValue());
                userProfileUpdateDto.setBirthDay(user.getDateOfBirth().getDayOfMonth());
            }
            userProfileUpdateDto.setPreferArea(user.getPreferArea());
            userProfileUpdateDto.setPreferAreaDetail(user.getPreferAreaDetail());
            userProfileUpdateDto.setProfileImageUrl(user.getProfileImg());

            model.addAttribute("userProfileUpdateDto", userProfileUpdateDto);
            populateDateModelAttributes(model);
            return "modifyprofile";
        } catch (IllegalArgumentException e) {
            log.error("Failed to load user info for profile edit: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage", "사용자 정보를 불러오는데 실패했습니다.");
            return "redirect:/login";
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

    @GetMapping("/chat")
    public String chatPage() {
        return "datesetting"; // 실시간 채팅 html
    }

    @GetMapping("/calendar")
    public String calendarPage() {
        return "/aiRecommend/recommendSetting"; // 데이트 코스 추천 html
    }
}