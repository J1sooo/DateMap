document.addEventListener("DOMContentLoaded", function () {
  const chatBox = document.querySelector(".chat-box");
  if (chatBox) {
    chatBox.scrollTop = chatBox.scrollHeight;
  }
});

function startSpeechRecognition() {
  if (!('webkitSpeechRecognition' in window)) {
    alert("이 브라우저는 음성 인식을 지원하지 않습니다. 크롬을 사용해주세요.");
    return;
  }

  const input = document.getElementById("messageInput");
  const recognition = new webkitSpeechRecognition();
  recognition.lang = "ko-KR";
  recognition.interimResults = false;
  recognition.maxAlternatives = 1;

  input.placeholder = "🎙️ 음성 인식 중...";

  recognition.onresult = function(event) {
    const transcript = event.results[0][0].transcript;
    input.value = transcript;
    input.placeholder = "메시지를 입력하세요";  // 다시 원래대로
  };

  recognition.onerror = function(event) {
    console.error("음성 인식 오류:", event.error);
    alert("음성 인식에 실패했어요. 다시 시도해 주세요.");
  };

  recognition.onend = function() {
    if (!input.value) {
      input.placeholder = "메시지를 입력하세요";  // 결과가 없을 때도 복원
    }
  };

  recognition.start();
}