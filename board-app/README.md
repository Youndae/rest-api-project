# Application Server

---

## History
> first commit
>> WebConfig와 각 controller, 전체적인 view 파일만 생성하고 커밋.
>
> #
>> 23/01/11
> 
> 
> 
> restAPI 에 연결하는 방법으로는 아래의 방법들이 존재.
> 1. WebClient
> 2. Java(HttpURLConnection)
> 3. RestTemplate(spring)
> 하지만 RestTemplate는 spring5부터 Deprecated 상태.   
> 버전에 따라 사용하게 되는 경우가 있을 수 있으니 아래처럼 분리해서 사용.
>> 1. WebClient -> HierarchicalBoard
>> 2. Java -> ImageBoard
>> 3. Comment, Member -> RestTemplate