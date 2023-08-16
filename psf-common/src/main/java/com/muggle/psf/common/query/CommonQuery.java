package com.muggle.psf.common.query;

import com.muggle.psf.common.base.BaseQuery;
import com.muggle.psf.common.exception.SimplePoseidonException;
import org.springframework.util.CollectionUtils;

import java.lang.reflect.Field;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


/**
 * @Description:
 * @Author: muggle
 * @Date: 2020/6/6
 **/
public class CommonQuery extends BaseQuery {

    private String finalSql;

    @Override
    public void processSql() {
        final Map<String, Operator> operatorMap = this.getOperatorMap();
        final StringBuilder builder = new StringBuilder();
        if (operatorMap != null) {
            final Iterator<String> iterator = operatorMap.keySet().iterator();
            while (iterator.hasNext()) {
                final String next = iterator.next();
                try {
                    final Object field = this.getFieldValue(next);
                    final Operator operator = operatorMap.get(next);
                    builder.append("AND ");
                    if ((field instanceof Number || Operator.leftLike.equals(operator) || Operator.allLike.equals(operator))) {
                        builder.append(String.format(next + " " + operator.getValue(), field));
                    } else {
                        builder.append(String.format(next + " " + operator.getValue(), "'" + field + "'"));
                    }
                } catch (final NoSuchFieldException | IllegalAccessException e) {
                    throw new SimplePoseidonException("查询参数异常：" + next);
                }
            }
        }

        final List<String> groupBy = this.getGroupBy();
        if (!CollectionUtils.isEmpty(groupBy)) {
            builder.append(" group by");
            for (int i = 0; i < groupBy.size(); i++) {
                if (i == groupBy.size() - 1) {
                    builder.append(groupBy.get(i));
                } else {
                    builder.append(groupBy.get(i) + ",");
                }
            }
        }
        this.finalSql = builder.toString();
    }

    private Object getFieldValue(final String next) throws NoSuchFieldException, IllegalAccessException {
        final Field field = this.getClass().getDeclaredField(next);
        //打开私有访问
        field.setAccessible(true);
        //获取属性值
        return field.get(this);
    }

    @Override
    public String getFinalSql() {
        return finalSql;
    }

    public void setFinalSql(final String finalSql) {
        this.finalSql = finalSql;
    }
}
