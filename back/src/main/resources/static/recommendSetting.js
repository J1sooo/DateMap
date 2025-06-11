const regions = {
    gangwon: "강원",
    gyeonggi: "경기",
    gyeongsangnam: "경남",
    gyeongsangbuk: "경북",
    gwangju: "광주",
    daegu: "대구",
    daejeon: "대전",
    busan: "부산",
    seoul: "서울",
    ulsan: "울산",
    incheon: "인천",
    jeonnam: "전남",
    jeonbuk: "전북",
    jeju: "제주",
    chungnam: "충남",
    chungbuk: "충북"
};

const subRegions = {
    gangwon: ["강릉시", "동해시", "삼척시", "속초시", "원주시", "춘천시", "태백시", "고성군", "양구군", "양양군", "영월군", "인제군", "정선군", "철원군", "평창군", "홍천군", "화천군", "횡성군"],
    gyeonggi: ["고양시", "과천시", "광명시", "광주시", "구리시", "군포시", "김포시", "남양주시", "동두천시", "부천시", "성남시", "수원시", "시흥시", "안산시", "안성시", "안양시", "양주시", "오산시", "용인시", "의왕시", "의정부시", "이천시", "파주시", "평택시", "포천시", "하남시", "화성시", "가평군", "양평군", "여주군", "연천군"],
    gyeongsangnam: ["거제시", "김해시", "마산시", "밀양시", "사천시", "양산시", "진주시", "진해시", "창원시", "통영시", "거창군", "고성군", "남해군", "산청군", "의령군", "창녕군", "하동군", "함안군", "함양군", "합천군"],
    gyeongsangbuk: ["경산시", "경주시", "구미시", "김천시", "문경시", "상주시", "안동시", "영주시", "영천시", "포항시", "고령군", "군위군", "봉화군", "성주군", "영덕군", "영양군", "예천군", "울릉군", "울진군", "의성군", "청도군", "청송군", "칠곡군"],
    gwangju: ["광산구", "남구", "동구", "북구", "서구"],
    daegu: ["남구", "달서구", "동구", "북구", "서구", "수성구", "중구", "달성군"],
    daejeon: ["대덕구", "동구", "서구", "유성구", "중구"],
    busan: ["강서구", "금정구", "남구", "동구", "동래구", "부산진구", "북구", "사상구", "사하구", "서구", "수영구", "연제구", "영도구", "중구", "해운대구", "기장군"],
    seoul: ["강남구", "강동구", "강북구", "강서구", "관악구", "광진구", "구로구", "금천구", "노원구", "도봉구", "동대문구", "동작구", "마포구", "서대문구", "서초구", "성동구", "성북구", "송파구", "양천구", "영등포구", "용산구", "은평구", "종로구", "중구", "중랑구"],
    ulsan: ["남구", "동구", "북구", "중구", "울주군"],
    incheon: ["계양구", "남구", "남동구", "동구", "부평구", "서구", "연수구", "중구", "강화군", "옹진군"],
    jeonnam: ["광양시", "나주시", "목포시", "순천시", "여수시", "강진군", "고흥군", "곡성군", "구례군", "담양군", "무안군", "보성군", "신안군", "영광군", "영암군", "완도군", "장성군", "장흥군", "진도군", "함평군", "해남군", "화순군"],
    jeonbuk: ["군산시", "김제시", "남원시", "익산시", "전주시", "정읍시", "고창군", "무주군", "부안군", "순창군", "완주군", "임실군", "장수군", "진안군"],
    jeju: ["서귀포시", "제주시", "남제주군", "북제주군"],
    chungnam: ["계룡시", "공주시", "논산시", "보령시", "서산시", "아산시", "천안시", "금산군", "당진시", "부여군", "서천군", "예산군", "청양군", "태안군", "홍성군"],
    chungbuk: ["제천시", "청주시", "충주시", "괴산군", "단양군", "보은군", "영동군", "옥천군", "음성군", "증평군", "진천군", "청원군"]
};

const regionSelect = document.getElementById("region");
const subregionSelect = document.getElementById("subregion");

// 시/도 옵션
for (const [value, label] of Object.entries(regions)) {
    const option = document.createElement("option");
    option.value = value;
    option.textContent = label;
    regionSelect.appendChild(option);
}

