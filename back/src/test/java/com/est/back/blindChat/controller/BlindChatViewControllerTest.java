package com.est.back.blindChat.controller;

import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import com.est.back.blindChat.service.BlindChatService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;


@WebMvcTest(BlindChatViewController.class)
class BlindChatViewControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BlindChatService blindChatService;


    @Test
    void showChatRoom() throws Exception {
        mockMvc.perform(get("/api/chat/1"))
            .andExpect(status().isOk())
            .andExpect(view().name("chat"));
        verify(blindChatService).getChatHistory(1L);
    }

    @Test
    void sendMessage() throws Exception {
        mockMvc.perform(post("/api/chat/1")
                .param("message", "안녕"))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/api/chat/1"));
        verify(blindChatService).chatWithGemini(1L, "안녕");
    }

    @Test
    void deleteMessage() throws Exception {
        mockMvc.perform(post("/api/chat/1")
                .param("_method", "delete"))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/api/chat/1"));
        verify(blindChatService).deleteChatRoom(1L);
    }

//    @Test
//    void blindDateFeedback() throws Exception {
//        mockMvc.perform(post("/api/chat/1/feedback"))
//            .andExpect(status().is3xxRedirection())
//            .andExpect(redirectedUrl("/api/chat/1"));
//        verify(blindChatService).feedbackFromGemini(1L);
//    }
}
