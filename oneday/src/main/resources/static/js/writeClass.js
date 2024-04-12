// combined.js

function classInfo() {
    document.querySelector("#information").style.display = 'block';
    document.querySelector("#curriculum").style.display = 'none';
}

function classCurri() {
    document.querySelector("#information").style.display = 'none';
    document.querySelector("#curriculum").style.display = 'block';
}

function backToMain() {
    console.log("backToMain");
    location.href = '/main';
}

// 카테고리 추가 함수
function addCategory() {
    var container = document.getElementById("categoryContainer");
    
    var selectCategory = document.getElementsByName("categoryCode");
    var addCategoryBtn = document.getElementById("addCategoryBtn");

    // 새로운 input 요소 생성
    var newCategoryInput = document.createElement("input");
    newCategoryInput.type = "text";
    newCategoryInput.name = "newCategoryName";
    newCategoryInput.placeholder = "새로운 카테고리 입력";
    newCategoryInput.required = true;

	// 새로운 button 요소 생성
    var newEnrollButton = document.createElement("input");
    newEnrollButton.type = "button";
    newEnrollButton.value = "등록";
    // 버튼에 CSS 스타일 적용
	newEnrollButton.style.width = "50px"; // 너비 설정
	newEnrollButton.style.height = "30px"; // 높이 설정
    
    // 생성한 input, button을 컨테이너에 추가
    container.appendChild(newCategoryInput);
    container.appendChild(newEnrollButton);
    
    // 등록 버튼을 클릭할 시 발생하는 이벤트
    newEnrollButton.addEventListener("click", function() {
		// 입력된 값 확인
	    var inputValue = newCategoryInput.value.trim();
	    
	    // 값이 비어있는 경우 경고 표시
	    if (inputValue === "") {
	        alert("새로운 카테고리를 입력하세요.");
	        return;
	    }
	    	    
	    // 카테고리 선택, 카테고리 추가, 등록 버튼 모두 비활성화
	    selectCategory[0].disabled = true;
	    addCategoryBtn.disabled = true;
	    newEnrollButton.disabled = true;
	});

    var newBr = document.createElement("br");
    container.appendChild(newBr);
}

// 파일명 따로 전송하는 함수
document.getElementById('file').addEventListener('change', function() {
    var fileInput = document.getElementById('file');
    var fileName = fileInput.value;     // 파일 이름

    var container = document.getElementById("imageContainer");

    // 새로운 input 요소 생성
    var newInput = document.createElement("input");
    newInput.type = "hidden";
    newInput.name = "imageName";
    newInput.value = fileName;

    // 생성한 input을 컨테이너에 추가
    container.appendChild(newInput);
});

// 업로드한 이미지 보여주기
function previewImage(input) {
    var imagePreview = document.getElementById('imagePreview');

    if (input.files && input.files[0]) {
        var reader = new FileReader();

        reader.onload = function (e) {
            imagePreview.src = e.target.result;
            imagePreview.style.display = 'block';
        };

        reader.readAsDataURL(input.files[0]);
    } else {
        imagePreview.style.display = 'none';
    }
}

function adressInfo() {
    new daum.Postcode({
        oncomplete: function (data) {
            var addr = data.userSelectedType === 'R' ? data.roadAddress : data.jibunAddress;
            var extraAddr = '';

            if (data.userSelectedType === 'R') {
                if (data.bname !== '' && /[동|로|가]$/g.test(data.bname)) {
                    extraAddr += data.bname;
                }
                if (data.buildingName !== '' && data.apartment === 'Y') {
                    extraAddr += (extraAddr !== '' ? ', ' + data.buildingName : data.buildingName);
                }
                if (extraAddr !== '') {
                    extraAddr = ' (' + extraAddr + ')';
                }
                document.getElementById("sample6_extraAddress").value = extraAddr;
            } else {
                document.getElementById("sample6_extraAddress").value = '';
            }

            document.getElementById('sample6_postcode').value = data.zonecode;
            document.getElementById("sample6_address").value = addr;
            document.getElementById("sample6_detailAddress").focus();
        }
    }).open();
}
