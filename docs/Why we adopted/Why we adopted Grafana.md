# Grafana 를 채택한 이유

## Grafana?
- Grafana 는 오픈소스 데이터 시각화 및 모니터링 도구다.
- 다양한 데이터 소스를 통합하여 시각적인 대시보드를 생성하고 관리할 수 있게 해준다.
- 특히 Prometheus 와 같은 모니터링 도구와 함께 사용되어, 수집된 메트릭 데이터를 시각화하고 분석하는 데 널리 사용된다.

## Grafana 주요 특징
- 다양한 데이터 소스 지원: Prometheus 를 포함한 여러 데이터 소스를 지원하여, 이기종 데이터베이스의 데이터를 통합하고 시각화할 수 있다.
- 강력한 시각화 도구: 다양한 시각화 유형(그래프, 테이블, 히트맵 등)을 제공하여, 데이터를 효과적으로 분석하고 이해할 수 있게 해준다.
- 유연한 대시보드: 사용자는 대시보드를 자유롭게 구성하고 커스터마이징할 수 있다. 드래그 앤 드롭 방식으로 패널을 배치하고, 여러 데이터를 결합하여 종합적인 인사이트를 얻을 수 있다.
- 경고 및 알림 기능: Grafana 는 데이터 소스에서 직접 알림을 설정할 수 있으며, 조건이 충족되면 즉시 알림을 보낼 수 있다.
- 플러그인 생태계: 다양한 플러그인을 통해 기능을 확장할 수 있으며, 커뮤니티와 공식 플러그인을 통해 지속적으로 새로운 기능을 추가할 수 있다.
- 사용자 관리: Grafana 는 사용자 및 권한 관리를 통해 다양한 사용자 그룹에 대한 접근 제어를 제공한다. 이를 통해 보안과 관리 효율성을 높일 수 있다.

## 유사 도구들의 장단점
- Kibana:
  - 장점: Elasticsearch 와의 통합이 뛰어나며, 로그 데이터 시각화에 강점이 있다.
  - 단점: 다른 데이터 소스 통합이 제한적이며, Elasticsearch 를 중심으로 한 사용 사례에 적합하다.
- Tableau:
  - 장점: 비즈니스 인텔리전스 도구로서 강력한 데이터 분석 및 시각화 기능을 제공한다.
  - 단점: 상용 소프트웨어로 비용이 많이 들며, 복잡한 설치 및 설정 과정이 필요하다.
- Power BI:
  - 장점: Microsoft 생태계와의 통합이 뛰어나며, Excel 사용자가 쉽게 사용할 수 있다.
  - 단점: 상용 소프트웨어로 라이선스 비용이 발생하며, 데이터 소스 통합이 Microsoft 중심이다.

## Grafana 를 다룰 시 주의사항
- 보안 설정: Grafana 는 웹 기반 도구이므로, 접근 제어 및 인증 설정을 통해 보안을 강화해야 한다. 특히 공용 네트워크에서 사용할 경우 HTTPS 설정과 강력한 비밀번호 정책이 필요하다.
- 데이터 소스 관리: 다양한 데이터 소스를 통합할 수 있지만, 각 데이터 소스의 성능과 안정성을 주기적으로 점검해야 한다. 데이터 소스 연결이 끊기거나 성능이 저하되면 대시보드가 정상적으로 동작하지 않을 수 있다.
- 자원 사용: Grafana 는 대시보드의 복잡도와 데이터 소스의 양에 따라 자원 사용량이 증가할 수 있다. 서버 자원을 모니터링하고, 필요한 경우 자원을 증설하여 성능을 유지해야 한다.
- 업데이트 및 유지보수: Grafana 는 지속적으로 업데이트되므로, 최신 버전을 사용하여 새로운 기능과 보안 패치를 적용해야 한다. 업데이트 전에 백업을 수행하고, 호환성을 확인하는 것이 좋다.
- 알림 설정: 알림 조건을 신중히 설정하여 불필요한 알림이 발생하지 않도록 해야 한다. 경고 알림이 빈번하면 사용자에게 불편을 줄 수 있다.
- 플러그인 관리: 플러그인은 강력한 기능을 제공하지만, 호환성 문제나 보안 취약점이 있을 수 있다. 필요한 플러그인만 설치하고, 정기적으로 업데이트를 확인해야 한다.
