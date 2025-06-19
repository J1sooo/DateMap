document.addEventListener('DOMContentLoaded', function() {
    const passwordInput = document.getElementById('password');
    const loginForm = document.querySelector('form[th\\:action="@{/login}"]');

    const findIdForm = document.getElementById('findIdForm');
    const findIdNameInput = document.getElementById('findId_name');
    const findIdEmailInput = document.getElementById('findId_email');
    const findIdResult = document.getElementById('findIdResult');
    const foundIdMessage = document.getElementById('foundIdMessage');
    const findIdError = document.getElementById('findIdError');
    const findIdModalEl = document.getElementById('findIdModal');

    const verifyUserForPasswordResetForm = document.getElementById('verifyUserForPasswordResetForm');
    const findPwdUsernameInput = document.getElementById('findPwd_username');
    const findPwdEmailInput = document.getElementById('findPwd_email');
    const verifyPasswordError = document.getElementById('verifyPasswordError');

    const resetPasswordForm = document.getElementById('resetPasswordForm');
    const resetUserUsnInput = document.getElementById('resetUserUsn');
    const newPasswordInput = document.getElementById('newPassword');
    const confirmNewPasswordInput = document.getElementById('confirmNewPassword');
    const resetPasswordError = document.getElementById('resetPasswordError');
    const passwordResetSuccess = document.getElementById('passwordResetSuccess');

    const findPasswordModalEl = document.getElementById('findPasswordModal');

    if (passwordInput && loginForm) {
        passwordInput.addEventListener('keydown', (event) => {
            if (event.key === 'Enter') {
                event.preventDefault();
                loginForm.submit();
            }
        });
    }

    // 아이디 찾기
    if (findIdForm) {
        findIdForm.addEventListener('submit', function(event) {
            event.preventDefault();

            findIdResult.style.display = 'none';
            findIdError.style.display = 'none';
            foundIdMessage.textContent = '';
            findIdError.textContent = '';

            const name = findIdNameInput.value;
            const email = findIdEmailInput.value;

            fetch('/api/users/id-recovery', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ name: name, email: email })
            })
                .then(response => {
                    if (!response.ok) {
                        return response.json().then(err => Promise.reject(err));
                    }
                    return response.json();
                })
                .then(data => {
                    if (data.success) {
                        foundIdMessage.textContent = `회원님의 아이디는 '${data.userId}' 입니다.`;
                        findIdResult.style.display = 'block';
                        findIdForm.style.display = 'none';
                    } else {
                        findIdError.textContent = data.message || '아이디를 찾을 수 없습니다. 정보를 확인해주세요.';
                        findIdError.style.display = 'block';
                    }
                })
                .catch(error => {
                    console.error('아이디 찾기 중 오류 발생:', error);
                    findIdError.textContent = error.message || '서버 오류가 발생했습니다. 잠시 후 다시 시도해주세요.';
                    findIdError.style.display = 'block';
                });
        });
    }

    if (findIdModalEl) {
        findIdModalEl.addEventListener('hidden.bs.modal', function () {
            findIdForm.reset();
            findIdForm.style.display = 'block';
            findIdResult.style.display = 'none';
            findIdError.style.display = 'none';
            foundIdMessage.textContent = '';
            findIdError.textContent = '';
        });
    }

    // 비밀번호 재설정
    if (verifyUserForPasswordResetForm) {
        verifyUserForPasswordResetForm.addEventListener('submit', function(event) {
            event.preventDefault();

            verifyPasswordError.style.display = 'none';
            verifyPasswordError.textContent = '';
            resetPasswordForm.style.display = 'none';

            const username = findPwdUsernameInput.value;
            const email = findPwdEmailInput.value;

            fetch('/api/users/verify-password', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ username: username, email: email })
            })
                .then(response => {
                    if (!response.ok) {
                        return response.json().then(err => Promise.reject(err));
                    }
                    return response.json();
                })
                .then(data => {
                    if (data.success) {
                        verifyUserForPasswordResetForm.style.display = 'none';
                        resetPasswordForm.style.display = 'block';
                        resetUserUsnInput.value = data.usn;
                        newPasswordInput.focus();
                    } else {
                        verifyPasswordError.textContent = data.message || '아이디 또는 이메일 정보가 일치하지 않습니다.';
                        verifyPasswordError.style.display = 'block';
                    }
                })
                .catch(error => {
                    console.error('비밀번호 재설정 본인 확인 중 오류 발생:', error);
                    verifyPasswordError.textContent = error.message || '서버 오류가 발생했습니다. 잠시 후 다시 시도해주세요.';
                    verifyPasswordError.style.display = 'block';
                });
        });
    }

    if (resetPasswordForm) {
        resetPasswordForm.addEventListener('submit', function(event) {
            event.preventDefault();

            resetPasswordError.style.display = 'none';
            resetPasswordError.textContent = '';
            passwordResetSuccess.style.display = 'none';

            const usn = resetUserUsnInput.value;
            const newPassword = newPasswordInput.value;
            const confirmNewPassword = confirmNewPasswordInput.value;

            if (newPassword !== confirmNewPassword) {
                resetPasswordError.textContent = '새 비밀번호와 확인 비밀번호가 일치하지 않습니다.';
                resetPasswordError.style.display = 'block';
                return;
            }

            fetch('/api/users/password-reset', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ usn: usn, newPassword: newPassword })
            })
                .then(response => {
                    if (!response.ok) {
                        return response.json().then(err => Promise.reject(err));
                    }
                    return response.json();
                })
                .then(data => {
                    if (data.success) {
                        resetPasswordForm.style.display = 'none';
                        passwordResetSuccess.style.display = 'block';
                    } else {
                        resetPasswordError.textContent = data.message || '비밀번호 변경에 실패했습니다. 다시 시도해주세요.';
                        resetPasswordError.style.display = 'block';
                    }
                })
                .catch(error => {
                    console.error('비밀번호 변경 중 오류 발생:', error);
                    resetPasswordError.textContent = error.message || '서버 오류가 발생했습니다. 잠시 후 다시 시도해주세요.';
                    resetPasswordError.style.display = 'block';
                });
        });
    }

    if (findPasswordModalEl) {
        findPasswordModalEl.addEventListener('hidden.bs.modal', function () {
            verifyUserForPasswordResetForm.reset();
            resetPasswordForm.reset();
            verifyUserForPasswordResetForm.style.display = 'block';
            resetPasswordForm.style.display = 'none';
            passwordResetSuccess.style.display = 'none';
            verifyPasswordError.style.display = 'none';
            resetPasswordError.style.display = 'none';
            verifyPasswordError.textContent = '';
            resetPasswordError.textContent = '';
            newPasswordInput.value = '';
            confirmNewPasswordInput.value = '';
        });
    }
});