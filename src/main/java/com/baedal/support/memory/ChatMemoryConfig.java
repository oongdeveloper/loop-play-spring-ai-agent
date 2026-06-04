package com.baedal.support.memory;

import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.ChatMemoryRepository;
import org.springframework.ai.chat.memory.InMemoryChatMemoryRepository;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 3주차 — Chat Memory 설정.
 *
<<<<<<< HEAD
 * <h3>구성 요소 (3레이어)</h3>
 * <ul>
 *     <li>{@link ChatMemoryRepository} : 메시지의 저장소 (CRUD) — JPA Repository에 대응</li>
 *     <li>{@link ChatMemory}           : 저장소 위에 "크기 제어 정책"을 얹은 것 — Service 계층에 대응</li>
 *     <li>{@link MessageChatMemoryAdvisor} : ChatClient 호출 흐름에 Memory를 연결하는 어댑터 — 인터셉터에 대응</li>
 * </ul>
 *
 * <p>세 Bean이 모두 등록되어야 Memory가 "자동으로" 동작한다.
 * 학생은 각 Bean을 구현하면서 <b>왜 이 값/전략을 선택했는지</b>를 README에 기록해야 한다.
 *
 * @see SessionController     Memory 상태 확인용 엔드포인트
 * @see JdbcChatMemoryExample JDBC 저장소로 전환하는 방법 (3단계 숙제)
=======
 * <h3>왜 Memory가 필요한가</h3>
 * <ul>
 *     <li><b>지시 대명사 해결</b>: "그거 취소해줘" 의 "그거"는 이전 턴에 언급된 주문번호</li>
 *     <li><b>이전 주문 참조</b>: "아까 그 주문 환불 돼요?" — 최근 질문의 orderId를 알아야 답 가능</li>
 *     <li><b>Tool 호출 파라미터 유추</b>: LLM이 대화 이력에서 orderId를 추출하여 Tool 호출에 사용</li>
 * </ul>
 *
 * <h3>구성 요소</h3>
 * <ul>
 *     <li>{@link ChatMemoryRepository}: 메시지의 저장소 — InMemory / JDBC / Redis 등으로 교체 가능</li>
 *     <li>{@link ChatMemory}: 저장소 위에 "크기 제어 정책"을 얹은 것 — 여기서는 MessageWindow 전략</li>
 *     <li>{@link MessageChatMemoryAdvisor}: ChatClient 호출 전/후에 Memory를 읽고 쓰는 Advisor</li>
 * </ul>
 *
 * <h3>InMemory의 한계</h3>
 * <ul>
 *     <li>서버 재시작 시 전체 소실</li>
 *     <li>멀티 인스턴스 환경에서 인스턴스마다 서로 다른 메모리 (sticky session 필요)</li>
 *     <li>교육용/로컬 개발/단기 세션에는 충분</li>
 * </ul>
 *
 * <p>영속화가 필요하다면 {@code spring-ai-starter-model-chat-memory-repository-jdbc} 의존성을 추가하고
 * {@link InMemoryChatMemoryRepository} 자리에 {@code JdbcChatMemoryRepository}를 주입하면 된다.
 * (강의 노트의 "InMemory vs JDBC 비교" 섹션 참조)
>>>>>>> upstream/round4
 */
@Configuration
public class ChatMemoryConfig {

<<<<<<< HEAD
    // TODO [1단계-A] MAX_MESSAGES 상수를 결정하라.
    //
    // 이 값은 "슬라이딩 윈도우"의 크기다. 최근 N개의 메시지만 Memory에 유지된다.
    // 2단계 실험에서 이 값을 1 / 20 / Integer.MAX_VALUE 로 바꿔 가며
    // 입력 토큰과 지시 대명사 해결 정확도를 비교할 예정이다.
    //
    // 기본 출발점으로 20을 권장하지만, 자신이 선택한 값과 근거를 README에 남겨라.
    //
    // 힌트:
    //   - 너무 작으면(예: 2): 지시 대명사 해결이 망가진다. 실패 시나리오를 직접 관찰할 것.
    //   - 너무 크면(예: Integer.MAX_VALUE): 입력 토큰이 선형 증가하여 비용/지연이 늘어난다.
    //   - 배달 상담 한 번의 평균 턴 수(예: 5~10턴)를 가정하고 계산해 보라.
    private static final int MAX_MESSAGES = 20; // TODO: 왜 이 값인지 README에 근거 기록

