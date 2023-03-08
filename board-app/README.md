# Application Server

---

## History
> first commit
>> WebConfig와 각 controller, 전체적인 view 파일만 생성하고 커밋.
>
> #
>> 23/02/10
>>> WebClient로 api Server로 데이터 요청 처리 구현.
> 
>> 23/02/11 ~ 23/02/28
>>> SpringSecurity 단일로 api에서 인증 인가 처리가 이해가 안되는 문제로 SpringSecurity + JWT 로 전환.   
>>> SpringSecurity에서 /login 요청을 가로채 처리하도록 AuthenticationFilter를 구현했으나   
>>> Client Server로 토큰을 리턴하도록 하는 방법을 찾지 못해   
>>> 로그인 요청을 /member/login 으로 받아 member 서비스단에서 직접 처리한 후 토큰 생성하고 토큰을 리턴하도록 구현.   
>>> 그로 인해 AuthenticationFilter가 불필요해져 삭제 예정.
>>> AccessToken(At)과 RefreshToken(Rt) 두가지 토큰을 생성하고 처리하도록 구현.   
>>> At은 1시간의 만료기간. Rt은 2주의 만료기간을 설정.   
>>> At가 만료되어 Rt를 통해 재발급을 받는 경우 Rt 역시 재발급 받도록 구현.   
>>> Rt는 DB에 저장하고 At, Rt 모두 Client Server에 리턴해 Client에서는 쿠키에 두가지 토큰을 보관.   
>>> localStorage에 보관하지 않은 이유로는 XSS 공격을 방어하기 위해서이며 Cookie에서 발생할 수 있는 문제인 csrf 공격을 막기 위해   
>>> Client Server에서 쿠키 생성 시 HttpOnly, Secure, same-site를 설정.   
>>> 또한 인터셉터에서 referer를 체크하도록 해 혹시나 뚫고 들어오더라도 한번 더 체크해 방지할 수 있도록 구현 필요.
>>> At Cookie의 경우 만료 시간이 1분 작게 설정.   
>>> Api Server에 요청하는 시점이 토큰 만료시점과 겹치는 경우 문제가 발생할 수 있기 때문에 Client에서 사전에 처리할 수 있도록 하기 위함.   
>>> 아직 부족해서 처리하지 못한점이 XSS 방지.   
>>> XSS의 경우 HttpOnly로 다 막을수는 있다고 하지만 마냥 안심할 수 없는 부분이 만약 내 도메인 내의 게시글에 스크립트를 삽입하는 경우   
>>> same-site 설정과 referer를 뚫고 들어올 수 있다고 판단해 게시글에 스크립트 삽입이 불가하도록 설정해야할 필요성이 있다고 생각함.   
>>> 이건 꼭 학습해서 처리해야 하는 부분.
>
>> 23/03/01 ~ 23/03/03
>>> TokenInterceptor로 referer 체크하도록 일단 추가만 처리.   
>>> interceptor 처리와 여기서 발생하는 Exception 처리는 모든 구현이 끝난 후에 마지막에 구현.   
>>> 초기 기능 구현 당시 At 하나만 갖고 있도록 구현했기 때문에 해당 부분들 Rt를 같이 갖고 있도록 수정.   
>>> At가 만료된 경우 Rt를 Api Server에 보내 재발급 받은 후 데이터 요청을 하도록 수정.
> 
>> 23/03/06 ~ 23/03/07 
>>> api 서버의 AuthorizationFilter에서 TokenProvider에 접근하지 못하는 문제 발생.
>>> nullpointerException이 발생했는데 Filter의 constructor에 TokenProvider를 추가해주고
>>> 필터를 등록해둔 SecurityConfig에도 같이 추가해줌으로써 해결.
>>> 당연히 @Component 달고 @Autowired를 달아주면 주입이 될거라고 생각을 했으나 전혀 주입 받지 못하는 상태였던 것.
>>> 이런 경우에는 생성자를 통한 주입으로 처리를 해야 정상적으로 동작한다.
>>>
>>> 사용자 로그인 정보 처리 구현.
>>> 계속 고민했으나 로그인 여부는 RefreshToken을 localStorage에 넣어두고 이 토큰 소지 여부로 파악 할 수 있다고 쳐도
>>> 게시판 상세페이지같은 곳에서 작성자와 사용자 아이디를 비교하는 경우에는 이 방법이 불가하다고 판단 함.
>>> 그래서 api에서 데이터를 받을 때 게시글 데이터와 사용자의 아이디를 같이 받는 형태로 구현.
>>> 클라이언트 서버에서는 세션을 전혀 사용하지 않고 있고 api 서버에서 그나마 권한관리로 인해 세션을 사용중인데   
>>> 이 문제점이 딱히 검색해서도 나오는 방법이 없었고 생각해본 방법은 세가지.
>>>> 1. Spring Security를 사용중이니 클라이언트 서버에 세션을 만들어 사용자 정보(아이디, 닉네임 정도)를 관리.
>>>> 2. api 서버에서 요청 데이터를 리턴할 때 사용자 아이디를 같이 리턴.
>>>> 3. 2번 처럼 처리하는데 리턴할 때 사용자 아이디를 리턴하는 것이 아닌 작성자와 사용자 아이디가 동일한지 api 서버가 체크 후 boolean으로 리턴   
>>>
>>> 여기서 2번 방법을 선택한 이유   
>>> 1번은 stateless여야 한다는 개념에서 벗어나기 때문이다. 물론 api 서버에서도 권한관리를 편하게 하기 위해 세션을 사용하고 있지만   
>>> 클라이언트 서버와 api 서버 모두 세션을 사용하면서 처리한다는 점에서 stateless로 처리하는것이 무의미 해진다고 생각을 했고
>>> 두 서버 모두 세션으로 인한 부담이 증가할 것이라고 생각했기 때문이다.   
>>> api에서의 권한관리를 위한 세션 활용도 개발 편의성은 높여주지만 아무래도 그에 따른 부담이 있다고 생각하고   
>>> 이걸 개선하기 위해서는 토큰에 권한 정보가 들어가거나, 아니면 토큰 검증 후 토큰의 사용자 아이디를 통해 권한을 체크해주는   
>>> 방법으로 개선이 가능할것으로 보이나 코드를 어떻게 작성하는지에 따라 다르겠지만 오히려 비 효율적일 수도 있다는 생각이 듦.   
>>> 예를 들어 메인 페이지를 제외한 모든 페이지에서 로그인이 필요하고 권한에 따라 출력되는 데이터가 달라지는 페이지라면.   
>>> 매 요청 시 마다 권한 확인을 위한 DB 접근과 코드의 처리가 필요하게 될것이고 그 후에 권한 체크를 해 리턴하는 방식이기 때문에   
>>> 세션 메모리에 대한 부담이 적어지기만 할 뿐 오히려 처리 속도에 있어서는 어떨지가 중점이라고 생각.
>>>
>>> 3번 방법을 사용하지 않는 이유는 상세페이지 데이터의 경우 하나의 row가 리턴되지만 그 페이지의 댓글의 경우는
>>> 리스트 형태로 리턴될건데 그 리스트의 모든 경우에 대해 api 서버에서 boolean으로 하나하나 다 대조해서 매핑한 뒤 보내는 것은   
>>> 아무래도 비효율적이다. 라는 생각을 했기 때문.   
>>> 그래서 사용자 아이디를 리턴해 처리하게 되면 상세페이지의 경우 프론트에서 eq로 비교해 출력해야해서 코드가 아주 살짝 길어지지만
>>> 반대로 comment에서는 api서버에서 처리도 덜해서 보내줄 수 있고 어차피 프론트에서는 json으로 받아 출력하도록 할것이기 때문에
>>> 이때 처리하는 것이 좀 더 빠르게 처리할 수 있겠다 라고 생각했기 때문.
> 

