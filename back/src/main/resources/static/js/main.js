function showModal(imageUrl, gender, ageGroup, personalType, hobby, partner) {
    document.getElementById("modal-img").src = imageUrl;
    const genderText = gender === 'male' ? '남성' : '여성';
    document.getElementById("modal-gender").innerText = "성별 : " + genderText;
    document.getElementById("modal-age").innerText = "나이 : " + ageGroup;
    document.getElementById("modal-type").innerText = "성격 : " + personalType;
    document.getElementById("modal-hobby").innerText = "취미 : " + hobby;
    document.getElementById("partner-modal").style.display = "flex";

    document.getElementById('form-imageUrl').value = imageUrl;
    document.getElementById('form-gender').value = gender;
    document.getElementById('form-age').value = ageGroup;
    document.getElementById('form-type').value = personalType;
    document.getElementById('form-hobby').value = hobby;
}

function closeModal() {
    document.getElementById("partner-modal").style.display = "none";
}

function openModalFromCard(card) {
    const imageUrl = card.dataset.imgurl;
    const gender = card.dataset.gender;
    const ageGroup = card.dataset.agegroup;
    const personalType = card.dataset.personaltype;
    const hobby = card.dataset.hobby;

    showModal(imageUrl, gender, ageGroup, personalType, hobby);
}

function scrollToBottom(id) {
    const x = document.getElementById(id);
    x.scrollIntoView({behavior: 'smooth'});
}