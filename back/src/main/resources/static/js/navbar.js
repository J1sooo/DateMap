var notificationStompClient = null;
var notificationPopup = document.getElementById('notificationPopup');
var notificationList = document.getElementById('notificationList');
var noNotificationsMessage = document.getElementById('noNotificationsMessage');
var markAllAsReadButton = document.getElementById('markAllAsReadButton');


document.addEventListener('DOMContentLoaded', function () {
    if (window.loggedInUserId && window.loggedInUserId !== 'null') {
        fetchInitialNotifications();
        connectNotificationWebSocket();
    }
});

function connectNotificationWebSocket() {
    if (notificationStompClient && notificationStompClient.connected) {
        return;
    }
    var socket = new SockJS('/ws/notifications');
    notificationStompClient = Stomp.over(socket);

    notificationStompClient.connect({}, function (frame) {
        notificationStompClient.subscribe('/user/queue/notifications', function (message) {
            const notification = JSON.parse(message.body);
            addNotificationToList(notification, true);
            updateNotificationCount(1, true);
        });

        notificationStompClient.subscribe('/user/queue/unreadCount', function (message) {
            const count = parseInt(message.body);
            updateNotificationCount(count, false);
        });
    }, function (error) {
        setTimeout(connectNotificationWebSocket, 5000);
    });
}

window.updateNotificationCount = function updateNotificationCount(count, isDelta) {
    const notificationCountSpan = document.getElementById('notification-count');
    if (!notificationCountSpan) {
        console.warn("❗ #notification-count 요소 없음");
        return;
    }

    let currentCount = parseInt(notificationCountSpan.textContent || '0');

    if (isDelta) {
        currentCount += count;
    } else {
        currentCount = count;
    }

    if (currentCount > 0) {
        notificationCountSpan.textContent = String(currentCount);
        notificationCountSpan.style.display = 'inline-block';
    } else {
        notificationCountSpan.textContent = '0';
        notificationCountSpan.style.display = 'none';
    }
}


window.addNotificationToList = function addNotificationToList(notification, isNew) {
    if (!notification.id && !notification.chatRoomId) {
        console.warn("❌ id 없음, 건너뜀", notification);
        return;
    }

    // fallback: id 없으면 chatRoomId 사용
    const id = notification.id || notification.chatRoomId;

    const listItem = document.createElement('li');
    listItem.dataset.notificationId = id;
    if (isNew || !notification.read) {
        listItem.classList.add('unread');
    }

    let displayMessage = notification.message;
    if (notification.type === 'CHAT_MESSAGE' && notification.senderNickname) {
        displayMessage = `[채팅] ${notification.senderNickname}님: ${notification.message}`;
    } else if (notification.type === 'MATCH_FOUND') {
        displayMessage = `[매칭] ${notification.message}`;
    } else if (notification.type === 'GENERAL') {
        displayMessage = `[공지] ${notification.message}`;
    }

    let contentHtml = `<span class="notification-content">${displayMessage}</span>`;
    if (notification.timestamp) {
        contentHtml += `<span class="notification-time">${formatTimestamp(notification.timestamp)}</span>`;
    }
    if (notification.link) {
        contentHtml += `<a href="${notification.link}" class="notification-link" onclick="handleNotificationClick(event, '${id}')">바로가기</a>`;
    }

    listItem.innerHTML = contentHtml;
    notificationList.prepend(listItem);
    noNotificationsMessage.style.display = 'none';
    markAllAsReadButton.style.display = 'block';
}


function fetchInitialNotifications() {
    fetch('/api/notifications/unread-count') // ✅ 슬래시 제거
    .then(response => response.ok ? response.json() : 0)
    .then(count => updateNotificationCount(count, false))
    .catch(() => {});

    fetch('/api/notifications')
    .then(response => response.ok ? response.json() : [])
    .then(notifications => {
        notificationList.innerHTML = '';
        if (notifications && notifications.length > 0) {
            notifications.forEach(notif => addNotificationToList(notif, false));
            noNotificationsMessage.style.display = 'none';
            markAllAsReadButton.style.display = 'block';
        } else {
            noNotificationsMessage.style.display = 'block';
            markAllAsReadButton.style.display = 'none';
        }
    })
    .catch(() => {
        noNotificationsMessage.style.display = 'block';
        markAllAsReadButton.style.display = 'none';
    });
}

function formatTimestamp(isoTimestamp) {
    try {
        const date = new Date(isoTimestamp);
        const year = date.getFullYear();
        const month = String(date.getMonth() + 1).padStart(2, '0');
        const day = String(date.getDate()).padStart(2, '0');
        const hours = String(date.getHours()).padStart(2, '0');
        const minutes = String(date.getMinutes()).padStart(2, '0');
        return `${year}-${month}-${day} ${hours}:${minutes}`;
    } catch (e) {
        return isoTimestamp;
    }
}

document.getElementById('notification-bell').addEventListener('click', function (event) {
    event.preventDefault();
    if (window.loggedInUserId && window.loggedInUserId !== 'null') {
        if (notificationPopup.classList.contains('show')) {
            notificationPopup.classList.remove('show');
        } else {
            fetchInitialNotifications();
            notificationPopup.classList.add('show');
        }
    } else {
        alert('로그인 후 알림을 확인하세요.');
    }
});

document.addEventListener('click', function (event) {
    if (notificationPopup.classList.contains('show') &&
        !notificationPopup.contains(event.target) &&
        !document.getElementById('notification-bell').contains(event.target)) {
        notificationPopup.classList.remove('show');
    }
});

function handleNotificationClick(event, notificationId) {
    fetch(`/api/notifications/mark-as-read/${notificationId}`, {
        method: 'POST'
    })
    .then(response => {
        if (response.ok) {
            const clickedItem = event.target.closest('li');
            if (clickedItem) clickedItem.classList.remove('unread');
            updateNotificationCount(-1, true);
            notificationPopup.classList.remove('show');
        }
    });
}

markAllAsReadButton.addEventListener('click', function () {
    if (window.loggedInUserId && window.loggedInUserId !== 'null') {
        fetch('/api/notifications/mark-all-read', { method: 'POST' })
        .then(response => {
            if (response.ok) {
                notificationList.querySelectorAll('li.unread').forEach(item => item.classList.remove('unread'));
                updateNotificationCount(0, false);
                notificationPopup.classList.remove('show');
                noNotificationsMessage.style.display = 'block';
                markAllAsReadButton.style.display = 'none';
            }
        });
    }
});

window.addEventListener('beforeunload', function () {
    if (notificationStompClient && notificationStompClient.connected) {
        notificationStompClient.disconnect();
    }
});
