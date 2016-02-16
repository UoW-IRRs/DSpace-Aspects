package nz.ac.waikato.its.dspace.app.xmlui.aspect.featuredcontent;

import org.dspace.app.xmlui.aspect.administrative.FlowResult;
import org.dspace.app.xmlui.wing.Message;
import org.dspace.content.Item;
import org.dspace.core.Context;

import java.io.IOException;
import java.sql.SQLException;

/**
 * Created by jjung on 9/02/16.
 *
 *
 * Overview of FeaturedContent process (Featured Content Processing)
 *    a. 'sitemap.xmap'
 *        => <map:match pattern="...">
 *                 <map:call function="..."/>                               : call server-side javascript(featuredcontent.js)
 *        => <map:match type="request" pattern="administrative-continue">   : related with cocoon processing
 *    b. 'featuredcontent.js'
 *        => sendPageAndWait("...")                                         : Control Web page flow
 *        => invoke 'FlowFeaturedContent.processPickFeaturedContent()       : Invoke Java class to perform server-side jobs
 *    c. 'FlowFeaturedContent.java'
 *        => FlowResult                                                     : generate response which presents execution result
 *        => invoke 'FeaturedContentController.setFeaturedItem()'           : Perform server-side operation using Java Class
 *    d. 'FeaturedContentController.java'
 *        => getFeaturedItem/setFeaturedItem                                : Perform server-side operation (Backend operation)
 *
 */
public class FlowFeaturedContent {
    private static final Message T_success = new Message("default", "xmlui.aspect.FeaturedContent.FeaturedContentForm.success");

    public static FlowResult processPickFeaturedContent(String img_location, String link_target, String caption) throws SQLException, IOException {

        FlowResult result ;
        result = new FlowResult();
        result.setContinue(false);

        FeaturedContentController.setFeaturedItem(img_location, link_target, caption);

        result.setOutcome(true);
        result.setContinue(true);
        result.setMessage(T_success);

        return result;
    }
}
