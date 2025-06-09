document.querySelectorAll('.options').forEach(group => {
  const isMulti = group.classList.contains('multi');
  const hiddenInput = group.querySelector('input[type=hidden]');

  group.querySelectorAll('.option-btn').forEach(btn => {
    btn.addEventListener('click', () => {
      if (!isMulti) {
        group.querySelectorAll('.option-btn').forEach(
            b => b.classList.remove('selected'));
        btn.classList.add('selected');
        hiddenInput.value = btn.innerText;
      } else {
        btn.classList.toggle('selected');
        const selected = [...group.querySelectorAll(
            '.option-btn.selected')].map(b => b.innerText);
        hiddenInput.value = selected.join(',');
      }
    });
  });
});

const maleImages = [
  "/images/partner/male1.png",
  "/images/partner/male2.png",
  "/images/partner/male3.png"
];
const femaleImages = [
  "/images/partner/female1.png",
  "/images/partner/female2.png",
  "/images/partner/female3.png"
];

function getRandomImage(gender) {
  const imageList = gender === 'male' ? maleImages : femaleImages;
  return imageList[Math.floor(Math.random() * imageList.length)];
}

// 📸 버튼이 눌렸을 때 현재 성별에 따라 이미지 바꾸기
function changeImage() {
  const gender = document.getElementById('genderInput').value;
  if (gender) {
    const randomImg = getRandomImage(gender);
    document.getElementById('preview').src = randomImg;
    document.getElementById('imageUrl').value = randomImg;
    console.log(document.getElementById('imageUrl').value);
  }
}

// 성별 버튼 누르면 이미지 랜덤 설정 + 선택 표시
document.addEventListener("DOMContentLoaded", () => {
  const genderButtons = document.querySelectorAll('#gender-options .option-btn');

  genderButtons.forEach(button => {
    button.addEventListener('click', () => {
      genderButtons.forEach(b => b.classList.remove('selected'));
      button.classList.add('selected');

      const selectedGender = button.getAttribute('data-gender');
      document.getElementById('genderInput').value = selectedGender;

      // 성별 선택 시 자동으로 이미지 바꾸기

      changeImage();
    });
  });
});
