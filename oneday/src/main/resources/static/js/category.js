function writeOnedayClass() {
    location.href = '/board/write';  
}

document.addEventListener('DOMContentLoaded', function () {
    // 처음 로딩될 때 별점 채우기
    const starRatings = document.querySelectorAll('.star-ratings');

    starRatings.forEach(function (rating) {
        const ratingValue = rating.getAttribute('data-rating');
        const ratingPercent = ratingToPercent(Number(ratingValue));

        const starRatingsFill = rating.querySelector('.star-ratings-fill');
        starRatingsFill.style.width = ratingPercent + '%';
    });
});


function ratingToPercent(score) {
    const percent = (score / 5) * 100;
    return percent;
}