package com.ocean.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class GeocodingService {
    private static final String KAKAO_API_KEY = "e7bc8ab5e0e3a0c9643a2e30db3817f5";
    private static final String KAKAO_GEOCODING_URL = "https://dapi.kakao.com/v2/local/search/address.json";

    public static LatLng geocodeAddress(String address) {
        // 카카오 API를 호출하여 주소를 위도 및 경도로 변환
        String apiUrl = KAKAO_GEOCODING_URL + "?query=" + address;

        // HTTP 요청을 보내기 위한 RestTemplate 생성
        RestTemplate restTemplate = new RestTemplate();

        // API 호출 및 응답 수신
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "KakaoAK " + KAKAO_API_KEY);
        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange(apiUrl, HttpMethod.GET, entity, String.class);

        // 응답 데이터를 파싱하여 위도 및 경도 추출
        LatLng coordinates = parseCoordinatesFromResponse(response.getBody());

        return coordinates;
    }

    private static LatLng parseCoordinatesFromResponse(String response) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(response);

            // 응답에서 첫 번째 결과만 사용
            JsonNode firstResult = rootNode.path("documents").get(0);

            // 위도 및 경도 추출
            double latitude = firstResult.path("y").asDouble();
            double longitude = firstResult.path("x").asDouble();

            return new LatLng(latitude, longitude);
        } catch (Exception e) {
            e.printStackTrace();
            // 예외 처리를 추가하거나 반환 타입을 Optional 등으로 변경할 수 있음
            return null;
        }
    }

    // LatLng 클래스는 위도와 경도를 저장하는 간단한 클래스로 가정
    public static class LatLng {
        private double latitude;
        private double longitude;

        public LatLng(double latitude, double longitude) {
            this.latitude = latitude;
            this.longitude = longitude;
        }

        public double getLatitude() {
            return latitude;
        }

        public double getLongitude() {
            return longitude;
        }
    }
}
