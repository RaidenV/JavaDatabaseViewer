package JavaDatabaseViewer;

import java.sql.*;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

/**=========================================================================<p>
Name        JavaSqlTableViewer<p>

Purpose     Simple application for connecting and viewing a database.<p>

History     07 Apr 18   AFB     Created<p>
==========================================================================**/
public class JavaSqlTableViewer extends Stage
{

    private Connection mConn;
    private String mDbName;
    private final TreeItem<TreeData> mTreeItems;
    public TabPane mTpane;
    private TextArea mTarea;

    public JavaSqlTableViewer( TreeItem<TreeData> td )
    {
        mTpane = new TabPane();
        mTreeItems = td;
    }
    
    /**=========================================================================<p>
    Name        start<p>

    Purpose     Loads the scene onto the passed stage.<p>
    
    @param primaryStage Stage - The stage onto which the scene will be loaded.
    
    @throws java.lang.ClassNotFoundException
    @throws java.sql.SQLException
    
    @see        Stage
   
    History     07 Apr 18   AFB     Created<p>
    =========================================================================**/
    public void start( Stage primaryStage ) throws ClassNotFoundException, SQLException
    {
        final Group root = new Group();
        VBox vbox = new VBox();
        setGrow(vbox);
        
        vbox.setSpacing(10);
        Scene scene = new Scene(root, 1024, 800);
        vbox.prefWidthProperty().bind( scene.widthProperty() );
        vbox.prefHeightProperty().bind( scene.heightProperty() );
        root.getChildren().add( vbox );
        
        this.setMinWidth( 1024 );
        this.setMinHeight( 800 );
        createCloseMenu(vbox);
        createMain(vbox);

        this.setTitle(mDbName);

        primaryStage.setOnCloseRequest(( WindowEvent ) ->
        {
            try
            {
                System.out.println("DB NAME: " + mDbName);
                DriverManager.getConnection(mDbName);
                System.out.println("Close Success");
            }
            catch ( SQLException ex )
            {
                System.out.println("Close Failure\n"
                                   + "Error Code: " + ex.getErrorCode()
                                   + "SQL State: " + ex.getSQLState());

            }
        });
        primaryStage.setTitle(mDbName);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    /**=========================================================================<p>
    Name        setDbName<p>

    Purpose     Sets the name of the database to which this window is connected.<p>
    
    @param dbName String - The name of the database to which this window is
                           connected.
    
    @see        BorderPane
    @see        TreeView
   
    History     07 Apr 18   AFB     Created<p>
    =========================================================================**/
    public void setDbName( String dbName )
    {
        mDbName = dbName;
    }

    /**=========================================================================<p>
    Name        setConnection<p>

    Purpose     Sets the connection that belongs to this window (the connection
                that the window has ownership over).<p>
    
    @param conn Connection - Connection attached to this window
    
    @see        Connection
   
    History     13 Apr 18   AFB     Created<p>
    =========================================================================**/
    public void setConnection( Connection conn )
    {
        mConn = conn;
    }

    /**=========================================================================<p>
    Name        createMain<p>

    Purpose     Constructs the main window.<p>
    
    @param root VBox - the type of layout we'll be using
    
    @see        VBox
    @see        TreeView
   
    History     07 Apr 18   AFB     Created<p>
    =========================================================================**/
    void createMain( VBox root )
    { 
        HBox hbox = new HBox();
        setGrow(hbox);
        hbox.setPadding( new Insets( 10, 10, 10, 10));
        hbox.setSpacing( 10 );

        TreeView<TreeData> tree = new TreeView<>();
        tree.setRoot(mTreeItems);
        tree.setMinWidth( 250 );

        mTreeItems.getChildren().forEach(( child ) ->
        {
            touchTrees(child);
        });

        VBox vbox = new VBox();
        vbox.setSpacing( 10 );
        setGrow(vbox);

        tree.setCellFactory(( TreeView<TreeData> param ) -> new TreeCellFact());
        tree.setPadding(new Insets(10, 10, 10, 10));

        mTarea = new TextArea();
        HBox.setHgrow( mTarea, Priority.ALWAYS);

        HBox btnbox = new HBox();
        HBox.setHgrow( btnbox, Priority.ALWAYS );

        Button btn = new Button("Run Query");
        btn.setAlignment(Pos.CENTER_RIGHT);
        btn.setOnMouseClicked(( e ) ->
        {
            this.loadTable(mTarea.getText());
        });
        Region hsep = new Region();
        HBox.setHgrow( hsep, Priority.ALWAYS);
        hsep.setPrefWidth( Region.USE_PREF_SIZE );
        btnbox.getChildren().addAll( hsep, btn );

        mTpane = new TabPane();
        setGrow(mTpane);
        vbox.getChildren().addAll(mTarea, btnbox, mTpane);
        hbox.getChildren().addAll(tree, vbox);
        root.getChildren().add(hbox);
    }

    /**=========================================================================<p>
    Name        touchTrees<p>

    Purpose     Recursively traverses a TreeItem, setting the parent of each
                node.<p>
    
    @param ti   TreeItem<TreeData>
    
    @see        TreeItem
    @see        TreeData
   
    History     07 Apr 18   AFB     Created<p>
    =========================================================================**/
    private void touchTrees( TreeItem<TreeData> ti )
    {
        ti.getValue().setParent(this);
        ti.getChildren().forEach(( elem ) ->
        {
            touchTrees(elem);
        });
    }
    
    private void setGrow( Node n )
    {
        HBox.setHgrow(n, Priority.ALWAYS);
        VBox.setVgrow(n, Priority.ALWAYS);
    }

    /**=========================================================================<p>
    Name        createCloseMenu<p>

    Purpose     Creates a menu within a VBox<p>
    
    @param root VBox - the type of layout we'll be using
    
    @see        VBox
    @see        MenuBar
   
    History     07 Apr 18   AFB     Created<p>
    =========================================================================**/
    void createCloseMenu( VBox root )
    {
        MenuBar menu = new MenuBar();
        Menu file = new Menu("File");
        menu.getMenus().add(file);

        MenuItem exit = new MenuItem("Exit");
        exit.setOnAction(( ActionEvent t ) ->
        {
            this.close();
        });

        file.getItems().addAll(exit);
        root.getChildren().add(menu);
    }

    /**=========================================================================<p>
    Name        createTab<p>

    Purpose     Creates a tab and adds a TableView to that tab.<p>
    
    @param table TableView - Tableview containing visible data.
    
    @see        TableView
    @see        TabPane
   
    History     07 Apr 18   AFB     Created<p>
    =========================================================================**/
    void createTab( TableView table )
    {
        Tab tab = new Tab();
        tab.setContent(table);
        mTpane.getTabs().add(tab);
    }

    /**=========================================================================<p>
    Name        loadTable<p>

    Purpose     Queries the database and loads a table based on that query.<p>
    
    @param      query String - The query that we'll use to access data and
                                therefrom load a table.

    @see        TableView
    @see        TreeData

    History     07 Apr 18   AFB     Created<p>
    =========================================================================**/
    void loadTable( String query )
    {
        System.out.println( "Query: " + query );
        TableView table = new TableView();
        try
        {
            ResultSet rs = mConn.createStatement().executeQuery(query);
            ResultSetMetaData rsmd = rs.getMetaData();
            for ( int i = 1; i <= rsmd.getColumnCount(); i++ )
            {
                final int columnNum = i - 1;
                TableColumn<ObservableList<String>, String> col = new TableColumn<>(rsmd.getColumnName(i));
                col.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().get(columnNum)));
                table.getColumns().add(col);
            }

            ObservableList<ObservableList> data = FXCollections.observableArrayList();
            while ( rs.next() )
            {
                ObservableList<String> row = FXCollections.observableArrayList();
                for ( int i = 1; i <= rs.getMetaData().getColumnCount(); ++i )
                {
                    row.add(rs.getString(i));
                }
                data.add(row);
            }

            table.setItems(data);
        }
        catch ( SQLException ex )
        {
            System.out.println("Unable to complete query");
            return;
        }

        createTab(table);
    }
}
