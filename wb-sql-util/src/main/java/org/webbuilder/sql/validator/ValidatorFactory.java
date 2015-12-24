package org.webbuilder.sql.validator;

import org.webbuilder.sql.TableMetaData;

/**
 * Created by 浩 on 2015-12-24 0024.
 */
public interface ValidatorFactory {
    Validator getValidator(TableMetaData metaData);

    Validator initValidator(TableMetaData metaData) ;
}
