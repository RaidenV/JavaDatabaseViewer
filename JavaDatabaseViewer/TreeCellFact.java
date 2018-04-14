package JavaDatabaseViewer;

import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TreeCell;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;

/*========================
 *
 * @author Alex Baum
 * Date: Apr 7, 2018
 * Time: 10:07:14 AM
 *
 *========================*/

/**=========================================================================
Name        TreeCellFact

Purpose     A TreeCell factory for updating the central tree and generating 
            table views for the available leafs.

History     07 Apr 18   AFB     Created
==========================================================================**/
public class TreeCellFact extends TreeCell<TreeData>
{
    /**=========================================================================
    Name        updateItem

    Purpose     Updates and provides item with necessary event monitoring.s

    History     07 Apr 18   AFB     Created
    =========================================================================**/
    @Override
    public void updateItem( TreeData item, boolean empty )
    {
        super.updateItem(item, empty);
        if ( empty )
        {
            this.setText(null);
        }

        if ( item != null )
        {
            this.setText(item.getStrFromLevel(this.getLevel()));

            this.setOnMouseClicked((MouseEvent me) ->
            {
                if ( me.getButton().equals(MouseButton.PRIMARY) )
                {
                    // If this is a double-click
                    if ( me.getClickCount() == 2 )
                    {
                        // If this is a leaf
                        if ( isViewable() )
                        {
                            item.getParent().loadTable( genQuery() );
                        }
                    }
                }
                else
                {
                    if ( me.getButton().equals(MouseButton.SECONDARY) )
                    {
                        if ( isViewable() )
                        {
                            createContextMenu(item);
                        }
                    }
                }
            });
        }
    }

    /**=========================================================================
    Name        createContextMenu

    Purpose     Creates a context menu above the given item.

    @param      item TreeData - Item to which a context menu will reference.

    History     07 Apr 18   AFB     Created
    =========================================================================**/
    public void createContextMenu( TreeData item )
    {
        final ContextMenu contextMenu = new ContextMenu();
        MenuItem open = new MenuItem("Open Table");
        open.setOnAction((ActionEvent) ->
        {
            //makeWindow(item);
            item.getParent().loadTable( this.genQuery() );
        });

        contextMenu.getItems().add(open);

        this.setOnContextMenuRequested((ContextMenuEvent event) ->
        {
            contextMenu.show(this.getTreeView(), event.getScreenX(), event.getScreenY());
        });
    }


    /**=========================================================================
    Name        isViewable

    Purpose     Answers whether or not the selected TreeData is a viewable
                item (Table, Column) or not viewable (Root, Schema).
    
    @return     boolean - True, can view, false, cannot view.

    History     07 Apr 18   AFB     Created
    =========================================================================**/
    boolean isViewable()
    {
        return getLevel() >= 2;
    }
    
    /**=========================================================================
    Name        genQuery

    Purpose     Generates a SQL query based on the item calling it.
    
    @return     String - string containing the query.

    History     07 Apr 18   AFB     Created
    =========================================================================**/
    String genQuery( )
    {
        
        String itemColName = "*";
        if ( this.getLevel() >= 3 )
        {
            this.getItem().getColumn();
        }
        String SQL = "SELECT " + itemColName + " FROM " + 
                     this.getItem().getSchema() + "." + 
                     this.getItem().getTable();
        
        return SQL;

    }

    /**=========================================================================
    Name        getLevel

    Purpose     Returns the level of the item within the TreeItem hierarchy.
    
    @return     int - level of item (0 = Root)

    History     07 Apr 18   AFB     Created
    =========================================================================**/
    int getLevel()
    {
        return this.getTreeView().getTreeItemLevel(this.getTreeItem());
    }
}
