document.addEventListener("DOMContentLoaded", () => {
    // const button = document.getElementById("analyze-btn");
    // const count = parseInt(button.dataset.count, 10); // 소개팅 횟수
    // const today = new Date();
    // const day = today.getDay()
    // const dateKey = today.toISOString().slice(0, 10);
    // const storageKey = `buttonClicked-${dateKey}`;

    // const timestampKey = "weeklyClickTimestamp";

// // 날짜 관련 처리
//     const now = Date.now();
//     const lastClicked = localStorage.getItem(timestampKey);
//
// // 7일(일주일) = 7 * 24 * 60 * 60 * 1000
//     const oneWeek = 7 * 24 * 60 * 60 * 1000;
//
// // 일주일 지났으면 초기화
//     if (lastClicked && now - parseInt(lastClicked) > oneWeek) {
//         localStorage.removeItem(storageKey);
//         localStorage.removeItem(timestampKey);
//     }
//
//     // 조건: 일요일 , 소개팅 5회 이상 , 아직 클릭 안 함
//     if (day === 0 && count >= 5 && !localStorage.getItem(storageKey)) {
//         button.style.display = "inline-block";
//
//         button.addEventListener("click", () => {
//             localStorage.setItem(storageKey, "true");
//             button.style.display = "none";
//         });
//     }

    // 삭제 버튼 이벤트
    const deleteButtons = document.querySelectorAll("button.delete-btn");
    deleteButtons.forEach(button => {
        button.addEventListener("click", function () {
            const courseId = this.getAttribute("data-course-id");
            if (!courseId) {
                console.error("삭제할 courseId가 없습니다.");
                return;
            }

            if (confirm("정말 삭제하시겠습니까?")) {
                fetch(`/api/recommend/${courseId}`, {
                    method: 'DELETE'
                }).then(response => {
                    if (response.ok) {
                        this.closest(".course-card")?.remove();
                        alert("코스가 삭제되었습니다.");
                    } else {
                        response.json().then(errorData => {
                            alert(`삭제에 실패했습니다: ${errorData.message || response.statusText}`);
                        }).catch(() => {
                            alert(`삭제에 실패했습니다: ${response.statusText}`);
                        });
                    }
                }).catch(err => {
                    console.error("서버 오류:", err);
                    alert("서버 오류로 삭제에 실패했습니다.");
                });
            }
        });
    });

    // 수정 버튼 클릭 (나의 데이트 코스)
    const courseEditButtons = document.querySelectorAll(
        "div.course-card > .button-row-horizontal > button.edit-btn");
    const hiddenCourseIdInput = document.getElementById("selected-course-id");
    const updateOverlay = document.getElementById("update-overlay");

    courseEditButtons.forEach(button => {
        button.addEventListener("click", function () {
            hiddenCourseIdInput.value = this.getAttribute("data-course-id");
            updateOverlay.classList.remove('d-none');
        });
    });

    document.getElementById("closeUpdate").addEventListener('click', () => {
        updateOverlay.classList.add('d-none');
        document.getElementById("formFile").value = '';
    })

    document.getElementById("updateRecommend-btn").addEventListener('click',
        () => {
            const courseId = hiddenCourseIdInput.value;
            const imageInput = document.getElementById("formFile");
            const image = imageInput?.files[0];

            if (!courseId) {
                alert("수정할 코스 ID가 선택되지 않았습니다.");
                return;
            }

            if (!image) {
                alert("수정할 이미지를 선택해주세요.");
                return;
            }

            const formData = new FormData();
            formData.append("image", image);

            fetch(`/api/recommend/${courseId}`, {
                method: "PATCH",
                body: formData
            })
            .then(res => {
                if (!res.ok) {
                    return res.json().then(errorData => {
                        throw new Error(errorData.message || "업로드 실패");
                    });
                }
                return res.json();
            })
            .then(() => {
                alert("수정 완료!");
                location.replace(`/mypage`);
            })
            .catch(err => {
                alert("수정 실패: " + err.message);
                console.error("수정 실패:", err);
            });
        });

    // 매칭 서비스
    const matchServiceBtn = document.getElementById('matchServiceBtn');
    const matchingServiceModal = document.getElementById('matchingServiceModal');
    const closeMatchModalBtn = matchingServiceModal.querySelector(
        '.close-button');
    const onlineUsersListDiv = document.getElementById('onlineUsersList');

    // 나이 계산
    function calculateAge(dateOfBirth) {
        if (!dateOfBirth) {
            return '정보 없음';
        }
        const birthDate = new Date(dateOfBirth);
        const today = new Date();
        let age = today.getFullYear() - birthDate.getFullYear();
        const m = today.getMonth() - birthDate.getMonth();
        if (m < 0 || (m === 0 && today.getDate() < birthDate.getDate())) {
            age--;
        }
        return age;
    }

    // 채팅 버튼
    async function handleChatButtonClick(event) {
        const button = event.currentTarget;
        const targetNickname = button.getAttribute('data-nickname');
        const targetUsn = button.getAttribute('data-usn');
        const currentLoggedInUserUsn = window.loggedInUserUsn;

        if (!currentLoggedInUserUsn) {
            alert('로그인 사용자 정보를 찾을 수 없습니다. 다시 로그인해주세요.');
            window.location.href = '/login';
            return;
        }

        if (!targetNickname || !targetUsn) {
            alert('채팅할 사용자 정보를 찾을 수 없습니다. 다시 시도해주세요.');
            console.error('Missing targetNickname or targetUsn for chat button.');
            return;
        }

        if (parseInt(targetUsn) === currentLoggedInUserUsn) {
            alert('자기 자신과는 채팅할 수 없습니다.');
            return;
        }

        try {
            const response = await fetch('/matchchat/createOrGetRoom', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({
                    targetNickname: targetNickname,
                    targetUsn: parseInt(targetUsn)
                })
            });

            if (response.redirected) {
                window.location.href = response.url;
                return;
            }

            if (!response.ok) {
                let errorMessage = `서버 오류 (${response.status}): ${response.statusText}`;
                if (response.status === 401) {
                    alert('로그인이 필요하거나 세션이 만료되었습니다. 다시 로그인해주세요.');
                    window.location.href = '/login';
                    return;
                }
                try {
                    const errorData = await response.json();
                    errorMessage = errorData.message || errorMessage;
                } catch (jsonError) {
                    console.warn('Error parsing error response as JSON:', jsonError);
                }
                throw new Error(errorMessage);
            }

            const data = await response.json();

            if (data && data.chatId) {
                alert(`${data.chatRoomName || '채팅방'}으로 이동합니다.`);
                window.location.href = '/chat/' + data.chatId;
            } else {
                alert('채팅방 정보를 받아오지 못했습니다. (예상치 못한 응답)');
                console.error('Invalid response data for chat room:', data);
            }
        } catch (error) {
            console.error('채팅 요청 중 오류 발생:', error);
            alert('채팅 요청 중 오류가 발생했습니다: ' + error.message);
        }
    }

    // 가입된 사용자 목록 불러오기
    async function fetchAndRenderMatchableUsers() {
        onlineUsersListDiv.innerHTML = '<p class="loading-message">사용자 목록을 불러오는 중...</p>';

        try {
            const response = await fetch('/api/matchableUsers');
            if (!response.ok) {
                if (response.status === 401) {
                    alert('로그인이 필요하거나 세션이 만료되었습니다. 다시 로그인해주세요.');
                    window.location.href = '/login';
                    return;
                }
                throw new Error(`HTTP error! status: ${response.status}`);
            }
            const matchableUsers = await response.json();

            onlineUsersListDiv.innerHTML = '';

            if (matchableUsers.length === 0) {
                onlineUsersListDiv.innerHTML = '<p class="no-users-message">현재 매칭 가능한 사용자가 없습니다.</p>';
            } else {
                matchableUsers.forEach(user => {
                    const userGender = user.gender === 'MALE' ? '남자' : '여자';
                    const userAge = calculateAge(user.dateOfBirth); // 나이 계산

                    const userItem = document.createElement('div');
                    userItem.className = 'online-user-item';
                    userItem.innerHTML = `
                        <img src="${user.profileImg
                    || '/images/default_profile.png'}" alt="프로필" class="profile-img">
                        <div class="user-info">
                            <span class="nickname">${user.nickName}</span>
                            <span class="details">${userGender} · ${userAge}세</span> </div>
                        <button class="chat-button" data-nickname="${user.nickName}" data-usn="${user.usn}">
                            <i class="fas fa-comment-dots"></i>
                        </button>
                    `;
                    onlineUsersListDiv.appendChild(userItem);
                });

                onlineUsersListDiv.querySelectorAll('.chat-button').forEach(button => {
                    button.addEventListener('click', handleChatButtonClick);
                });
            }

        } catch (error) {
            console.error('매칭 가능한 사용자 목록을 불러오는 중 오류 발생:', error);
            onlineUsersListDiv.innerHTML = '<p class="no-users-message">사용자 목록을 불러오는 데 실패했습니다.</p>';
        }
    }

    // 매칭 서비스 버튼 클릭
    if (matchServiceBtn) {
        matchServiceBtn.addEventListener('click', () => {
            matchingServiceModal.classList.remove('d-none');
            fetchAndRenderMatchableUsers(); // 사용자 목록 불러오기
        });
    }

    // 모달 닫기 버튼
    if (closeMatchModalBtn) {
        closeMatchModalBtn.addEventListener('click', () => {
            matchingServiceModal.classList.add('d-none');
        });
    }

    // 모달 외부 클릭 시 닫기
    if (matchingServiceModal) {
        matchingServiceModal.addEventListener('click', (event) => {
            if (event.target === matchingServiceModal) {
                matchingServiceModal.classList.add('d-none');
            }
        });
    }
});