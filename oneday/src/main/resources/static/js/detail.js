var selectedDate = null;
var currentMonth = null;
var currentYear = null;
var reservationDate = null;
// 예약이 다 찬 날짜를 저장할 배열
var fullyBookedDates = [];

document.addEventListener('DOMContentLoaded', function () {
    // 클래스 소개, 커리큘럼, 리뷰 버튼에 대한 이벤트 리스너 등록
    document.getElementById('content').addEventListener('click', function () {
        scrollToSection('.content');
    });

    document.getElementById('curri').addEventListener('click', function () {
        scrollToSection('.curri');
    });
    
     document.getElementById('location').addEventListener('click', function () {
        scrollToSection('.location');
    });

    document.getElementById('review').addEventListener('click', function () {
        scrollToSection('.review');
    });
    
    // 현재 페이지의 URL을 가져오기
	var currentUrl = window.location.href;
	
	// 특정 페이지의 URL에 포함된 문자열('review/update')을 확인
	// 리뷰 수정 화면으로 스크롤
	if (currentUrl.includes('review/update')) {
	    // 특정 페이지('review/update' 페이지)에만 실행할 코드
	    var targetSection = document.getElementById('updateReview');
	
	    if (targetSection) {
	        // 해당 섹션의 위치로 스크롤
	        targetSection.scrollIntoView({
	            behavior: 'auto',
	            block: 'center'
	        });
	    }
	}
	
	// 특정 페이지의 URL에 포함된 문자열('review/sort')을 확인
	// 리뷰 정렬 화면으로 스크롤 
	if (currentUrl.includes('review/sort')) {
	    // 특정 페이지('review/update' 페이지)에만 실행할 코드
	    var targetSection = document.querySelector('.review');
	    if (targetSection) {
	        // 해당 섹션의 위치로 스크롤
	        targetSection.scrollIntoView({
	            behavior: 'auto',
	            block: 'center'
	        });
	    }
	}

    // 처음 로딩될 때 별점 채우기
    const starRatings = document.querySelectorAll('.star-ratings');
    const starRatings2 = document.querySelectorAll('.stars');

    starRatings.forEach(function (rating) {
        const ratingValue = rating.getAttribute('data-rating');
        const ratingPercent = ratingToPercent(Number(ratingValue));

        const starRatingsFill = rating.querySelector('.star-ratings-fill');
        starRatingsFill.style.width = ratingPercent + '%'; 
    });
    
    starRatings2.forEach(function (rating) {
		const ratingValue = rating.getAttribute('data-rating');
		const ratingPercent = ratingToPercent(Number(ratingValue));
		
		const starRatingsFill2 = rating.querySelector('.star-fill');
		starRatingsFill2.style.width = ratingPercent + '%';
	});

    // 현재 날짜 설정 및 달력 생성
    const today = new Date();
    currentMonth = today.getMonth();
    currentYear = today.getFullYear();

    // onedayId 가져오기
    let onedayId = document.querySelector('input[name="onedayId"]').value;

    // fetch를 사용하여 예약이 다 찬 날짜를 가져온 후에 달력을 렌더링
    fetch('/getFullyBookedDates/' + onedayId)
        .then(response => response.json())
        .then(data => {
            fullyBookedDates = data.map(date => new Date(date));
            applyFullyBookedStyle(); // 예약이 다 찬 날짜에 대한 스타일 적용
            buildCalendar(); // 달력 렌더링
        })
        .catch(error => {
            console.error('예약 다 찬 날짜 가져오기 실패:', error);
        });
});

function ratingToPercent(score) {
    const percent = (score / 5) * 100;
    return percent;
}

function scrollToSection(sectionSelector) {
    const section = document.querySelector(sectionSelector);
    if (section) {
        section.scrollIntoView({
            behavior: 'smooth',
            block: 'start'
        });
    }
}

function applyFullyBookedStyle() {
    console.log('Fully Booked Dates:', fullyBookedDates);

    // 예약 다 찬 날짜에 fully-booked 클래스 추가
    fullyBookedDates.forEach(function (fullyBookedDate) {
        const dayElement = document.querySelector('.clickable[data-day="' + fullyBookedDate.getDate() + '"]');
        if (dayElement) {
            dayElement.classList.add('fully-booked');
        }
    });
}


// 예약이 다 찬 날짜인지 확인하는 함수
function isFullyBookedDate(year, month, day) {
    const currentDate = new Date(year, month, day);

    // fullyBookedDates 배열에 해당 날짜가 포함되어 있는지 확인
    return fullyBookedDates.some(function (fullyBookedDate) {
        const bookedDate = new Date(fullyBookedDate);
        return currentDate.toDateString() === bookedDate.toDateString();
    });
}

