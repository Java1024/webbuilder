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
try (OutputStream outputStream = new FileOutputStream("test.xlsx")) {
        List<Header> headers = new LinkedList<>();//表头 Header{title:field}
        List<Object> datas = new ArrayList<>();// 数据集合
        //创建写出配置
        ExcelWriterConfig config = new ExcelWriterConfig();
        //设置表头和数据
        config.setHeaders(headers);
        config.setDatas(datas);
        //1、自动合并相同的列
        config.mergeColumn("grade", "classes", "sex");
        //2、从第2行开始写出
        config.setStartWith(1);
        //3、合并第一行的第一列到第六列,因为设置了startWith起始行号为1,所以第一列为-1
        config.addMerge(-1, 0, 5, -1);
        config.setCallBack(new CommonExcelWriterCallBack() {
            @Override
            public Object startBefore(int row, int column) {
                //被跳过的行(代码[2、]处设置)填充此值
                return "这是一个自动合并单元格并且自定义样式的示例";
            }

            //此方法在渲染一个单元格的时候被调用,可返回自定义样式
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

             //此方法在渲染一个行的时候被调用,可返回自定义行样式
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
            //此方法在渲染一列的时候被调用,可返回自定义列样式
            @Override
            public CustomColumnStyle getColumnStyle(int column, String header) {
                //设置姓名列的宽度
                if ("姓名".equals(header)) {
                    return new CustomColumnStyle(5000);
                }
                return null;
            }
        });
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