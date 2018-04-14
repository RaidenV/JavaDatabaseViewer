package JavaDatabaseViewer;

import java.sql.Connection;

/*========================
 *
 * @author Alex Baum
 * Date: Apr 7, 2018
 * Time: 9:56:07 AM
 *
 *========================*/

/**=========================================================================<p>
Name        TreeData<p>

Purpose     Holds the necessary information contained within each TreeItem<p>

History     07 Apr 18   AFB     Created<p>
==========================================================================**/
public class TreeData
{
    public TreeData( String schema, String table, String column, Connection conn )
    {
        this.schema = schema;
        this.table = table;
        this.column = column;
        this.conn = conn;
        parent = null;
    }

    private final String schema;
    private final String table;
    private final String column;
    private final Connection conn;
    private JavaSqlTableViewer parent;

    public String getSchema()
    {
        return schema;
    }

    public String getTable()
    {
        return table;
    }

    public String getColumn()
    {
        return column;
    }
    
    public Connection getConnection()
    {
        return conn;
    }
    
    public void setParent( JavaSqlTableViewer p )
    {
        parent = p;
    }

    public JavaSqlTableViewer getParent()
    {
        return parent;
    }
    public String getStrFromLevel( int lvl )
    {
        switch ( lvl )
        {
            case 1:
                return schema;
            case 2:
                return table;
            case 3:
                return column;
            default:
                return "Root";
        }

    }    
          @Override
        public String toString()
        {
            String str = schema;
            if ( table != null )
                str = table;
            if ( column != null )
                str = column;
            return str;
        }
}
