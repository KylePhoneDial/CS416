package longbottom;

import longbottom.DAO.DAO;
import longbottom.accounts.Dashboard;
import longbottom.accounts.User;
import longbottom.pages.homePageController;
import longbottom.pages.sponsorsPageController;
import longbottom.util.DemoController; //import the needed controller classes here
import longbottom.chat.ChatController;
import longbottom.pages.contactPageController;
import longbottom.pages.aboutPageController;
import org.sql2o.Connection;
import org.sql2o.Sql2o;

import java.util.List;

import static spark.Spark.*;

public class LongBottomUniversity {

    public static Sql2o sql2o;

    public static void main(String[] args){

        //which local port application binds to
        port(1250);
        staticFiles.location("/public");

        get("/hello", (req, res) -> {
            return "hello world!";
        });

        // Simple controller test.
        // to see the results of this controller
        // In Postman, set the request type to POST then
        // paste this URL below where is says "Enter request URL":
        // localhost:1250/post?test=456
        post("/post", DemoController.testing);
        get("/chat", ChatController.chatHistory);
        post("/projects", Dashboard.projectPartial);

        get("/dashboard", Dashboard.userDashboard);
        get("/contact", contactPageController.serveContactPage);
        get("/about", aboutPageController.serveAboutPage);
        get("/home", homePageController.serveHomePage);
        get("/sponsors", sponsorsPageController.serveSponsorsPage);

        //CONNECT TO MYSQL DATABASE
        DAO.sql2o = new Sql2o("jdbc:mysql://localhost:3306/long_bottom_university", "admin", "$80k");

    }
}
