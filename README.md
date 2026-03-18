# Spring HATEOAS & JWT 기반 REST API 프로젝트 (spring-hateoas-shop)

이 프로젝트는 Spring Boot 환경에서 **Spring HATEOAS**를 활용하여 자기 서술성(Self-Descriptive)을 가진 RESTful API를 구축하고, **Spring Security**와 **JWT**를 도입하여 안전한 인증 및 권한 관리를 구현한 예제 프로젝트입니다.

## 🛠 사용 기술 (Tech Stack)
- **Language**: Java 17
- **Framework**: Spring Boot 4.0.3 (or 3.x with Jakarta)
- **Database / ORM**: MySQL, Spring Data JPA
- **API Documentation**: SpringDoc OpenAPI (Swagger 3.0.2)
- **Security & Auth**: Spring Security, JWT (jjwt-api 0.11.5)
- **Others**: Spring HATEOAS, Lombok, Validation

---

## 🌟 주요 기능 및 특징 (Key Features)

### 1. Spring HATEOAS 기반 REST API 구현
API 응답 모델이 단순한 데이터를 넘어 `EntityModel` 및 `PagedModel` 등의 객체로 반환됩니다. 클라이언트는 각 응답 결과 내에 포함된 `_links` 프로퍼티를 통해 연관된 행동(예: 목록으로 돌아가기, 특정 유저 엔티티 조회, 시리즈 상세 조회하기 등)을 즉시 알 수 있어 REST API 성숙도 모델(Richardson Maturity Model)의 가장 높은 단계를 구현합니다.

### 2. 세션 리스포트 & JWT 기반 토큰 인증 환경
- 불필요한 서버 측 세션 유지를 피하기 위해 `SessionCreationPolicy.STATELESS` 방식으로 전환하였습니다.
- `JwtAuthenticationFilter` 필터를 도입하여 매 요청마다 클라이언트가 제공한 JWT 토큰의 유효성을 검사하고 사용자 인증 정보를 시큐리티 컨텍스트(`SecurityContext`)에 등록합니다.
- **접근 권한 정책**:
  - `GET /users/*` 요청이나 시스템 상태를 변화시키는 POST, PUT, DELETE 요청은 **인증(토큰) 필요**.
  - 로그인(`/auth/login`) 및 주요 페이징 리스트 읽기(`GET /**`) 요청은 조건 없이 **모두 허용(`permitAll`)**.

### 3. 복합 키 매핑 관계 설정
- 사용자와 시리즈 간의 다대다(N:M) 리뷰 평점 관계를 연결 테이블 엔티티 클래스 **`Rating`**으로 풀어냈습니다. 
- 복합키 전략(`RatingId`: username, seriesId)을 활용해 하나의 유저가 특정 시리즈에 하나의 평점만 내리도록 구현되어 있습니다.

---

## 📡 API 엔드포인트 요약 (Endpoints)

### 🔐 인증 (Auth)
- `POST /auth/login`: 유저네임(username)과 패스워드(password) 검증 후 유효한 `JWT 토큰` 객체를 발급하여 반환.

### 📺 시리즈 (Series)
- `GET /series/list`: 모든 시리즈의 정보를 페이징 된 형태로 반환 (`PagedModel`). 
- `GET /series/{id}`: 특정 시리즈에 대한 상세 정보 반환 (`EntityModel`).

### 👥 사용자 (User)
- `GET /users/{username}`: 특정 사용자에 대한 정보 조회를 담당. (인증 필요)

### ⭐ 평점 및 리뷰 (Ratings)
- `GET /ratings/search`: 특정 사용자와 특정 시리즈(`username`, `seriesId`)를 조합해 특정 평점을 단건 조회합니다.
- `GET /ratings/list`: 시스템 내 등록된 모든 평점의 페이징 목록 조회를 제공합니다.
- `GET /ratings/user/{username}`: 특정 유저가 남긴 모든 평점을 조회합니다.
- `GET /ratings/series/{seriesId}`: 특정 시리즈에 등록된 모든 평점을 조회합니다.

#### **(인증 필수)**
해당 요청들은 `SecurityContext`에 저장된 현재 사용자의 인증 정보(`UserDetails`)를 기반으로 안전하게 수행됩니다.
- `POST /ratings`: 인증된 사용자의 이름으로 특정 시리즈에 새로운 평점 정보를 등록합니다.
- `PUT /ratings/{seriesId}`: 인증된 사용자가 본인이 남긴 평점 정보를 수정합니다.
- `DELETE /ratings/{seriesId}`: 인증된 사용자가 본인이 등록한 평점을 삭제하여 리뷰 권한을 회수합니다.


### 🖥️ Swagger
- `GET /swagger-ui/index.html`: Swagger를 통해서 각 API의 동작을 실현해볼 수 있습니다.

---

## TODO
- Bean Validation 적용하여 클라이언트 측에 부적합한 입력에 대한 에러 발생시키기
- Application Profile 분리하여 테스트 환경과 운영환경 분리
