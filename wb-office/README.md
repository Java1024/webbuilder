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
##写出excel
```java
try (OutputStream outputStream = new FileOutputStream("test.xlsx")) {
        List<Header> headers = new LinkedList<>();//表头 Header{title:field}
        List<Object> datas = new ArrayList<>();// 数据集合
        ExcelIO.write(outputStream, headers, datas);
        outputStream.flush();
}
```

####自定义导出
```java
  try (OutputStream outputStream = new FileOutputStream("C:\\Users\\浩\\Desktop\\test_2.xlsx")) {
            ExcelWriterConfig config = new ExcelWriterConfig() {
                @Override
                public Object startBefore(int row, int column) {
                    //被跳过的行(代码[2、]处设置)填充此值
                    return "这是一个自动合并单元格并且自定义样式的示例";
                }

                @Override
                public CustomCellStyle getCellStyle(int row, int column, String header, Object value) {
                    CustomCellStyle style = super.getCellStyle(row, column, header, value);
                    //不为表头并且为姓名列
                    if (row > 0 && "姓名".equals(header)) {
                        //设置红色
                        style.setFontColor(HSSFColor.RED.index);
                    } else {
                        style.setFontColor(HSSFColor.BLACK.index);
                    }
                    return style;
                }

                @Override
                public CustomRowStyle getRowStyle(int row, String header) {
                    if (row == -1) {
                        //表头高度
                        return new CustomRowStyle(20);
                    }
                    if (row == 0) {
                        //第一行的高度
                        return new CustomRowStyle(50);
                    }
                    return null;
                }

                @Override
                public CustomColumnStyle getColumnStyle(int column, String header) {
                    //设置姓名列的宽度
                    if ("姓名".equals(header)) {
                        return new CustomColumnStyle(5000);
                    }
                    return null;
                }
            };
            //设置表头和数据
            config.setHeaders(headers);
            config.setDatas(datas);
            //1、自动合并年级和班级相同的列
            config.mergeColumn("grade", "classes", "sex");
            //2、从第2行开始写出
            config.setStartWith(1);
            //3、合并第一行的第一列到第六列,因为设置了startWith起始行号为1,所以第一列为-1
            config.addMerge(-1, 0, 5, -1);

            //第二个sheet
            ExcelWriterConfig config2 = new ExcelWriterConfig();
            config2.setSheetName("第二个");
            //设置表头和数据
            config2.setHeaders(headers);
            config2.setDatas(datas);
            //写出
            ExcelIO.write(outputStream, config, config2);
            outputStream.flush();
}
```

####按模板导出
完整演示请看单元测试

在单元格中定义表达式来渲染数据
表达式以${开头,以}结尾,如:${标题}。
目前语法解析使用groovy引擎，所以表达式支持groovy语法
模板当前版本支持的语法有:

|             语法              |             说明                        | 备注                      |
| ------------------------------|:--------------------------------------:| -------------------------:|
| def user=[name:"张三",age:10] |              定义变量                    |          /              |
| user.name                     |              引用变量                    |     /            |
| user.age+"岁"                 | 逻辑运算                                 |        /             |
| user.sex?"男":"女"            |  三目运算                                |     /     |
| for(data in list)            |   循环输出                                |   循环目前仅针对行循环,因此表达式必须在同一行。并且以/for结束循环      |
-
```
     try (InputStream inputStream = Resources.getResourceAsStream("template.xlsx")
                 ; OutputStream outputStream = new FileOutputStream("d:\\test_template.xlsx")) {
                 //模板变量
                Map<String, Object> var = new HashMap<>();
                var.put("标题", "测试");
                var.put("list", dataList);
                //导出模板
                ExcelIO.writeTemplate(inputStream, outputStream, var);
                outputStream.flush();
                }
```
注意: 单元格只会输出变量引用的返回值,且仅会输出一个。
如果一个单元格有多个变量引用表达式,将只输出最后一个。
如果要进行内容拼接,可以在表达式中进行拼接


##WORD 文档操作

####模板写出
目前版本仅支持2007以上的word文档(.docx)
内部表达式引擎使用Groovy
```java
    //定义变量
    Map<String, Object> vars = new HashMap<>();
    //写出模板
    WordIO.writeTemplate(in, out, vars);
```
模板目前支持段落以及表格渲染
段落模板:
1、语法支持: ${name},${sex?"男":"女"}
表格模板:
2、支持段落模板语法以及循环,使用方式同excel
注意: 如果是循环渲染表格,表格的样式将被忽略