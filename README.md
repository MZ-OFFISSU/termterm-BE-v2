## termterm v2 - auth server

### 16. 2024/02/21
- 오늘의 용어를 선정하는 로직에서, 4개의 용어를 무작위로 추출하기 위해 `ORDER BY RAND() LIMIT 4` 를 사용하고 있다.
  - 데이터의 개수가 적은 테이블에서 이 쿼리를 실행하는 것은 괜찮으나, 수많은 데이터가 있는 테이블에서 `ORDER BY RAND()` 는 DB 에 부하를 굉장히 많이 주는 작업이다. 
  - 이 역시 해결법이 있고, 이는 추후 수정하도록 하자. TODO
- 오늘의 용어 API 

---
### 15. 2024/02/20
- 아카이브한 Curation API
- Term 검색 응답에 BookmarkStatus 추가
  - 이전 버전에서는 Bookmark 가 null 로 나갈 때도 있었지만, 여기서는 무조건 NO 로 나간다. 프론트님들께 말씀드리기
- Curation 상세에서 bookmark 가 제대로 반영되지 않던 버그 수정
- Term 상세 API
- Term List API 
  - Page 로 구현을 했는데, 요청할 때마다 전체의 리스트를 불러와서 잘라내고 있다.
  - 그냥 그 자른 만큼만 DB 에서 불러오는 방법은 없을까?

-- TODAY ISSUE
  - 어제, `m.categories` 를 호출할 떄는 `List<ArrayList<CategoryEnum>>` 타입이 잘 반환되었었는데, 이는 `m.categories` 를 그대로 SELECT 하면 그렇게 된다.
    - `@Query("SELECT new TermDetailDto(t.id, t.name, t.description, t.categories, tb) " +`
      - 여기에서는 `List<CategoryEnum>` 타입으로 리턴이 된다.
  - Term 상세 API 에서, CommentDto 에 대해서 아래와 같은 에러가 발생했다.
    - `org.springframework.http.converter.HttpMessageConversionException: Type definition error: [simple type, class`
    - 원인 : Controller 에서 return 하여 넘긴 데이터를 Spring 내부적으로 JSON 형태로 변환하려 하였으나, 넘긴 객체의 필드 중에 getter 메소드가 없었고, 해당 필드의 접근제어자도 private 였기에, 해당 데이터의 필드 값을 읽어서 처리할 수가 없다.
    - 해결 : 필드들을 읽을 수 있게 @Getter 어노테이션을 붙어주었다.

~~`TODO : Comment 리턴 시 Comment Status 고려하여 포함 여부 결정하기`~~

---
### 14. 2024/02/19
- Curation 상세정보 API 
- Curation List - 카테고리 미지정 시 관심사 기반
- Curation List - 카테고리 지정 

-- TODAY ISSUE
  - CurationPaid 에서 Member 와의 OneToOne 관계를 끊고, CurationPaid 의 PK 값에 항상 MemberId 를 주입하는 방식으로 OneToOne 구현방식을 변경했다.
  - Member 는 `List<CategoryEnum>` 타입의 Categories 컬럼을 String 으로 Convert 하여 저장하고 있다. 
    - 여기서 다음 JPQL 을 실행하면, 어떤 결과를 넘겨 받을까?
      - `@Query("SELECT m.categories FROM Member m WHERE m.id = :memberId")`
    - `List<ArrayList<CategoryEnum>>` 타입을 리턴한다. 
    - 당연히 List 의 size 는 1이고, empty 예외 처리 이후 `get(0)` 을 해주어 `ArrayList<CategoryEnum>` 을 추출하여 행복 코딩하면 된다. 
  - ``` 
        sql.append("SELECT DISTINCT c.curation_id, c.title, c.cnt, c.description, c.thumbnail, cb.status ");
        sql.append("FROM curation c ");
        sql.append("LEFT JOIN curation_bookmark cb ");
        sql.append("ON cb.curation_id = c.curation_id AND cb.member_id = :memberId ");
        sql.append("WHERE c.categories LIKE CONCAT('%', :category, '%') ");
        sql.append("ORDER BY RAND() ");
    ``` 
    - DISTINCT 와 ORDER BY RAND() 는 함께 쓸 수 없다. 
    - ORDER BY RAND() 를 뺴고, 애플리케이션 내부 로직에서 shuffle 해주기로 했다. 
    - 큐레이션의 수가 많지 않기 때문에, 성능에는 영향을 주지 않을 것이라고 생각했다. 
  - 카테고리 별로 큐레이션 추출에서, SQL 쿼리를 LIKE 절로 작성을 했는데, PM 은 DEVELOPMENT 에도 있어서 원하지 않는 결과까지 추출되는 문제 발생 
    - PM 을 찾는 것이 아닌, "PM" 을 찾도록 SQL 쿼리를 변경했다.


---
### 13. 2024/02/18
- Curation 등록 API 요청서 유효성 검사 Test 코드 작성
- Curation 북마크 취소
- Curation 상세정보 API 제작이 너무 힘들다.
  - SQL 문도 복잡하고, 이를 내부 로직에서 처리하는 것도 복잡하다.

`TODO : 유저 최근 로그인 일시 저장`

