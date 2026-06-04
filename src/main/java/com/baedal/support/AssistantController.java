package com.baedal.support;

import com.baedal.support.tool.OrderTools;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.web.bind.annotation.*;

/**
 * Tool Calling + Chat Memory가 적용된 자연어 응답 엔드포인트.
 *
 * <p>3주차 변경점 (숙제에서 직접 구현):
 * <ul>
 *     <li>{@link MessageChatMemoryAdvisor}를 Advisor 체인에 추가 — 이전 대화 이력을 자동 주입
 *         (Advisor 등록은 {@link AssistantChatClientConfig}에서 수행)</li>
 *     <li>{@code X-Session-Id} HTTP 헤더로 고객 세션을 식별하고
 *         {@link ChatMemory#CONVERSATION_ID} 파라미터에 전달</li>
 *     <li>헤더가 없으면 {@code "default"} 세션으로 폴백 (개발용)</li>
 * </ul>
 *
 * <p>구현 후 관찰 포인트: 같은 세션 ID로 연속 호출 시 "그거", "방금 주문한 거" 같은
 * 지시 대명사가 Tool 호출 파라미터(orderId)로 정확히 치환되는 과정을 DEBUG 로그에서 확인할 수 있다.
 *
 * <p>구조 메모: {@link ChatClient.Builder}가 아니라 조립이 끝난 {@link ChatClient}를 주입받는다.
 * Builder는 싱글톤이라 요청마다 defaultTools/defaultAdvisors를 호출하면 누적되어
 * "Multiple tools with the same name" 오류가 발생하기 때문이다.
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/assistant")
public class AssistantController {

    private final ChatClient assistantChatClient;
    private ChatClient chatClient;

    private final ChatClient.Builder builder;
    private final PerformanceLoggingAdvisor performanceAdvisor;
    private final OrderTools orderTools;

    // TODO [1단계-4] 이 엔드포인트에 OrderTools를 등록하라.
    //
    // 요구사항:
    //   1) 메서드 파라미터에 다음을 추가:
    //         @RequestHeader(value = "X-Session-Id", defaultValue = "default") String sessionId
    //   2) log.info("[Assistant] sessionId={}, message={}", sessionId, req.message()); 로 관찰 포인트 확보
    //   3) 이 호출에 한해 Memory가 사용할 conversationId를 지정:
    //         .advisors(a -> a.param(ChatMemory.CONVERSATION_ID, sessionId))
    //
    // 힌트: .defaultTools(...) vs .tools(...) — 전자는 이 컨트롤러의 모든 호출에 적용,
    //      후자는 개별 호출에만 적용된다. 여기서는 defaultTools()가 맞다.

    @PostConstruct
    public void init() {
        this.chatClient = builder
                .defaultSystem(BaedalPrompt.ASSISTANT_PROMPT)
                .defaultAdvisors(performanceAdvisor)
                .defaultTools(orderTools)
                .build();
    }

    @PostMapping
    public String ask(@RequestBody ChatRequest req) {
        return chatClient.prompt()
                .user(req.message())
                .call()
                .content();
    }
}
