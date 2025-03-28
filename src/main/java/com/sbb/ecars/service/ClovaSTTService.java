package com.sbb.ecars.service;

import com.sbb.ecars.config.ClovaConfig;
import com.sbb.ecars.dto.CallLogsDto;
import com.sbb.ecars.dto.GPTRequestDto;
import com.sbb.ecars.dto.DjangoResponseDto;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

@Service
public class ClovaSTTService {

    private final String CLOVA_STT_URL = "https://naveropenapi.apigw.ntruss.com/recog/v1/stt?lang=Kor";

    private final RestTemplate restTemplate;
    private final ClovaConfig clovaConfig;
    private final GPTService gptService;
    private final DjangoService djangoService;

    public ClovaSTTService(RestTemplate restTemplate, ClovaConfig clovaConfig, GPTService gptService, DjangoService djangoService) {
        this.restTemplate = restTemplate;
        this.clovaConfig = clovaConfig;
        this.gptService = gptService;
        this.djangoService = djangoService;
    }

    public CallLogsDto recognizeSpeech(MultipartFile audioFile, String audioFilePath) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.set("X-NCP-APIGW-API-KEY-ID", clovaConfig.getClientId());
            headers.set("X-NCP-APIGW-API-KEY", clovaConfig.getClientSecret());

            HttpEntity<byte[]> requestEntity = new HttpEntity<>(audioFile.getBytes(), headers);
            ResponseEntity<String> response = restTemplate.exchange(
                    CLOVA_STT_URL, HttpMethod.POST, requestEntity, String.class);

            if (response.getStatusCode() == HttpStatus.OK) {
                String recognizedText = response.getBody();

                // GPT 분석 실행 (이제 CallLogsDto를 반환)
                GPTRequestDto gptRequestDto = new GPTRequestDto(recognizedText);
                CallLogsDto gptResult = gptService.processEmergencyText(gptRequestDto);

                // Django 모델 호출 (추가 분류 데이터 가져오기)
                DjangoResponseDto djangoResponse = djangoService.sendFullTextToDjango(recognizedText);

                // CallLogsDto에 Django 데이터 추가 후 반환
                return new CallLogsDto(
                        null, null,
                        djangoResponse.getCategory(),
                        gptResult.getLocation(),
                        gptResult.getDetails(),
                        gptResult.getAddressName(),
                        gptResult.getPlaceName(),
                        gptResult.getPhoneNumber(),
                        recognizedText,
                        false,
                        djangoResponse.getEmergencyType(),
                        audioFilePath,
                        gptResult.getLat(),
                        gptResult.getLng(),
                        djangoResponse.getJurisdiction()
                );
            } else {
                return new CallLogsDto();
            }
        } catch (Exception e) {
            return new CallLogsDto();
        }
    }
}