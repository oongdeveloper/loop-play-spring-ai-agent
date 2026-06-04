package com.baedal.support.memory;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.ChatMemoryRepository;
import org.springframework.ai.chat.messages.Message;
import org.springframework.web.bind.annotation.*;

<<<<<<< HEAD
import java.util.Collections;
=======
>>>>>>> upstream/round4
import java.util.List;

/**
 * 3주차 — 세션 운영용 엔드포인트.
 * <p>
<<<<<<< HEAD
 * Memory가 "실제로 몇 개의 메시지를 들고 있는지"를 직접 관찰하기 위한 개발 전용 API.
 * 숙제의 시나리오 검증에서 이 엔드포인트로 Memory 상태를 캡처해 README에 붙인다.
 *
 * <p>프로덕션에서는 관리자 전용 엔드포인트로 분리하고 인증을 걸어야 한다.
=======
 * 수업 중 라이브 데모와 숙제에서 Memory의 상태를 직접 관찰하기 위한 개발 전용 API다.
 * 프로덕션에서는 관리자 전용 엔드포인트로 분리하고 인증을 걸어야 한다.
>>>>>>> upstream/round4
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/session")
public class SessionController {

    private final ChatMemory chatMemory;
    private final ChatMemoryRepository chatMemoryRepository;

<<<<<<< HEAD
    // TODO [1단계-E] 세션의 저장된 메시지 목록을 반환하라.
    //
    // 요구사항:
    //   - chatMemory.get(sessionId)로 메시지 리스트를 가져온다.
    //   - 각 Message를 MessageView(type, content)로 변환하여 반환한다.
    //     (MessageView.from(Message m) 가 이미 아래에 준비되어 있다.)
    //
    // 검증 포인트:
    //   - 같은 세션으로 두 번 대화한 뒤 이 엔드포인트 호출
    //     → USER 2건 + ASSISTANT 2건이 순서대로 보여야 한다.
    @GetMapping("/{sessionId}/messages")
    public List<MessageView> messages(@PathVariable String sessionId) {
        // TODO: chatMemory.get(sessionId)를 MessageView 리스트로 변환
        return Collections.emptyList();
    }

    // TODO [1단계-F] 세션을 비우라.
    //
    // 요구사항:
    //   - chatMemory.clear(sessionId) 호출
    //   - log.info("[Session] clear sessionId={}", sessionId) 로 감사 로그를 남긴다.
    //
    // 검증 포인트 (1단계 시나리오 4번):
    //   - 2024-1234 대화 후 이 엔드포인트 호출 → "그거" 질문이 다시 맥락을 못 찾아야 한다.
    @DeleteMapping("/{sessionId}")
    public void clear(@PathVariable String sessionId) {
        // TODO: chatMemory.clear(sessionId) + 로그
    }

    // TODO [1단계-G] Repository에 등록된 모든 세션 ID를 반환하라.
    //
    // 요구사항:
    //   - chatMemoryRepository.findConversationIds() 반환
    //
    // 검증 포인트 (1단계 시나리오 3번):
    //   - 세션 A와 세션 B로 각각 대화한 뒤 이 엔드포인트 호출
    //     → ["A-id", "B-id"] 가 나와야 한다. (세션 분리 검증)
    @GetMapping("/ids")
    public List<String> sessions() {
        // TODO: chatMemoryRepository.findConversationIds() 반환
        return Collections.emptyList();
=======
    /**
     * 특정 세션의 현재 저장된 메시지 목록을 조회한다.
     * Memory가 실제로 몇 개 메시지를 들고 있는지, 어떤 내용인지 눈으로 확인할 때 사용.
     */
    @GetMapping("/{sessionId}/messages")
    public List<MessageView> messages(@PathVariable String sessionId) {
        return chatMemory.get(sessionId).stream()
                .map(MessageView::from)
                .toList();
    }

    /**
     * 특정 세션의 Memory를 비운다.
     * 라이브 데모에서 "세션 오염" 이후 깔끔하게 리셋할 때 사용.
     */
    @DeleteMapping("/{sessionId}")
    public void clear(@PathVariable String sessionId) {
        log.info("[Session] clear sessionId={}", sessionId);
        chatMemory.clear(sessionId);
    }

    /**
     * 현재 Repository에 등록된 모든 세션 ID를 조회한다.
     * 멀티 세션 데모에서 "두 세션이 실제로 분리되어 저장되고 있다"는 걸 보여줄 때 사용.
     */
    @GetMapping("/ids")
    public List<String> sessions() {
        return chatMemoryRepository.findConversationIds();
>>>>>>> upstream/round4
    }

    public record MessageView(String type, String content) {
        static MessageView from(Message m) {
            return new MessageView(m.getMessageType().name(), m.getText());
        }
    }
}