---
### 12. 2024/02/15
- 폴더 상세페이지 API
- 추후 Term 데이터를 NoSQL 기반 DB 로 옮길 것을 고려하여, Term 에서 모든 연관관계를 제거하였습니다.
  - 기존에 연관되었던 테이블에는 전부 termId 라는 컬럼을 따로 만들었습니다.
  - Folder 테이블에 termId 들을 convert 하여 저장하길 정말 잘했다는 생각이 듭니다 
- Curation API 제작을 위해 폴더 구조 구성
- Curation 등록 (for ADMIN) API
- TermBookmark 테이블을 복합키로 변경하였음
- Curation Bookmark API

---
### 11. 2024/02/14

- TODAY ISSUE
  - 폴더 상세페이지 _ 하나씩 보기(/s/folder/detail/each/{folderId}) 구현 중에 마주친 문제...
    - 현재 Folder 는 Term 과 연관관계가 맺어져 있지 않고, terms 컬럼에 id 들만 String 으로 저장하고 있습니다.
    - 그런데 이 API 에서는, Folder 에 들어있는 Term 들을 불러오고, 이 Term 과 연관된 Comment 를 불러오고, Comment 와 연관된 CommentLike 도 불러와야 합니다.
    - Folder 와 Term 이 연관되어 있지 않기 때문에, 만약 Folder 에 Term 이 50개 들어있고 이를 매번 Term 테이블에서 select 해온다면, 총 50번의 쿼리가 발생하게 될 것입니다.
    - 충분히 쿼리문 1번으로 모두 가져올 수 있을거라고 생각이 들어 계속 검색해보고 공부하고 있는 중입니다.
    - 6시간 동안 찾아봤지만 해결할 방법을 못 찾았다. 다음에 시간 여유로울 때 해 보는 걸로 합시다.
    - Term 에 Comment 가 존재하지 않을 수도 있기 때문에, `JOIN FETCH t.comments` 가 아닌, `LEFT JOIN FETCH t.comments` 절을 써야합니다.
    - 쿼리 문 2번만에 API 응답을 구성하였습니다. 후기는 다음으로 ->  https://thisisjoos.tistory.com/627


---
### 10. 2024/02/12
- Comment 신고 접수 API

-- TODAY ISSUE
  - JWT 토큰 자체가 서명된 증명된 토큰이기 때문에, API 마다 다시 DB 를 통해 증명할 필요가 없다.
    - 따라서 Service 클래스 내 메소드에서 member 를 검증하는 로직을 제외하였다. 
  - 폴더 생성 Service 로직에서, Member 엔티티에서 폴더를 Convert 해서 저장하고 있기 때문에 JPQL 의 DTO Select 가 제대로 동작하지 않았다.
    - Member Entity 에서 Folder 의 개수를 찾는 것이 아닌, Folder 테이블에서 memberId 를 COUNT 하는 방식으로 SQL 쿼리를 굉장히 단순화 하였다.  

---
### 9. 2024/02/11
- Comment 도메인 생성
- Report 도메인 생성
- 나만의 용어 설명(Comment) 등록 API
- Comment 좋아요 API
- Comment 좋아요 취소 API

`TODO : 예외 메시지에 정확한 에러 변수를 String format 으로 삽입하기`


---
### 8. 2024/02/10
- 폴더 상세 정보 보기 API
- 내 폴더 리스트 조회 API
  - __프론트님들께 물어볼 것 : 폴더가 존재하지 않았을 때, 로직을 어떻게 처리하셨는지? 응답 메시지에 따라? 혹은 그냥 data 가 비어있으면?__
  - v1 에서는 존재하지 않을 시 null 을 리턴해주고 있지만, v2 에서는 그냥 빈 리스트를 리턴하고 있다. 
- 폴더 관련 정보 (모달) API
- 아카이브한 용어 중 10개 랜덤으로 추출 API
- 폴더에 특정 단어 포함 여부 API

---
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


---
### 6. 2024/02/08
- 기존에는 Curation 과 Term 이 다대다 관계로 연결되어 데이터베이스를 많이 차지하고 있었는데,.. 큐레이션 단에 스트링으로 convert 해서 엮을까 생각중
- 폴더 생성, 정보 수정 API
- 용어에 큐레이션을 저장하는 로직에서, findById() 대신 getReferenceById() 를 이용하여 쿼리 발생을 2회 줄였다. 
  - 자세한 내용은 블로그 글 참고


---
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


---
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


---
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

---
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

---
### 1. 2024/02/03
- v1 과 무엇이 바뀔것인가?
  - v1 에서는 단일 프로젝트에 모든 백엔드 로직이 다 들어있었다
  - Spring boot 2 에서 Spring boot 3 으로 업그레이드
- 새로운 Spring Security 코드 도입 
- 카카오, 구글 서버와 연결하여, 유저 회원가입, jwt access token 발급까지 완료
- googleService 에서, RestTemplate 이 Deprecated 되어서 WebClient 로 Migration
- member_tb 에서, password 는 socialId 를 encrypt 한 것. 

`TODO : kakaoService 도 WebClient 로 Migration 후 시간 성능 측정 비교`