    // TODO [1단계-B] ChatMemoryRepository Bean을 등록하라.
    //
    // 요구사항:
    //   - 반환: new InMemoryChatMemoryRepository()
    //   - 3단계에서 JDBC로 전환할 때 이 Bean을 @Profile("!jdbc")로 한정하게 된다.
    //
    // 설계 결정 질문 (README):
    //   - InMemory로 충분한 상황 vs JDBC가 필요한 상황의 경계는 어디인가?
    //     (서버 재시작 / 멀티 인스턴스 / 감사 요구 / 개인정보 보존 기간 중 어느 것이 먼저 깨지는가?)
    @Bean
    public ChatMemoryRepository chatMemoryRepository() {
        // TODO: InMemoryChatMemoryRepository 인스턴스 반환
        return null;
    }

    // TODO [1단계-C] ChatMemory Bean을 등록하라.
    //
    // 요구사항:
    //   MessageWindowChatMemory.builder()
    //       .chatMemoryRepository(repository)
    //       .maxMessages(MAX_MESSAGES)
    //       .build();
    //
    // 설계 결정 질문 (README):
    //   - MessageWindowChatMemory는 "최근 N개만" 유지한다.
    //     반면 "대화 요약(summarization) 전략"은 언제 더 적합한가?
    //   - 두 전략의 장단점 표를 작성하라.
    @Bean
    public ChatMemory chatMemory(ChatMemoryRepository repository) {
        // TODO: MessageWindowChatMemory를 MAX_MESSAGES 크기로 빌드해 반환
        return null;
    }

    // TODO [1단계-D] MessageChatMemoryAdvisor Bean을 등록하라.
    //
    // 요구사항:
    //   MessageChatMemoryAdvisor.builder(chatMemory).order(10).build();
    //
    // order(10)의 의미:
    //   - Advisor 체인에서 실행 순서를 결정한다. 낮을수록 먼저 실행된다.
    //   - Memory Advisor는 PerformanceLoggingAdvisor보다 먼저 동작해야 한다
    //     (프롬프트 조립 전에 이전 대화 이력을 끼워 넣어야 하므로).
    //
    // 설계 결정 질문 (README):
    //   - 만약 order 순서를 바꾼다면 어떤 관찰 가능한 차이가 생기는가?
    @Bean
    public MessageChatMemoryAdvisor messageChatMemoryAdvisor(ChatMemory chatMemory) {
        // TODO: MessageChatMemoryAdvisor를 order(10)으로 빌드해 반환
        return null;
=======
    /**
     * 최근 N개 메시지만 유지하는 슬라이딩 윈도우 전략.
     * <p>
     * 배달 상담의 특성상 오래된 대화는 자주 참조하지 않으므로 최근 20개만 유지해도 충분하다.
     * 이 값은 <b>입력 토큰 예산과 직결</b>되므로 프로덕션에서는 대화 턴당 평균 토큰 × N으로 계산해 결정한다.
     */
    private static final int MAX_MESSAGES = 20;

    @Bean
    public ChatMemoryRepository chatMemoryRepository() {
        // InMemory 구현: ConcurrentHashMap 기반, 서버 재시작 시 소실.
        //
        // JDBC로 전환하려면 — 미션 3단계 가이드:
        //   1) build.gradle의 JDBC 의존성 3줄(spring-ai-starter-model-chat-memory-repository-jdbc,
        //      spring-boot-starter-jdbc, h2) 주석 해제.
        //   2) 이 Bean에 @Profile("!jdbc") 을 붙여 InMemory 구현은 기본 프로필에서만 쓰이게 한다.
        //      (Spring AI 1.0 GA는 JDBC 의존성만 추가되면 JdbcChatMemoryRepository를 자동 구성.)
        //   3) application-jdbc.yml 을 활성화: ./gradlew bootRun --args='--spring.profiles.active=jdbc'
        //   4) 서버 재시작 후에도 같은 세션의 대화가 유지되는지 확인.
        return new InMemoryChatMemoryRepository();
    }

    @Bean
    public ChatMemory chatMemory(ChatMemoryRepository repository) {
        return MessageWindowChatMemory.builder()
                .chatMemoryRepository(repository)
                .maxMessages(MAX_MESSAGES)
                .build();
    }

    @Bean
    public MessageChatMemoryAdvisor messageChatMemoryAdvisor(ChatMemory chatMemory) {
        // order를 낮게 두어 다른 Advisor(PerformanceLogging 등)보다 먼저 동작하도록 한다.
        return MessageChatMemoryAdvisor.builder(chatMemory)
                .order(10)
                .build();
>>>>>>> upstream/round4
    }
}
