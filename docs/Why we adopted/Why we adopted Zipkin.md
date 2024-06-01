# Zipkin 을 채택한 이유

## Zipkin?
- 분산 추적 시스템으로, MSA 에서 요청이 어떻게 처리되는지 추적하는 데 사용된다.
- 서비스 간의 호출 관계를 시각화하고 성능 문제를 식별하는 데 매우 유용하다.
- 오픈소스 프로젝트로, 트레이스 데이터를 수집하고 분석하며, 이를 통해 시스템의 병목 현상을 파악하고 개선할 수 있도록 돕는다.

## 주요 특징
- 분산 추적: 서비스 간의 호출을 추적하여 각 요청이 어디에서 얼마나 시간이 소요되는지 분석할 수 있다.
- 데이터 수집 밎 집계: 각 서비스에서 발생하는 트레이스 데이터를 중앙에서 수집하고 집계하여 종합적인 시각을 제공한다.
- 시각화: 요청의 흐름을 시각화하여 직관적으로 문제를 파악할 수 있다.
- 경량화: 낮은 오버헤드로 시스템에 부하를 최소화하면서 효과적으로 작동한다.
- 확장성: 대규모 시스템에서도 효율적으로 동작할 수 있도록 설계되었다.

## 유사 도구들의 장단점
- Jaeger:
  - 장점: CNCF 프로젝트로 커뮤니티 지원이 활발하고, k8s 와의 통합이 용이하다.
  - 단점: 설정이 복잡할 수 있다.
- OpenTracing:
  - 장점: 벤더 중립적인 API 를 제공하여 다양한 트레이싱 도구와 호환성이 높다.
  - 단점: 직접적인 트레이싱 도구가 아니므로 별도의 구현이 필요하다.
- New Relic:
  - 장점: 강력한 대시보드와 다양한 모니터링 기능을 제공한다.
  - 단점: 상용 도구로 비용이 발생하며, 커스터마이징이 제한적일 수 있다.
- Datadog:
  - 장점: 통합 모니터링 솔루션으로 다양한 메트릭을 한 곳에서 관리할 수 있다.
  - 단점: 비용이 높고, 일부 고급 기능은 추가 요금이 발생할 수 있다.

## Zipkin 을 다룰 시 주의사항
- 데이터 보안: 트레이스 데이터에 민감한 정보가 포함될 수 있으므로, 보안에 주의해야 한다.
- 성능 오버헤드: 경량화되었지만, 대규모 시스템에서는 여전히 성능에 영향을 미칠 수 있으므로 적절한 모니터링과 조정이 필요하다.
- 데이터 저장소 관리: 트레이스 데이터의 양이 많아질 수 있으므로, 저장소 관리와 데이터 보존 정책을 명확히 설정해야 한다.
- 업데이트 및 유지보수: 오픈소스 도구인 만큼 정기적인 업데이트와 유지보수가 필요하며, 커뮤니티의 최신 정보를 지속적으로 확인해야 한다.