function showModal(imageUrl, gender, ageGroup, personalType, hobby) {
    document.getElementById("modal-img").src = imageUrl;
    document.getElementById("modal-gender").innerText = "성별 : " + gender;
    document.getElementById("modal-age").innerText = "나이 : " + ageGroup;
    document.getElementById("modal-type").innerText = "성격 : " + personalType;
    document.getElementById("modal-hobby").innerText = "취미 : " + hobby;
    document.getElementById("partner-modal").style.display = "flex";
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
