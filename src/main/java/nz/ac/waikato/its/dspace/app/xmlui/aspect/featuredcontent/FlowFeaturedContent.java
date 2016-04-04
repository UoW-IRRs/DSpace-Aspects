package nz.ac.waikato.its.dspace.app.xmlui.aspect.featuredcontent;

import org.apache.commons.lang3.StringUtils;
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
    private static final Message T_success = new Message("default", "xmlui.aspect.FeaturedContent.success");
    private static final Message T_missing_required_fields = new Message("default", "xmlui.aspect.FeaturedContent.missing_required_fields");

    public static FlowResult processPickFeaturedContent(String img_location, String link_target, String caption) throws SQLException, IOException {
        FlowResult result = new FlowResult();
        result.setContinue(false);

        if (StringUtils.isBlank(img_location)) {
            result.addError("img_location");
        }
        if (StringUtils.isBlank(link_target)) {
            result.addError("link_target");
        }
        if (StringUtils.isBlank(caption)) {
            result.addError("caption");
        }
        if (result.getErrors() != null && !result.getErrors().isEmpty()) {
            result.setMessage(T_missing_required_fields);
            result.setOutcome(false);
            return result;
        }

        FeaturedContentController.setFeaturedItem(img_location, link_target, caption);

        result.setOutcome(true);
        result.setContinue(true);
        result.setMessage(T_success);

        return result;
    }
}
