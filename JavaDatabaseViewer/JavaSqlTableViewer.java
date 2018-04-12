package JavaDatabaseViewer;

import java.sql.*;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**=========================================================================
Name        JavaSqlTableViewer

Purpose     Simple application for connecting and viewing a database.

History     07 Apr 18   AFB     Created
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

    public void start( Stage primaryStage ) throws ClassNotFoundException, SQLException
    {
        VBox root = new VBox();
        root.setSpacing(10);
        Scene scene = new Scene(root, 800, 600);
        this.setMinWidth( 800 );
        this.setMinHeight( 600 );
        createCloseMenu(root);
        createMain(root);

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

    public void setDbName( String dbName )
    {
        mDbName = dbName;
    }

    public void setConnection( Connection conn )
    {
        mConn = conn;
    }

    /**=========================================================================
    Name        createTree

    Purpose     Creates a TreeView within a BorderPane
    
    @param root BorderPane - the type of layout we'll be using
    
    @see        BorderPane
    @see        TreeView
   
    History     07 Apr 18   AFB     Created
    =========================================================================**/
    void createMain( VBox root )
    {
        HBox hbox = new HBox();
        hbox.setSpacing( 10 );

        TreeView<TreeData> tree = new TreeView<>();
        tree.setRoot(mTreeItems);
        tree.setMinWidth( 250 );

        mTreeItems.getChildren().forEach(( child ) ->
        {
            touchTrees(child);
        });

        VBox vbox = new VBox();

        tree.setCellFactory(( TreeView<TreeData> param ) -> new TreeCellFact());
        tree.setPadding(new Insets(10, 10, 10, 10));

        mTarea = new TextArea();

        HBox btnbox = new HBox();

        Button btn = new Button("Run Query");
        btn.setAlignment(Pos.CENTER_RIGHT);
        btn.setOnMouseClicked(( e ) ->
        {
            this.loadTable(mTarea.getText());
        });
        Separator hsep = new Separator();
        hsep.setPrefWidth( 300 );
        btnbox.getChildren().addAll( hsep, btn );

        mTpane = new TabPane();
        vbox.getChildren().addAll(mTarea, btnbox, mTpane);
        hbox.getChildren().addAll(tree, vbox);
        root.getChildren().add(hbox);
    }

    private void touchTrees( TreeItem<TreeData> ti )
    {
        ti.getValue().setParent(this);
        ti.getChildren().forEach(( elem ) ->
        {
            touchTrees(elem);
        });
    }

    /**=========================================================================
    Name        createMenu

    Purpose     Creates a menu within a BorderPane
    
    @param root BorderPane - the type of layout we'll be using
    
    @see        BorderPane
    @see        MenuBar
   
    History     07 Apr 18   AFB     Created
    =========================================================================**/
    void createCloseMenu( VBox root )
    {
        MenuBar menu = new MenuBar();
        Menu file = new Menu("File");
        menu.getMenus().add(file);

        MenuItem exit = new MenuItem("Exit");
        exit.setOnAction(( ActionEvent t ) ->
        {
            System.exit(0);
        });

        file.getItems().addAll(exit);
        root.getChildren().add(menu);
    }

    void createTab( TableView table )
    {
        Tab tab = new Tab();
        tab.setContent(table);
        mTpane.getTabs().add(tab);
    }

    /**=========================================================================
    Name        loadTable

    Purpose     Queries the database for the data relevant to the item.
    
    @param      lvl   int -  Level of the node in the tree
    @param      td    TreeData - the selected item
    @param      table TableView - tableview to load with data

    @see        TableView
    @see        TreeData

    History     07 Apr 18   AFB     Created
    =========================================================================**/
    void loadTable( String query )
    {
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
        }

        createTab(table);
    }
}
