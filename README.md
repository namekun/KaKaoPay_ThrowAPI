# 카카오페이 뿌리기 기능 구현하기

## 요구사항

* 뿌리기, 받기, 조회 기능을 수행하는 Rest API를 구현합니다.
  * 요청한 사용자의 식별값은 숫자 형태이며 `X-USER-ID` 라는 HTTP Header 로 전달
  * 요청한 사용자가 속한 대화방의 식별값은 문자 형태이며, `X-ROOM-ID` 라는 HTTP Header로 전달
  * 모든 사용자는 뿌리기에 충분한 잔액을 보유하고 있다는 가정하에 별도로 잔액은 체크하지 않음
* 작성한 애플리케이션이 다수의 서버에 다수의 인스턴스로 작동한다고해도, 기능에 문제가 없도록 설계해야 한다.
* 각 기능 및 제약사항에 대한 단위 테스트를 반드시 작성한다.

## 상세 구현 요건 및 제약사항

### 1. 뿌리기 API

* 다음 조건을 만족하는 뿌리기 API

  * 뿌릴 금액, 뿌릴 인원을 요청 값으로 받는다.
  * 뿌리기 요청건에 대한 고유 token을 발급하고 응답값으로 내려준다.

  * 뿌릴 금액을 인원수에 맞게 분배해서 저장(분배로직은 자유롭게 구현)
  * token은 3자리 문자열로 구성되며, 예측이 불가능해야한다

### 2. 받기 API

* 다음 조건을 만족하는 받기 API
  * 뿌리기 시 발급된 token을 요청값으로 받는다.
  * token에 해당하는 뿌리기 건 중 아직 누구에게도 할당되지 않은 분배건 하나를 API 를 호출한 사용자에게 할당하고, 그 금액을 응답값으로 내려준다.
  * 뿌리기 당 한 사용자는 한번만 받을 수 있다.
  * 자신이 뿌리기 한 건은 자신이 받을 수 없다.
  * 뿌리기가 호출된 대화방과 동일한 대화방에 속한 사용자만이 받을 수 있다.
  * 뿌린 건은 10분간만 유효하며, 뿌린지 10분이 지난 요청에 대해서는 받기 실패응답이 내려가야한다.

### 3. 조회 API

* 다음 조건을 만족하는 조회 API
  * 뿌리기 시 발급된 token 을 요청 값으로 받는다.
  * token 에 해당하는 뿌리기 건의 현재 상태를 응답값으로 내려줍니다. 현재 상태는 다음의 정보를 포함합니다.
  * 뿌린 시각, 뿌린 금액, 받기 완료된 금액, 받기 완료된 정보([받은 금액, 받은 사용자 아이디]리스트)
  * 뿌린 사람 자신만 조회를 할 수 있습니다. 다른 사람의 뿌리기 건이나, 유효하지않은 token에 대해서는 조회 실패 응답이 내려가야합니다.
  * 뿌린 건에 대한 조회는 7일동안 할 수 있습니다.

## DB Table 구조

![](/imageForReadMe/ERD.png)

* 테이블 구조는 다음과 같이 두개로 나누었다.
  * 뿌리기 정보를 담을 throw_info table
  * 받기 관련 정보를 담을 receive_info table

*throw_info DDL*

```SQL
create table throw_info
(
    token        varchar(3)   not null
        primary key,
    room_id      varchar(255) not null,
    user_id      bigint       not null,
    reg_dttm     datetime     not null,
    amount_money bigint       not null,
    people_cnt   bigint       not null
);

create index throwInfo_token_room_id_index
    on throw_info (token, room_id);
```

*receive_info DDL*

```sql
create table receive_info
(
    token        varchar(3) not null,
    object_id    bigint auto_increment
        primary key,
    user_id      bigint     null,
    receive_dttm datetime   null,
    money        bigint     not null,
    constraint receive_info_throwinfo_token_fk
        foreign key (token) references throw_info (token)
);
```



