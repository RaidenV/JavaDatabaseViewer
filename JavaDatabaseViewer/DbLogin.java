package JavaDatabaseViewer;

import java.util.Optional;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;

/*========================
 *
 * @author Alex Baum
 * Date: Apr 7, 2018
 * Time: 9:47:16 AM
 *
 *========================*/

/**=========================================================================
Name        DbLogin

Purpose     Creates a Database Login dialog.

History     07 Apr 18   AFB     Created
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
    
    /**=========================================================================
    Name        dbLoginDialog

    Purpose     Generates a dialog for obtaining the URL, Username, and Password 
                for a Java SQL database.
                <p>
                This method always returns a string, the string will contain 
                null items if the user canceled the dialog instead of attempting 
                to login.
    
    @see        Dialog
    
    @return     String [] - Contains login credentials for connecting to a
                            database [0]=URL, [1]=USERNAME, [2]=PASSWORD

    History     07 Apr 18   AFB     Created
    =========================================================================**/
    public static String[] dbLoginDialog()
    {
        // Create the custom dialog.
        Dialog<String[]> dialog = new Dialog<>();
        dialog.setTitle("Login");
        dialog.setHeaderText("Database Access");

        // Set the button types.
        ButtonType loginButtonType = new ButtonType("Login", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(loginButtonType, ButtonType.CANCEL);
        Label urlLbl = new Label("URL");
        Label usernameLbl = new Label("User");
        Label passLbl = new Label("Password");
        TextField url = new TextField();
        TextField username = new TextField();
        PasswordField pass = new PasswordField();
        url.setText("jdbc:derby:/home/raidenv/Documents/School_CLU/CSC-400_Graphical_User_Interface/GroupProject/src/groupproject/BirtSample");
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
            else
            {
                String[] str =
                {
                    null, null, null
                };
                return str;
            }
        });

        // Wait for it...
        Optional<String[]> result = dialog.showAndWait();

        return result.get();
    }

    private DbLogin(){}
}
