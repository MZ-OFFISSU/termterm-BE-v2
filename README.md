## termterm v2 - auth server

### 10. 2024/02/12
- Comment 신고 접수 API

-- TODAY ISSUE
  - JWT 토큰 자체가 서명된 증명된 토큰이기 때문에, API 마다 다시 DB 를 통해 증명할 필요가 없다.
    - 따라서 Service 클래스 내 메소드에서 member 를 검증하는 로직을 제외하였다. 
  - 폴더 생성 Service 로직에서, Member 엔티티에서 폴더를 Convert 해서 저장하고 있기 때문에 JPQL 의 DTO Select 가 제대로 동작하지 않았다.
    - Member Entity 에서 Folder 의 개수를 찾는 것이 아닌, Folder 테이블에서 memberId 를 COUNT 하는 방식으로 SQL 쿼리를 굉장히 단순화 하였다.  

### 9. 2024/02/11
- Comment 도메인 생성
- Report 도메인 생성
- 나만의 용어 설명(Comment) 등록 API
- Comment 좋아요 API
- Comment 좋아요 취소 API

`TODO : 예외 메시지에 정확한 에러 변수를 String format 으로 삽입하기`


### 8. 2024/02/10
- 폴더 상세 정보 보기 API
- 내 폴더 리스트 조회 API
  - __프론트님들께 물어볼 것 : 폴더가 존재하지 않았을 때, 로직을 어떻게 처리하셨는지? 응답 메시지에 따라? 혹은 그냥 data 가 비어있으면?__
  - v1 에서는 존재하지 않을 시 null 을 리턴해주고 있지만, v2 에서는 그냥 빈 리스트를 리턴하고 있다. 
- 폴더 관련 정보 (모달) API
- 아카이브한 용어 중 10개 랜덤으로 추출 API
- 폴더에 특정 단어 포함 여부 API

### 7. 2024/02/09
- 용어 아카이브 API
- 폴더 삭제 API
- 용어 아카이브 해제 API

- `FolderService.archiveTerm` 에서, stream 을 통해 저장한 Folder 객체들은 Persistence 에 저장되지 않은 객체들이었다.
  - 일단 Folder 를 불러오면 무조건 그 용어를 추가하고, 예외 상황에 throw Exception 을 하면, 롤백이 되므로, map 함수 내부에서 term 을 추가해주어도 괜찮았다. 
  - 처음에 여러개의 폴더에 저장하려고 헀을 때, TermBookmark 의 folderCnt 가 무조건 1로 저장되는 버그 수정
    - 어떻게 이런 버그를 그동안 방치해 놓았을 수 있지? 이래서 혼자 하면 안돼... 페어 프로그래밍, 코드리뷰가 절실하다.
- TermBookmark 테이블에서 STATUS 컬럼을 삭제했다. 
  - __이제 bookmark status 가 무조건 NO 일 것이다. null 로 응답되는 경우는 없다. 프론트 개발자님들께 알려드리기.__


### 6. 2024/02/08
- 기존에는 Curation 과 Term 이 다대다 관계로 연결되어 데이터베이스를 많이 차지하고 있었는데,.. 큐레이션 단에 스트링으로 convert 해서 엮을까 생각중
- 폴더 생성, 정보 수정 API
- 용어에 큐레이션을 저장하는 로직에서, findById() 대신 getReferenceById() 를 이용하여 쿼리 발생을 2회 줄였다. 
  - 자세한 내용은 블로그 글 참고


### 5. 2024/02/07
-- 인증서버와, 저쪽 서버를 분리하면, 엔티티에 변경사항이 생길 경우, 두 군데 다 분리 해주어야 한다.
  - 이럴 떄 발생할 수 있는 문제를 해결하기 위해 사용하는 게 멀티모듈인데, 왜 굳이 돌아가나...
  - 일단 먼저 모놀리식으로 구현한 다음, 멀티모듈로 개선해나가자. 더 나아가 마이크로서비스까지
-- 그래서 일단 모놀리식하게 하기 위해, 패키지명 auth 없애기 위해 레포 이사를 했다.


