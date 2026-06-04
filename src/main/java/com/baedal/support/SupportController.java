package com.baedal.support;

import com.baedal.support.tool.OrderTools;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.web.bind.annotation.*;

/**
 * Structured Output + Tool Calling + Chat Memory 통합 엔드포인트.
 *
 * <p>3주차 변경점 (숙제에서 직접 구현): Memory Advisor 추가 + X-Session-Id 헤더 처리.
 * Triage 용도에도 Memory를 연결해 두면 "같은 세션에서 반복 분류할 때 맥락이 유지"된다.
 *
 * <p>구현 방법은 {@link AssistantController}와 동일하다. 거기서 배운 패턴을 여기에도 적용하라.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/support")
public class SupportController {

    private final ChatClient.Builder builder;
    private final PerformanceLoggingAdvisor performanceAdvisor;
    private final MessageChatMemoryAdvisor memoryAdvisor; // 3주차에서 추가
    private final OrderTools orderTools;

    private ChatClient chatClient;

    // TODO [1단계-5] 이 엔드포인트에도 OrderTools를 등록하라.
    //
    // 요구사항 (AssistantController와 동일):
    //   1) @RequestHeader(value = "X-Session-Id", defaultValue = "default") String sessionId
    //   2) .defaultAdvisors(memoryAdvisor, performanceAdvisor)
    //   3) .advisors(a -> a.param(ChatMemory.CONVERSATION_ID, sessionId))
    //
    // 관찰 과제 (README에 기록):
    // - /api/v1/assistant(자연어) 와 /api/v1/support(JSON)의 입력 토큰 수 차이는?
    // - Structured Output과 Tool Calling이 함께 걸리면 2차 LLM 호출에서 어떤 프롬프트가 붙는가?
    //   (DEBUG 로그에서 ToolResponseMessage를 찾아본다.)

    @PostConstruct
    public void init() {
        this.chatClient = builder
                .defaultSystem(BaedalPrompt.SYSTEM_PROMPT)
                .defaultAdvisors(performanceAdvisor)
                .defaultTools(orderTools)
                .build();
    }

    @PostMapping
    public SupportResponse triage(@RequestBody ChatRequest req) {
        // round1 소스
        /*return builder
                .defaultSystem(BaedalPrompt.SYSTEM_PROMPT)
                .build()
                .prompt()
                .advisors(performanceAdvisor)
                .user(req.message())
                .call()
                .entity(SupportResponse.class);*/

        return chatClient
                .prompt()
                .user(req.message())
                .call()
                .entity(SupportResponse.class);
    }
}
