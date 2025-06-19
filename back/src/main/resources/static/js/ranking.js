document.addEventListener('DOMContentLoaded', function() {
    const scoreRankingBtn = document.getElementById('scoreRankingBtn');
    const countRankingBtn = document.getElementById('countRankingBtn');
    const top3RankingSection = document.getElementById('top3Ranking');
    const rankingTableBody = document.getElementById('rankingTableBody');
    const rankingValueHeader = document.getElementById('rankingValueHeader');

    // 랭킹 데이터 렌더링
    async function fetchAndRenderRanking(type) {
        let apiUrl = '';
        let valueLabel = '';
        let valueUnit = '';

        if (type === 'score') {
            apiUrl = '/api/rankings/score';
            valueLabel = '점수';
            valueUnit = '점';
        } else if (type === 'count') {
            apiUrl = '/api/rankings/count';
            valueLabel = '소개팅 횟수';
            valueUnit = '회';
        }

        try {
            const response = await fetch(apiUrl);
            if (!response.ok) {
                throw new Error(`HTTP error! status: ${response.status}`);
            }
            const rankingList = await response.json();

            // Top 3 렌더링
            renderTop3(rankingList, valueUnit, type);

            // 테이블 렌더링
            renderRankingTable(rankingList, valueUnit, type);

            // 테이블 헤더 업데이트
            rankingValueHeader.textContent = valueLabel;

            // 버튼 활성화 상태 업데이트
            updateButtonState(type);

            attachChatButtonListeners();

        } catch (error) {
            console.error('랭킹 데이터를 불러오는 중 오류 발생:', error);
            top3RankingSection.innerHTML = '<p>랭킹 데이터를 불러올 수 없습니다.</p>';
            rankingTableBody.innerHTML = '<tr><td colspan="5">랭킹 데이터를 불러올 수 없습니다.</td></tr>';
        }
    }

    // Top 3  렌더링 함수
    function renderTop3(rankingList, valueUnit, type) {
        top3RankingSection.innerHTML = ''; // 기존 내용 지우기

        const topRanks = [];
        if (rankingList.length > 0) topRanks.push({ rank: 1, data: rankingList[0], badge: '🥇', class: 'first' });
        if (rankingList.length > 1) topRanks.push({ rank: 2, data: rankingList[1], badge: '🥈', class: 'second' });
        if (rankingList.length > 2) topRanks.push({ rank: 3, data: rankingList[2], badge: '🥉', class: 'third' });

        topRanks.sort((a, b) => {
            if (a.rank === 2) return -1; // a가 2위면, b보다 앞으로 (왼쪽으로)
            if (b.rank === 2) return 1;  // b가 2위면, a보다 뒤로 (오른쪽으로)
            if (a.rank === 1) return -1; // a가 1위면, b보다 앞으로 (왼쪽으로)
            if (b.rank === 1) return 1;  // b가 1위면, a보다 뒤로 (오른쪽으로)
            return 0; // 순위가 같으면 순서 변경 없음
        });

        topRanks.forEach(item => {
            const card = document.createElement('div');
            card.className = `card ${item.class}`;

            let displayValue = (type === 'score') ? item.data.score : item.data.count;
            const valueText = `${displayValue}${valueUnit}`;

            card.innerHTML = `
            <div class="rank-badge">${item.badge}</div>
            <div class="profile-area">
                <img src="${item.data.profileImg}" alt="프로필">
            </div>
            <div class="info-area">
                <div class="nickname-chat">
                    <span>${item.data.nickname}</span>
                    <button class="chat-button" data-nickname="${item.data.nickname}" data-usn="${item.data.usn}">
                        <i class="fas fa-comment-dots"></i>
                    </button>
                </div>
                <div class="score-count">${valueText}</div>
            </div>
        `;
            top3RankingSection.appendChild(card);
        });
    }

    // 랭킹 테이블 렌더링
    function renderRankingTable(rankingList, valueUnit, type) {
        rankingTableBody.innerHTML = '';

        for (let i = 3; i < rankingList.length; i++) {
            const rank = rankingList[i];
            const row = document.createElement('tr');

            let displayValue = (type === 'score') ? rank.score : rank.count;
            const valueText = `${displayValue}${valueUnit}`;

            row.innerHTML = `
                <td>${i + 1}</td>
                <td><img src="${rank.profileImg}" class="profile-img" alt="프로필" /></td>
                <td>
                    <span>${rank.nickname}</span>
                    <button class="chat-button" data-nickname="${rank.nickname}" data-usn="${rank.usn}">
                        <i class="fas fa-comment-dots"></i>
                    </button>
                </td>
                <td class="gender">${rank.gender}</td>
                <td>${valueText}</td>
            `;
            rankingTableBody.appendChild(row);
        }
    }

    // 버튼 활성화
    function updateButtonState(activeType) {
        scoreRankingBtn.classList.remove('active');
        countRankingBtn.classList.remove('active');
        if (activeType === 'score') {
            scoreRankingBtn.classList.add('active');
        } else if (activeType === 'count') {
            countRankingBtn.classList.add('active');
        }
    }

    function attachChatButtonListeners() {
        document.querySelectorAll('.chat-button').forEach(button => {
            button.addEventListener('click', handleChatButtonClick);
        });
    }

    // 채팅 버튼
    async function handleChatButtonClick(event) {
        const button = event.currentTarget;
        const targetNickname = button.getAttribute('data-nickname');
        const targetUsn = button.getAttribute('data-usn');

        if (!targetNickname || !targetUsn) {
            alert('채팅할 사용자 정보를 찾을 수 없습니다. 다시 시도해주세요.');
            console.error('Missing targetNickname or targetUsn for chat button.');
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
                try {
                    const errorData = await response.json();
                    errorMessage = errorData.message || errorMessage;
                } catch (jsonError) {
                    console.warn('Error parsing error response as JSON:', jsonError);
                }
                throw new Error(errorMessage);
            }

            const data = await response.json();

            if (data && data.roomId) {
                window.location.href = '/matchchat/room/' + data.roomId;
            } else {
                alert('채팅방 정보를 받아오지 못했습니다. (예상치 못한 응답)');
                console.error('Invalid response data for chat room:', data);
            }
        } catch (error) {
            console.error('채팅 요청 중 오류 발생:', error);
            alert('채팅 요청 중 오류가 발생했습니다: ' + error.message);
        }
    }

    scoreRankingBtn.addEventListener('click', () => fetchAndRenderRanking('score'));
    countRankingBtn.addEventListener('click', () => fetchAndRenderRanking('count'));

    fetchAndRenderRanking('score');
});