function buildCalendar() {
    const calendarTable = document.getElementById("calendar");
    const calendarTitle = document.getElementById("calendarTitle");
    calendarTitle.textContent = currentYear + "년 " + (currentMonth + 1) + "월";

    const daysOfWeek = ['일', '월', '화', '수', '목', '금', '토'];

    var table = '<tr>';
    for (var i = 0; i < daysOfWeek.length; i++) {
        table += '<th>' + daysOfWeek[i] + '</th>';
    }
    table += '</tr>';

    var firstDay = new Date(currentYear, currentMonth, 1);
    var lastDay = new Date(currentYear, currentMonth + 1, 0);

    var daysInMonth = lastDay.getDate();
    var dayOfWeek = firstDay.getDay();

    var date = 1;
    table += '<tr>';

    for (var i = 0; i < dayOfWeek; i++) {
        table += '<td class="inactive-date"></td>';
    }

    while (date <= daysInMonth) {
	    var className = '';
	
	    if (dayOfWeek === 0) {
	        className = 'sunday';
	    } else if (dayOfWeek === 6) {
	        className = 'saturday';
	    }
	
	    const currentDate = new Date();
	    const isPastDate = currentYear === currentDate.getFullYear() && (currentMonth < currentDate.getMonth() || (currentMonth === currentDate.getMonth() && date < currentDate.getDate()));
	
	    if (selectedDate && date === selectedDate.getDate() && currentMonth === selectedDate.getMonth() && currentYear === selectedDate.getFullYear()) {
	        className += ' selected';
	    } else if (isPastDate) {
	        className += ' past-date';
	    } else {
	        className += ' clickable';
	    }
	
	    // 예약이 다 찬 날짜인 경우 fully-booked 클래스 추가
		if (isFullyBookedDate(currentYear, currentMonth, date)) {
		    className += ' fully-booked';
		}
		
		const clickEvent = date > 0 ? 'onclick="selectDate(' + date + ')"' : '';
		
		table += '<td class="' + className + '" ' + clickEvent + '>' + (date > 0 ? date : '') + '</td>';
			
	    if (++dayOfWeek > 6) {
	        table += '</tr>';
	        if (date < daysInMonth) {
	            table += '<tr>';
	        }
	        dayOfWeek = 0;
	    }
	
	    date++;
    }

    table += '</table>';
    calendarTable.innerHTML = table;
}

function prevMonth() {
    if (currentMonth > 0) {
        currentMonth--;
    } else {
        currentYear--;
        currentMonth = 11;
    }
    buildCalendar();
}

function nextMonth() {
    if (currentMonth < 11) {
        currentMonth++;
    } else {
        currentYear++;
        currentMonth = 0;
    }
    buildCalendar();
}

function selectDate(day) {
    selectedDate = new Date(currentYear, currentMonth, day);
    buildCalendar(); // 선택한 날짜 스타일을 적용하기 위해 캘린더를 다시 만듭니다
    reservationDate = new Date(currentYear, currentMonth, day + 1);
}

//예약하기 누르면 실행되는 함수
function validateReservation() {
    const countInput = document.getElementById('count');

	//사용자가 날짜를 선택했는지 확인
    if (!selectedDate) {
    	alert('날짜를 선택하세요.');
    	return;
	}

    // 사용자가 유효한 인원 수를 입력했는지 확인
    const count = parseInt(countInput.value);
    if (isNaN(count) || count <= 0) {
        alert('올바른 인원 수를 입력하세요.');
        return;
    }
   
   console.log('선택한 날짜:', selectedDate);
   console.log('인원 수:', count);
    

    const localDateTimeString  = reservationDate.toISOString();

    // 서버로 예약 데이터 전송
    fetch('/performReservation', {
        method: 'POST',
        body: new URLSearchParams({
            onedayId: document.querySelector('input[name="onedayId"]').value,
            selectedDate: localDateTimeString ,
            count: count
        }),
        headers: {
            'Content-Type': 'application/x-www-form-urlencoded'
        }
    })
	.then(response => response.text())
    .then(message => {
        alert(message); // 서버에서 전달한 메시지를 알림창으로 표시
        if (message.includes("예약이 완료되었습니다.")) {
            window.location.reload(); // 예약이 완료되었을 경우 현재 페이지 리로드
        }
    })
    .catch(error => {
        console.error('예약 요청 실패:', error);
    });
}