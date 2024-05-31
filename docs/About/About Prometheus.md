# Prometheus 에 대해

## Prometheus 아키텍처 설명
Prometheus 의 주요 아키텍처에 대해 설명하겠다.
![아키텍처](../prometheus%20architecture.png)

- Prometheus Server
  - Retrieval (데이터 수집)
    - Prometheus 서버는 주기적으로 타겟에서 메트릭 데이터를 수집한다.
    - 타겟은 애플리케이션, 시스템, 서비스 등이 될 수 있으며, Prometheus 는 HTTP 를 통해 이 데이터를 긁어온다(pull)
    - 데이터 수집 주기는 설정에 따라 다르게 조정될 수 있다.
  - TSDB (시계열 데이터베이스)
    - 수집된 메트릭 데이터는 시계열 데이터베이스에 저장된다.
    - TSDB 는 고도로 최적화된 데이터베이스로, 메트릭 데이터를 효율적으로 저장하고 조회할 수 있도록 설계되었다.
  - HTTP Server
    - Prometheus 서버는 HTTP 를 통해 데이터를 외부에 노출한다.
    - 이를 통해 사용자는 PromQL 을 사용하여 데이터를 조회하거나, 다른 시스템과 연동할 수 있다.
- Service Discovery
  - Prometheus 는 서비스 디스커버리 기능을 통해 모니터링할 타겟을 자동으로 발견한다.
  - 이는 K8S, Consul, Zookeeper 등 다양한 서비스 디스커버리 메커니즘과 통합될 수 있다.
  - 또한, 파일 기반 서비스 디스커버리(file_sd)도 지원하여, 정적 파일을 통해 타겟을 정의할 수 있다.
- Pushgateway
  - Pushgateway 는 짧은 수명을 가지는 작업(short-lived jobs)이나 배치 작업(batch jobs)의 메트릭을 수집하는 역할을 한다.
  - 이러한 작업은 자체적으로 Prometheus 서버에 데이터를 푸시(push)한다.
  - Pushgateway 는 이 데이터를 수집하여 Prometheus 서버가 주기적으로 풀(pull)할 수 있도록 한다.
- Prometheus Targets (모니터링 타겟)
  - Prometheus 는 다양한 타겟에서 메트릭 데이터를 노출한다.
  - 각 타겟은 Prometheus 서버의 요청에 응답하여 자신의 상태와 성능을 나타내는 메트릭을 노출한다.
  - 이러한 메트릭은 Prometheus 의 클라이언트 라이브러리를 사용하여 직접 생성하거나, 익스포터(exporter)를 통해 수집될 수 있다.
- Alertmanager
  - Alertmanager 는 Prometheus 서버에서 발생한 경고를 받아 사용자에게 알리는 역할을 한다.
  - Alertmanager 는 다양한 경고 조건을 설정할 수 있으며, 경고를 그룹화하고 라우팅할 수 있다.
  - 경고 알림은 이메일, Slack, PagerDuty 등 다양한 매체를 통해 전송될 수 있다.
- Data Visualization and Export
  - Prometheus Web UI
    - Prometheus 서버는 내장된 웹 UI를 통해 데이터 조회와 시각화를 지원한다.
    - 사용자는 PromQL 을 사용하여 쿼리를 작성하고, 결과를 시각화할 수 있다.
  - Grafana
    - Grafana 는 Prometheus 데이터를 시각화하는 데 널리 사용되는 도구다.
    - Grafana 는 아름답고 직관적인 대시보드를 제공하여, 다양한 메트릭 데이터를 효과적으로 시각화할 수 있다.
  - API Clients
    - Prometheus 는 HTTP API 를 통해 데이터를 외부 시스템과 연동할 수 있다.
    - 이를 통해 다른 애플리케이션이나 도구에서 Prometheus 데이터를 사용할 수 있다.
- HDD/SSD
  - Prometheus 서버는 메트릭 데이터를 HDD 나 SSD 와 같은 물리적 저장소에 저장한다.
  - 시계열 데이터베이스는 고성능 저장소에 최적화되어 있으며, 효율적인 데이터 접근과 검색을 지원한다.

   
