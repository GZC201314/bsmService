package org.bsm.interceptor;

import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.executor.parameter.ParameterHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.mapping.ParameterMode;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.SystemMetaObject;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.type.TypeHandlerRegistry;
 
import java.sql.PreparedStatement;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;
 

@Slf4j
@Intercepts(value = {
  @Signature(type = ParameterHandler.class, method = "setParameters", args = {PreparedStatement.class})
})
public class ParameterInterceptorPlugin implements Interceptor {
  private final boolean printSql;
  public ParameterInterceptorPlugin(boolean printSql) {
    this.printSql = printSql;
  }

  @Override
  public Object intercept(Invocation invocation) throws Throwable {
    if (printSql){
      try {
        ParameterHandler parameterHandler = (ParameterHandler) invocation.getTarget();
        Object parameterObject = parameterHandler.getParameterObject();

        MetaObject metaObjectParameterHandler = SystemMetaObject.forObject(parameterHandler);
        BoundSql boundSql = (BoundSql) metaObjectParameterHandler.getValue("boundSql");
        Configuration configuration = (Configuration) metaObjectParameterHandler.getValue("configuration");
        TypeHandlerRegistry typeHandlerRegistry = (TypeHandlerRegistry) metaObjectParameterHandler.getValue("typeHandlerRegistry");

        List<Object> paramValues = new ArrayList<>();
        List<ParameterMapping> parameterMappings = boundSql.getParameterMappings();
        if (parameterMappings != null) {
          for (ParameterMapping parameterMapping : parameterMappings) {
            //  不为 函数 中的 输出值模式
            if (parameterMapping.getMode() != ParameterMode.OUT) {
              Object value;
              String propertyName = parameterMapping.getProperty();
              // issue #448 ask first for additional params
              //  问题#448首先请求额外的参数，foreach 操作影响
              if (boundSql.hasAdditionalParameter(propertyName)) {
                //  条件参数
                value = boundSql.getAdditionalParameter(propertyName);
              } else if (parameterObject == null) {
                value = null;
              } else if (typeHandlerRegistry.hasTypeHandler(parameterObject.getClass())) {
                value = parameterObject;
              } else {
                //  对象参数
                MetaObject metaObject = configuration.newMetaObject(parameterObject);
                //  获取 参数对象 中的属性值
                value = metaObject.getValue(propertyName);
              }
              paramValues.add(value);
            }
          }
        }
        log.info("====================== ");
        log.info("完整Sql 打印");
        log.info(toStringSql(paramValues, boundSql.getSql()));
        log.info("======================");

      } catch (Exception e) {
        e.printStackTrace();
      }
    }
    return invocation.proceed();
  }
 
  private final static SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
  private final static DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
  private final static DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
 
  private String toStringSql(List<Object> keys, String sql) {
    if (keys.isEmpty()) return sql;
    StringBuilder returnSQL = new StringBuilder();
    String[] subSQL = sql.split("\\?");
    for (int i = 0; i < keys.size(); i++) {
      if (keys.get(i) instanceof LocalDateTime) {
        returnSQL.append(subSQL[i]).append(" '").append(
          dateTimeFormatter.format((LocalDateTime) keys.get(i))
        ).append("' ");
      } else if (keys.get(i) instanceof LocalDate) {
        returnSQL.append(subSQL[i]).append(" '").append(dateFormatter.format((LocalDate) keys.get(i))).append("' ");
      } else if (keys.get(i) instanceof Date) {
        returnSQL.append(subSQL[i]).append(" '").append(formatter.format((java.util.Date) keys.get(i))).append("' ");
      } else {
        returnSQL.append(subSQL[i]).append(" '").append(keys.get(i)).append("' ");
      }
      if (i == keys.size() - 1) {
        try {
          returnSQL.append(subSQL[keys.size()]);
        } catch (Exception ignored) {
        }
      }
    }
    return returnSQL.toString();
  }
 
  @Override
  public Object plugin(Object target) {
    return Plugin.wrap(target, this);
  }
 
  @Override
  public void setProperties(Properties properties) {
 
  }
}