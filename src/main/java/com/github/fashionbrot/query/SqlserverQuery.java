package com.github.fashionbrot.query;

import com.github.fashionbrot.common.util.ObjectUtil;
import com.github.fashionbrot.entity.ColumnEntity;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class SqlserverQuery extends AbstractQuery {


    @Override
    public String tablesListSql(String tableName) {

        StringBuilder sql = new StringBuilder();
        sql.append("select * from ");
        sql.append("( select cast(so.name as varchar(500)) as tableName, cast(sep.value as varchar(500)) as tableComment, getDate() as createTime ");
        sql.append("from sysobjects so ");
        sql.append("left JOIN sys.extended_properties sep on sep.major_id=so.id and sep.minor_id=0 ");
        sql.append("where (xtype='U' or xtype='v') ) ");
        sql.append("t where 1=1 ");
        if (ObjectUtil.isNotEmpty(tableName)) {
            sql.append(" and t.tableName like  '%" + tableName + "%' ");
        }
        sql.append("order by t.tableName ");

        return sql.toString();
    }

    @Override
    public String tablesSql(String tableName) {
        StringBuilder sql = new StringBuilder();
        sql.append("select * from ");
        sql.append("( select cast(so.name as varchar(500)) as tableName, cast(sep.value as varchar(500)) as tableComment, getDate() as createTime ");
        sql.append("from sysobjects so ");
        sql.append("left JOIN sys.extended_properties sep on sep.major_id=so.id and sep.minor_id=0 ");
        sql.append("where (xtype='U' or xtype='v') ) ");
        sql.append("t where 1=1 ");
        if (ObjectUtil.isNotEmpty(tableName)) {
            sql.append(" and t.tableName = '" + tableName + "' ");
        }
        sql.append("order by t.tableName ");
        return sql.toString();
    }

    @Override
    public String tableFieldsSql(String tableName) {
        /**
         * SELECT
         * 		cast(
         * 			b.NAME AS VARCHAR(500)
         * 		) AS columnName,
         * 		cast(
         * 			sys.types.NAME AS VARCHAR(500)
         * 		) AS dataType,
         * 		cast(
         * 			c.VALUE AS VARCHAR(500)
         * 		) AS columnComment,
         * 		(
         * 			SELECT
         * 				CASE
         * 					count( 1 )
         * 					WHEN 1 then 'PRI'
         * 					ELSE ''
         * 				END
         * 			FROM
         * 				syscolumns,
         * 				sysobjects,
         * 				sysindexes,
         * 				sysindexkeys,
         * 				systypes
         * 			WHERE
         * 				syscolumns.xusertype = systypes.xusertype
         * 				AND syscolumns.id = object_id(A.NAME)
         * 				AND sysobjects.xtype = 'PK'
         * 				AND sysobjects.parent_obj = syscolumns.id
         * 				AND sysindexes.id = syscolumns.id
         * 				AND sysobjects.NAME = sysindexes.NAME
         * 				AND sysindexkeys.id = syscolumns.id
         * 				AND sysindexkeys.indid = sysindexes.indid
         * 				AND syscolumns.colid = sysindexkeys.colid
         * 				AND syscolumns.NAME = B.NAME
         * 			) as columnKey,
         * 			'' as extra
         * 		FROM
         * 			(
         * 				select
         * 					name,
         * 					object_id
         * 				from
         * 					sys.tables
         * 			UNION all select
         * 					name,
         * 					object_id
         * 				from
         * 					sys.views
         * 			) a
         * 		INNER JOIN sys.COLUMNS b ON
         * 			b.object_id = a.object_id
         * 		LEFT JOIN sys.types ON
         * 			b.user_type_id = sys.types.user_type_id
         * 		LEFT JOIN sys.extended_properties c ON
         * 			c.major_id = b.object_id
         * 			AND c.minor_id = b.column_id
         * 		WHERE
         * 			a.NAME = #{tableName}
         * 			and sys.types.NAME != 'sysname'
         */
        String sql = "SELECT\n" +
                "cast(\n" +
                "\tb.NAME AS VARCHAR(500)\n" +
                ") AS columnName,\n" +
                "cast(\n" +
                "\tsys.types.NAME AS VARCHAR(500)\n" +
                ") AS dataType,\n" +
                "cast(\n" +
                "\tc.VALUE AS VARCHAR(500)\n" +
                ") AS columnComment,\n" +
                "(\n" +
                "\tSELECT\n" +
                "CASE\n" +
                "\tcount( 1 )\n" +
                "\tWHEN 1 then 'PRI'\n" +
                "\tELSE ''\n" +
                "END\n" +
                "\tFROM\n" +
                "syscolumns,\n" +
                "sysobjects,\n" +
                "sysindexes,\n" +
                "sysindexkeys,\n" +
                "systypes\n" +
                "\tWHERE\n" +
                "syscolumns.xusertype = systypes.xusertype\n" +
                "AND syscolumns.id = object_id(A.NAME)\n" +
                "AND sysobjects.xtype = 'PK'\n" +
                "AND sysobjects.parent_obj = syscolumns.id\n" +
                "AND sysindexes.id = syscolumns.id\n" +
                "AND sysobjects.NAME = sysindexes.NAME\n" +
                "AND sysindexkeys.id = syscolumns.id\n" +
                "AND sysindexkeys.indid = sysindexes.indid\n" +
                "AND syscolumns.colid = sysindexkeys.colid\n" +
                "AND syscolumns.NAME = B.NAME\n" +
                "\t) as columnKey,\n" +
                "\t'' as extra\n" +
                "FROM\n" +
                "\t(\n" +
                "select\n" +
                "\tname,\n" +
                "\tobject_id\n" +
                "from\n" +
                "\tsys.tables\n" +
                "\tUNION all select\n" +
                "\tname,\n" +
                "\tobject_id\n" +
                "from\n" +
                "\tsys.views\n" +
                "\t) a\n" +
                "INNER JOIN sys.COLUMNS b ON\n" +
                "\tb.object_id = a.object_id\n" +
                "LEFT JOIN sys.types ON\n" +
                "\tb.user_type_id = sys.types.user_type_id\n" +
                "LEFT JOIN sys.extended_properties c ON\n" +
                "\tc.major_id = b.object_id\n" +
                "\tAND c.minor_id = b.column_id\n" +
                "WHERE\n" +
                "\ta.NAME ='" + tableName + "'\n" +
                "\tand sys.types.NAME != 'sysname'";

        return sql;
    }

    @Override
    public boolean isKeyIdentity(ColumnEntity columnEntity) {
        return "PRI".equals(columnEntity.getColumnKey());
    }

    @Override
    public String formatColumn(String columnName) {
        String upperCase = columnName.toUpperCase();
        Optional<String> first = KEY_WORDS.stream().filter(m -> m.equals(upperCase)).findFirst();
        if (first.isPresent()){
            return String.format(formatStyle(),columnName);
        }
        return columnName;
    }

    private String formatStyle() {
        return "`%s`";
    }

    private static final List<String> KEY_WORDS = Arrays.asList("ADD","EXTERNAL","PROCEDURE","ALL","FETCH","PUBLIC","ALTER","FILE","RAISERROR","AND","FILLFACTOR","READ","ANY","FOR","READTEXT","AS","FOREIGN","RECONFIGURE","ASC","FREETEXT","REFERENCES","AUTHORIZATION","FREETEXTTABLE","复制","BACKUP","FROM","RESTORE","BEGIN","FULL","RESTRICT","BETWEEN","FUNCTION","RETURN","BREAK","GOTO","REVERT","BROWSE","GRANT","REVOKE","BULK","GROUP","RIGHT","BY","HAVING","ROLLBACK","CASCADE","HOLDLOCK","ROWCOUNT","CASE","IDENTITY","ROWGUIDCOL","CHECK","IDENTITY_INSERT","RULE","CHECKPOINT","IDENTITYCOL","SAVE","CLOSE","IF","SCHEMA","CLUSTERED","IN","SECURITYAUDIT","COALESCE","INDEX","SELECT","COLLATE","INNER","SEMANTICKEYPHRASETABLE","COLUMN","INSERT","SEMANTICSIMILARITYDETAILSTABLE","COMMIT","INTERSECT","SEMANTICSIMILARITYTABLE","COMPUTE","INTO","SESSION_USER","CONSTRAINT","IS","SET","CONTAINS","JOIN","SETUSER","CONTAINSTABLE","KEY","SHUTDOWN","CONTINUE","KILL","SOME","CONVERT","LEFT","STATISTICS","CREATE","LIKE","SYSTEM_USER","CROSS","LINENO","TABLE","CURRENT","LOAD","TABLESAMPLE","CURRENT_DATE","MERGE","TEXTSIZE","CURRENT_TIME","NATIONAL","THEN","CURRENT_TIMESTAMP","NOCHECK","TO","CURRENT_USER","NONCLUSTERED","TOP","CURSOR","NOT","TRAN","DATABASE","Null","TRANSACTION","DBCC","NULLIF","TRIGGER","DEALLOCATE","OF","TRUNCATE","DECLARE","OFF","TRY_CONVERT","DEFAULT","OFFSETS","TSEQUAL","DELETE","ON","UNION","DENY","OPEN","UNIQUE","DESC","OPENDATASOURCE","UNPIVOT","DISK","OPENQUERY","UPDATE","DISTINCT","OPENROWSET","UPDATETEXT","DISTRIBUTED","OPENXML","USE","DOUBLE","OPTION","USER","DROP","OR","VALUES","DUMP","ORDER","VARYING","ELSE","OUTER","VIEW","End","OVER","WAITFOR","ERRLVL","PERCENT","WHEN","ESCAPE","PIVOT","WHERE","EXCEPT","PLAN","WHILE","EXEC","PRECISION","WITH","EXECUTE","PRIMARY","WITHIN GROUP","EXISTS","PRINT","WRITETEXT","EXIT","PROC");


}
