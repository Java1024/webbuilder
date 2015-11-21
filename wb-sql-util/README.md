# sql-util
##使用html来定义表结构，自动维护数据库表，提供通用的增删改查接口

#应用场景
   在一个多表单的系统中 如：审批系统，工作流。有大量表单需要维护。按照传统方式
建立一个表单需要从页面到实体再到数据库代码挨个写一遍(或者代码生成器)，
**而且**，当需要在这个表单中新增一个字段时又要从头到尾加一遍。
   此工具用于解决动态表单维护，可通过html用来定义表结构（可直接用来做前端页面），
自动维护表结构，提供统一的增删改查结构(后台代码不用写)，在需要增加表单或者维护表单的时候，
直接使用编辑表定义文件重新加载即可。

#开始使用
##1.引入maven
```xml
<dependency>
    <groupId>org.webbuilder</groupId>
    <artifactId>wb-sql-util</artifactId>
    <version>${org.webbuilder.utils.version}</version>
</dependency>
```
##2.定义表结构
```html
<!--使用field-meta标识这是一个表字段-->
<input name="id" field-meta java-type="string" data-type="varchar2(256)" primary-key="true"/>
<input name="name" field-meta java-type="string" data-type="varchar2(256)"/>
```
更多功能请查看:[s_user.html](https://github.com/wb-goup/webbuilder/blob/master/wb-sql-util/src/test/resources/tables/s_user.html)
##3.创建数据库元信息并加载表
```java
    //定义数据库
    DataBaseMetaData dataBaseMetaData = new OracleDataBaseMetaData();//oracle
    DataBase dataBase = new CommonDataBase(dataBaseMetaData, new AbstractJdbcSqlExecutor() {
        @Override
        public Connection getConnection() {
            return connection;
        }
        @Override
        public void resetConnection(Connection connection) {
        }
    });
     //解析表结构
    String s_user_content = FileUtil.readFile2String(Resources.getResourceAsFile("tables/s_user.html").getAbsolutePath());
    TableMetaData s_user = new CommonTableMetaDataParser().parse(s_user_content, "html");
    dataBaseMetaData.addTable(s_user);//添加到数据库
```
##4.调用增删改查
### QUERY
```java
    DataBase database = .....//可通过spring注入
    
    Table table = dataBase.getTable("s_user_02");
    //创建查询
    Query query = table.createQuery();
    QueryParam param = new QueryParam();
    //-----------------多条件查询条件-------------
    String where = "{\"area_id$NOTNULL\":\"1\"," +
       "\"username$LIKE\":{\"value\":\"w\",\"nest\":{\"area.id\":{\"type\":\"or\",\"value\":2}} }}";
    
    //-----------需要查询的字段-----------------------查询条件---------排序------------
    param.select("id", "username", "area.name").where(where).orderBy("id").noPaging();
    List dataList = query.list(param);
    //进行分页
    param.doPaging(0, 5);
    dataList = query.list(param);
    Map data = query.single(param);//单个值
    int total = query.total(param);//查询总数
```
### INSERT
```java
    Table table = dataBase.getTable("s_user_02");
    Insert insert = table.createInsert();
    InsertParam param = new InsertParam();
    Map<String, Object> data = new HashMap<>();
    data.put("username", "admin");
    data.put("id", "aaa");
    param.values(data);
    insert.insert(param)
```

### UPDATE
```java
    Table table = dataBase.getTable("s_user_02");
    //创建更新
    Update update = table.createUpdate();
    UpdateParam param = new UpdateParam();
    //--------更新的字段-------------------------条件(此条件支持query中的条件风格)--------------
    param.set("username", "admin").where("username", "admin");
    update.update(param);
```

### DELETE
```java
    Table table = dataBase.getTable("s_user_02");
    Delete delete = table.createDelete();
    //-----------------条件-------------
    DeleteParam param = new DeleteParam();
    param.where("id$NOT", 1);
    delete.delete(param);
```

###SQL条件支持
如例子QUERY中的条件所示：条件支持类似 name$LIKE写法，用$进行分割，后面对应数据库中相应的条件
条件支持:LIKE,IN,GT,LT,NOT,NOTNULL等等
##触发器
每个表都支持触发器，如在html中可以通过以下方式定义:
```html
    <script trigger="select.before" language="groovy">
        def user = param.get("user");
        //添加条件
        if(user!=null)
            param.where([area_id:user.get('area_id')]);
        // else
        //   param.where([area_id:-1]);
        return true;
    </script>
```
触发器使用脚本引擎进行执行，语法目前支持js,groovy,spel,ognl。
触发器列表见:[Constant.java](https://github.com/wb-goup/webbuilder/blob/master/wb-sql-util/src/main/java/org/webbuilder/sql/Constant.java)
