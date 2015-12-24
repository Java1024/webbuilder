package org.webbuilder.web.service.form;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.webbuilder.sql.DataBase;
import org.webbuilder.sql.Insert;
import org.webbuilder.sql.Table;
import org.webbuilder.sql.TableMetaData;
import org.webbuilder.sql.exception.CreateException;
import org.webbuilder.sql.exception.TriggerException;
import org.webbuilder.sql.param.insert.InsertParam;
import org.webbuilder.sql.parser.CommonTableMetaDataParser;
import org.webbuilder.sql.parser.TableMetaDataParser;
import org.webbuilder.sql.trigger.TriggerResult;
import org.webbuilder.utils.base.file.CallBack;
import org.webbuilder.utils.base.file.FileUtil;
import org.webbuilder.web.core.aop.transactional.TransactionDisabled;
import org.webbuilder.web.po.form.Form;

import javax.annotation.Resource;
import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 默认table工厂
 * Created by 浩 on 2015-11-17 0017.
 */
@Service
public class DefaultTableFactory {

    protected Logger logger = LoggerFactory.getLogger(this.getClass());

    private DataBase dataBase;

    private TableMetaDataParser parser = new CommonTableMetaDataParser();

    private org.springframework.core.io.Resource[] localFiles;

    private String bakPath = "/webbuilder/table_bak";

    private boolean autoAlter = true;
    @Resource
    private FormService formService;

    @TransactionDisabled
    public void init() {
        initLocalFiles();
        initDb();
    }

    @TransactionDisabled
    public void initLocalFiles() {
        if (getLocalFiles() == null) return;
        for (org.springframework.core.io.Resource localFile : localFiles) {
            try {
                File file = localFile.getFile();
                if (file.getName().endsWith(".html")) {
                    initLocalFile(file);
                }
            } catch (Exception e) {
                logger.error("init table error", e);
            }
        }
    }

    public void initLocalFile(File file) throws Exception {
        TableMetaData newTable = parser.parse(file, "file");
        newTable.setLocation(file.getAbsolutePath());
        if (newTable.getName() == null) {
            newTable.setName(file.getName().split("[.]")[0]);
        }
        String oldName = newTable.getName();
        TableMetaData old_ver = getLastVer(oldName);
        //获取旧版本
        if (old_ver != null) {
            old_ver.setName(oldName);
            if (!autoAlter) {
                dataBase.getMetaData().addTable(newTable);
            } else {
                dataBase.getMetaData().addTable(old_ver);
                dataBase.alterTable(newTable);
            }
        } else {//无备份说明是新表
            if (!autoAlter) {
                dataBase.getMetaData().addTable(newTable);
            } else {
                try {
                    Table table = dataBase.createTable(newTable);
                    try {
                        //加载初始化数据
                        TriggerResult result = table.getMetaData().on("init.data");
                        if (result != null && result.isSuccess()) {
                            Object data = result.getData();
                            Insert insert = table.createInsert();
                            if (data instanceof Collection) {
                                for (Object d : ((Collection) data)) {
                                    insert.insert(new InsertParam().values(((Map) d)));
                                }
                            }
                            if (data instanceof Map) {
                                insert.insert(new InsertParam().values(((Map) data)));
                            }
                        }
                    } catch (TriggerException e) {
                    }
                } catch (CreateException e) {
                    logger.debug("表{}已存在，忽略创建!", oldName);
                }
            }
        }
        //备份
        if (autoAlter) {
            String content = FileUtil.readFile2String(file.getAbsolutePath());
            bakTable(oldName, content);
        }
        logger.debug("init table success {}", file);
    }

    protected void bakTable(String tableName, String content) {
        File bak = new File(bakPath.concat("/").concat(tableName));
        if (!bak.exists()) {
            bak.mkdirs();
        }
        File lastFile = new File(bak, tableName.concat(String.format("ver.%s.bak", String.valueOf(System.currentTimeMillis()))));
        try {
            FileUtil.writeString2File(content, lastFile.getAbsolutePath(), "utf-8");
        } catch (Exception e) {
            logger.error("备份失败", e);
        }
    }

    protected TableMetaData getLastVer(String tableName) {
        //备份目录
        File bak = new File(bakPath.concat("/").concat(tableName));
        if (!bak.exists()) {
            bak.mkdirs();
        }
        final File[] last = new File[1];
        //获取最后版本的文件
        FileUtil.readFile(bak.getAbsolutePath(), true, new CallBack() {
            @Override
            public void isFile(File file) {
                if (file.getName().endsWith(".bak")) {
                    File lst = last[0];
                    if (lst == null) {
                        lst = file;
                    } else {
                        if (file.lastModified() > lst.lastModified()) {
                            lst = file;
                        }
                    }
                    last[0] = lst;
                }
            }

            @Override
            public void isDir(File dir) {

            }

            @Override
            public void readError(File file, Throwable e) {

            }
        });
        File lastFile = last[0];
        if (lastFile != null) {
            try {
                String content = FileUtil.readFile2String(lastFile.getAbsolutePath());
                TableMetaData old = parser.parse(content, "html");
                old.setLocation(lastFile.getAbsolutePath());
                return old;
            } catch (Exception e) {
                logger.error("读取备份失败", e);
            }
        }
        return null;
    }

    @TransactionDisabled
    public void initDb() {
        try {
            Map<String, Object> map = new HashMap<>();
            map.put("db_name", dataBase.getMetaData().getName());
            List<Form> forms = formService.select(map);
            for (Form form : forms) {
                TableMetaData tableMetaData = parser.parse(form.getContent(), "html");
                tableMetaData.setName(form.getName());
                dataBase.getMetaData().addTable(tableMetaData);
                logger.debug("init table success {}", form.getU_id());
            }
        } catch (Exception e) {
            logger.error("init table error", e);
        }
    }

    @TransactionDisabled
    public DataBase getDataBase() {
        return dataBase;
    }

    @TransactionDisabled
    public void setDataBase(DataBase dataBase) {
        this.dataBase = dataBase;
    }

    @TransactionDisabled
    public void setParser(TableMetaDataParser parser) {
        this.parser = parser;
    }

    @TransactionDisabled
    public TableMetaDataParser getParser() {
        return parser;
    }

    @TransactionDisabled
    public org.springframework.core.io.Resource[] getLocalFiles() {
        return localFiles;
    }

    @TransactionDisabled
    public void setLocalFiles(org.springframework.core.io.Resource[] localFiles) {
        this.localFiles = localFiles;
    }

    @TransactionDisabled
    public String getBakPath() {
        return bakPath;
    }

    @TransactionDisabled
    public void setBakPath(String bakPath) {
        this.bakPath = bakPath;
    }

    @TransactionDisabled
    public void setAutoAlter(boolean autoAlter) {
        this.autoAlter = autoAlter;
    }

    @TransactionDisabled
    public boolean isAutoAlter() {
        return autoAlter;
    }

}
