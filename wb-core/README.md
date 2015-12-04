#模块组成
wb项目核心模块，基于Spring+SpringMvc+MyBaits,所有模块通过restful+json访问。
#目录结构
```bash
|---org.webbuilde.web
|-----------|--controller             springMvc控制层
|------------------|------config      配置管理
|------------------|------file        文件管理,文件上传下载
|------------------|------form        自定义表单管理
|------------------|------index       其他功能,获取在线人数等等
|------------------|------login       用户登录
|------------------|------module      系统模块管理
|------------------|------resource    系统资源管理,用于管理上传的文件信息等
|------------------|------role        系统角色管理
|------------------|------script      动态脚本管理
|------------------|------user        用户管理
|--
|-----------|--core                   框架底层核心代码
|------------------|------aop         spring AOP相关,权限控制,日志记录等
|------------------|------authorize   权限注解。拦截器式权限控制(已弃用)
|------------------|------bean        基础PO,系统常用bean等
|------------------|------cache       缓存相关
|------------------|------controller  基础控制器,实现基本的CRUD功能的控制器
|------------------|------dao         基础DAO,实现基本的CRUD功能的控制器和简单的mybaits分页插件
|------------------|------exception   异常
|------------------|------logger      日志相关
|------------------|------service     基础Service,实现基本的CRUD功能的控制器
|------------------|------utils       web常用工具类
|------------------|------websocket   websocket相关，实现基于命令的websocket服务
|-- 
|-----------|--dao                    mybatis数据映射层
|------------------|------basic       基础映射接口配置,实现通用的查询sql生成。
|------------------|------config      配置管理
|------------------|------form        自定义表单管理
|------------------|------module      系统模块管理
|------------------|------resource    系统资源管理,用于管理上传的文件信息等
|------------------|------role        系统角色管理
|------------------|------script      动态脚本管理
|------------------|------user        用户管理   
|-- 
|-----------|--po                     持久化映射对象
|------------------|------config      配置管理
|------------------|------form        自定义表单管理
|------------------|------logger      日志信息
|------------------|------module      系统模块管理
|------------------|------resource    系统资源管理,用于管理上传的文件信息等
|------------------|------role        系统角色管理
|------------------|------script      动态脚本管理
|------------------|------user        用户管理
|--
|-------------service                服务层
|------------------|------basic       基础服务层，用于直接执行sql语句,返回结果等
|------------------|------config      配置管理
|------------------|------form        自定义表单管理
|------------------|------module      系统模块管理
|------------------|------resource    系统资源管理,用于管理上传的文件信息等
|------------------|------role        系统角色管理
|------------------|------script      动态脚本管理
|------------------|------storage     缓存存储器
|------------------|------user        用户管理   
```

#使用
####1、安装webbuilder
```shell
$ git clone https://github.com/wb-goup/webbuilder.git
$ cd webbuilder
$ mvn install -Dmaven.test.skip=true
```
####2、引入maven
```xml
<dependency>
    <groupId>org.webbuilder</groupId>
    <artifactId>core</artifactId>
    <version>${org.webbuilder.version}</version>
</dependency>
```
####3、配置文件
安装后的包不带有任何配置文件，如果需要使用框架的功能，需要手动建立配置。
请参考示例,[wb-example](https://github.com/zhou-hao/wb-example)

#如何建立一个简单的增删改查功能模块
###以下介绍建立在配置文件建立好之后
##一、传统方式
####1、建立数据库表以及PO对象
####2、建立对应的dao Mapper接口和xml配置:
mapper接口:
```java
/**
* 普通的增删改查 只需要继承通用接口(GenericMapper)即可
*/
public interface ConfigMapper extends GenericMapper<Config,String> {

}
```

mybatis配置,配置应根据数据库类型,建立不同的目录,以实现不同数据库之前的切换(尽管不同数据库的配置文件可能是相同的).
```xml
<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE mapper
        PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN"
        "http://www.mybatis.org/dtd/mybatis-3-mapper.dtd">
<mapper namespace="org.webbuilder.web.dao.config.ConfigMapper">
    <resultMap id="ConfigResultMap" type="Config" >
        <id property="u_id" column="u_id" javaType="string" jdbcType="VARCHAR" />
        <!--此为示例，未列出所有字段-->
    </resultMap>

    <!-------------重要------------------>
    <!--字段信息配置，在查询的时候，指定字段信息，引用通用mapper配置，即可动态生成查询条件-->
    <sql id="fieldConfig">
        <bind name="$fieldsInfo"
              value="#{'u_id':'string','remark':'string','content':'string'
                    ,'create_date':'date','update_date':'date'}"/>
        <bind name="$fields" value="$fieldsInfo.keySet()"/>
    </sql>
    <!--表名-->
    <sql id="tableName">
        <bind name="$tableName" value="'S_CONFIG'"/>
    </sql>
    <!----------------------------------->
    
    <!---增删改查sql语句就不一一列出了--->
    <insert id="insert" parameterType="Config" useGeneratedKeys="true" keyProperty="u_id" keyColumn="U_ID">
    </insert>

    <delete id="delete" parameterType="Config" >
    </delete>

    <update id="update" parameterType="Config" >
    </update>

    <select id="selectByPk" parameterType="string" resultMap="ConfigResultMap">
    </select>
    
    <!--这里的查询时调用了通用配置,根据指定的字段和条件动态生成sql语句.mybatis配置中表达式使用的是ognl,拓展性很强-->
    <select id="select" parameterType="map" resultMap="ConfigResultMap">
        <include refid="fieldConfig"/>
        <include refid="tableName"/>
        <include refid="BasicMapper.selectSql"/>
    </select>

    <select id="total" parameterType="map" resultType="int">
        <include refid="fieldConfig"/>
        <include refid="tableName"/>
        <include refid="BasicMapper.totalSql"/>
    </select>
</mapper>
```
####3、建立service
```java
/**
* 普通的增删改查 只需要继承通用服务类(GenericService),实现getMapper方法即可
*/
@Service
public class ConfigService extends GenericService<Config, String> {
    //默认数据映射接口
    @Resource
    protected ConfigMapper configMapper;

    @Override
    protected ConfigMapper getMapper() {
        return this.configMapper;
    }
}
```

####4、建立controller
```java
/**
 *普通的增删改查 只需要继承通用控制器(ConfigController),实现getService方法即可。
 *注意: 增删改查使用restful+json方式
 */
@RestController  //rest控制器
@RequestMapping(value = "/config")
@AccessLogger("配置管理") //访问日志注解，用来记录本模块的功能摘要
@Authorize //访问授权，不设置属性代表用户只需登录即可访问
public class ConfigController extends GenericController<Config, String> {
    //默认服务类
    @Resource
    private ConfigService configService;

    @Override
    public ConfigService getService() {
        return this.configService;
    }
}
```
####5、客户端调用
由于服务端使用的restful+json方式,因此客户端(浏览器),需要使用ajax进行访问。


##二、代码生成器
对于以上所描述的功能,基本大多类似功能都是重复的工作，因此可以使用wb-code-generator模块
来生成上述代码。
注意: 代码生成器暂未完善

