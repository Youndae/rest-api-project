# RestAPI Server

---

## History
> 22/12/29
>> 전체적인 기능 코드 작성.   
>> 현재 누락 코드는 각 Entity Service에서 getList 코드들.   
>> 기존 프로젝트를 그대로 사용하는것이기 때문에 딱히 문제는 없을것이라고 가정하고   
>> 기존 프로젝트와 다른 getList 기능들 먼저 구현해서 테스트 필요.   
>> getList 테스트가 완료되면 전체적인 기능 테스트 필요.   
>
> #
> 23/01/04
>> 계층형 게시판 List 리턴 처리 완료.   
>> 데이터는 DTO로 매핑해 받고 이를 리턴하는 형태로.   
>> 문제점으로는 Paging에 대한 데이터를 어떻게 처리할것인가가 문제.   
>> DTO에 PageDTO를 추가해봤지만 그렇게 되면 쿼리 실행시에 SyntaxError가 발생.   
>> 생각난 방법 중 하나는 리스트 페이지 접근시에 여기에서는 게시판 데이터만 가져가고
>> PageController를 만들어서 거기서 한번 더 처리하는 형태가 생각나긴 했으나
>> 그럼 count 쿼리가 두번 발생한다는 점에서 성능 저하가 우려.   
>> 기존 코드 그대로 사용하면 그것도 어차피 count 쿼리를 두번 사용하긴 하는데   
>> 아무래도 count 쿼리가 지금 많이 느리게 동작하는 상황이므로 중간에 지연이 많이 발생할것으로 보임.   
>> 1차적으로 paging 데이터를 어떻게 넘길것인가를 처리하고
>> count 쿼리에 대한 점을 좀 더 수정해야할 필요가 있음.   
>> 남은 이미지 게시판과 댓글 역시 DTO로 매핑해 처리하도록 수정필요.
>> 일단은 계층형 게시판 먼저 문제 해결할것.
>
> #
> 23/01/05
>> 계층형 게시판 List 리턴 처리 문제 해결.
>> HierarchicalBoardListDTO를 하나 더 만들어서 그 안에서 HierarchicalBoardDTO와 PageDTO를 필드로 갖도록 해 처리.   
>> HierarchicalBoardDTO 내에서 처리를 하고자 하면 이 DTO 자체가 List화 되기 때문에 PageDTO를 담아줄 방법이 없었음.   
>> 그것을 해결하기 위해 MultiValueMap으로 변환해 거기에 PageDTO 데이터를 추가하는 방법을 생각했지만 뜻대로 되지 않았고   
>> 구글 검색 결과 보통 요청을 받을 때 MultiValueMap으로 받지 리턴을 MultiValueMap으로 하지는 않는것 같다고 판단해 해결 방법이 아니라고 생각.   
>> 다음으로 생각한 방법이 PageDTO에 HierarchicalBoardDTO를 List로 만들어두고 생성자를 통해 가져온 데이터를 추가하는 방법.   
>> 이 방법은 정상적으로 데이터를 넘겨주기는 했으나 PageDTO 데이터가 쭉~ 나오고 그다음 DTO List 데이터가 나와 데이터가 정리가 전혀 되지 않은 느낌이었고   
>> 이걸 처리하고자 한다면 하긴 하겠지만 문제는 PageDTO는 댓글에서 역시 사용해야 하는 페이징 기능이고 추후 이미지 게시판에 페이징을 적용하게 된다면 
>> 그 모든 엔티티들을 다 PageDTO에 받아서 처리하도록 하기에는 무리가 있다고 판단.   
>> 다른 방법으로 HierarchicalBoardDTO에 데이터를 매핑하는것이 아닌 HierarchicalBoard Entity에 받아온 다음 DTO에서는 이 엔티티를 List로 받아오고
>> PageDTO를 갖고 있도록 하는 방법.   
>> 이 방법의 문제점으로는 stackOverFlow가 발생한다는 문제.   
>> '양방향이 꼭 필요한 경우가 아니라면 oneToMany는 지양하는것이 좋다' 라는 의견에 Member Entity에서 각 게시판과 댓글 entity의 OneToMany를 모두 끊었으나
>> Auth Entity와는 양방향이 필요하다고 판단했기에 이걸 끊어낼 수 없었는데 여기서 StackOverFlow가 발생해서 해결이 안되는 상황.
>> @ToString.Exclude로 해결하고자 했으나 그것 역시 안되었고 이건 JPA 연관관계에 대한 지식 부족 문제라고 생각함.   
>> 추후 이 방법에 대해서는 추가적인 학습이 필요. 그리고 또 한가지 문제점으로 불필요하게 사용자 아이디를 제외한 나머지 정보까지 다 가져왔기 때문에 패스.   
>> 마지막 방법이 HierarchicalboardListDTO.   
>> 너무 많은 DTO를 만들어서 처리하는것이 아닌가 싶긴 했지만 어차피 게시판 상세 페이지 정보를 위해서는 페이징 기능이 필요없는 HierarchicalBoardDTO와 분리하는것도
>> 괜찮은 방법이라고 생각했기 때문에 이 방법으로 해결.
>> #
>> 추가로 count 쿼리에 대한 문제도 해결.   
>> 여기저기 찾아본 것으로는 페이징을 위해 전체 데이터 개수를 조회할 때 대략적인 개수만 가져와서 처리하는 방법과
>> 데이터의 총 개수를 담고 있는 테이블을 별도로 생성해 처리하는 방법 두가지가 보편적인 해결 방안으로 언급되었다.   
>> index를 생성해 처리하는것도 얘기가 많이 나왔지만 테이블 전체를 조회하다보니 이것도 의미가 없었고 대략적인 개수를 가져와 처리한다는 것은
>> google 검색의 기능을 예로 들어서 설명해주신 것을 봤지만 자세한 설명도 아니었고 '아 이런 형태구나' 이정도만 이해가 갈 뿐 어떻게 구현해야 할지
>> 감이 잡히지 않아 패스. 그래서 count_table이라는 테이블을 생성 후 그 안에서 boardName을 PK로 전체 데이터 개수가 필요한 hierarchicalBoard 테이블과
>> imageBoard 테이블만 넣어 전체 개수를 처리.
>> 그리고 trigger를 설정해 insert, delete 시에 개수가 조절되도록 수정.   
>> 이걸 Entity로 굳이 만들어야 할까 싶었지만 JPA에서 table과 Entity의 구조는 맞춰야 하는것 아닌가 해서 생성.
>> #
>> comment 수정중
> 
> #
> 23/01/06
>> Comment 수정.   
>> Page<>로 페이징 관련 데이터를 넘길 수 있다는 것을 확인. 그래서 리턴을 List 로 하는것이 아닌 Page로 리턴하도록 수정.   
>> DTO에 매핑할 때 null값이 존재하면 오류가 발생하면서 매핑이 안되는 부분때문에 각 게시판별 DTO를 생성하고
>> 이 DTO들을 Page 타입으로 담고 있는 CommentListDTO를 만들어 해당하지 않는 게시판의 DTO는 null값으로 만들어 리턴하도록 구현.
>> 그리고 HierarchicalBoard 역시 ListDTO를 삭제하고 DTO하나만 Page 타입으로 리턴하도록 해 처리.   
>> 프론트에서는 totalElements와 totalPages가 같이 리턴되니까 이걸 받아서 데이터 하단에 페이지 이동 처리만 하도록 하면 되니 PageDTO는 삭제.   
>> #
>> url은 rest에 맞게 전체적으로 수정이 필요.   
>> imageBoard에 대한 처리 필요.
> 
> #
> 23/01/10
>> ImageBoard 처리 수정.   
>> image-board-list는 정상적으로 리턴하는것을 확인.   
>> image-detail과 image-modify는 따로 분리해서 처리하고는 있으나 동일한 repository 메소드를 가져와 사용중.   
>> 이 처리를 위해 ImageDetailDTO를 생성.   
>> detail과 modify는 동일한 데이터를 리턴하고 있으나 프론트에서 요청하는것을 감안해 지금처럼 분리하는것이 나은지
>> 아니면 동일하게 detail로만 받아서 처리하도록 할것인지 고민.   
>> 이제 전체적인 구현은 끝난것으로 보이고 ApplicationServer 만들어서 테스트 해볼것.
---

## 남아있는 처리 과정
> 1. 각 Entity getList
> 2. 상태코드 리턴처리
> 3. 전체적인 테스트
>
>
> 테스트 
> 1. 만료된 AccessToken이 넘어오더라도 토큰에서 userId를 빼올 수 있는지
> 2. 요청에 따른 토큰 검증들이 정상적으로 동작하는지.
> 
> 테스트 전 코드 마무리 사항
> 1. repository에 exists 추가. O
> 2. 로그인 처리에서 provider로 연결해 토큰 처리하도록 수정. O
> 3. Authorization에서 provider로 연결해 처리하도록 수정. O