document.addEventListener("DOMContentLoaded", () => {
    const button = document.getElementById("analyze-btn");
    const count = parseInt(button.dataset.count, 10); // 소개팅 횟수
    const today = new Date();
    const day = today.getDay()
    const dateKey = today.toISOString().slice(0, 10);
    const storageKey = `buttonClicked-${dateKey}`;

    // 조건: 일요일 , 소개팅 5회 이상 , 아직 클릭 안 함
    // if (day === 0 && count >= 5 && !localStorage.getItem(storageKey)) {
    // button.style.display = "inline-block";
    //
    //   button.addEventListener("click", () => {
    //     localStorage.setItem(storageKey, "true");
    //     button.style.display = "none";
    //   });
    // }
    if (count >= 0) {
        button.style.display = "inline-block";

    }

    // 삭제 버튼 이벤트
    const deleteButtons = document.querySelectorAll("button.delete-btn");
    deleteButtons.forEach(button => {
        button.addEventListener("click", function () {
            const courseId = this.getAttribute("data-course-id");
            if (!courseId) return;

            if (confirm("정말 삭제하시겠습니까?")) {
                fetch(`/api/recommend/${courseId}`, {
                    method: 'DELETE'
                }).then(response => {
                    if (response.ok) {
                        this.closest(".course-card")?.remove();
                    } else {
                        alert("삭제에 실패했습니다.");
                    }
                }).catch(err => {
                    console.error("서버 오류", err);
                    alert("서버 오류로 삭제에 실패했습니다.");
                });
            }
        });
    });

    // 수정 버튼 클릭
    const editButtons = document.querySelectorAll("button.edit-btn");
    const hiddenCourseIdInput = document.getElementById("selected-course-id");
    const updateOverlay = document.getElementById("update-overlay");

    editButtons.forEach(button => {
        button.addEventListener("click", function () {
            hiddenCourseIdInput.value = this.getAttribute("data-course-id");
            updateOverlay.classList.remove('d-none');
        });
    });

    document.getElementById("closeUpdate").addEventListener('click', () => {
        updateOverlay.classList.add('d-none');
    })

    document.getElementById("updateRecommend-btn").addEventListener('click', () => {
        const courseId = hiddenCourseIdInput.value;
        const image = document.getElementById("formFile")?.files[0];

        if (!image) {
            alert("이미지를 선택해주세요.");
            return;
        }

        const formData = new FormData();
        formData.append("image", image);

        fetch(`/api/recommend/${courseId}`, {
            method: "PATCH",
            body: formData
        })
            .then(res => {
                if (!res.ok) throw new Error("업로드 실패");
                return res.json();
            })
            .then(() => {
                alert("수정 완료!");
                location.replace(`/mypage`);
            })
            .catch(err => {
                alert("수정 실패: " + err.message);
            });
    });
});
