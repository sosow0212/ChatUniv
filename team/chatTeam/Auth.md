# Auth

## 23.06.11

- [x] JWT Token Login 구현
- [x] 유효하지 않은 토큰 혹은 로그인의 경우 예외 발생 구현
- [x] Filter 이용해서 유효하지 않은 인증이라면 접근을 막음, Auth 기능을 제외한 나머지 요청은 토큰이 필요하도록 구현
- [x] ArgumentResolver 이용해서 토큰 헤더를 컨트롤러에서 Member 객체로 바인딩하는 기능 추가
- [x] 인수테스트 뼈대 코드 완성
- [x] Auth API 테스트
  - [x] API E2E 테스트
  - [x] API 인수 테스트
  - [x] Service 통합 테스트
  - [x] Service 단위 테스트 (예외 발생 검증)

