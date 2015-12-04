####目录
+ [权限控制&访问日志](#权限控制)
+ [缓存](#缓存)

##权限控制
#####权限结构说明:
```bash
|--------Module                 #系统模块,系统的所有可访问的功能,可设置此模块的可操作类型(CRUD等)
|--------Role                   #角色
|----------|----RoleModule      #模块与角色关联,一个角色持有多个模块,每个模块可分配操作类型
|--------User                   #用户
|----------|----UserRole        #用户与角色关联,一个用户可以持有多个角色
```
#####验证方式: 
1、可直接调用当前登录用户的方法进行验证权限
```java
//此方法验证用户是否持有user模块的权限
boolean hasAccessModule = user.hasAccessModule("user");

//此方法验证用户是否持有user模块的C和U权限
boolean hasModuleAndLevel = user.hasAccessModuleLevel("user","C","U");

//此方法验证用户是否持有admin角色
boolean hasRole = user.hasAccessRole("admin");
```
2、在需要权限验证的controller类或者requestMapping方法上注解:Authorize.
Authorize注解属性支持:(module,level),role,expression(表达式支持ognl和spel)
注意,验证优先级为:module>role>expression
```java
    @RestController
    @RequestMapping(value = "/config")
    @Authorize(module="config") //config模块
    public class ConfigController extends GenericController<Config, String> {
        @RequestMapping(value = "/{id:.+}", method = RequestMethod.GET)
        @Authorize(level="R") //持有R权限
        public Object info(@PathVariable("id") String id) {
            return super.info(id);
        }
        
    }
```
使用注解需要在springmvc配置中加入:
```xml
   <!--此类将权限控制和访问日志集中在一起-->
  <bean id="authorizeAndLoggerAdvice" class="org.webbuilder.web.core.aop.authorize.AuthorizeAndLoggerAdvice">
        <!-- 此为日志服务类,如果不注入此属性，将不会产生访问日志-->
        <property name="loggerService">
                <!--logback日志服务,将访问日志输出到控制台或者文件
                    如果需要将日志输出到数据库等,实现:org.webbuilder.web.core.logger.LoggerService接口即可
                -->
                <bean class="org.webbuilder.web.core.logger.LogBackLoggerService"></bean>
        </property>
    </bean>
    <!--配置aop，拦截需要权限验证的方法-->
    <aop:config>
        <aop:pointcut id="authorize" expression="execution(* org.webbuilder.*.controller..*(..))
                                                    or execution(* org.webbuilder.web.core.controller..*(..))
                                                    "/>
        <aop:aspect ref="authorizeAndLoggerAdvice">
            <aop:around method="authorize" pointcut-ref="authorize"/>
        </aop:aspect>
    </aop:config>
```

##缓存
缓存功能基于web-util中的storage功能,轻度封装了spring的cacheManager,已实现本地缓存,和redis缓存支持.
配置:
在spring配置文件中加入如下片段:
本地缓存配置:
```xml
<!--注册本地storage驱动-->
<bean id="localDriver" class="org.webbuilder.utils.storage.driver.local.LocalStorageDriver" init-method="init">
    <property name="name" value="${storage.default.name}"/>
</bean>

<!--对spring cache的支持-->
<bean id="cacheManager" class="org.webbuilder.web.core.cache.CacheManager">
    <property name="driver" ref="localDriver"/>
</bean>

<!--缓存注解支持,在需要缓存的service方法中，注解 @Cacheable 或者@CacheEvict-->
<cache:annotation-driven cache-manager="cacheManager"/>
```
redis缓存配置:
```xml
  <!--连接池配置-->
    <bean id="redisPoolConfig" class="redis.clients.jedis.JedisPoolConfig">
        <property name="maxIdle" value="${redis.pool.maxIdle}"/>
        <property name="maxWaitMillis" value="${redis.pool.maxWaitMillis}"/>
        <property name="maxTotal" value="${redis.pool.maxTotal}"/>
    </bean>
    <!--基于一致性hash算法集群-->
    <bean id="redisPool" class="redis.clients.jedis.ShardedJedisPool">
        <constructor-arg index="0" name="poolConfig" ref="redisPoolConfig"/>
        <constructor-arg index="1" name="shards">
            <list>
                <value>http://userInfo:${redis.password}@${redis.host}:${redis.port}/${redis.database}</value>
            </list>
        </constructor-arg>
    </bean>
    <!--注册redis驱动-->
    <bean id="redisDriver" class="org.webbuilder.utils.storage.driver.redis.RedisStorageDriver" init-method="init">
        <property name="name" value="${storage.default.name}"/>
        <property name="pool" ref="redisPool"/>
        <!--使用2进制序列化与反序列化。如果不注入此属性默认使用的是json方式(存储的对象无序实现Serializable接口)-->
        <property name="defaultParserClass" value="org.webbuilder.utils.storage.instance.parser.ByteStorageParser"/>
        <!--使用redis的hash方式实现对象的缓存存储（默认）-->
        <property name="defaultStorageClass" value="org.webbuilder.utils.storage.instance.redis.RedisHashStorage"/>
    </bean>
    <!--缓存注解支持,在需要缓存的service方法中，注解 @Cacheable 或者@CacheEvict-->
    <cache:annotation-driven cache-manager="cacheManager"/>
```
如何使用:
1、可通过在controller或者service方法上加入注解@Cacheable启用缓存,@CacheEvict销毁缓存,来实现对方法返回结果缓存操作
```java
    //在查询到结果后，将u_id作为key加入缓存，下次调用，如果命中缓存，将直接返回缓存数据
    @Cacheable(value = "my_cache", key = "#u_id")
    public String selectByPk(String u_id) throws Exception {
        return getMapper().selectByPk(u_id);
    }
    //修改后
    @CacheEvict(value = "my_cache", key = "#data.u_id")
    public String update(PO data) throws Exception {
        return getMapper().update(data);
    }
```
具体用法请咨询度娘:
[spring Cacheable 注解](https://www.baidu.com/s?wd=Spring缓存注解Cacheable)
[spring spel 表达式](https://www.baidu.com/s?wd=Spring+spel+表达式)

2、可通过StorageDriverManager.getDriver(name);来获取一个已经注册的存储驱动,通过驱动来获取存储器,通过存储器来获取缓存
    如:
```java
StorageDriver driver = StorageDriverManager.getDriver("default");
Storage<String, User> storate = driver.getStorage("user.cache",User.class);
User user = storate.get("user_id");
```