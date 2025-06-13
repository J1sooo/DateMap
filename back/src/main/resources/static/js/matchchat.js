var stompClient = null;

function connect() {
    var socket = new SockJS('/ws/matchchat');
    stompClient = Stomp.over(socket);
    stompClient.connect({}, onConnected, onError);
}

function onConnected() {
    stompClient.subscribe('/topic/matchchat/' + chatroomId, onMessageReceived);
    stompClient.send("/app/chat.addUser", {}, JSON.stringify({
        chatRoomId: chatroomId,
        senderNickname: myNickname,
        senderUserId: loggedInUserId,
        type: 'ENTER'
    }));
}

function onError(error) {
    alert('WebSocket 연결에 실패했습니다. 페이지를 새로고침해주세요.');
}

function sendMessage(event) {
    var messageInput = document.getElementById('messageInput');
    var messageContent = messageInput.value.trim();
    if (messageContent && stompClient) {
        var chatMessage = {
            chatRoomId: chatroomId,
            senderNickname: myNickname,
            message: messageContent,
            type: 'TALK'
        };
        stompClient.send("/app/chat.sendMessage", {}, JSON.stringify(chatMessage));
        messageInput.value = '';
    }
    event.preventDefault();
}

function onMessageReceived(payload) {
    var message = JSON.parse(payload.body);
    var chatBox = document.getElementById('chatBox');
    var messageElement = document.createElement('div');
    messageElement.classList.add('message');
    if (message.type === 'ENTER') {
        messageElement.classList.add('system');
        messageElement.textContent = message.message;
        chatBox.appendChild(messageElement);
    } else if (message.type === 'TALK') {
        if (message.senderNickname === myNickname) {
            messageElement.classList.add('user');
        } else {
            messageElement.classList.add('partner');
        }
        messageElement.innerHTML = `<span class="message-content">${message.message}</span>`;
        chatBox.appendChild(messageElement);
    } else if (message.type === 'QUIT') {
        messageElement.classList.add('system');
        messageElement.textContent = message.message;
        chatBox.appendChild(messageElement);
        if (message.senderNickname !== myNickname) {
            alert(message.message);
            document.getElementById('messageInput').disabled = true;
            document.querySelector('.input-form button[type="submit"]').disabled = true;
            setTimeout(() => {
                window.location.href = '/main';
            }, 2000);
        }
    }
    chatBox.scrollTop = chatBox.scrollHeight;
}

document.addEventListener('DOMContentLoaded', connect);

let hasQuit = false;

function sendQuitMessage() {
    if (hasQuit || !stompClient || !chatroomId || !myNickname) return;
    const quitMessage = {
        chatRoomId: chatroomId,
        senderNickname: myNickname,
        type: 'QUIT',
        message: myNickname + ' 님이 채팅방을 나갑니다.'
    };
    stompClient.send("/app/chat.quitRoom", {}, JSON.stringify(quitMessage));
    hasQuit = true;
}

document.addEventListener('DOMContentLoaded', function() {
    document.querySelector('.end-button').addEventListener('click', function() {
        sendQuitMessage();
        window.location.href = '/main';
    });
});

window.addEventListener('beforeunload', function() {
    if (stompClient && chatroomId && myNickname) {
        const quitMessage = {
            chatRoomId: chatroomId,
            senderNickname: myNickname,
            type: 'QUIT',
            message: myNickname + ' 님이 채팅방을 나갑니다.'
        };
        stompClient.send("/app/chat.quitRoom", {}, JSON.stringify(quitMessage));
        stompClient.disconnect();
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
        input.placeholder = "메시지를 입력하세요";
    };
    recognition.onerror = function(event) {
        alert("음성 인식에 실패했어요. 다시 시도해 주세요.");
    };
    recognition.onend = function() {
        if (!input.value) {
            input.placeholder = "메시지를 입력하세요";
        }
    };
    recognition.start();
}
