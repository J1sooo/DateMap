document.addEventListener("DOMContentLoaded", () => {
  console.log("JS 로드");

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
            console.log("삭제 완료:", courseId);
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

  // 수정 버튼 클릭 → 페이지 이동
  const editButtons = document.querySelectorAll("button.edit-btn");
  editButtons.forEach(button => {
    button.addEventListener("click", function () {
      const courseId = this.getAttribute("data-course-id");
      if (courseId) {
        window.location.href = `/recommendplace/edit/${courseId}`;
      }
    });
  });


});
