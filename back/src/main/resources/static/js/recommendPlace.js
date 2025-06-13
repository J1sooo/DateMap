const contentStringData = sessionStorage.getItem("alanRequest");
const area = sessionStorage.getItem("recommendArea");
const areaDetail = sessionStorage.getItem("areaDetail");

const contentJsonData = JSON.parse(contentStringData); // content json 변경

const container = document.getElementById("result");

const titleMap = ["오전 장소", "점심 식사", "오후 장소", "저녁 식사"];

// AI 응답 출력
document.addEventListener("DOMContentLoaded", () => {
    const topTitle = document.getElementById("top");
    const dateArea = document.createElement("h6");
    dateArea.textContent = areaDetail;
    topTitle.appendChild(dateArea);

    Object.values(contentJsonData).forEach((item, index) => {
        const wrapper = document.createElement("div");
        wrapper.className = "timeline-item";

        const title = document.createElement("h6");
        title.className = "fw-bold mb-1";
        title.textContent = titleMap[index];

        const name = document.createElement("div");
        name.innerHTML = `<strong>${item.name}</strong>`;

        const desc = document.createElement("div");
        desc.innerHTML = `<span>설명: ${item.description}</span>`;

        wrapper.appendChild(title);
        wrapper.appendChild(name);
        wrapper.appendChild(desc);
        container.appendChild(wrapper);
    });
})

// save 팝업창 열고 닫기
const saveOverlay = document.getElementById('save-overlay');

function openSave() {
    saveOverlay.classList.remove('d-none');
}

function closeSave() {
    saveOverlay.classList.add('d-none');
}

document.getElementById("saveRecommend-btn").addEventListener('click', () => {
    const title = document.getElementById("title").value;
    const file = document.getElementById("formFile")?.files[0];
    if (!title) return alert("제목을 입력해주세요")

    const formData = new FormData();
    formData.append("image", file); // 파일 전송
    formData.append("title", title);
    formData.append("area", areaDetail);
    Object.values(contentJsonData).forEach((item, index) => {
        formData.append(`content${index + 1}`, JSON.stringify(item));
    })
    fetch(`/api/recommend`, {
        method: 'POST',
        body: formData
    }).then(res => {
        if (!res.ok) throw new Error("저장 실패");
        return res.json();
    }).then(data =>{
        const id = data.courseId;
        alert("저장되었습니다");
        location.replace(`/recommend/place/${id}`);
    }).catch(() => {
        alert("에러가 발생했습니다")
    });
})