## 메모
> restAPI 에 연결하는 방법으로는 아래의 방법들이 존재.
> 1. WebClient
> 2. Java(HttpURLConnection)
> 3. RestTemplate(spring)
> 하지만 RestTemplate는 spring5부터 Deprecated 상태.   
> 버전에 따라 사용하게 되는 경우가 있을 수 있으니 아래처럼 분리해서 사용.
>> 1. WebClient -> HierarchicalBoard
>> 2. Java -> ImageBoard
>> 3. Comment, Member -> RestTemplate
>> 
> 
> interceptor 구현해서 referer 체크 할 수 있도록.   
> 단, get 요청이면서 단순 페이지 데이터만 나오는 요청들은 제외   
> 인터셉터 제외 페이지 리스트
> 1. boardList
> 2. boardDetail
> 3. imageList
> 4. imageDetail
> 5. loginForm
> 6. join
> 7. commentList(hierarchical & image)
> 
> 두 게시판 게시글 작성 시 xss 대비 방안 세워서 처리.   
> 
> 전체적인 구현 이후 TokenInterceptor 통해서 referer 체크 마저 구현하고 ExceptionHandling까지 처리.
> 
> interceptor
>> referer 체크 후 토큰 체크하도록 하면
>> rt, at가 null일때는 login 페이지로 가도록 하고
>> rt만 존재할때는 reissued를 받게 하면 요청마다 토큰 체크를 하도록 할 필요가 없을듯.
>> 그럼 interceptor를 거치지 않는 페이지 리스트에서 각 게시판 detail 페이지는 있으면 안된다.
>> 여기서 문제점. response.addCookie로 재발급된 토큰을 저장할건데.
>> 이게 response가 완전하게 전달되지 않은 상태에서 새로 발급 받은 토큰을 어떻게 요청할것이며
>> 이 요청에서 토큰이 재발급이 되었는지 여부는 어떻게 확인할것인지가 관건.
>> 만약 인터셉터에서 null 여부만 체크해 처리하게 되면 사실상 인터셉터를 거치는 모든 페이지는 권한이 필요한 페이지 이므로 
>> null일 경우 로그인 페이지로 리다이렉트 하면 되긴 하지만
>> 각 게시판 Detail 페이지의 경우 그럼 한번 더 토큰 여부를 검증해야 하는 문제가 발생.
>> 인터셉터에서 토큰 존재 여부를 체크하지 않는다면 토큰 서비스로 분리해두긴 했지만 모든 요청에서 해당 서비스를 호출해 체크하도록 해야한다.
> 
> 토큰 여부 체크 과정
>> at, rt 모두 존재하는지 rt만 존재하는지 둘다 존재하지 않는지를 체크.
>> at가 존재한다면 클라이언트의 요청을 수행
>> rt만 존재한다면 토큰 재발급 후 재발급 받은 at로 요청 수행
>> 둘다 존재하지 않는다면 로그인 페이지로 redirect
>> at가 존재한다는 경우는 고려해야할 사항이 없음. 그대로 cookie로 꺼내 전송하면 되기 때문.
>> 둘다 존재하지 않는 경우도 로그인 페이지로 리다이렉트 해주거나 권한이 필요 없는 페이지는 요청을 수행하면 되기 때문에 고려사항이 없음.
>> rt만 존재하는 경우는 재발급 요청 -> 리턴된 rt, at 쿠키에 저장 -> at 리턴 -> 리턴받은 at로 요청 수행 이 순서로 진행되어야 하는데
>> 분리된 과정을 하나의 메소드로 묶어서 그 메소드에서 각각 호출해 처리하도록 하기에는 리턴되는 타입들의 차이가 발생.
>> 그럼 리턴되는 타입을 하나로 통일시켜 처리하거나 묶지 못하고 매 요청시마다 각각 호출해 사용하는 방법으로 처리해야 함.
>> 재발급 된 토큰은 JwtDTO 타입으로 리턴될것. at가 존재하는 경우도 JwtDTO에 담아 리턴이 가능. 없는 경우는 그럼 그냥 null을 리턴?
>> 이 방법의 부작용은 기껏 checkExistsToken으로 cookie를 털어 체크했는데 DTO에 담겨 왔다는 이유로 한번 더 체크해야하는 문제가 발생.
>> 그럼 어차피 리턴받은 시점에 또 다시 dto 값을 체크해 그에 맞는 조건으로 수행하도록 해야함.
>> 그래도 한가지 조건이 줄어드는 장점이 있음.
>> null이 아니라면 재발급 받은 토큰이 DTO에 들어가있거나 존재하던 쿠키값이 DTO에 들어가서 리턴될것이므로 
>> dto가 null이면 로그인페이지로 리다이렉트. 그게 아니라면 요청을 수행하도록 할 수는 있음.
>> 그럼 모든 요청에서 JwtDTO 타입으로 체크 메소드를 호출하고 그 체크메소드에서는 존재 여부 체크 후 DTO에 값을 담거나 null을 리턴하도록 하면.
>> 리턴되는 dto를 통해 그에 맞는 조건을 수행할 수 있게 됨.
> 로직 정리
>> 1. at 존재
>>> client request -> existsToken -> checkExistsToken -> dto.setAt -> return to clientServer -> api server request
>> 2. token 재발급
>>> client request -> existsToken -> checkExistsToken -> reissuedToken -> savedToken -> dto.setAt -> return to clientServer -> api serverRequest
>> 3. token null
>>> client request -> existsToken -> checkExistsToken -> return to clientServer(null) -> redirect loginForm
>> 간단하게 코드 정리
>>> ``` java
>>> @PostMapping("/")
>>> public String requestController(HttpServletRequest request) {
>>>     JwtDTO dto = tokenService.existsToken(request);
>>>     if(dto == null)
>>>         return "th/member/loginForm";
>>>     
>>>     0000service.insert(request, dto);
>>>     
>>>     return "0000";
>>> }
>>> 
>>> @Override
>>> public JwtDTO existsToken(HttpServletRequest request){
>>>     Cookie at = ...;
>>>     Cookie rt = ...;
>>> 
>>>     if(at == null && rt == null)
>>>         return null;
>>>     else if(at != null && rt != null)
>>>         return JwtDTO.builder()
>>>                 .accessHeader(at.getName())
>>>                 .accessValue(at.getValue())
>>>                 .build();
>>>     else if(at == null && rt != null)
>>>         return reissuedToken(request);
>>> }
>>> ```
>> 다른 문제점.
>> refreshToken을 cookie에 담아둔다면 로그인 여부 체크를 어떻게 할지.
>> 아무생각없이 rt가 localStorage에 존재하면 로그인 한것으로 간주하려고 했으나
>> 둘다 cookie에 저장하는 경우에는 httpOnly 설정으로 클라이언트에서 접근할 수 없을건데 뭘로 검증할것인가가 관건.
>> 그럼 그나마 떠오르는 방법이 api server에서 모든 요청에 principal 을 체크해 리턴해주는 방법이 있음.
>> 토큰이 존재하면 AuthorizationFilter에서 contextHolder에 저장하도록 되어있기 때문에 principal을 통해 로그인한 사용자 아이디 리턴이 가능함.
>> 그럼 여기서 고민해야 할 사항.
>> 사용자 아이디를 리턴할 것인가. 아니면 boolean으로 로그인 여부만 리턴할 것인가.
>> 사용자 아이디가 직접적으로 필요한 페이지는 작성자와 사용자를 구분해야 하는 각 게시판 detail 페이지와 댓글 데이터 정도이다.
>> boolean으로 리턴하게 되면 로그인 여부는 쉽게 처리할 수 있지만 작성자와 구분이 어려워진다.
>> 모든 요청에서 아이디를 리턴하는 것은 정보 유출이 되지 않을까 생각했지만. 생각해보니까 어차피 아이디 알아내는것 정도는 매우 쉽다.
>> 어차피 왠만한 페이지에서 글 남기면 출력되는게 아이디 혹은 닉네임인데 닉네임만 눌러도 아이디가 출력되는 페이지도 많기 때문.
>> 또한 아이디는 알아낸다고 해도 중요정보인 사용자 실명이나 비밀번호, 그 외 주소등 개인정보가 같이 딸려오는 것은 아니기 때문에 그걸 같이 보내지만 않으면
>> 아이디를 매번 리턴하는것도 나쁘지 않은 선택으로 보인다.
>> 그렇게 하면 로그인 여부에서도 아이디로 체크하고 한번에 detail 페이지에서 작성자와의 비교역시 가능해지기 때문.
>> 보안에 있어서 주의해야할 점은 사이트에 따라 아이디 혹은 닉네임만 리턴하도록 하고 그 외 정보를 리턴하도록 하지만 않으면 문제는 발생하지 않을것으로 보임.
>> 그럼 rt는 그대로 cookie에 저장하도록 하고 아이디를 리턴받아 로그인 여부와 작성자와의 비교를 처리하는 방법으로 진행.
> 
>> 프로젝트 마무리 하면서 고민해야 할 사항.
>>> 만약 rt를 localStorage에 저장하는 경우 재발급을 받아야 할 때 어떻게 처리할것인가를 고민해야할 필요가 있음.
>>> JQuery를 사용하면 어차피 ajax를 통한 요청이 대다수일텐데 요청을 보낼때 rt만 헤더에 담아 보내면 쿠키에 있는 at는 알아서 넘어갈 것.
>>> 그럼 클라이언트 서버는 requestHeader에서 rt를 꺼내 확인하고 at 역시 꺼내서 확인.
>>> 없으면 재발급 요청할 건데.
>>> 해결 방안. 별거 없었음. response.addHeader에 재발급 받은 rt를 담아 요청 데이터와 같이 리턴해주고 JQuery에서는 데이터 처리 전후 아무때나
>>> localStorage에 rt를 다시 저장하면 된다.
>>> localStorage에 저장하는 방법을 택한다면 클라이언트서버에서 역시 페이지요청과 데이터 요청을 정확하게 분리해서 처리해줘야 한다.