- [ ] 슬랙 연결

- 회원 탈퇴 테스트 코드 작성
  - __회원 탈퇴 API 를 기존 GET 에서 PUT 으로 바꾸었다. REST 규칙에 이것이 맞는 것 같다.__

- TODAY ISSUE
  - Apple 로그인을 위해, 나는 redirect url 변경이 필요한데, 한 번에 한 번만 등록이 되는 것 같아서 나중에 새벽에 테스트 해보아야 할 것 같다. 
    - JPQL 에서, DTO 객체로 SELECT 를 하는데, 내 ResponseDto 들을 전부 Inner Class 라서, 이를 불러오지 못하는 문제가 발생했다.
    - `SELECT new OuterClass.InnerClass()` 를 , `SELECT new OuterClass$InnerClass()` 로 하면 정상적으로 컴파일된다.
    - 그러나 인텔리제이 IDE 에서는 이를 오류로 인식하고 빨갛게 표시하고 있다. 무시하고 어플리케이션을 실행하면 잘 동작한다.


### 4. 2024/02/06

- Member 엔티티 job, domain 10자 이내로 수정
- 사용자 정보 수정  - 유효성 검사 테스트 추가
- 사용자 관심사 카테고리 수정 API
- 사용자 프로필 사진 등록, 삭제 API
- __DB 에 사용자의 프로필 이미지 주소 동기화 API 의 HTTP METHOD 를 변경하였다 (v1 과 비교하여)__
  - REST 규칙에 GET 보다는 PUT 이 맞다고 판단 하였음
  - `GET /member/info/profile-image/sync` -> `PUT /member/info/profile-image/sync`
- dev 용 S3 버킷을 새로 만들었다. `버킷 명 : XXXXX-dev`
- 닉네임 중복 체크 API
- 회원 탈퇴 API


- TODAY ISSUE
  ```java
    public class MemberCategoriesUpdateRequestDto {
        List<String> categories;
    }
  ```
  - 유저 관심사 카테고리 업데이트 API 요청 DTO 를 만들면서, 문득, `List<String>` 은 유효성 검사를 어떻게 하지? 라는 생각이 들었다.
  ```java
    public static class MemberCategoriesUpdateRequestDto { 
        List<@Pattern(regexp = "^(?i)(IT|BUSINESS|MARKETING|DEVELOPMENT|PM|DESIGN)$") String> categories;
    }
  ```
  - 복잡하게 할 것 없이, 문자열 검사는 이렇게 하면 되었다. 
  - 그러나, List 의 크기가 최대 4개로 제한되는 조건을 확인하고 싶었다. 이럴 때는 어떻게 할까?
    - 리스트의 길이가 1이상 4이하 임을 검사하는 `@ListMaxSize4Constraint` 어노테이션을 만들어서 적용시켜주었다.  

~~`TODO : 오늘 구현한 API 들 테스트코드 작성`~~


### 3. 2024/02/05
- [x] v1의 member API Migration
- [x] member API 테스트 코드
- [x] GET `/v2/s/member/info`
- [x] PUT `/v2/s/member/info`


- 기존에는 Category 테이블이 따로 있고, member_category 테이블에 연관지어, 유저 한명당 최대 4개의 row를 차지했다.
  - 너무 비효율적인 데이터 저장 방법이라, member 테이블에 그냥 문자열로 저장을 해 놓으려 한다. ex. [BUSINESS, IT, DESIGN]
  - 이를 위해, Entity와 DB 사이에서 속성의 변환을 담당하는 AttributeConverter 를 활용하여 배열을 통째로 문자열로 변환해 저장한다.
- DummyDebInit.java 로, dev 프로필에서 애플리케이션 실행 시 member 객체를 새로 save 한다.


