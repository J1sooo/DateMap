document.addEventListener('DOMContentLoaded', function () {
    const userIdInput = document.getElementById('userId');
    const nickNameInput = document.getElementById('nickName');
    const emailIdInput = document.getElementById('emailIdInput');
    const fullEmailInput = document.getElementById('fullEmail');

    const passwordInput = document.getElementById('password');
    const passwordCheckInput = document.getElementById('passwordCheck');

    const userIdStatus = document.getElementById('userIdStatus');
    const nickNameStatus = document.getElementById('nickNameStatus');
    const emailStatus = document.getElementById('emailStatus');
    const passwordMismatch = document.getElementById('passwordMismatch');

    const checkUserIdBtn = document.getElementById('checkUserIdBtn');
    const checkNickNameBtn = document.getElementById('checkNickNameBtn');
    const checkEmailBtn = document.getElementById('checkEmailBtn');

    const joinForm = document.getElementById('joinForm');

    const userIdValidationMessage = document.getElementById('userIdValidationMessage');
    const emailValidationMessage = document.getElementById('emailValidationMessage');
    const nickNameValidationMessage = document.getElementById('nickNameValidationMessage');


    // 아이디 유효성 검사 및 중복 확인
    function validateUserId(userId) {
        const userIdRegex = /^[a-zA-Z0-9]+$/;
        if (userId.length < 6 || userId.length > 12) {
            return "아이디는 영문&숫자 6~12자 이내로 입력해야 합니다.";
        }
        if (!userIdRegex.test(userId)) {
            return "아이디는 영문과 숫자만 가능합니다.";
        }
        return "";
    }

    userIdInput.addEventListener('input', function() {
        const userId = userIdInput.value;
        const validationMessage = validateUserId(userId);

        if (validationMessage) {
            if (userIdValidationMessage) {
                userIdValidationMessage.textContent = validationMessage;
                userIdValidationMessage.style.color = 'red';
            }
            if (userIdStatus) userIdStatus.textContent = '';
            if (checkUserIdBtn) checkUserIdBtn.disabled = true;
        } else {
            if (userIdValidationMessage) userIdValidationMessage.textContent = '';
            if (checkUserIdBtn) checkUserIdBtn.disabled = false;
            if (userIdStatus) userIdStatus.textContent = '';
        }
    });

    checkUserIdBtn.addEventListener('click', async () => {
        const userId = userIdInput.value;
        const validationMessage = validateUserId(userId);

        if (validationMessage) {
            if (userIdValidationMessage) {
                userIdValidationMessage.textContent = validationMessage;
                userIdValidationMessage.style.color = 'red';
            }
            if (userIdStatus) userIdStatus.textContent = '';
            return;
        }
        await checkDuplication('userId', userId, userIdStatus);
    });


    // 닉네임 유효성 검사 및 중복 확인
    function validateNickName(nickName) {
        if (nickName.length === 0) {
            return "닉네임은 필수 항목입니다.";
        }
        if (nickName.length > 8) {
            return "닉네임은 8자 이내로 입력해야 합니다.";
        }
        return "";
    }

    nickNameInput.addEventListener('input', function() {
        const nickName = nickNameInput.value;
        const validationMessage = validateNickName(nickName);
        if (nickNameValidationMessage) {
            if (validationMessage) {
                nickNameValidationMessage.textContent = validationMessage;
                nickNameValidationMessage.style.color = 'red';
                if (nickNameStatus) nickNameStatus.textContent = '';
                if (checkNickNameBtn) checkNickNameBtn.disabled = true;
            } else {
                nickNameValidationMessage.textContent = '';
                if (checkNickNameBtn) checkNickNameBtn.disabled = false;
                if (nickNameStatus) nickNameStatus.textContent = '';
            }
        }
    });

    checkNickNameBtn.addEventListener('click', async () => {
        const nickName = nickNameInput.value;
        const validationMessage = validateNickName(nickName);

        if (validationMessage) {
            if (nickNameValidationMessage) {
                nickNameValidationMessage.textContent = validationMessage;
                nickNameValidationMessage.style.color = 'red';
            }
            if (nickNameStatus) nickNameStatus.textContent = '';
            return;
        }
        await checkDuplication('nickName', nickName, nickNameStatus);
    });


    //이메일
    const emailDomainSelect = document.getElementById('emailDomainSelect');
    const emailDomainDirect = document.getElementById('emailDomainDirect');

    function updateFullEmail() {
        let fullEmail = emailIdInput.value.trim();

        if (emailDomainSelect.value === "") {

        } else {
            fullEmail += "@" + emailDomainSelect.value;
        }
        fullEmailInput.value = fullEmail;
        validateEmailFormat(fullEmail);
    }

    function validateEmailFormat(email) {
        const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
        if (email.length === 0) {
            return "이메일은 필수 항목입니다.";
        }
        if (!emailRegex.test(email)) {
            return "유효한 이메일 주소를 입력해주세요.";
        }
        return "";
    }

    if (typeof joinRequestDto !== 'undefined' && joinRequestDto.email) {
        const parts = joinRequestDto.email.split('@');
        if (parts.length === 2) {
            emailIdInput.value = parts[0];
            const domain = parts[1];

            const predefinedDomains = ['naver.com', 'gmail.com', 'daum.net'];

            if (predefinedDomains.includes(domain)) {
                emailDomainSelect.value = domain;
                emailDomainDirect.style.display = 'none';
            } else {
                emailDomainSelect.value = '';
                emailDomainDirect.value = domain;
                emailDomainDirect.style.display = 'block';
            }
        } else {
            emailIdInput.value = joinRequestDto.email;
            emailDomainSelect.value = '';
            emailDomainDirect.style.display = 'block';
        }
    }
    updateFullEmail();

    emailIdInput.addEventListener('input', updateFullEmail);
    emailDomainSelect.addEventListener('change', updateFullEmail);
    emailDomainDirect.addEventListener('input', updateFullEmail);

    checkEmailBtn.addEventListener('click', async () => {
        const email = fullEmailInput.value;
        const validationMessage = validateEmailFormat(email);

        if (validationMessage) {
            if (emailValidationMessage) {
                emailValidationMessage.textContent = validationMessage;
                emailValidationMessage.style.color = 'red';
            }
            if (emailStatus) emailStatus.textContent = '';
            return;
        }

        if (emailValidationMessage) emailValidationMessage.textContent = '';
        await checkDuplication('email', email, emailStatus);
    });


    // 생년월일
    const birthYearSelect = document.getElementById('birthYear');
    const birthMonthSelect = document.getElementById('birthMonth');
    const birthDaySelect = document.getElementById('birthDay');
    const currentYear = new Date().getFullYear();

    for (let i = currentYear; i >= 1900; i--) {
        const option = document.createElement('option');
        option.value = i.toString();
        option.textContent = i.toString();
        birthYearSelect.appendChild(option);
    }
    if (typeof joinRequestDto !== 'undefined' && joinRequestDto.birthYear) {
        birthYearSelect.value = joinRequestDto.birthYear.toString();
    }


    for (let i = 1; i <= 12; i++) {
        const option = document.createElement('option');
        option.value = i.toString();
        option.textContent = i.toString();
        birthMonthSelect.appendChild(option);
    }
    if (typeof joinRequestDto !== 'undefined' && joinRequestDto.birthMonth) {
        birthMonthSelect.value = joinRequestDto.birthMonth.toString();
    }

    function updateDays() {
        const year = parseInt(birthYearSelect.value);
        const month = parseInt(birthMonthSelect.value);
        const currentSelectedDay = birthDaySelect.value ? parseInt(birthDaySelect.value) : NaN;

        birthDaySelect.innerHTML = '<option value="">일</option>';

        if (!isNaN(year) && !isNaN(month)) {
            const lastDay = new Date(year, month, 0).getDate();
            for (let i = 1; i <= lastDay; i++) {
                const option = document.createElement('option');
                option.value = i.toString();
                option.textContent = i.toString();
                birthDaySelect.appendChild(option);
            }
            if (!isNaN(currentSelectedDay) && currentSelectedDay <= lastDay) {
                birthDaySelect.value = currentSelectedDay.toString();
            } else if (typeof joinRequestDto !== 'undefined' && joinRequestDto.birthDay && year === joinRequestDto.birthYear && month === joinRequestDto.birthMonth && joinRequestDto.birthDay <= lastDay) {
                birthDaySelect.value = joinRequestDto.birthDay.toString();
            } else {
                birthDaySelect.value = '';
            }
        } else {
            birthDaySelect.value = '';
        }
    }

    birthYearSelect.addEventListener('change', updateDays);
    birthMonthSelect.addEventListener('change', updateDays);
    updateDays();


    // 선호 지역 선택
    const serverDetailRegionsMap = {
        "서울": ["강남구", "강동구", "강북구", "강서구", "관악구", "광진구", "구로구", "금천구", "노원구", "도봉구", "동대문구", "동작구", "마포구", "서대문구", "서초구", "성동구", "성북구", "송파구", "양천구", "영등포구", "용산구", "은평구", "종로구", "중구", "중랑구"],
        "부산": ["강서구", "금정구", "남구", "동구", "동래구", "부산진구", "북구", "사상구", "사하구", "서구", "수영구", "연제구", "영도구", "중구", "해운대구", "기장군"],
        "대구": ["남구", "달서구", "동구", "북구", "서구", "수성구", "중구", "달성군"],
        "인천": ["계양구", "미추홀구", "남동구", "동구", "부평구", "서구", "연수구", "중구", "강화군", "옹진군"],
        "광주": ["광산구", "남구", "동구", "북구", "서구"],
        "대전": ["대덕구", "동구", "서구", "유성구", "중구"],
        "울산": ["남구", "동구", "북구", "중구", "울주군"],
        "세종": ["세종시"],
        "경기": ["수원시", "성남시", "용인시", "고양시", "화성시", "안산시", "남양주시", "안양시", "평택시", "의정부시", "파주시", "김포시", "광주시", "광명시", "군포시", "하남시", "오산시", "이천시", "안성시", "의왕시", "양주시", "포천시", "여주시", "동두천시", "과천시", "가평군", "양평군", "연천군"],
        "강원": ["춘천시", "원주시", "강릉시", "동해시", "태백시", "속초시", "삼척시", "홍천군", "횡성군", "영월군", "평창군", "정선군", "철원군", "화천군", "양구군", "인제군", "고성군", "양양군"],
        "충북": ["청주시", "충주시", "제천시", "보은군", "옥천군", "영동군", "증평군", "진천군", "괴산군", "음성군", "단양군"],
        "충남": ["천안시", "공주시", "보령시", "아산시", "서산시", "논산시", "계룡시", "당진시", "금산군", "부여군", "서천군", "청양군", "홍성군", "예산군", "태안군"],
        "전북": ["전주시", "군산시", "익산시", "정읍시", "남원시", "김제시", "완주군", "진안군", "무주군", "장수군", "임실군", "순창군", "고창군", "부안군"],
        "전남": ["목포시", "여수시", "순천시", "나주시", "광양시", "담양군", "곡성군", "구례군", "고흥군", "보성군", "화순군", "장흥군", "강진군", "해남군", "영암군", "무안군", "함평군", "영광군", "장성군", "완도군", "진도군", "신안군"],
        "경북": ["포항시", "경주시", "김천시", "안동시", "구미시", "영주시", "영천시", "상주시", "문경시", "경산시", "군위군", "의성군", "청송군", "영양군", "영덕군", "청도군", "고령군", "성주군", "칠곡군", "예천군", "봉화군", "울진군", "울릉군"],
        "경남": ["창원시", "진주시", "통영시", "사천시", "김해시", "밀양시", "거제시", "양산시", "의령군", "함안군", "창녕군", "고성군", "남해군", "하동군", "산청군", "함양군", "거창군", "합천군"],
        "제주": ["제주시", "서귀포시"]
    };
    const mainRegionSelect = document.getElementById('mainRegionSelect');
    const detailRegionSelect = document.getElementById('detailRegionSelect');
    const addRegionBtn = document.getElementById('addRegionBtn');
    const selectedRegionsDisplay = document.getElementById('selectedRegionsDisplay');
    const preferAreaInput = document.getElementById('preferAreaInput');
    const preferAreaDetailInput = document.getElementById('preferAreaDetailInput');
    const regionCountMessage = document.getElementById('regionCountMessage');

    let selectedRegions = [];

    if (mainRegionSelect) {

        Object.keys(serverDetailRegionsMap).forEach(mainRegion => {
            const option = document.createElement('option');
            option.value = mainRegion;
            option.textContent = mainRegion;
            mainRegionSelect.appendChild(option);
        });

        if (typeof joinRequestDto !== 'undefined' && joinRequestDto.preferArea) {
            const initialPreferAreaMain = joinRequestDto.preferArea.split(' ')[0]; // 첫 번째 선호 지역의 지역
            if (Array.from(mainRegionSelect.options).some(option => option.value === initialPreferAreaMain)) {
                mainRegionSelect.value = initialPreferAreaMain;
                populateDetailAreasForMainRegion();
            }
        }
    }

    function parseFullRegionString(fullRegionStr) {
        if (!fullRegionStr || typeof fullRegionStr !== 'string' || fullRegionStr.trim() === "") return null;
        const parts = fullRegionStr.trim().split(' ');
        const main = parts[0];
        const detail = parts.length > 1 ? parts.slice(1).join(' ') : '';
        return { main: main, detail: detail };
    }

    if (typeof joinRequestDto !== 'undefined') {
        const initialPreferArea = parseFullRegionString(joinRequestDto.preferArea);
        const initialPreferAreaDetail = parseFullRegionString(joinRequestDto.preferAreaDetail);

        if (initialPreferArea) {
            selectedRegions.push(initialPreferArea);
        }
        if (initialPreferAreaDetail) {
            selectedRegions.push(initialPreferAreaDetail);
        }
    }
    renderSelectedRegions();

    function populateDetailAreasForMainRegion() {
        // null
        if (!mainRegionSelect || !detailRegionSelect || typeof serverDetailRegionsMap === 'undefined') return;

        const selectedMainRegion = mainRegionSelect.value;
        const detailRegions = serverDetailRegionsMap[selectedMainRegion] || [];

        detailRegionSelect.innerHTML = '<option value="">구/군 선택</option>';
        detailRegionSelect.disabled = true;

        if (detailRegions.length > 0) {
            detailRegions.forEach(area => {
                const option = document.createElement('option');
                option.value = area;
                option.textContent = area;
                detailRegionSelect.appendChild(option);
            });
            detailRegionSelect.disabled = false;
        } else {
            detailRegionSelect.value = '';
        }

        const currentMainSelectedInRegions = selectedRegions.find(r => r.main === selectedMainRegion);
        if (currentMainSelectedInRegions && currentMainSelectedInRegions.detail) {
            setTimeout(() => {
                if (Array.from(detailRegionSelect.options).some(option => option.value === currentMainSelectedInRegions.detail)) {
                    detailRegionSelect.value = currentMainSelectedInRegions.detail;
                } else {
                    detailRegionSelect.value = '';
                }
            }, 0);
        } else {
            detailRegionSelect.value = '';
        }
    }

    if (mainRegionSelect) {
        mainRegionSelect.addEventListener('change', populateDetailAreasForMainRegion);
    }
    populateDetailAreasForMainRegion();

// 지역 추가
    if (addRegionBtn) {
        addRegionBtn.addEventListener('click', () => {
            if (!mainRegionSelect || !detailRegionSelect) return;

            const main = mainRegionSelect.value;
            const detail = detailRegionSelect.value; // 구/군

            if (!main) {
                alert('지역을 선택해주세요.');
                return;
            }

            if (detailRegionSelect.disabled === false && !detail) {
                alert('구/군을 선택해야 합니다.');
                return;
            }

            const newFullRegionString = main + (detail ? ' ' + detail : '');

            // 이미 선택된 지역인지 확인
            const isAlreadySelected = selectedRegions.some(region =>
                (region.main + (region.detail ? ' ' + region.detail : '')) === newFullRegionString
            );

            if (isAlreadySelected) {
                alert('이미 추가된 지역입니다.');
                return;
            }

            if (selectedRegions.length >= 2) {
                alert('선호 지역은 최대 2개까지 선택할 수 있습니다.');
                return;
            }

            selectedRegions.push({ main: main, detail: detail });
            renderSelectedRegions();
        });
    }

    function renderSelectedRegions() {
        if (selectedRegionsDisplay) selectedRegionsDisplay.innerHTML = '';

        // preferArea
        if (preferAreaInput) {
            if (selectedRegions.length > 0) {
                preferAreaInput.value = selectedRegions[0].main + (selectedRegions[0].detail ? ' ' + selectedRegions[0].detail : '');
            } else {
                preferAreaInput.value = '';
            }
        }

        // preferAreaDetail
        if (preferAreaDetailInput) {
            if (selectedRegions.length > 1) {
                preferAreaDetailInput.value = selectedRegions[1].main + (selectedRegions[1].detail ? ' ' + selectedRegions[1].detail : '');
            } else {
                preferAreaDetailInput.value = null;
            }
        }

        selectedRegions.forEach((region, index) => {
            const badge = document.createElement('span');
            badge.className = 'badge bg-primary text-white p-2 me-2 mb-2 d-flex align-items-center';
            badge.textContent = region.main + (region.detail ? ' ' + region.detail : '');

            const removeBtn = document.createElement('button');
            removeBtn.type = 'button';
            removeBtn.className = 'btn-close btn-close-white ms-2';
            removeBtn.setAttribute('aria-label', 'Close');
            removeBtn.addEventListener('click', () => {
                selectedRegions.splice(index, 1);
                renderSelectedRegions();
            });

            badge.appendChild(removeBtn);
            if (selectedRegionsDisplay) selectedRegionsDisplay.appendChild(badge);
        });

        // 선택된 지역 개수
        if (regionCountMessage) regionCountMessage.textContent = `${selectedRegions.length} / 2 선택됨`;
    }

    // 비밀번호 일치 확인
    function checkPasswordMatch() {
        if (passwordInput && passwordCheckInput && passwordMismatch) {
            if (passwordInput.value.length === 0 && passwordCheckInput.value.length === 0) {
                passwordMismatch.style.display = 'none';
                return;
            }

            const passwordRegex = /^(?=.*[A-Za-z])(?=.*\d)[A-Za-z\d]{8,20}$/;
            if (passwordInput.value.length > 0 && !passwordRegex.test(passwordInput.value)) {
                passwordMismatch.style.display = 'none';
                return;
            }

            if (passwordInput.value !== passwordCheckInput.value) {
                passwordMismatch.textContent = '비밀번호가 일치하지 않습니다.';
                passwordMismatch.style.display = 'block';
            } else {
                passwordMismatch.style.display = 'none';
            }
        }
    }

    if (passwordInput) passwordInput.addEventListener('input', checkPasswordMatch);
    if (passwordCheckInput) passwordCheckInput.addEventListener('input', checkPasswordMatch);


    // 약관 동의
    const agreeAll = document.getElementById('agreeAll');
    const termsAgreed = document.getElementById('termsAgreed');
    const privacyAgreed = document.getElementById('privacyAgreed');

    function updateAgreeAllCheckbox() {
        if (agreeAll && termsAgreed && privacyAgreed) {
            agreeAll.checked = termsAgreed.checked && privacyAgreed.checked;
        }
    }

    if (typeof joinRequestDto !== 'undefined' && termsAgreed && privacyAgreed && agreeAll) {
        agreeAll.checked = !!(joinRequestDto.termsAgreed && joinRequestDto.privacyAgreed);
        termsAgreed.checked = joinRequestDto.termsAgreed || false;
        privacyAgreed.checked = joinRequestDto.privacyAgreed || false;

        agreeAll.addEventListener('change', function () {
            const isChecked = this.checked;
            if (termsAgreed) termsAgreed.checked = isChecked;
            if (privacyAgreed) privacyAgreed.checked = isChecked;
        });

        termsAgreed.addEventListener('change', updateAgreeAllCheckbox);
        privacyAgreed.addEventListener('change', updateAgreeAllCheckbox);
    }

    const agreeButtons = document.querySelectorAll('.agree-button');
    agreeButtons.forEach(button => {
        button.addEventListener('click', function() {
            const targetCheckboxId = this.dataset.targetCheckbox;
            if (targetCheckboxId) {
                const targetCheckbox = document.getElementById(targetCheckboxId);
                if (targetCheckbox) {
                    targetCheckbox.checked = true;
                    updateAgreeAllCheckbox();
                }
            }
        });
    });

    // 중복 확인
    async function checkDuplication(type, value, statusElement, usnToExclude) {
        if (!value.trim()) {
            if (statusElement) {
                statusElement.textContent = '';
                statusElement.className = 'mt-1';
            }
            return false;
        }

        let validationMessage = '';
        if (type === 'nickName') {
            validationMessage = validateNickName(value);
            if (validationMessage) {
                nickNameValidationMessage.textContent = validationMessage;
                nickNameValidationMessage.style.color = 'red';
                statusElement.textContent = '';
                return false;
            } else {
                nickNameValidationMessage.textContent = '';
            }
        } else if (type === 'email') {
            validationMessage = validateEmailFormat(value);
            if (validationMessage) {
                emailValidationMessage.textContent = validationMessage;
                emailValidationMessage.style.color = 'red';
                statusElement.textContent = '';
                return false;
            } else {
                emailValidationMessage.textContent = '';
            }
        }

        const endpointMap = {
            userId: "/api/users/availability/user-id",
            nickName: "/api/users/availability/nickname",
            email: "/api/users/availability/email"
        };

        try {
            const params = new URLSearchParams({ [type]: value });
            if (usnToExclude) params.append("usn", usnToExclude);

            const response = await fetch(`${endpointMap[type]}?${params.toString()}`);
            if (!response.ok) throw new Error(`HTTP error! status: ${response.status}`);
            const data = await response.json();

            if (data.exists) {
                statusElement.textContent = `이미 사용 중인 ${getKoreanName(type)}입니다.`;
                statusElement.className = 'text-danger mt-1';
                return false;
            } else {
                statusElement.textContent = `사용 가능한 ${getKoreanName(type)}입니다.`;
                statusElement.className = 'text-success mt-1';
                return true;
            }
        } catch (error) {
            console.error(`Error checking ${type} duplication:`, error);
            statusElement.textContent = `중복 확인 중 오류가 발생했습니다.`;
            statusElement.className = 'text-danger mt-1';
            return false;
        }
    }

    function getKoreanName(type) {
        switch (type) {
            case 'userId': return '아이디';
            case 'nickName': return '닉네임';
            case 'email': return '이메일';
            default: return type;
        }
    }

    if (joinForm) {
        joinForm.addEventListener('submit', async function (event) {
            let isValid = true;

            // 아이디 유효성
            const userIdValidationMsg = validateUserId(userIdInput.value);
            if (userIdValidationMsg) {
                if (userIdValidationMessage) {
                    userIdValidationMessage.textContent = userIdValidationMsg;
                    userIdValidationMessage.style.color = 'red';
                }
                isValid = false;
            }

            // 닉네임 유효성
            const nickNameValidationMsg = validateNickName(nickNameInput.value);
            if (nickNameValidationMsg) {
                if (nickNameValidationMessage) {
                    nickNameValidationMessage.textContent = nickNameValidationMsg;
                    nickNameValidationMessage.style.color = 'red';
                }
                isValid = false;
            } else {
                if (nickNameValidationMessage) nickNameValidationMessage.textContent = '';
            }

            // 비밀번호 일치
            if (passwordInput && passwordCheckInput && passwordMismatch) {
                if (passwordInput.value !== passwordCheckInput.value) {
                    passwordMismatch.textContent = '비밀번호가 일치하지 않습니다.';
                    passwordMismatch.style.display = 'block';
                    isValid = false;
                } else {
                    passwordMismatch.style.display = 'none';
                }
            }


            // 이메일 유효성
            const emailValidationMsg = validateEmailFormat(fullEmailInput.value);
            if (emailValidationMsg) {
                if (emailValidationMessage) {
                    emailValidationMessage.textContent = emailValidationMsg;
                    emailValidationMessage.style.color = 'red';
                }
                isValid = false;
            } else {
                if (emailValidationMessage) emailValidationMessage.textContent = '';
            }

            // 선호 지역
            if (selectedRegions.length === 0) {
                alert('선호 지역은 최소 1개 이상 선택해야 합니다.');
                isValid = false;
            }

            // 약관동의
            if (termsAgreed && !termsAgreed.checked) {
                alert('이용약관에 동의해야 합니다.');
                isValid = false;
            }
            if (privacyAgreed && !privacyAgreed.checked) {
                alert('개인정보 취급방침에 동의해야 합니다.');
                isValid = false;
            }

            if (!isValid) {
                event.preventDefault();
                window.scrollTo(0, 0);
            }
        });
    }

});