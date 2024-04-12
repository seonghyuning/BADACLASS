document.addEventListener('DOMContentLoaded', function() {
    // 버튼 클릭 이벤트 핸들러 등록
    $('#nearbyClassesBtn').click(function() {
        // 위치 정보를 가져오는 함수 호출
        getLocation();
    });
});

function getLocation() {
    if (navigator.geolocation) {
        // 사용자의 현재 위치 정보를 비동기적으로 가져옴
        navigator.geolocation.getCurrentPosition(
            function (position) {
                // 위치 정보를 성공적으로 받아왔을 때 좌표를 서버로 전송
                sendLocationToServer(position.coords.latitude, position.coords.longitude);
            },
            function (error) {
                // 위치 정보를 받아오는데 실패한 경우
                alert('위치 정보를 가져오는데 실패했습니다.');
            }
        );
    } else {
        alert('이 브라우저는 Geolocation API를 지원하지 않습니다.');
    }
}

function sendLocationToServer(latitude, longitude) {
    // 전송할 데이터 객체 생성

    // Ajax 요청
    $.ajax({
        type: 'POST', // 메서드를 POST로 변경
        contentType: 'application/json', // 데이터 타입을 JSON으로 설정
        url: '/mylocationupdate', // 엔드포인트 URL 설정
        data: JSON.stringify({
			latitude: latitude,
        	longitude: longitude
		}), // JSON 문자열로 데이터 변환
        success: function() {
            console.log(latitude, longitude);
            // 서버에서의 응답에 따라 처리
            window.location.href = '/mylocation';
        },
        error: function(error) {
            console.error('서버로 위치 데이터를 전송하는 중 오류 발생:', error);
        }
    });
}