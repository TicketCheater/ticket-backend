# Spring Cloud Gateway 를 채택한 이유

## Spring Cloud Gateway?
- Spring 기반의 MSA 에서 API 게이트웨이를 구축하는 데 사용되는 강력한 도구이다.

## 주요 특징
- 리액티브 프로그래밍 지원: Reactor 프로젝트를 기반으로 하여 리액티브 프로그래밍 모델을 지원한다. 이것은 비동기 및 논블로킹 처리를 통해 높은 성능과 확장성을 제공한다.
- 필터 체인: Gateway 에 전달되는 요청 및 응답을 조작하고 로깅하는 등의 작업을 수행하기 위해 필터 체인을 사용할 수 있다. 이것은 요청 및 응답 처리 과정을 세분화하고 커스터마이징하는 데 유용하다.
- 로드 밸런싱: 여러 인스턴스로 확장 가능한 애플리케이션을 지원하기 위해 로드 밸런싱을 제공한다. 이것은 트래픽을 여러 서버로 분산시켜 확장성을 향상시킨다.
- 동적 라우팅: Gateway 는 요청의 특성에 따라 동적으로 다른 서비스로 라우팅할 수 있다. 이것은 MSA 아키텍처에서 유연한 라우팅을 가능하게 한다.

## 다른 API Gateway 서비스를 채택하지 않은 이유
- Spring 기반 MSA 아키텍처, 커스터마이징 가능성, 리액티브 프로그래밍 지원 등의 이유로 다른 API Gateway 서비스를 채택하지 않기로 했다.

## 향후 AWS API Gateway 로 이전 가능성?
- AWS API Gateway 가 Spring Cloud Gateway 에 비해서 가지고 있는 장점은 다음과 같다.
  - 관리형 서비스: AWS API Gateway 는 완전히 관리되는 서비스로, 우리가 서버 관리와 같은 인프라 관련 작업을 최소화할 수 있다. 이것은 우리의 운영 부담을 줄여주고 개발에 집중할 수 있도록 도와준다.
  - 서버리스 아키텍처 지원: AWS Lambda 와의 통합을 통해 우리는 백엔드 로직을 서버리스로 실행하고 비용을 절감할 수 있다.
  - 확장성과 성능: AWS 는 전 세계적으로 확장 가능한 인프라를 제공한다. 이것은 우리가 언제든지 트래픽 증가에 대응하여 우리의 애플리케이션을 확장할 수 있다는 것을 의미한다.
  - 보안 및 인증 기능: 다양한 인증 및 보안 메커니즘을 제공하여 우리의 API 를 보호할 수 있다. AWS IAM 을 통한 인증, API 키 및 OAuth 를 통한 인가 등 다양한 옵션이 있다.
  - 통합 및 모니터링: 다양한 AWS 서비스와의 강력한 통합 기능을 제공한다. Amazon CloudWatch 를 통해 우리의 API 를 모니터링하고 성능을 분석할 수 있다.
- 결론적으로, AWS 에 배포하면서 장단점을 파악하면서 AWS API Gateway 로 이전할 수도 있다.
