const contentStringData = sessionStorage.getItem("alanRequest");
const area = sessionStorage.getItem("recommendArea");

const contentJsonData = JSON.parse(contentStringData); // content json 변경

const container = document.getElementById("result");

// AI 응답 출력
document.addEventListener("DOMContentLoaded", () => {
    Object.values(contentJsonData).forEach(item => {
        const div = document.createElement("div");
        const h3 = document.createElement("h3");
        const p = document.createElement("p");

        h3.textContent = item.name;
        p.textContent = item.description;

        div.appendChild(h3);
        div.appendChild(p);
        container.appendChild(div);
    });
})

// save 팝업창 열고 닫기
const loadingOverlay = document.getElementById('loading-overlay');

function openSave() {
    loadingOverlay.classList.remove('d-none');
}

function closeSave() {
    loadingOverlay.classList.add('d-none');
}

document.getElementById("saveRecommend-btn").addEventListener('click', () => {
    const title = document.getElementById("title").value;
    const file = document.getElementById("formFile")?.files[0];
    if (!title) return alert("제목을 입력해주세요")

    const formData = new FormData();
    formData.append("image", file); // 파일 전송
    formData.append("title", title);
    formData.append("area", area);
    Object.values(contentJsonData).forEach((item, index) => {
        formData.append(`content${index + 1}`, JSON.stringify(item));
    })
    fetch(`/api/recommend`, {
        method: 'POST',
        body: formData
    }).then(res => {
        if (!res.ok) throw new Error("저장 실패");
        alert("저장되었습니다");
        location.replace(`/index`); // 임시 경로
    }).catch(e => {
        console.log(e.message);
        // alert("에러: " + e.message)
    });
})
