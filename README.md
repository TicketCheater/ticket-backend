# ticket-backend

## 목표
- 서비스 간 독립적인 환경을 구성하기 위해 MSA (Micro Service Architecture) 를 구축한다
- 대기열 서비스는 Spring WebFlux 를 사용하고, 회원 관리, 티켓 관리 등 그 외는 Spring Mvc 를 사용한다
- 마이크로서비스 간 통신이 필요한 경우 Feign Client 도입을 고려한다
- API Gateway 를 사용하여 인증 & 인가 절차를 사전에 처리한다
- Zipkin, Prometheus, Grafana 를 도입하여 서비스를 분산 추적 및 모니터링한다
- 프로젝트가 시작되기 전에 반드시 ERD 를 README 파일에 업로드한다
- PR 이 진행될 때마다 반드시 아래 사항들을 실천한다
  - Test Code 를 작성하여 개발 비용을 낮춘다
  - mermaid chart 를 작성하여 변경점을 기록한다
  - PR 을 진행한 이유, 기술을 선택한 이유에 대해서 markdown 파일을 작성한다

## ERD

```mermaid
erDiagram
    teams {
        bigint id PK "팀 ID"
        string name "팀 이름"
    }
    games {
        bigint id PK "게임 ID"
        string category "카테고리"
        string title "제목"
        bigint home_id FK "홈 팀"
        bigint away_id FK "상대 팀"
        string place "장소"
        timestamp started_at "시작 시간"
        timestamp created_at "생성 시간"
        timestamp updated_at "수정 시간"
        timestamp removed_at "삭제 시간"
    }
    tickets {
        bigint id PK "티켓 ID"
        bigint game_id FK "게임 ID"
        bigint member_id FK "회원 ID"
        string grade "좌석 등급"
        int price "티켓 가격"
        timestamp created_at "생성 시간"
        timestamp updated_at "수정 시간"
        timestamp removed_at "삭제 시간"
    }
    members {
        bigint id PK "회원 ID"
        string name "회원 이름"
        string password "비밀번호"
        string email "이메일"
        string nickname "닉네임"
        string role "회원 등급"
        timestamp created_at "생성 시간"
        timestamp updated_at "수정 시간"
        timestamp removed_at "삭제 시간"
    }
    reviews {
        bigint id PK "리뷰 ID"
        bigint member_id FK "회원 ID"
        varchar(255) title "제목"
        varchar(65535) content "본문"
        timestamp created_at "생성 시간"
        timestamp updated_at "수정 시간"
        timestamp removed_at "삭제 시간"
    }
    
    teams ||--|{ games: "participates"
    games ||--|{ tickets: "has"
    members ||--o{ tickets : "books"
    members ||--|{ reviews : "writes"
```

## Architecture
![image](./docs/architecture-1.png)
