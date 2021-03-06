package JavaDatabaseViewer;

import java.util.Optional;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.StageStyle;

/*========================
 *
 * @author Alex Baum
 * Date: Apr 7, 2018
 * Time: 9:47:16 AM
 *
 *========================*/

/**=========================================================================<p>
Name        DbLogin<p>

Purpose     Creates a Database Login dialog.<p>

<p>History     07 Apr 18   AFB     Created<p>
==========================================================================**/
public class DbLogin
{

    /**
     * Why? Because I like to use enumerations as implicit int conversions. 
     **/
    enum DiagResult
    {
        URL(0), USERNAME(1), PASSWORD(2);
        private final int value;

        private DiagResult( int value )
        {
            this.value = value;
        }

        public int getValue()
        {
            return value;
        }
    };
    
    /**=========================================================================<p>
    Name        dbLoginDialog<p>

    Purpose     Generates a dialog for obtaining the URL, Username, and Password 
                for a Java SQL database.
                <p>
    
    @see        Dialog
    
    @return     String [] - Contains login credentials for connecting to a
                            database [0]=URL, [1]=USERNAME, [2]=PASSWORD or null
                            if cancel was used.

    <p>History     07 Apr 18   AFB     Created<p>
    =========================================================================**/
    public static String[] dbLoginDialog() throws RuntimeException
    {
        // Create the custom dialog.
        Dialog<String[]> dialog = new Dialog<>();
        dialog.setTitle("Login");
        dialog.setHeaderText("Database Access");
        dialog.initStyle(StageStyle.UNDECORATED);

        // Set the button types.
        ButtonType loginButtonType = new ButtonType("Login", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(loginButtonType, ButtonType.CANCEL);
        Label urlLbl = new Label("URL");
        Label usernameLbl = new Label("User");
        Label passLbl = new Label("Password");
        TextField url = new TextField();
        TextField username = new TextField();
        PasswordField pass = new PasswordField();
        url.setText("");
        username.setText("root");

        // Create the grid and add the items to the appropriate locations
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(10, 10, 10, 10));
        grid.add(urlLbl, 0, 0);
        grid.add(url, 1, 0);
        grid.add(usernameLbl, 0, 1);
        grid.add(username, 1, 1);
        grid.add(passLbl, 0, 2);
        grid.add(pass, 1, 2);

        dialog.getDialogPane().setContent(grid);

        // Request focus on the username field by default.
        Platform.runLater(() -> url.requestFocus());

        // Convert the result to a username-password-pair when the login button is clicked.
        dialog.setResultConverter(dialogButton ->
        {
            if ( dialogButton == loginButtonType )
            {
                // Attempt to connect here;
                String[] str =
                {
                    url.getText(), username.getText(), pass.getText()
                };
                return str;
            }
           
            return null;
        });

        // Wait for it...
        Optional<String[]> result = dialog.showAndWait();
        
        if ( !result.isPresent() )
        {
            return null;
        }

        return result.get();
    }

    private DbLogin(){}
}
