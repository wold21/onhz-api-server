# On-Hz Music Review Platform

## environment
- java 17
- spring boot 3.4.2
- gradle 8.xx

> 개발 시 spring profile 설정 필요

## Branch rule
- main : 운영 소스코드
- develop : 스테이징 소스코드
- feature : 기능별 소스코드
- {이름} : 개인별 소스코드

## Commit rule
- feat : 새로운 기능 추가
- fix : 버그 수정
- docs : 문서 수정
- style : 코드 포맷팅, 세미콜론 누락, 코드 변경이 없는 경우
- refactor : 코드 리팩토링
- test : 테스트 코드, 리팩토링 테스트 코드 추가
- temp : 임시 저장
- etc : 기타 수정사항

### 작성 예시
```Plain Text
feat : 로그인 기능 추가
- jwt access token 발급 로직 추가
- jwt refresh token 발급 로직 추가
```