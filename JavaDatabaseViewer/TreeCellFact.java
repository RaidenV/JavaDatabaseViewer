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

/**=========================================================================<p>
Name        TreeCellFact<p>

Purpose     A TreeCell factory for updating the central tree and generating 
            table views for the available leafs.<p>

History     07 Apr 18   AFB     Created<p>
==========================================================================**/
public class TreeCellFact extends TreeCell<TreeData>
{
    /**=========================================================================<p>
    Name        updateItem<p>

    Purpose     Updates and provides item with necessary event monitoring.<p>

    History     07 Apr 18   AFB     Created<p>
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

    /**=========================================================================<p>
    Name        createContextMenu<p>

    Purpose     Creates a context menu above the given item.<p>

    @param      item TreeData - Item to which a context menu will reference.

    History     07 Apr 18   AFB     Created<p>
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


    /**=========================================================================<p>
    Name        isViewable<p>

    Purpose     Answers whether or not the selected TreeData is a viewable
                item (Table, Column) or not viewable (Root, Schema).<p>
    
    @return     boolean - True, can view, false, cannot view.

    History     07 Apr 18   AFB     Created<p>
    =========================================================================**/
    boolean isViewable()
    {
        return getLevel() >= 2;
    }
    
    /**=========================================================================<p>
    Name        genQuery<p>

    Purpose     Generates a SQL query based on the item calling it.<p>
    
    @return     String - string containing the query.

    History     07 Apr 18   AFB     Created<p>
    =========================================================================**/
    String genQuery( )
    {
        
        String itemColName = "*";
        if ( this.getLevel() >= 3 )
        {
           itemColName = this.getItem().getColumn();
        }
        String SQL = "SELECT " + itemColName + " FROM " + 
                     this.getItem().getSchema() + "." + 
                     this.getItem().getTable();
        
        return SQL;

    }

    /**=========================================================================<p>
    Name        getLevel<p>

    Purpose     Returns the level of the item within the TreeItem hierarchy.<p>
    
    @return     int - level of item (0 = Root)

    History     07 Apr 18   AFB     Created<p>
    =========================================================================**/
    int getLevel()
    {
        return this.getTreeView().getTreeItemLevel(this.getTreeItem());
    }
}
