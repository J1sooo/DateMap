document.addEventListener('DOMContentLoaded', function () {
    const profileImagePreview = document.getElementById('profileImagePreview');
    const profileImageInput = document.getElementById('profileImageInput');
    const editImageBtn = document.getElementById('editImageBtn');

    const nickNameInput = document.getElementById('nickName');
    const emailIdInput = document.getElementById('emailIdInput');
    const emailDomainSelect = document.getElementById('emailDomainSelect');
    const emailDomainDirect = document.getElementById('emailDomainDirect');
    const fullEmailInput = document.getElementById('fullEmail');

    const currentPasswordInput = document.getElementById('currentPassword');
    const newPasswordInput = document.getElementById('newPassword');
    const newPasswordCheckInput = document.getElementById('newPasswordCheck');

    const nickNameStatus = document.getElementById('nickNameStatus');
    const emailStatus = document.getElementById('emailStatus');
    const currentPasswordError = document.getElementById('currentPasswordError');
    const newPasswordValidationMessage = document.getElementById('newPasswordValidationMessage');
    const newPasswordMismatch = document.getElementById('newPasswordMismatch');

    const checkNickNameBtn = document.getElementById('checkNickNameBtn');
    const checkEmailBtn = document.getElementById('checkEmailBtn');

    const editProfileForm = document.getElementById('editProfileForm');
    const withdrawBtn = document.getElementById('withdrawBtn');
    const confirmWithdrawBtn = document.getElementById('confirmWithdrawBtn');
    const withdrawModal = new bootstrap.Modal(document.getElementById('withdrawModal'));

    const nickNameValidationMessage = document.getElementById('nickNameValidationMessage');
    const emailValidationMessage = document.getElementById('emailValidationMessage');

    const birthYearSelect = document.getElementById('birthYear');
    const birthMonthSelect = document.getElementById('birthMonth');
    const birthDaySelect = document.getElementById('birthDay');

    const mainRegionSelect = document.getElementById('mainRegionSelect');
    const detailRegionSelect = document.getElementById('detailRegionSelect');
    const addRegionBtn = document.getElementById('addRegionBtn');
    const selectedRegionsDisplay = document.getElementById('selectedRegionsDisplay');
    const preferAreaInput = document.getElementById('preferAreaInput');
    const preferAreaDetailInput = document.getElementById('preferAreaDetailInput');
    const regionCountMessage = document.getElementById('regionCountMessage');

    if (userProfileUpdateDto && userProfileUpdateDto.profileImageUrl && userProfileUpdateDto.profileImageUrl !== '/images/default_profile.png') {
        profileImagePreview.src = userProfileUpdateDto.profileImageUrl;
    } else {
        profileImagePreview.src = '/images/default_profile.png';
    }

    if (userProfileUpdateDto && userProfileUpdateDto.email) {
        const parts = userProfileUpdateDto.email.split('@');
        if (parts.length === 2) {
            emailIdInput.value = parts[0];
            const domain = parts[1];
            const predefinedDomains = ['naver.com', 'gmail.com', 'daum.net'];

            if (predefinedDomains.includes(domain)) {
                emailDomainSelect.value = domain;
                emailDomainDirect.style.display = 'none';
            } else {
                emailDomainSelect.value = 'direct';
                emailDomainDirect.value = domain;
                emailDomainDirect.style.display = 'block';
            }
        } else {
            emailIdInput.value = userProfileUpdateDto.email;
            emailDomainSelect.value = 'direct';
            emailDomainDirect.value = '';
            emailDomainDirect.style.display = 'block';
        }
    }
    updateFullEmail();

    const currentYear = new Date().getFullYear();
    for (let i = currentYear; i >= 1900; i--) {
        const option = document.createElement('option');
        option.value = i.toString();
        option.textContent = i.toString();
        birthYearSelect.appendChild(option);
    }
    if (userProfileUpdateDto && userProfileUpdateDto.birthYear) {
        birthYearSelect.value = userProfileUpdateDto.birthYear.toString();
    }

    for (let i = 1; i <= 12; i++) {
        const option = document.createElement('option');
        option.value = i.toString();
        option.textContent = i.toString();
        birthMonthSelect.appendChild(option);
    }
    if (userProfileUpdateDto && userProfileUpdateDto.birthMonth) {
        birthMonthSelect.value = userProfileUpdateDto.birthMonth.toString();
    }
    updateDays();

    let selectedRegions = [];
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

    if (mainRegionSelect) {
        Object.keys(serverDetailRegionsMap).forEach(mainRegion => {
            const option = document.createElement('option');
            option.value = mainRegion;
            option.textContent = mainRegion;
            mainRegionSelect.appendChild(option);
        });

        const initialPreferArea = parseFullRegionString(userProfileUpdateDto.preferArea);
        const initialPreferAreaDetail = parseFullRegionString(userProfileUpdateDto.preferAreaDetail);

        if (initialPreferArea) {
            selectedRegions.push(initialPreferArea);
        }
        if (initialPreferAreaDetail) {
            selectedRegions.push(initialPreferAreaDetail);
        }
        renderSelectedRegions();
    }

    editImageBtn.addEventListener('click', function () {
        profileImageInput.click();
    });

    // 프로필 이미지 변경 시 미리보기
    profileImageInput.addEventListener('change', function () {
        const file = this.files[0];
        if (file) {
            const reader = new FileReader();
            reader.onload = function (e) {
                profileImagePreview.src = e.target.result;
            };
            reader.readAsDataURL(file);
        } else {
            profileImagePreview.src = userProfileUpdateDto.profileImageUrl || '/images/default_profile.png';
        }
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

    nickNameInput.addEventListener('input', function () {
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
        await checkDuplication('nickName', nickName, nickNameStatus, userProfileUpdateDto.usn);
    });


    // 이메일 유효성 검사 및 중복 확인
    emailDomainSelect.addEventListener('change', function () {
        if (this.value === 'direct') {
            emailDomainDirect.style.display = 'block';
            emailDomainDirect.value = '';
            emailDomainDirect.focus();
        } else {
            emailDomainDirect.style.display = 'none';
        }
        updateFullEmail();
    });

    function updateFullEmail() {
        let fullEmail = emailIdInput.value.trim();
        if (emailDomainSelect.value === 'direct') {
            fullEmail += "@" + emailDomainDirect.value.trim();
        } else if (emailDomainSelect.value !== "") {
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

    emailIdInput.addEventListener('input', updateFullEmail);
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
        await checkDuplication('email', email, emailStatus, userProfileUpdateDto.usn);
    });

    // 생년월일
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

            if (userProfileUpdateDto && userProfileUpdateDto.birthDay && year === userProfileUpdateDto.birthYear && month === userProfileUpdateDto.birthMonth) {
                if (userProfileUpdateDto.birthDay <= lastDay) {
                    birthDaySelect.value = userProfileUpdateDto.birthDay.toString();
                }
            } else if (!isNaN(currentSelectedDay) && currentSelectedDay <= lastDay) {
                birthDaySelect.value = currentSelectedDay.toString();
            } else {
                birthDaySelect.value = '';
            }
        } else {
            birthDaySelect.value = '';
        }
    }

    birthYearSelect.addEventListener('change', updateDays);
    birthMonthSelect.addEventListener('change', updateDays);

    // 선호 지역
    function parseFullRegionString(fullRegionStr) {
        if (!fullRegionStr || typeof fullRegionStr !== 'string' || fullRegionStr.trim() === "") return null;
        const parts = fullRegionStr.trim().split(' ');
        const main = parts[0];
        const detail = parts.length > 1 ? parts.slice(1).join(' ') : '';
        return {main: main, detail: detail};
    }

    function populateDetailAreasForMainRegion() {
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

    if (addRegionBtn) {
        addRegionBtn.addEventListener('click', () => {
            if (!mainRegionSelect || !detailRegionSelect) return;

            const main = mainRegionSelect.value;
            const detail = detailRegionSelect.value;

            if (!main) {
                alert('지역을 선택해주세요.');
                return;
            }

            if (detailRegionSelect.disabled === false && !detail) {
                alert('구/군을 선택해야 합니다.');
                return;
            }

            const newFullRegionString = main + (detail ? ' ' + detail : '');

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

            selectedRegions.push({main: main, detail: detail});
            renderSelectedRegions();
        });
    }

    function renderSelectedRegions() {
        if (selectedRegionsDisplay) selectedRegionsDisplay.innerHTML = '';

        if (preferAreaInput) {
            if (selectedRegions.length > 0) {
                preferAreaInput.value = selectedRegions[0].main + (selectedRegions[0].detail ? ' ' + selectedRegions[0].detail : '');
            } else {
                preferAreaInput.value = '';
            }
        }

        if (preferAreaDetailInput) {
            if (selectedRegions.length > 1) {
                preferAreaDetailInput.value = selectedRegions[1].main + (selectedRegions[1].detail ? ' ' + selectedRegions[1].detail : '');
            } else {
                preferAreaDetailInput.value = '';
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

        if (regionCountMessage) regionCountMessage.textContent = `${selectedRegions.length} / 2 선택됨`;
    }


    // 비밀번호 유효성 검사 및 일치 확인
    const passwordRegex = /^(?=.*[A-Za-z])(?=.*\d)[A-Za-z\d]{8,20}$/;

    function validateNewPassword(password) {
        if (password.length === 0) {
            return "";
        }
        if (!passwordRegex.test(password)) {
            return "비밀번호는 영문과 숫자를 포함하여 8~20자 이내여야 합니다.";
        }
        return "";
    }

    function checkNewPasswordMatch() {
        if (newPasswordInput.value.length === 0 && newPasswordCheckInput.value.length === 0) {
            newPasswordMismatch.style.display = 'none';
            newPasswordValidationMessage.textContent = '';
            return;
        }

        const newPasswordValidationMsg = validateNewPassword(newPasswordInput.value);
        if (newPasswordValidationMsg) {
            newPasswordValidationMessage.textContent = newPasswordValidationMsg;
            newPasswordValidationMessage.style.color = 'red';
            newPasswordMismatch.style.display = 'none';
            return;
        } else {
            newPasswordValidationMessage.textContent = '';
        }

        if (newPasswordInput.value !== newPasswordCheckInput.value) {
            newPasswordMismatch.textContent = '새 비밀번호가 일치하지 않습니다.';
            newPasswordMismatch.style.display = 'block';
        } else {
            newPasswordMismatch.style.display = 'none';
        }
    }

    newPasswordInput.addEventListener('input', checkNewPasswordMatch);
    newPasswordCheckInput.addEventListener('input', checkNewPasswordMatch);


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
                if (nickNameValidationMessage) {
                    nickNameValidationMessage.textContent = validationMessage;
                    nickNameValidationMessage.style.color = 'red';
                }
                if (statusElement) statusElement.textContent = '';
                return false;
            } else {
                if (nickNameValidationMessage) nickNameValidationMessage.textContent = '';
            }
        } else if (type === 'email') {
            validationMessage = validateEmailFormat(value);
            if (validationMessage) {
                if (emailValidationMessage) {
                    emailValidationMessage.textContent = validationMessage;
                    emailValidationMessage.style.color = 'red';
                }
                if (statusElement) statusElement.textContent = '';
                return false;
            } else {
                if (emailValidationMessage) emailValidationMessage.textContent = '';
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
                if (statusElement) {
                    statusElement.textContent = `이미 사용 중인 ${getKoreanName(type)}입니다.`;
                    statusElement.className = 'text-danger mt-1';
                }
                return false;
            } else {
                if (statusElement) {
                    statusElement.textContent = `사용 가능한 ${getKoreanName(type)}입니다.`;
                    statusElement.className = 'text-success mt-1';
                }
                return true;
            }
        } catch (error) {
            console.error(`Error checking ${type} duplication:`, error);
            if (statusElement) {
                statusElement.textContent = `중복 확인 중 오류가 발생했습니다.`;
                statusElement.className = 'text-danger mt-1';
            }
            return false;
        }
    }


    function getKoreanName(type) {
        switch (type) {
            case 'userId':
                return '아이디';
            case 'nickName':
                return '닉네임';
            case 'email':
                return '이메일';
            default:
                return type;
        }
    }

    editProfileForm.addEventListener('submit', async function (event) {
        event.preventDefault();

        let isValid = true;

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

        const genderSelected = document.querySelector('input[name="gender"]:checked');
        if (!genderSelected) {
            alert("성별을 선택해주세요.");
            isValid = false;
        }

        if (!birthYearSelect.value || !birthMonthSelect.value || !birthDaySelect.value) {
            alert("생년월일을 모두 선택해주세요.");
            isValid = false;
        }

        if (selectedRegions.length === 0) {
            alert('선호 지역은 최소 1개 이상 선택해야 합니다.');
            isValid = false;
        }

        const currentPassword = currentPasswordInput.value.trim();
        const newPassword = newPasswordInput.value.trim();
        const newPasswordCheck = newPasswordCheckInput.value.trim();

        if (currentPassword.length > 0 || newPassword.length > 0 || newPasswordCheck.length > 0) {
            if (currentPassword.length === 0) {
                currentPasswordError.textContent = '새 비밀번호를 변경하려면 현재 비밀번호를 입력해야 합니다.';
                currentPasswordError.style.display = 'block';
                isValid = false;
            } else {
                currentPasswordError.style.display = 'none';
            }

            const newPasswordValidationMsg = validateNewPassword(newPassword);
            if (newPasswordValidationMsg) {
                newPasswordValidationMessage.textContent = newPasswordValidationMsg;
                newPasswordValidationMessage.style.color = 'red';
                isValid = false;
            } else {
                newPasswordValidationMessage.textContent = '';
            }

            if (newPassword !== newPasswordCheck) {
                newPasswordMismatch.textContent = '새 비밀번호가 일치하지 않습니다.';
                newPasswordMismatch.style.display = 'block';
                isValid = false;
            } else {
                newPasswordMismatch.style.display = 'none';
            }
        }
        else {
            currentPasswordError.style.display = 'none';
            newPasswordValidationMessage.textContent = '';
            newPasswordMismatch.style.display = 'none';
        }
        if (!isValid) {
            window.scrollTo(0, 0);
            return;
        }

        const formData = new FormData();

        if (userProfileUpdateDto && userProfileUpdateDto.usn) {
            formData.append('usn', userProfileUpdateDto.usn);
        } else {
            alert("사용자 정보를 가져오는 데 문제가 발생했습니다. 페이지를 새로고침 해주세요.");
            console.error("USN is missing from userProfileUpdateDto.");
            return;
        }

        formData.append('nickName', nickNameInput.value);
        formData.append('email', fullEmailInput.value);
        formData.append('gender', document.querySelector('input[name="gender"]:checked').value);
        formData.append('birthYear', birthYearSelect.value);
        formData.append('birthMonth', birthMonthSelect.value);
        formData.append('birthDay', birthDaySelect.value);

        if (selectedRegions.length > 0) {
            formData.append('preferArea', selectedRegions[0].main + (selectedRegions[0].detail ? ' ' + selectedRegions[0].detail : ''));
        } else {
            formData.append('preferArea', '');
        }
        if (selectedRegions.length > 1) {
            formData.append('preferAreaDetail', selectedRegions[1].main + (selectedRegions[1].detail ? ' ' + selectedRegions[1].detail : ''));
        } else {
            formData.append('preferAreaDetail', '');
        }

        if (newPassword.length > 0) {
            formData.append('currentPassword', currentPassword);
            formData.append('newPassword', newPassword);
        }
        if (profileImageInput.files.length > 0) {
            formData.append('profileImageFile', profileImageInput.files[0]);
        }

        try {
            const response = await fetch('/api/user/profile', {
                method: 'PATCH',
                body: formData
            });

            if (response.ok) {
                const result = await response.json();
                alert(result.message || '회원 정보가 성공적으로 수정되었습니다.');
                window.location.href = '/main';
            } else {
                let errorData;
                try {
                    errorData = await response.json();
                } catch (e) {
                    errorData = await response.text();
                }
                console.error("서버 응답 오류 상세:", errorData);
                alert('회원 정보 수정 중 오류가 발생했습니다: ' + (errorData.message || JSON.stringify(errorData) || errorData || '알 수 없는 오류'));
            }
        } catch (error) {
            console.error('회원 정보 수정 요청 실패:', error);
            alert('회원 정보 수정 중 네트워크 오류가 발생했습니다.');
        }
    });

    // 회원 탈퇴
    withdrawBtn.addEventListener('click', function () {
        withdrawModal.show();
    });

    confirmWithdrawBtn.addEventListener('click', async function () {
        withdrawModal.hide();

        if (!userProfileUpdateDto || !userProfileUpdateDto.usn) {
            alert("사용자 정보를 가져오는 데 문제가 발생했습니다. 페이지를 새로고침 해주세요.");
            console.error("USN is missing for withdrawal.");
            return;
        }

        try {
            const response = await fetch(`/api/user/${userProfileUpdateDto.usn}`, {
                method: 'DELETE',
                headers: {
                    'Content-Type': 'application/json'
                }
            });

            if (response.ok) {
                const result = await response.json();
                alert(result.message || '회원 탈퇴가 성공적으로 처리되었습니다.');
                window.location.href = '/main';
            } else {
                let errorData;
                try {
                    errorData = await response.json();
                } catch (e) {
                    errorData = await response.text();
                }
                console.error("회원 탈퇴 서버 응답 오류 상세:", errorData);
                alert('회원 탈퇴 중 오류가 발생했습니다: ' + (errorData.message || JSON.stringify(errorData) || errorData || '알 수 없는 오류'));
            }
        } catch (error) {
            console.error('회원 탈퇴 요청 실패:', error);
            alert('회원 탈퇴 중 네트워크 오류가 발생했습니다.');
        }
    });
});