## API

### 1. 뿌리기 기능

* Request

  * Url :`/api/throwing`

  * Http Method : POST

  * Header

    * X-USER-ID : 유저 아이디
    * X-ROOM-ID : 방 번호

  * Request

    * amount : 뿌릴 금액

    * count : 뿌릴 인원 수

      ```json
      {
          "amount": 10000,
          "count": 5
      }
      ```

* Response

    ```json
    {
        "responseCode": "200",
        "responseDescription": "정상처리",
        "value": "FYc"
    }
    ```

    * value 에 토큰값을 리턴할 수 있도록 하였습니다.
    * Type
      * value : String

### 2. 받기 기능

* Request

  * URL : `/api/receiving/[token]`

  * Http Method : PATCH

  * Header

    * X-USER-ID : 유저 아이디
    * X-ROOM-ID : 방 번호
* Response

    ```json
    {
        "responseCode": "200",
        "responseDescription": "정상처리",
        "value": 1091
    }
    ```

    * value 에 분배된 금액을 리턴 할 수 있도록 하였습니다.
    * Type
      * value : Long

### 3. 조회기능

* Request

  * URL : /api/receiving/[token]
  * Http Method : GET
  * Header
    * X-USER-ID : 유저 아이디
    * X-ROOM-ID : 방 번호

* Response

  ```json
  {
      "responseCode": "200",
      "responseDescription": "정상처리",
      "value": {
          "throwTime": "2020-11-22T21:11:35",
          "moneyAmount": 10000,
          "receivedMoneyAmount": 1940,
          "receiverInfoList": [
              {
                  "receivedMoney": 1091,
                  "userId": 456
              },
              {
                  "receivedMoney": 849,
                  "userId": 789
              }
          ]
      }
  }
  ```
  
  * Type
    * throwTime : LocalDateTime
    * moneyAmount : Long
    * receivedMoneyAmount : Long 
    * receiverInfoList
      * receiveMoney : Long
      * userId : Long

## ResponseCode

* `200` : 정상 응답 
* `101` : 이미 종료된 뿌리기
* `102` : 뿌린 사용자와 같은 사용자
* `103` : 시간 초과
* `104` : 잘못된 방 번호
* `105` : 이미 주운 사용자
* `106` : 뿌린 사용자와 다른 사용자
* `107` : 유효하지 않은 토큰값



## TEST

![](/imageForReadMe/TEST.png)

### 1. 요청 Header 관련 테스트

* ROOM-ID 가 누락된 경우 - `ThrowRequestTest.nonRoomId()`
* UESR-ID 가 누락된 경우 - `ThrowRequestTest.nonUserId()`

### 2. 뿌리기 기능 관련 테스트

* 정상적으로 잘 뿌려지는지 확인 - `ThrowingTest.isSave()`

### 3. 받기 기능 관련 테스트

* 받기 기능 정상 작동 확인 - `ReceivingTest.isSave()`
* 토큰 값이 다른 경우 - `ReceivingTest.isWrongToken()`
* 이미 모든 사람이 받은 경우 - `ReceivingTest.isEndThrowing()`
* 뿌리기가 끝난(10분이 지난) 경우 - `ReceivingTest.isTimeOver()`
* 이미 주운 사용자가 또 줍기를 눌렀을 경우 - `ReceivingTest.isAlreadyTaken()`
* 다른 방의 사용자가 주으려고 하는 경우 - `ReceivingTest.isWrongRoom()`
* 뿌린 사용자가 주으려는 경우 - `isSameUser()`

### 4. 조회 기능 테스트

* 조회 기능 정상 작동확인 - `RetrievingTest().isRetrieve()`
* 뿌린 사람과 다른 사람이 조회 요청 - `RetrievingTest().isCorrectUser()`
* 조회되는 토큰값 없음 - `RetrievingTest().nonExistToken()`
* 7일이 지난 뿌리기 조회 - `RetrievingTest().after7Days()`