- TODAY ISSUE
  - 테스트 코드에서는 성공했었으나, 실제 코드에서는 제대로 동작하지 않았던 JwtProcess.
    - `JwtVO` 의 secret 값을 제대로 넘겨받지 못했다. 
    - 테스트 코드에서는 `@TestConfiguration` 으로 잘 생성해 주었어서 잘 되었던 것
    - ~~JwtProcess 는 JwtVO 값만 필요하고, JwtAuthorizationFilter 에서만 사용하고 있으니, SecurityConfig 에서의 JwtProcess 필드를 없앴다.~~
    - ~~JwtAuthorizationFilter 에 JwtVO 만 생성자 파라미터로 넘겨주고, 생성자에서 직접 JwtProcess 객체를 생성했다.~~
    - `JwtProcess` 에 `@NoArgsConstructor` 를 없앴더니, JwtVO 빈이 잘 주입되었다. 
  - Test 코드에서 `@WithUserDetails` 를 사용할 때, 나는 username 을 memberId 로 지정해 두었기 때문에, `@BeforeEach` 로 계속해서 새로운 Member 객체를 save 면, id 가 1로 일관되지 않았다. 
    - 검색해본 결과, `@BeforeEachCallback` 을 사용하여 `@BeforeEach` 전에 truncate 하는 로직을 추가하면 된다는 것을 알았다. 
    - truncate 이후, ALTER ID RESTART 구문을 추가하여 autoincrement 가 계속 되는 현상을 방지하였다.

`TODO : Actuator & Grafana`

### 2. 2024/02/04
- [x] 로그인 요청 validation 검증
- [x] 로그인 테스트 코드 작성
- [x] Refresh-Token 구현 
- [x] 토큰 예외 처리 
- [x] JwtVO 환경변수 분리
- [x] POST `/v2/auth/**`


- TODAY ISSUE
  - `JwtProcess` 에서, 토큰 만료시 예외 처리 (`throw CustomApiException`) 해 주었으나, 실패
    - `ExceptionHandler` 는 `RestControllerAdvice` 인데, 이는 필터 이후에 동작하므로, 필터단에서 jwt 토큰을 검증하고 `CustomApiException` 을 던져버리면, 핸들러가 받지 못한다.
    - 그러므로 Filter 에서 발생하는 예외를 핸들링하려면, 예외 발생이 예상되는 Filter 의 상위에 예외를 핸들링하는 필터를 만들어서 Filter Chain 에 추가해준다.
    - `JwtAuthorizationFilter` 에서 발생하는 예외를, `JwtValidationFilter` 에서 처리해준다. 이 때, 발생한 에러는 상위 필터로 넘어가기 때문에, `JwtValidationFilter` 를 `JwtAuthorizationFilter` 이전에 넣어두어야 한다. 
  - 환경변수 jwt 객체를 필터 단에서 사용하고 싶었다.
    - SecurityConfig 객체가 스프링 빈으로 등록되어 있으므로, 등록된 JwtVO 빈을 사용할 수 있었다.
    - JwtVO 빈을 주입받아, 필터에 생성자 파라미터로 넘겨주어 내부에서 jwt 관련 작업을 처리할 수 있었다.
  - ~~테스트 코드에서 JwtProcess 빈을 어떻게 사용할 것인가?~~
    - ~~`@TestConfiguration` 사용~~
    - 그냥 스프링 애플리케이션 자체에서 JwtProcess 를 제대로 생성하지 못했기 때문에 안됐던 것. 
    - 제대로 JwtVO 빈을 주입하도록 수정하였으니, @TestConfiguration 은 필요하지 않아 삭제하였다. - 240205

`TODO : 애플 로그인 구현 | Member Info API 도 가져와서 여기에 할지? | 배포는 어떻게....`
  
### 1. 2024/02/03
- v1 과 무엇이 바뀔것인가?
  - v1 에서는 단일 프로젝트에 모든 백엔드 로직이 다 들어있었다
  - Spring boot 2 에서 Spring boot 3 으로 업그레이드
- 새로운 Spring Security 코드 도입 
- 카카오, 구글 서버와 연결하여, 유저 회원가입, jwt access token 발급까지 완료
- googleService 에서, RestTemplate 이 Deprecated 되어서 WebClient 로 Migration
- member_tb 에서, password 는 socialId 를 encrypt 한 것. 

`TODO : kakaoService 도 WebClient 로 Migration 후 시간 성능 측정 비교`
