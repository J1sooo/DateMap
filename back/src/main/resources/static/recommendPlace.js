const contentStringData = sessionStorage.getItem("alanRequest");
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