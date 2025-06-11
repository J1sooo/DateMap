function copyUrl() {
    const url = window.location.href;
    navigator.clipboard.writeText(url)
        .then(() => alert('현재 페이지 URL이 복사되었습니다!'))
        .catch(() => alert('복사에 실패했습니다.'));
}