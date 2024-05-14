# TestContainer 를 채택한 이유

## TestContainer?
- 개발 및 테스트를 위한 컨테이너화된 애플리케이션 실행 도구이다. 테스트 환경에서 외부 의존성을 관리하고 애플리케이션의 격리된 실행을 지원한다.
- 흔히 Docker 컨테이너를 기반으로 작동하며, 테스트 도구에서 필요한 서비스를 실행하여 테스트를 진행할 수 있도록 돕는다.

## 주요 특징
- 다양한 컨테이너 지원: 필요에 따라 DB, 메시지 브로커, 웹 서버 등을 테스트 환경에서 실행할 수 있다.
- 테스트 환경의 격리: 각 테스트는 독립적인 환경에서 실행되며, 외부 의존성에 대한 격리를 제공하여 테스트 간의 영향을 방지한다.
- 간편한 설정 및 사용: 간단한 설정으로 테스트 코드에서 사용할 수 있으며, 다양한 언어와 프레임워크에서 편리하게 통합하여 사용할 수 있다.
- 실제 환경 모방: 여러 컨테이너를 조합하여 복잡한 시나리오를 테스트할 수 있으며, 이는 실제 prod 환경을 모방하는 데 도움이 된다.
- 지속적인 업데이트: TestContainer 는 지속적으로 업데이트되어 최신 기술과 요구 사항에 대응하고, 사용자들의 피드백을 반영하여 개선된다.

## Embedded Redis 를 채택하지 않은 이유
- 최신성 및 유지보수: Embedded Redis 의 마지막 커밋은 2020년 6월이다. 이는 해당 라이브러리가 현재의 요구 사항에 대응하지 못할 수 있다는 것을 시사한다.
- 그 외에도 테스트 환경의 격리, 실제 환경 모방 등 TestContainer 가 Embedded Redis 에 비해 장점이 많기 때문에 Embedded Redis 를 채택하지 않기로 했다.

## TestContainer 단점
- 리소스 사용량: 컨테이너를 실행하기 위한 추가적인 리소스를 필요로 한다. 따라서 테스트 시에 시스템 자원을 더 많이 사용할 수 있으며, 이로 인해 테스트 속도가 느려질 수 있다.
- 컨테이너 실행시간: 컨테이너를 실행하는 데 걸리는 시간은 테스트의 실행 속도에 영향을 줄 수 있다. 특히 여러 컨테이너를 실행해야 하는 경우, 이러한 시간이 더욱 증가할 수 있다.
- 하지만 장점이 이러한 단점을 상쇄할 만큼 강력하다.