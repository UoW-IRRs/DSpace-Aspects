package nz.ac.waikato.its.dspace.app.xmlui.aspect.featuredcontent;

import org.dspace.app.xmlui.aspect.administrative.FlowResult;
import org.dspace.app.xmlui.wing.Message;
import org.dspace.content.Item;
import org.dspace.core.Context;

import java.io.IOException;
import java.sql.SQLException;

/**
 * Created by jjung on 9/02/16.
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
