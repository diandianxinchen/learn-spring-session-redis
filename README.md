### spring-session-data-redis 实现session 共享

---

#### 使用实例（主要步骤）
1. 在web.xml中配置一个filter 以及对于的mapping
    ```xml
    <filter>
       <filter-name>springSessionRepositoryFilter</filter-name>
       <filter-class>org.springframework.web.filter.DelegatingFilterProxy</filter-class>
    </filter>
    ```
    **注意**：这里的filter-name一定要是springSessionRepositoryFilter，DelegatingFilterProxy会根据filter-name来决定具体使用哪一个filter
    ```xml
    <filter-mapping>
       <filter-name>springSessionRepositoryFilter</filter-name>
       <url-pattern>*.do</url-pattern>
    </filter-mapping>
    ```
2. 配置实际使用的filter
    ```xml
    <bean class="org.springframework.session.data.redis.config.annotation.web.http.RedisHttpSessionConfiguration"/>
    ```
3. 配置redis
    ```xml
    <bean id="jedisConnectionFactory" class="org.springframework.data.redis.connection.jedis.JedisConnectionFactory">
        <property name="hostName" value="${redis.hostName}"/>
        <property name="port" value="${redis.port}"/>
        <property name="poolConfig" ref="redisPoolConfig"/>
    </bean>
    ```
    ```xml
    <bean id="redisPoolConfig" class="redis.clients.jedis.JedisPoolConfig"/>
    ```
     我这里没有使用最新的spring-data-redis的jar包，新的jar中setHostName,setPort等方法废弃了，如使用最新的jar包使用新的配置方式。
          
通过filter处理
- filter 配置<url-pattern>/</url-pattern>时不能生效？
> 实际使用过程中发现，还没有找到原因

filter的生存周期
- 容器启动时init
- 过滤时doFilter()
- 容器关闭时destroy()

servlet 的生存周期
- 第一次调用时init
- 调用时service
- 容器关闭时destroy()

#### 原理分析  

---

##### 基本原理
通过filter,将session中的信息保存在redis中。
具体需要完成的事情
1. 收到请求读取session信息时，根据请求中的sessionId在redis数据库中查询是否存在。
2. servlet doServlet()调用结束后，将session信息保存到redis数据库中。
##### 调用过程
- DelegatingFilterProxy doFilter()  
  initDelegate()根据filter-name(springSessionRepositoryFilter)查找bean
  并调用其doFilter方法
- **springSessionRepositoryFilter** 的初始化  
  在**RedisHttpSessionConfiguration**的父类**SpringHttpSessionConfiguration**初始化
  ```
  protected void doFilterInternal(HttpServletRequest request,
  			HttpServletResponse response, FilterChain filterChain)
  			throws ServletException, IOException {
     request.setAttribute(SESSION_REPOSITORY_ATTR, this.sessionRepository);
  
     SessionRepositoryRequestWrapper wrappedRequest = new SessionRepositoryRequestWrapper(request, response, this.servletContext);
     SessionRepositoryResponseWrapper wrappedResponse = new SessionRepositoryResponseWrapper(wrappedRequest, response);
  
     HttpServletRequest strategyRequest = this.httpSessionStrategy.wrapRequest(wrappedRequest, wrappedResponse);
     HttpServletResponse strategyResponse = this.httpSessionStrategy.wrapResponse(wrappedRequest, wrappedResponse);
  
     try {
         filterChain.doFilter(strategyRequest, strategyResponse);
     }
     finally {
         //将session中信息保存到redis
         wrappedRequest.commitSession();
     }
  }
  ```
  实际调用getSession()时是调用sessionRepository的getSession(sessionId)方法
- **sessionRepository**的初始化 
  sessionRepository在RedisHttpSessionConfiguration中定义 
  **sessionRedisTemplate**  
  **RedisConnectionFactory**  
  **JedisConnectionFactory**  
  故需要配置的只有一个Redis的连接工厂
  
看不动了，先这样吧