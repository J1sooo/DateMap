document.addEventListener('DOMContentLoaded', function() {
    document.querySelectorAll('.chat-button').forEach(function(button) {
        button.addEventListener('click', function() {
            const targetNickname = button.getAttribute('data-nickname');
            if (targetNickname) {
                fetch('/matchchat/createOrGetRoom', {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json'
                    },
                    body: JSON.stringify({ targetNickname: targetNickname })
                })
                    .then(response => {
                        if (response.redirected) {
                            window.location.href = response.url;
                            return Promise.reject(new Error("리다이렉션 성공"));
                        }
                        if (!response.ok) {
                            return response.json()
                                .then(errorData => {
                                    throw new Error(errorData.message || `서버 오류: ${response.status} ${response.statusText}`);
                                })
                                .catch(() => {
                                    throw new Error(`서버 오류: ${response.status} ${response.statusText}. 응답 내용을 확인할 수 없습니다.`);
                                });
                        }
                        return response.json();
                    })
                    .then(data => {
                        if (data && data.roomId) {
                            window.location.href = '/matchchat/room/' + data.roomId;
                        } else {
                            alert('채팅방 정보를 받아오지 못했습니다. (예상치 못한 응답)');
                        }
                    })
                    .catch(error => {
                        if (error.message === "리다이렉션 성공") {
                        } else {
                            console.error('채팅 요청 중 오류 발생:', error);
                            alert('채팅 요청 중 오류가 발생했습니다: ' + error.message);
                        }
                    });
            } else {
                alert('채팅할 닉네임을 찾을 수 없습니다. 다시 시도해주세요.');
            }
        });
    });
});
