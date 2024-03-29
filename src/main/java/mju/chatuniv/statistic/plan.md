## word의 frequency를 이용하는 것을 전제
1. 실검에 필요한 건 무엇?
    - 단어의 id
    - 단어

## 현재 문제
    - frequency가 누적이됨 즉 테이블을 스케쥴링으로 frequency를 정리해줘야함
    - 테이블 하나 만들자 Statistic(id, word_id, frequency)
    - 캐싱을 해주고 나서 Statistic의 데이터를 모두 제거
    - 1시간동안 다시 수집
    - 반복

### 문제 해결 방법

### ver1 캐싱 (실검의 변화는 시간당)
    - word를 스케쥴링으로 1시간에 한 번씩 테이블 스캔해서 어플리케이션 레벨에서 캐싱해준다.
    - 조회시 캐싱을 준다
      --> 단점 (스프링 서버가 다운되면? 실검 다 날아감)

### ver2 Redis (추후 예정)
    - 마찬가지로 1시간에 한 번씩 테이블 스캔해서 레디스에 캐싱해준다.
    - 조회시 레디스를 통해 빠른 조회를 해준다.
      --> 장점 (트래픽으로 스프링 서버가 다운이 돼도 레디스에 남아있기 때문에 날아가지 않는다)


---

### word의 역할
- 실검에서 단어 클릭시 채팅방 리다이렉트 (문제 있음)
- 현재 역할로는 `단어 + 빈도` -> 통계에 사용하려고 했음


### 현재 문제를 해결하는 방법?
- word의 modifiedAt을 이용해보기
  - 수정이 최근 1시간 이내라면 실검 반영 대상에 적용하고 `frequency == 0` 시킨다.
  - 만약 누적값이 필요하다면 `totalFrequency` Column을 생성
  - 쿼리 한 번으로 실검 가능

### 또 다른 문제점
- 동시성 문제 존재
  - 만약 n명이 같은 단어를 입력할 때 += 1씩 되어야 하는데 Race Condition 발생할 수 있음 
