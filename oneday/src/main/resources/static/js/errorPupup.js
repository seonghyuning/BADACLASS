document.addEventListener('DOMContentLoaded', function() {
    var findPwForm = document.getElementById('findPw-form');
    
    if (findPwForm) {
        findPwForm.addEventListener('submit', function(event) {
            // 폼 제출 전에 이벤트를 가로채서 처리
            event.preventDefault();

            // 폼 데이터 가져오기
            var formData = new FormData(findPwForm);
            
            // 비동기적으로 서버에 요청
            fetch('/auth/findPw', {
                method: 'POST',
                body: formData
            })
            .then(response => response.json())  // 서버에서 JSON 응답을 받음
            .then(data => {
                // 응답 데이터에서 오류 메시지 확인
                if (data.error) {
                    alert(data.error);  // 오류가 있으면 팝업창으로 표시
                } else {
                    // 오류가 없으면 서버에 응답된대로 페이지 이동
                    window.location.href = data.redirect;
                }
            })
            .catch(error => {
                console.error('Error:', error);
            });
        });
    }
});

document.addEventListener('DOMContentLoaded', function() {
    var findIdform = document.getElementById('findId-form');
    
    if (findIdform) {
        findIdform.addEventListener('submit', function(event) {
            // 폼 제출 전에 이벤트를 가로채서 처리
            event.preventDefault();

            // 폼 데이터 가져오기
            var formData = new FormData(findIdform);
            
            // 비동기적으로 서버에 요청
            fetch('/auth/findId', {
                method: 'POST',
                body: formData
            })
            .then(response => response.json())  // 서버에서 JSON 응답을 받음
            .then(data => {
                // 응답 데이터에서 오류 메시지 확인
                if (data.error) {
                    alert(data.error);  // 오류가 있으면 팝업창으로 표시
                } else {
                    // 오류가 없으면 서버에 응답된대로 페이지 이동
                    window.location.href = data.redirect;
                }
            })
            .catch(error => {
                console.error('Error:', error);
            });
        });
    }
});

document.addEventListener('DOMContentLoaded', function() {
    var resetPwForm = document.querySelector('#Pwreset-form');

    if (resetPwForm) {
        resetPwForm.addEventListener('submit', function(event) {
            // 폼 제출 전에 이벤트를 가로채서 처리
            event.preventDefault();

            // 폼 데이터 가져오기
            var formData = new FormData(resetPwForm);

            // 비동기적으로 서버에 요청
            fetch('/auth/resetPw', {
                method: 'POST',
                body: formData
            })
            .then(response => response.json())  // 서버에서 JSON 응답을 받음
            .then(data => {
                // 응답 데이터에서 오류 메시지 확인
                if (data.error) {
                    alert(data.error);  // 오류가 있으면 팝업창으로 표시
                } else {
                    // 오류가 없으면 서버에 응답된대로 페이지 이동
                    window.location.href = data.redirect;
                }
            })
            .catch(error => {
                console.error('Error:', error);
            });
        });
    }
});
