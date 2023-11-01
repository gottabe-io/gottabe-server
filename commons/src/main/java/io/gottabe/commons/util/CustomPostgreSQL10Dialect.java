package io.gottabe.commons.util;

import org.hibernate.dialect.PostgreSQL95Dialect;
import org.hibernate.dialect.function.SQLFunctionTemplate;
import org.hibernate.type.BooleanType;

public class CustomPostgreSQL10Dialect extends PostgreSQL95Dialect {

    public CustomPostgreSQL10Dialect() {
        super();
        registerFunction("similarTo", new SQLFunctionTemplate(BooleanType.INSTANCE, "(?1 ~ ?2)"));
    }
}