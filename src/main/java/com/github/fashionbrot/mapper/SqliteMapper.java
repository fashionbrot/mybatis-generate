package com.github.fashionbrot.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.github.fashionbrot.entity.ColumnEntity;
import com.github.fashionbrot.entity.TableEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * @author fashionbrot
 */
@Mapper
public interface SqliteMapper extends BaseMapper<TableEntity> {

    /**
     * 获取数据库表
     * @param tableName 查询条件
     * @return
     */
    List<TableEntity> tableList(@Param("tableName") String tableName);

    /**
     * 获取当前表
     * @param tableName 查询条件
     * @return
     */
    TableEntity queryTable(@Param("tableName") String tableName);
    /**
     * 获取表的列集合
     * @param tableName 查询条件
     * @return
     */
    List<ColumnEntity> queryColumns(@Param("tableName") String tableName);

    /**
     * 是否是主键
     * @return
     */
    default boolean isKeyIdentity(ColumnEntity columnEntity){
        if (columnEntity.getPrimaryKey()!=null &&columnEntity.getPrimaryKey()){
            return true;
        }
        return false;
    }

    static final List<String> SQLITE_KEYWORDS = Arrays.asList(
            "ABORT", "ACTION", "ADD", "AFTER", "ALL", "ALTER", "ANALYZE", "AND", "AS",
            "ASC", "ATTACH", "AUTOINCREMENT", "BEFORE", "BEGIN", "BETWEEN", "BY", "CASCADE",
            "CASE", "CAST", "CHECK", "COLLATE", "COLUMN", "COMMIT", "CONFLICT", "CONSTRAINT",
            "CREATE", "CROSS", "CURRENT", "CURRENT_DATE", "CURRENT_TIME", "CURRENT_TIMESTAMP",
            "DATABASE", "DEFAULT", "DEFERRABLE", "DEFERRED", "DELETE", "DESC", "DETACH", "DISTINCT",
            "DROP", "EACH", "ELSE", "END", "ESCAPE", "EXCEPT", "EXCLUDE", "EXCLUSIVE", "EXISTS",
            "EXPLAIN", "FAIL", "FILTER", "FIRST", "FOLLOWING", "FOR", "FOREIGN", "FROM", "FULL",
            "GLOB", "GROUP", "GROUPS", "HAVING", "IF", "IGNORE", "IMMEDIATE", "IN", "INDEX",
            "INDEXED", "INITIALLY", "INNER", "INSERT", "INSTEAD", "INTERSECT", "INTO", "IS",
            "ISNULL", "JOIN", "KEY", "LAST", "LEFT", "LIKE", "LIMIT", "MATCH", "MATERIALIZED",
            "NATURAL", "NO", "NOT", "NOTHING", "NOTNULL", "NULL", "NULLS", "OF", "OFFSET", "ON",
            "OR", "ORDER", "OTHERS", "OUTER", "OVER", "PARTITION", "PLAN", "PRAGMA", "PRECEDING",
            "PRIMARY", "QUERY", "RAISE", "RANGE", "RECURSIVE", "REFERENCES", "REGEXP", "REINDEX",
            "RELEASE", "RENAME", "REPLACE", "RESTRICT", "RETURNING", "RIGHT", "ROLLBACK", "ROW",
            "ROWS", "SAVEPOINT", "SELECT", "SET", "TABLE", "TEMP", "TEMPORARY", "THEN", "TIES",
            "TO", "TRANSACTION", "TRIGGER", "UNBOUNDED", "UNION", "UNIQUE", "UPDATE", "USING",
            "VACUUM", "VALUES", "VIEW", "VIRTUAL", "WHEN", "WHERE", "WINDOW", "WITH", "WITHOUT"
    );
    /**
     * 关键字格式化
     * @param columnName
     * @return
     */
    default String formatColumn(String columnName){
        String upperCase = columnName.toUpperCase();
        Optional<String> first = SQLITE_KEYWORDS.stream().filter(m -> m.equals(upperCase)).findFirst();
        if (first.isPresent()){
            return String.format(formatStyle(),columnName);
        }
        return columnName;
    }

    default String formatStyle() {
        return "`%s`";
    }
}
