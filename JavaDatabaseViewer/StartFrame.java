package JavaDatabaseViewer;

import static JavaDatabaseViewer.DbLogin.DiagResult.PASSWORD;
import static JavaDatabaseViewer.DbLogin.DiagResult.URL;
import static JavaDatabaseViewer.DbLogin.DiagResult.USERNAME;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TreeItem;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

/**
 *
 * @author alexander.baum
 */
public class StartFrame extends Application
{

    /**=========================================================================<p>
    Name        start<p>

    Purpose     Loads the scene onto the passed stage.<p>
    
    @param primaryStage Stage - The stage onto which the scene will be loaded.
    
    @see        Stage
   
    History     07 Apr 18   AFB     Created<p>
    =========================================================================**/
    @Override
    public void start( Stage primaryStage )
    {
        BorderPane root = new BorderPane();

        createMenu(root);

        Scene scene = new Scene( root, 300, 30 );

        primaryStage.setTitle("Java SQL Browser");
        primaryStage.setResizable( false );
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    /**=========================================================================<p>
    Name        createMenu<p>

    Purpose     Creates a menu within a BorderPane<p>
    
    @param root BorderPane - the type of layout we'll be using
    
    @see        BorderPane
    @see        MenuBar
   
    History     07 Apr 18   AFB     Created<p>
    =========================================================================**/
    void createMenu( BorderPane root )
    {
        MenuBar menu = new MenuBar();
        Menu file = new Menu("File");
        menu.getMenus().add(file);

        MenuItem connect = new MenuItem("Connect");
        connect.setOnAction(( ActionEvent t ) ->
        {
            Connection conn = null;
            TreeItem<TreeData> items = new TreeItem<>();
            StringBuilder dbName = new StringBuilder();
            JavaSqlTableViewer viewer = new JavaSqlTableViewer(items);

            if ( (conn = loadDb(items, dbName )) != null )
            {
                Stage newStage = new Stage();
                newStage.setTitle(dbName.toString());
                viewer.setDbName(dbName.toString());
                viewer.setConnection( conn );
                if ( conn == null )
                {
                    System.out.println( "CONN IS NULL HERE!!!!" );
                }

                try
                {
                    viewer.start(newStage);
                }
                catch ( ClassNotFoundException | SQLException ex )
                {
                    Logger.getLogger(StartFrame.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });

        MenuItem exit = new MenuItem("Exit");
        exit.setOnAction(( ActionEvent t ) ->
        {
            System.exit(0);
        });

        file.getItems().addAll(connect, exit);
        root.setTop(menu);
    }

    /**=========================================================================
    Name        loadDb

    Purpose     Loads a database
    
    @see TreeItem
    @see DbLogin
    @see DbLoad
    @see TreeData
    @see TreeCellFact
   
    History     07 Apr 18   AFB     Created
    =========================================================================**/
    Connection loadDb( TreeItem<TreeData> ti, StringBuilder dbName )
    {
        DbLoad db = new DbLoad();
        Connection conn;
        try
        {
            // Open a dialog and get credentials from user;
            String[] cred = DbLogin.dbLoginDialog();
            // Check if null was returned;
            if ( cred == null )
            {
                return null;
            }
            // Load the database based on the user credentials; 
            conn = db.dbConnect(cred[URL.getValue()],
                         cred[USERNAME.getValue()],
                         cred[PASSWORD.getValue()],
                         ti);
        }
        catch ( SQLException ex )
        {
            // If we were unable to connect to the database, pop an alert!
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Database Not Found");
            alert.setContentText("The database login information\n"
                                 + "entered was incorrect.");
            alert.showAndWait();
            return null;
        }
        dbName.append(db.getDbUrl());
        
        return conn;
    }

    /**
     * @param args the command line arguments
     */
    public static void main( String[] args )
    {
        launch(args);
    }

}
