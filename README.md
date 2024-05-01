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
