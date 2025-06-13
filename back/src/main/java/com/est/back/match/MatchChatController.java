package com.est.back.match;

import com.est.back.match.dto.MatchChatMessageDto;
import com.est.back.match.dto.MatchChatRoomRequestDto;
import com.est.back.user.User;
import com.est.back.user.UserService;
import com.est.back.user.dto.UserInfoResponseDto;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Controller
@RequestMapping("/matchchat")
public class MatchChatController {

    private final UserService userService;
    private final MatchChatRoomService chatRoomService;

    public MatchChatController(UserService userService, MatchChatRoomService chatRoomService) {
        this.userService = userService;
        this.chatRoomService = chatRoomService;
    }

    @PostMapping("/createOrGetRoom")    // 채팅방 생성
    public String createOrGetMatchChatRoom(@RequestBody MatchChatRoomRequestDto matchChatRoomRequestDto,
                                           HttpSession session,
                                           RedirectAttributes redirectAttributes) {
        User currentUser = (User) session.getAttribute("loggedInUser");
        if (currentUser == null) {
            return "redirect:/login";
        }

        String targetNickname = matchChatRoomRequestDto.getTargetNickname();

        User targetUser = userService.findByNickName(targetNickname)
                .orElseThrow(() -> new IllegalArgumentException("상대방 사용자를 찾을 수 없습니다."));

        String chatRoomId = UUID.randomUUID().toString();

        Map<String, String> participants = new ConcurrentHashMap<>();
        participants.put(currentUser.getUserId(), currentUser.getNickName());
        participants.put(targetUser.getUserId(), targetUser.getNickName());

        // 채팅방 정보 추가
        chatRoomService.addChatRoom(chatRoomId, participants);

        redirectAttributes.addAttribute("chatroomId", chatRoomId);
        return "redirect:/matchchat/room/{chatroomId}";
    }

    @GetMapping("/room/{chatroomId}")   // 특정 채팅방
    public String getMatchChatRoom(@PathVariable String chatroomId, Model model, HttpSession session) {
        User currentUser = (User) session.getAttribute("loggedInUser");
        if (currentUser == null) {
            return "redirect:/login";
        }

        // 참여자 정보 가져오기
        Map<String, String> participants = chatRoomService.getParticipants(chatroomId);
        if (participants == null || !participants.containsKey(currentUser.getUserId())) {
            return "redirect:/main";
        }

        String partnerNickname = null;
        String partnerUserId = null;
        for (Map.Entry<String, String> entry : participants.entrySet()) {
            if (!entry.getKey().equals(currentUser.getUserId())) {
                partnerNickname = entry.getValue();
                partnerUserId = entry.getKey();
                break;
            }
        }

        if (partnerNickname == null) {
            return "redirect:/main";
        }

        UserInfoResponseDto myProfile = UserInfoResponseDto.builder()
                .profileImg(currentUser.getProfileImg())
                .nickName(currentUser.getNickName())
                .build();

        User partnerUser = userService.findByUserId(partnerUserId)
                .orElseThrow(() -> new IllegalArgumentException("상대방 사용자를 찾을 수 없습니다."));

        UserInfoResponseDto partnerProfile = UserInfoResponseDto.builder()
                .profileImg(partnerUser.getProfileImg())
                .nickName(partnerUser.getNickName())
                .build();

        model.addAttribute("chatroomId", chatroomId);
        model.addAttribute("myProfile", myProfile);
        model.addAttribute("partnerProfile", partnerProfile);

        List<MatchChatMessageDto> messages = new ArrayList<>();
        model.addAttribute("messages", messages);

        return "matchchat";
    }

    @PostMapping("/{chatroomId}/end")   // 대화종료
    public String handleChatFeedback(@PathVariable String chatroomId,
                                     HttpSession session,
                                     RedirectAttributes redirectAttributes) {
        User currentUser = (User) session.getAttribute("loggedInUser");
        if (currentUser == null) {
            return "redirect:/login";
        }

        redirectAttributes.addFlashAttribute("message", "대화가 종료되었습니다.");

        chatRoomService.removeChatRoom(chatroomId); // 채팅방 삭제 메서드 호출
        return "redirect:/main";
    }
}