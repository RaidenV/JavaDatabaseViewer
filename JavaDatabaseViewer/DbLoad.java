package JavaDatabaseViewer;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import javafx.scene.control.TreeItem;

/*========================
 *
 * @author Alex Baum
 * Date: Apr 7, 2018
 * Time: 9:54:03 AM
 *
 *========================*/

/**=========================================================================<p>
Name        DbLoad<p>

Purpose     Loads a database.<p>

History     07 Apr 18   AFB     Created<p>
==========================================================================**/
public class DbLoad
{

    Connection mConnection;
    boolean mAccessedDb;
    String mDbUrl;

    public DbLoad()
    {
        mConnection = null;
        mAccessedDb = false;
        mDbUrl = "";
    }

    /**=========================================================================<p>
    Name        dbConnect<p>

    Purpose     Connects to a database and loads the items if available.<p>
    
    @param      url     String - URL of the database
    @param      uname   String - Username of the database
    @param      passwd  String - Password for the database
    @param      items   TreeItem - hierarchy to be loaded
    
    @throws java.sql.SQLException
    
    @see        TreeItem
    @see        TreeData
    
    @return     Connection - A successful database connection or null if failed

    History     07 Apr 18   AFB     Created<p>
    =========================================================================**/
    public Connection dbConnect( String url, 
                                 String uname, 
                                 String passwd, 
                                 TreeItem<TreeData> items ) 
        throws SQLException
    {
        Connection connection = null;
        try
        {
            connection = DriverManager.getConnection(url,
                uname,
                passwd);
        }
        catch ( SQLException ex )
        {
            System.out.println("Unable to connect to database");
            throw ex;
        }
        if ( connection != null )
        {
            mDbUrl = url;
            mConnection = connection;
            if ( mAccessedDb )
            {
                items = new TreeItem<>(new TreeData("Root","Root","Root", null));
            }
            mAccessedDb = true;
        }
        else
        {
            return null;
        }

        dbLoad( connection, items );
 
        return connection;
    }
    
    /**=========================================================================<p>
    Name        dbLoad<p>

    Purpose     Loads a database into a given TreeItem hierarchy<p>
    
    @param      conn    Connection - Valid connection to a database
    @param      items   TreeItem<TreeData> - hierarchy to be loaded
    
    @see        Connection
    @see        TreeItem
    @see        TreeData
    
    History     07 Apr 18   AFB     Created<p>
    =========================================================================**/
    private void dbLoad( Connection conn, TreeItem<TreeData> items )
    {
        try
        {
            DatabaseMetaData dbmd = conn.getMetaData();
            ResultSet sc = dbmd.getSchemas();
            int count = 0;
            while ( sc.next() )
            {
                items.getChildren().add(new TreeItem<>(
                        new JavaDatabaseViewer.TreeData(
                            sc.getString("TABLE_SCHEM"),
                            null,
                            null,
                            conn)));
                
                ResultSet tb = dbmd.getTables(null,
                                          sc.getString("TABLE_SCHEM"),
                                          null,
                                          null);
                int tbCount = 0;
                while ( tb.next() )
                {
                    items.getChildren().get(count).
                        getChildren().add(new TreeItem<>(
                                new JavaDatabaseViewer.TreeData(
                                    sc.getString("TABLE_SCHEM"),
                                    tb.getString("TABLE_NAME"),
                                    null,
                                    conn)));
                    ResultSet co = dbmd.getColumns(null,
                                               null,
                                               tb.getString("TABLE_NAME"),
                                               null);
                    while ( co.next() )
                    {
                        items.getChildren().get(count).
                            getChildren().get(tbCount).
                            getChildren().add(new TreeItem<>(
                                    new JavaDatabaseViewer.TreeData(
                                        sc.getString("TABLE_SCHEM"),
                                        tb.getString("TABLE_NAME"),
                                        co.getString("COLUMN_NAME"),
                                        conn)));
                    }
                    tbCount++;
                    
                }
                count++;
            }
        }
        catch ( SQLException ex )
        {
            System.out.println( "Unable to load database" );
        }
    }

    /**=========================================================================<p>
    Name        getDbUrl<p>

    Purpose     Returns the database URL<p>
    
    @return     String  - Database URL
    
    History     07 Apr 18   AFB     Created<p>
    =========================================================================**/
    public String getDbUrl()
    {
        return mDbUrl;
    }
    
    /**=========================================================================<p>
    Name        getConnection<p>

    Purpose     Returns the database connection<p>
    
    @return     Connection  - Database connection
    
    @see        Connection
    
    History     07 Apr 18   AFB     Created<p>
    =========================================================================**/
    public Connection getConnection()
    {
        return mConnection;
    }
    
    /**=========================================================================<p>
    Name        getConnected<p>

    Purpose     Returns whether the connection was successful.<p>
    
    @return     boolean  - True = successful, false = unsuccessful
    
    History     07 Apr 18   AFB     Created<p>
    =========================================================================**/
    public boolean getConnected()
    {
        return mAccessedDb;
    }

}
