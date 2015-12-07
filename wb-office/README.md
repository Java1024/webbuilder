# wb-office
用于对office文档进行操作

##读excel
```java
try (InputStream in = Resources.getResourceAsStream("User.xlsx")) {
    //读取excel为map
    List<Map<String, Object>> dataList = ExcelIO.read2Map(in);
    System.out.println(dataList);
} catch (Exception e) {
    e.printStackTrace();
}
```

```java
try (InputStream in = Resources.getResourceAsStream("User.xlsx")) {
    //设置表头与字段映射,可通过反射获取
    Map<String, String> mapper = new HashMap<>();
    mapper.put("姓名", "name");
    mapper.put("年龄", "age");
    mapper.put("备注", "remark");
    //解析为User对象集合
    List<User> dataList = ExcelIO.read2Bean(in, mapper, User.class);
    System.out.println(dataList);
} catch (Exception e) {
    e.printStackTrace();
}
```