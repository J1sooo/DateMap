// document.addEventListener("DOMContentLoaded", () => {
//   const button = document.getElementById("analyze-btn");
//   const count = parseInt(button.dataset.count, 10); // 소개팅 횟수
//   const today = new Date();
//   const day = today.getDay()
//   const dateKey = today.toISOString().slice(0, 10);
//   const storageKey = `buttonClicked-${dateKey}`;
//
//   // 조건: 일요일 , 소개팅 5회 이상 , 아직 클릭 안 함
//   if (day === 0 && count >= 5 && !localStorage.getItem(storageKey)) {
//     button.style.display = "inline-block";
//
//     button.addEventListener("click", () => {
//       localStorage.setItem(storageKey, "true");
//       button.style.display = "none";
//     });
//   }
// });