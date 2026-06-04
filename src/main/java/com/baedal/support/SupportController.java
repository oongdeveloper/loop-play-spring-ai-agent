package com.baedal.support;

import com.baedal.support.tool.OrderTools;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.web.bind.annotation.*;

/**
<<<<<<< HEAD
 * Structured Output + Tool Calling + Chat Memory 통합 엔드포인트.
 *
 * <p>3주차 변경점 (숙제에서 직접 구현): Memory Advisor 추가 + X-Session-Id 헤더 처리.
 * Triage 용도에도 Memory를 연결해 두면 "같은 세션에서 반복 분류할 때 맥락이 유지"된다.
 *
 * <p>구현 방법은 {@link AssistantController}와 동일하다. 거기서 배운 패턴을 여기에도 적용하라.
=======
 * Structured Output + Tool Calling + Chat Memory + RAG 통합 엔드포인트.
 * <p>
 * 4주차 변경점: {@link QuestionAnswerAdvisor}(order=20)를 체인에 추가한다.
 * Triage 응답도 정책/FAQ 근거가 있으면 더 정확한 카테고리/다음 액션을 반환한다.
 * <p>
 * ⚠️ {@link ChatClient.Builder}는 싱글톤 빈이므로 핸들러 내부에서
 * {@code .defaultXxx()}를 매 요청마다 호출하면 누적된다. 생성자에서 한 번만 빌드해 재사용한다.
>>>>>>> upstream/round4
 */
@RestController
@RequestMapping("/api/v1/support")
public class SupportController {

//    private final ChatClient.Builder builder;
//    private final PerformanceLoggingAdvisor performanceAdvisor;
//    private final MessageChatMemoryAdvisor memoryAdvisor; // 3주차에서 추가
//    private final OrderTools orderTools;

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

    // TODO [1단계-I] SupportController에도 동일한 Advisor 체인(memory → rag → performance)을 적용하라.
    //
    // 요구사항: 아래 생성자의 .defaultAdvisors(...)를 다음과 같이 바꾼다.
    //   .defaultAdvisors(memoryAdvisor, ragAdvisor, performanceAdvisor)
    //
    // AssistantController와 완전히 동일한 순서여야 한다 — 두 엔드포인트가
    // 같은 정책 지식과 같은 대화 맥락을 공유해야 일관된 상담이 된다.
    public SupportController(ChatClient.Builder builder,
                             PerformanceLoggingAdvisor performanceAdvisor,
                             MessageChatMemoryAdvisor memoryAdvisor,
                             QuestionAnswerAdvisor ragAdvisor,
                             OrderTools orderTools) {
        this.chatClient = builder
                .defaultSystem(BaedalPrompt.SYSTEM_PROMPT)
                // TODO: ragAdvisor를 memoryAdvisor 다음, performanceAdvisor 앞에 추가하라.
                .defaultAdvisors(memoryAdvisor, performanceAdvisor)
                .defaultTools(orderTools)
                .build();
    }

    @PostMapping
    public SupportResponse triage(@RequestBody ChatRequest req,
                                  @RequestHeader(value = "X-Session-Id", defaultValue = "default") String sessionId) {
        return chatClient.prompt()
                .user(req.message())
                .advisors(a -> a.param(ChatMemory.CONVERSATION_ID, sessionId))
                .call()
                .entity(SupportResponse.class);
    }
}
