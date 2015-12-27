package org.webbuilder.web.core.dao.interceptor;

import org.webbuilder.utils.common.StringUtils;

import java.util.Map;

/**
 * Created by 浩 on 2015-09-30 0030.
 */
public interface SqlWrapper {
    String wrapper(WrapperConf conf);

    class WrapperConf {
        public String sql;
        public int pageSize = 20;
        public int pageIndex = 0;
        public String sortField;
        public String sortOrder;

        public boolean doPaging = true;

        public String getSql() {
            return sql;
        }

        public void setSql(String sql) {
            this.sql = sql;
        }

        public int getPageSize() {
            return pageSize;
        }

        public void setPageSize(int pageSize) {
            if (pageSize == 0) pageSize = 20;
            this.pageSize = pageSize;
        }

        public String getSortField() {
            return sortField;
        }

        public void setSortField(String sortField) {
            this.sortField = sortField;
        }

        public String getSortOrder() {
            return sortOrder;
        }

        public void setSortOrder(String sortOrder) {
            this.sortOrder = sortOrder;
        }

        public int getPageIndex() {
            return pageIndex;
        }

        public void setPageIndex(int pageIndex) {
            this.pageIndex = pageIndex;
        }

        public boolean isDoPaging() {
            return doPaging;
        }

        public void setDoPaging(boolean doPaging) {
            this.doPaging = doPaging;
        }

        public static WrapperConf fromMap(Map<String, Object> param) {
            WrapperConf wrapperConf = new WrapperConf();
            int pageSize = StringUtils.toInt(param.get("pageSize"));
            int pageIndex = StringUtils.toInt(param.get("pageIndex"));
            if (pageSize == 0 && pageIndex == 0)
                wrapperConf.setDoPaging(false);
            try {
                String sortField = (String) param.get("sortField");
                String sortOrder = (String) param.get("sortOrder");
                wrapperConf.setSortField(sortField);
                wrapperConf.setSortOrder(sortOrder);
            } catch (Exception e) {
            }
            wrapperConf.setPageSize(pageSize);
            wrapperConf.setPageIndex(pageIndex);
            return wrapperConf;
        }
    }
}
