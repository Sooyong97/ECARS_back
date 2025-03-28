package com.sbb.ecars.websocket;

import com.sbb.ecars.domain.CallLogs;
import com.sbb.ecars.dto.CallLogsDto;
import com.sbb.ecars.repository.CallLogsRepository;
import com.sbb.ecars.service.ClovaSTTService;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.socket.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.ByteBuffer;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
public class WebSocketAudioHandler implements WebSocketHandler {

    private final ClovaSTTService clovaSTTService;
    private final CallLogsRepository callLogsRepository;

    public WebSocketAudioHandler(ClovaSTTService clovaSTTService, CallLogsRepository callLogsRepository) {
        this.clovaSTTService = clovaSTTService;
        this.callLogsRepository = callLogsRepository;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        System.out.println("WebSocket Connected" + session.getId());
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        System.out.println("WebSocket Closed" + session.getId());
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) {
        System.out.println("WebSocket Transport Error" + session.getId());
    }

    @Override
    public boolean supportsPartialMessages() {
        return false;
    }

    @Override
    public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) {
        if (message instanceof BinaryMessage) {
            ByteBuffer payload = ((BinaryMessage) message).getPayload();
            byte[] audioData = new byte[payload.remaining()];
            payload.get(audioData);

            try {
                // 오디오 파일 저장
                String fileName = "audio_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")) + ".wav";
                File audioFile = new File("uploads/" + fileName);
                try (FileOutputStream fos = new FileOutputStream(audioFile)) {
                    fos.write(audioData);
                }

                System.out.println("✅ Audio File Saved: " + fileName);

                // File → MultipartFile 변환
                MultipartFile multipartFile = convertFileToMultipartFile(audioFile);

                // STT 변환 후 GPT 분석 실행 (CallLogsDto 반환)
                CallLogsDto callLogsDto = clovaSTTService.recognizeSpeech(multipartFile, fileName);

                // CallLogs 저장
                CallLogs callLogs = new CallLogs(callLogsDto);
                callLogsRepository.save(callLogs);

                // React로 변환된 데이터 전송
                session.sendMessage(new TextMessage("{"
                        + "\"incidentType\": \"" + callLogsDto.getCategory() + "\","
                        + "\"incidentLocation\": \"" + callLogsDto.getLocation() + "\","
                        + "\"situationDetails\": \"" + callLogsDto.getDetails() + "\"}"));

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    // File → MultipartFile 변환
    private MultipartFile convertFileToMultipartFile(File file) throws Exception {
        FileInputStream inputStream = new FileInputStream(file);
        return new MockMultipartFile(
                file.getName(),           // 원본 파일 이름
                file.getName(),           // 클라이언트에서 보낸 파일 이름
                MediaType.APPLICATION_OCTET_STREAM_VALUE, // MIME 타입
                inputStream               // 파일 데이터
        );
    }
}
