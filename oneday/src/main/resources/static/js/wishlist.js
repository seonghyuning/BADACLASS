$(document).ready(function () {
    $('.icon-wishlist').on('click', function () {
		// 앵커 태그의 기본 동작을 막습니다.
   		event.preventDefault();
        $(this).toggleClass('in-wishlist');

        // 클릭된 아이콘의 부모 클래스 항목에서 onedayId 읽기
        let onedayId = parseInt($(this).closest('.class-item').find('[id^=onedayId]').val());
        let isWishlist = $(this).hasClass('in-wishlist');

        $.ajax({
            method: 'POST',
            url: '/wishlist/update',
            contentType: 'application/json',
            data: JSON.stringify({
                onedayId: onedayId,
                isWishlist: isWishlist
            }),
            success: function (response) {
                console.log('Wishlist updated successfully:', response);
                if (isWishlist) {
                    showMessage('위시리스트에 추가 되었습니다.');
                } else {
                    showMessage('위시리스트에서 삭제 되었습니다.');
                }
            },
            error: function (error) {
                console.error('Error updating wishlist:', error);
                showMessage('위시리스트 업데이트에 실패했습니다.');
            },
            dataFilter: function (data) {
                console.log('Sending data:', { onedayId, isWishlist });
                return data;
            }
        });
    });

    // 닫기 버튼 클릭 이벤트 처리
    $('#closeButton').on('click', function () {
        hideMessage();
    });

    // 위시리스트로 이동 버튼 클릭 이벤트 처리
    $('#wishlistButton').on('click', function () {
        hideMessage();
    });

    // 메시지를 표시하는 함수
    function showMessage(message) {
        $('#messageText').text(message);
        $('#messageContainer').show();
    }

    // 메시지를 숨기는 함수
    function hideMessage() {
        $('#messageContainer').hide();
    }
    

     // 슬라이더 초기화 및 옵션 설정
    $('.slider').slick({
        infinite: true,
        speed: 300,
        slidesToShow: 1,
        slidesToScroll: 1,
        autoplay: true, // 자동 재생 활성화
        autoplaySpeed: 2000, // 슬라이드 간 시간 간격 (ms)
        prevArrow: '<button type="button" class="slick-prev"></button>', // 이전 버튼
        nextArrow: '<button type="button" class="slick-next"></button>' // 다음 버튼
    });
    
    // 이전 버튼 클릭 시 슬라이더 이동
    $('.slick-prev').click(function () {
        $('.slider').slick('slickPrev');
    });

    // 다음 버튼 클릭 시 슬라이더 이동
    $('.slick-next').click(function () {
        $('.slider').slick('slickNext');
    });
    
    $('#categoryBtn').on('click', function () {
        // 카테고리 컨테이너를 토글
        $('.category-container').toggle();
        
        // 지역 컨테이너가 열려있으면 닫음
        $('.region-container').hide();
    });

    $('#regionBtn').on('click', function () {
        // 지역 컨테이너를 토글
        $('.region-container').toggle();
        
        // 카테고리 컨테이너가 열려있으면 닫음
        $('.category-container').hide();
    });
    
});