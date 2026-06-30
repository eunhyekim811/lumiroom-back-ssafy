### 🏠 LumiRoom (루미룸) - Backend Core Engine 🌓

공공 빅데이터 공간 인덱싱 및 Spring AI를 융합하여 데이터로 검증하고 AI로 요약하는 **안전 특화 부동산 정보 서비스 백엔드 API 서버**

- [LumiRoom Front Repo 바로가기](https://github.com/eunhyekim811/lumiroom-front-ssafy.git)

---

## 📝 프로젝트 개요

기존의 대형 부동산 플랫폼들은 매물의 평수, 보증금, 월세 가격, 역세권 여부 등 주로 '경제적·물리적 조건'의 정적 데이터 제공에만 몰두해 왔습니다. 그러나 1인 가구, 여성, 야간 통행자 등 현대의 실주거층이 거주지를 선택할 때 결코 간과할 수 없는 본질적인 지표는 "내가 이 동네에서 밤에 안심하고 걸어 다닐 수 있는가?"라는 치안과 안전의 문제입니다.

LumiRoom(루미룸)은 이러한 부동산 시장의 안전 데이터 사각지대를 해소하기 위해 기획되었습니다. 행정안전부 재난안전 개방 API 및 공공 데이터 포털로부터 전국 단위의 CCTV, 가로등, 보안등 데이터(약 100만 건 규모)를 수집하여 백엔드 인프라에 통합하고, MySQL Spatial Extensions 기술을 고도화하여 내 주변 인프라 밀집 조도를 계량화합니다. 더불어 복잡한 통계 수치나 수많은 원천 텍스트를 일일이 분석하기 어려워하는 사용자들을 위해, 지역별 실제 방범·치안 뉴스 데이터를 실시간 결합한 **하이브리드 RAG(검색 증강 생성) 기반의 생성형 AI 자연어 안심 브리핑**을 제공하여 혁신적이고 스마트한 주거 탐색 경험을 선사합니다.

## ✨ 핵심 기능 명세

### 1. 지도 기반 치안 인프라 및 안전도 조회

* **안전 특화 레이어 토글:** CCTV 위치, 가로등 밀집 구역, 경찰서 및 지구대 위치 등 치안 인프라만 필터링하여 지도 위에 시각화합니다.

![Lumi Room 지도 화면](/docs/06_화면설계서/04_map_properties.png)

### 2. 알고리즘 기반 안전 지수 산출 (시스템 내부 기능)

* **다각도 데이터 연산:** 치안 인프라 밀집도, 인프라별 가중치를 종합하여 정밀한 안전 점수를 계산합니다.


* **신뢰성 높은 등급 매칭:** 계산된 안전 점수를 기준으로 최종 **A, B, C, D 등급**을 행정구역 및 매물 단위에 매칭하여 화면에 직관적인 지표로 제공합니다.

![Lumi Room 매물 상세](/docs/06_화면설계서/08_property_detail.png)

### 3. AI 분석 및 자연어 브리핑 (LLM)

* **종합 위험도 자연어 요약:** 수치화된 치안 데이터와 뉴스 크롤링 데이터를 LLM이 종합 분석하여, 사용자에게 단순 통계 대신 직관적인 요약 보고서를 생성해 줍니다.


* **Structured Output (구조화된 출력 강제):** AI의 무작위 답변으로 인한 환각 현상과 파싱 에러를 완벽히 통제하기 위해, 자바 21 불변 Record 구조인 `StructuredBriefing` 고정 JSON 스키마 구조로만 응답하도록 통제합니다.

![Lumi Room AI](/docs/06_화면설계서/10_ai_briefing.png)

### 4. 사용자 참여 및 개인화 기능

* **관심 지역 및 주택 찜하기:** 자주 확인하거나 거주를 고려 중인 동네 및 특정 매물을 북마크(`ON DELETE CASCADE`)하여 마이페이지에서 효율적으로 관리합니다.


* **체감 치안 리뷰 소통:** 실제 거주자나 유저들이 체감한 동네의 야간 분위기, 골목길 치안 상태 등을 댓글로 공유하고 소통할 수 있는 기능을 제공합니다.

![Lumi Room review](/docs/06_화면설계서/11_review.png)

![Lumi Room mypage](/docs/06_화면설계서/09_mypage.png)

### 5. Spring Security + JWT 체계의 인증 관리

* **무상태성 JWT 인증 체계:** API 권한 인가용 Access Token과 세션 유지 및 연장 목적의 Refresh Token을 사용하며, JwtAuthenticationFilter를 통해 보호된 API 요청 헤더 토큰을 검증합니다.

* **인메모리 Redis 기반 세션 관리 및 refresh:** 로그인시 Redis에 Refresh Token을 저장하며, 토큰 재발급 요청이 들어오면 토큰쌍을 재발급합니다.

* **로그아웃 블랙리스트:** 로그아웃시 Redis에 저장한 토큰을 폐기하며, 로그아웃 후 수명이 남은 Access Token 잔여 시간동안 해당 토큰으로 접근하지 못하도록 Redis에 블랙리스트로 해당 토큰을 등록합니다.

![Lumi Room security](/docs/07_security.png)


## 🗂️ 백엔드 설계

### ERD

![Lumi Room erd](/docs/04_ER_diagram.png)

### APIs

백엔드 코어 API 및 기능 매핑 테이블 구조입니다.

![Lumi Room 유스케이스 다이어그램](/docs/02_usecase_diagram.png)

| 기능 ID | 대분류 | 중분류 | 요구사항 명칭 | 상세 구현 스펙 | 관련 기술 스택 |
| --- | --- | --- | --- | --- | --- |
| **REQ-01** | 회원 관리 | 인증/인가 | JWT 무상태 회원가입 및 로그인 | 이메일, 패스워드, 닉네임 기반 회원가입 처리 및 세션리스 인증 체계 구현 | Spring Security, JWT, MySQL |
| **REQ-02** | 회원 관리 | 인증/인가 | Redis 활용 로그인 유지 및 갱신 | Refresh Token을 인메모리 Redis에 보관, Access Token 만료 시 무중단 재발급 처리 | Spring Security, Redis |
| **REQ-03** | 회원 관리 | 인증/인가 | 로그아웃 및 토큰 블랙리스트 처리 | 로그아웃 시 사용된 Access Token의 남은 유효시간(TTL)을 계산하여 Redis에 블랙리스트 등록 | Redis, OncePerRequestFilter |
| **REQ-04** | 인프라 데이터 | 데이터 적재 | 매물 거래 데이터 적재 파이프라인 | 매물 원천 정보 및 가격 데이터 파싱, 유효성 검증을 통한 데이터베이스properties 테이블 적재 | Spring Batch, MyBatis, MySQL |
| **REQ-05** | 인프라 데이터 | 데이터 적재 | 치안안전시설 데이터 적재 파이프라인 | CCTV, 가로등, 보안등을 포함한 통합 치안안전시설 인프라 이기종 소스 동기화 파이프라인 구축 | Spring Batch, Open API |
| **REQ-06** | 인프라 데이터 | 데이터 적재 | 보안등 초대용량 데이터 처리 | 95만 건 이상의 대용량 데이터 특성을 고려한 CSV 파일 기반 FlatFileItemReader 파이프라인 구축 | Spring Batch (FlatFile) |
| **REQ-07** | 인프라 데이터 | 공간 조회 | 반경 내 안전 시설물 고속 조회 | R-Tree 공간 인덱스(SPATIAL INDEX) 및 ST_Distance_Sphere 함수를 이용한 매물 주변 시설물 고속 연산 | MySQL Spatial Extensions |
| **REQ-08** | 인프라 데이터 | 공간 조회 | MBR 영역 내 매물 고속 조회 | 현재 뷰포트의 격자 영역(Minimum Bounding Rectangle) 정의 후 MBRContains 함수 기반 고속 로딩 | MySQL Spatial, Map API |
| **REQ-09** | 매물 탐색 | 관심 매물 | 관심 매물 등록 및 해제 (하트 토글) | users와 properties 테이블 간 다대다(M:N) 매핑 테이블 연동 및 종속 삭제(on delete cascade) 처리 | Java 21 Record, MyBatis |
| **REQ-10** | 매물 탐색 | 매물 리뷰 | 별점 및 리뷰 권한 연동 제어 | 특정 매물 평점/텍스트 입력 기능, Security Context 내부 인증 유저 고유 일련번호(userId PK) 검증 | Spring Boot, MyBatis XML |
| **REQ-11** | 지능형 서비스 | AI 브리핑 | 지능형 RAG 기반 안전 가이드 요약 | 사용자 위경도 기반 인프라 밀집도 통계 데이터와 실시간 치안 뉴스 API 검색 결과를 콘텍스트로 취합 | Spring AI (ChatClient) |

👉🏻 [상세 API Endpoint 명세서 바로보기](https://www.google.com/search?q=/backend/APIs.md)

## ⚙ 기술 스택

### Back-end

* **Language & Runtime:** Java 21

* **Framework:** Spring Boot 3.x 

* **Security:** Spring Security & JWT (세션리스 Stateless 인증 및 인가 필터 체인 구축) 

* **ORM / Data Access:** MyBatis (XML Mapper 기반 고성능 SQL 매핑 및 트랜잭션 최적화) 


* **Spatial Database:** MySQL Spatial Extensions (POINT 데이터 타입, R-Tree 공간 인덱싱 최적화) 


### AI & Data Pipeline

* **Artificial Intelligence:** Spring AI (ChatClient 플루언트 API 기반 하이브리드 RAG 아키텍처 수립) 

* **LLM Engine:** OpenAI GPT API (Structured Output 기술 적용 및 고정 JSON 스키마 아웃풋 바인딩) 

* **Batch Framework:** Spring Batch 5.x (Chunk 지향 아키텍처 및 FlatFileItemReader 활용 대용량 마이그레이션) 

* **In-Memory Cache:** Redis (Refresh Token RTR 전략 및 수명 주기 기반 로그아웃 블랙리스트 캐싱) 


### Infra & Tools

* **Containerization:** Docker & Docker Compose (개발 및 운영 인프라 환경 격리 및 컨테이너 가동) 

* **Cloud Infrastructure:** AWS & OpenStack (클라우드 가상 서버 환경 빌드 및 웹 서비스 배포) 

* **Collaboration & VCS:** GitHub (형상 관리 및 이슈 트래킹 코드 협업) 

* **Documentation:** Notion (요구사항 정의서, UI 화면 명세 및 작업 공간 관리)

## 🛠️ 프로젝트 아키텍쳐

LumiRoom 백엔드는 결합도 완화 및 확장 보안 레이어링 모델을 채택하고 있습니다. 모든 인프라 치안 포인트는 공간 데이터베이스 튜닝 하에 정합되며, 무상태성 검증 로직과 데이터 수집 파이프라인이 독립적으로 구동됩니다.

![Lumi Room 구조도](/docs/06_architecture.png)

## 🤔 기술적 이슈와 해결 과정 (Troubleshooting)


### 1. Spring AI 버전 지옥 우회 및 하이브리드 RAG 아키텍처 선회

* **문제 상황:** Spring AI 내부 라이브러리를 활용하여 LLM에게 도구 실행권을 위임하는 Function Calling 구현 단계에서, 스프링 컨테이너 빈 스캐너와 프레임워크 내부 리플렉션 상속 메커니즘의 호환성 버그로 인해 콘솔에 `No @Tool annotated methods found` 예외가 다량 유출되며 지능형 분석 브리핑 빌드가 중단되었습니다.


* **원인 분석:** 프레임워크 자체의 유동적인 빈 탐색 시점이 특정 자바 런타임 버전 환경 하위 상속 메커니즘과 충돌하는 고질적인 라이브러리 자체의 결함이었습니다.


* **교정 내용:** 불확실성이 내포된 대리 도구 실행(Function Calling) 방식을 완전히 배제하고, 백엔드 서비스 비즈니스 레이어 단에서 매물 주변의 인프라 통계 수치와 뉴스 크롤링 텍스트 스트링을 선제 수집(Pre-fetching)한 후 프롬프트 변수 파라미터에 다이렉트로 결합하여 주입하는 **선제 수집형 Standard RAG 패턴**으로 선회하여 구동 신뢰성을 100%로 끌어올렸습니다.


### 3. Spring Security 순환 참조 및 Axios 인증 무한 루프 교정

* **문제 상황:** 배포 및 통합 테스트 단계에서 Spring Boot 3.x 시스템 아키텍처 가동 시 컴포넌트 간 의존성 주입이 얽히며 `Circular Dependency(순환 참조)` 예외가 발생해 애플리케이션 빌드가 중단되거나, 클라이언트(Vue.js 3) 환경과 연동 시 로그인/회원가입 프리패스 구간에서 인증 인터셉터의 정합성이 깨져 무한 루프 가중 어택이 발생하는 현상이 식별되었습니다.


* **원인 분석:** 백엔드 단에서는 Spring Security 필터 체인(`SecurityConfig`) 내에 커스텀 `JwtAuthenticationFilter`, `UserDetailsService`, `PasswordEncoder`를 동시에 빈(Bean)으로 상호 등록 및 주입하려다 생명주기 타이밍이 교차하는 프레임워크 순환 구조가 원인이었습니다. 프론트엔드 단에서는 인증 토큰이 없는 상태의 회원가입/로그인 HTTP 요청 마저도 공통 Axios 인터셉터 레이어가 가로채 토큰 재발급(Reissue) 혹은 리다이렉트 흐름을 강제 작동시키는 로직 결함이 존재했습니다.


* **교정 내용:** 암호화 컴포넌트 및 핵심 보안 컨텍스트의 빈 등록 설정 레이어를 물리적으로 계층 분리하여 순환 참조 병목을 원천 해제했습니다. 또한, 클라이언트 레이어에서는 인증이 필수적인 핵심 비즈니스 API 구역과 로그인/회원가입 엔드포인트를 철저히 분리 제어할 수 있도록 **Axios 통신 객체를 투트랙으로 독립 분리 적용**하여 인증 인터셉터 무한 루프 맹점을 완전히 해결했습니다.


### 4. 배포 인프라 내 공공 API 차단 및 네트워크 순단 리스크 극복

* **문제 상황:** 클라우드 배포 인프라(AWS/OpenStack) 환경 상에서 플랫폼 가동 시, 초기 치안 데이터 마이그레이션을 위해 공공데이터 포털 및 행정안전부 REST API 서버로 대량의 HTTP 요청을 전송했으나, 공공기관 방화벽의 트래픽 제한(Throttling) 정책 및 네트워크 순단 현상으로 인해 데이터 무더기 누실 및 파이프라인 다운 에러가 발생했습니다.


* **원인 분석:** 대한민국 전역의 보안등 데이터는 무려 95만 건에 달하는 초거대 소스였기에 단순 HTTP 스크랩 방식으로 마이그레이션을 완주하는 것은 원천 서버의 불안정성에 극도로 종속되는 아키텍처적 취약점을 안고 있었습니다.


* **교정 내용:** 단순 Open API 호출 방식의 한계를 회피하기 위해, 실무 엔터프라이즈 규격의 **투트랙(Two-Track) 파일 기반 배치 전략**으로 선회했습니다. 원천 CSV 데이터를 안전하게 확보하여 로컬 배포 패키지 내부 인프라로 이식한 뒤, 스프링 배치의 `FlatFileItemReader` 파이프라인을 구축했습니다. 여기에 자바 메모리 단 고속 수학 가공 연산을 이식하여 Web Mercator 투영 좌표계를 순수 WGS84 위경도 좌표계로 실시간 변환 및 인덱싱 처리함으로써 단 한 건의 유실과 네트워크 통신 스트레스 없이 95만 건 대용량 마이그레이션을 무정지로 완수해 냈습니다.

## 🚀 실행 방법 (Getting Started)

### Prerequisites

* Java 21 SDK 이상
* Docker 및 Docker Compose
* MySQL 8.0+ (Spatial Extensions 지원 컴포넌트 포함 필수)
  * [DDL 문서 참고](https://github.com/eunhyekim811/lumiroom-back-ssafy/blob/master/src/main/resources/db/ddl.sql)
* Redis Cache Server

### Installation & Run

1. 본 백엔드 레포지토리를 로컬 환경에 클론합니다.
```bash
git clone https://github.com/your-repository/lumiroom-backend.git
cd [lumiroom-backend 프로젝트 루트 디렉터리]
```

2. Docker Compose를 구동하여 데이터베이스와 Redis 환경을 컨테이너화하여 가동합니다.
```bash
docker-compose up --build -d
```

## 💁‍♂️ 프로젝트 팀원

| **손예림** | **김은혜** |
| --- | --- |
|  |  |
| [GitHub Profile](https://github.com/sonyerim) | [GitHub Profile](https://github.com/eunhyekim811) |
| Backend & Frontend | Backend & Frontend |
| Spring Batch 기반 매물 데이터 수집 및 공간 인프라&매물 조회, 매물별 안전점수 계산 파이프라인 설계, Docker 컨테이너 기반 배포 | Spring Batch 기반 CCTV/가로등/보안등 데이터 수집, 사용자 리뷰/관심 매물 기능 구현, Spring Security+JWT 기반 인증, Spring AI 활용, Docker 컨테이너 기반 배포 및 Nginx 설정 |
