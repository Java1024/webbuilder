package org.webbuilder.web.core.dao.interceptor.dialect;

import org.webbuilder.utils.common.StringUtils;

/**
 * Created by 浩 on 2015-09-30 0030.
 */
public class MysqlSqlWrapper extends AbstractSqlWrapper {
    @Override
    public String wrapper(WrapperConf conf) {
        StringBuilder builder = new StringBuilder();
        builder.append(this.formatSql(conf.getSql())); //sql格式化
        if (!StringUtils.isNullOrEmpty(conf.getSortField())) {
            builder.append(" order by ").append(conf.getSortField());
            if (!StringUtils.isNullOrEmpty(conf.getSortOrder())) {
                builder.append(" ").append(conf.getSortOrder().toLowerCase().equals("desc") ? "desc" : "asc");
            }
        }
        builder.append(" limit ").append(conf.getPageSize() * conf.getPageIndex()).append(",").append(conf.getPageSize() * (conf.getPageIndex() + 1));
        return builder.toString();
    }


}