// 시/도 선택 시 구/군 옵션 업데이트
regionSelect.addEventListener("change", function () {
    // 기존 옵션 초기화
    subregionSelect.innerHTML = '<option value="">-- 구/군 선택 --</option>';

    const selectedRegion = regionSelect.value;
    const selectSubregions = subRegions[selectedRegion] || [];

    for (const sub of selectSubregions) {
        const option = document.createElement("option");
        option.value = sub;
        option.textContent = sub;
        subregionSelect.appendChild(option);
    }
});

// 취미 버튼 생성
const hobbies = [
    "운동", "게임", "요리", "공예", "예술", "음악", "춤", "사진", "영화", "독서",
    "여행", "자연/야외 활동", "동물", "힐링/명상", "수집", "체험", "패션/뷰티", "봉사"
];

const hobbyContainer = document.getElementById("hobby-buttons");

hobbies.forEach(hobby => {
    const button = document.createElement("button");
    button.type = "button";
    button.className = "btn btn-outline-primary hobby-btn me-2 mb-2";
    button.setAttribute("data-value", hobby);
    button.textContent = hobby;
    hobbyContainer.appendChild(button);
});

// 이동 수단 버튼 생성
const transports = ["자동차", "대중교통", "뚜벅이"];

const transportContainer = document.getElementById("transport-buttons");

transports.forEach(transport => {
    const button = document.createElement("button");
    button.type = "button";
    button.className = "btn btn-outline-primary transport-btn me-2 mb-2";
    button.setAttribute("data-value", transport);
    button.textContent = transport;
    transportContainer.appendChild(button);
});

// 토글 선택
const selectedHobbies = new Set();
const selectedTransports = new Set();

function toggleSelection(set, btn, max) {
    const value = btn.getAttribute("data-value");

    if (set.has(value)) {
        set.delete(value);
        btn.classList.remove("selected-button");
        btn.classList.add("btn-outline-primary");
    } else if (set.size >= max) {
        alert(`취미는 최대 ${max}개까지 선택 가능합니다.`)
    } else {
        set.add(value);
        btn.classList.remove("btn-outline-primary");
        btn.classList.add("selected-button");
    }
}

// 버튼 이벤트 연결
document.querySelectorAll(".hobby-btn").forEach(btn => {
    btn.addEventListener("click", () => toggleSelection(selectedHobbies, btn, 3));
});
document.querySelectorAll(".transport-btn").forEach(btn => {
    btn.addEventListener("click", () => toggleSelection(selectedTransports, btn));
});

// flatpickr 달력 설정
flatpickr("#date-picker", {
    dateFormat: "Y-m-d", // 표시 형식: 2025-06-03
    minDate: "today", // 오늘 이후만 선택 가능
    locale: "ko" // 한국어
});

const submitButton = document.querySelector("button[type='submit']");

// 지역 선택 없으면 추천 버튼 비활성화
function updateSubmitState() {
    const selectedRegion = subregionSelect.value;
    submitButton.disabled = !selectedRegion;
}
subregionSelect.addEventListener("change", updateSubmitState);
updateSubmitState(); // 초기 상태 확인

// form submit 처리
const loadingOverlay = document.getElementById('loading-overlay');

document.querySelector("form").addEventListener("submit", (e) => {
    e.preventDefault();
    loadingOverlay.classList.remove('d-none');

    // 프롬프트 생성
    const area = regions[document.getElementById("region").value] + document.getElementById("subregion").value;
    const budget = document.getElementById("budget").value || 0;
    const hobbies = Array.from(selectedHobbies).join(",");
    const date = document.getElementById("date-picker").value;
    const transport = Array.from(selectedTransports).join(",");

    const content = `area=${area};budget=${budget};hobbies=${hobbies};date=${date};transport=${transport}`;

    fetch(`/api/alan?content=${content}`, {
        method: "GET"
    })
        .then(res => res.text())
        .then(data => {
            // 데이터 가공
            const rawJsonString = data.replace(/```json\n?|\n?```/g, ""); // 마크 다운 블록 제거
            const jsonData = JSON.parse(rawJsonString); // json 변경

            sessionStorage.setItem("recommendArea", regions[document.getElementById("region").value]); // string
            sessionStorage.setItem("alanRequest", jsonData.content); // string
            loadingOverlay.classList.add('d-none');
            window.location.href = "/recommend/place";
        })
        .catch(e => {
            console.error(e);
            alert(e.message);
        });
});