package nz.ac.waikato.its.dspace.app.xmlui.aspect.featuredcontent;

import org.apache.commons.lang3.StringUtils;
import org.dspace.app.xmlui.cocoon.AbstractDSpaceTransformer;
import org.dspace.app.xmlui.wing.Message;
import org.dspace.app.xmlui.wing.WingException;
import org.dspace.app.xmlui.wing.element.*;
import org.dspace.authorize.AuthorizeException;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

/**
 * Created by jjung on 9/02/16.
 */
public class FeaturedContentForm extends AbstractDSpaceTransformer {
    /** Language strings */
    private static final Message T_dspace_home = message("xmlui.general.dspace_home");
    private static final Message T_featuredcontent_trail = message("xmlui.aspect.FeaturedContent.trail");
    private static final Message T_featuredcontent_title = message("xmlui.aspect.FeaturedContent.title");
    private static final Message T_head1 = message("xmlui.aspect.FeaturedContent.head1");
    private static final Message T_head_current = message("xmlui.aspect.FeaturedContent.FeaturedContentForm.head_current");
    private static final Message T_head_find = message("xmlui.aspect.FeaturedContent.FeaturedContentForm.head_find");
    private static final Message T_no_current = message("xmlui.aspect.FeaturedContent.FeaturedContentForm.no_current");
    private static final Message T_img_location = message("xmlui.aspect.FeaturedContent.form_img_location");
    private static final Message T_link_target = message("xmlui.aspect.FeaturedContent.form_link_target");
    private static final Message T_caption = message("xmlui.aspect.FeaturedContent.form_caption");
    private static final Message T_img_location_desc = message("xmlui.aspect.FeaturedContent.form_img_location_desc");
    private static final Message T_link_target_desc = message("xmlui.aspect.FeaturedContent.form_link_target_desc");
    private static final Message T_set_featured_content = message("xmlui.aspect.FeaturedContent.FeaturedContentForm.set_featured_content");

    @Override
    public void addPageMeta(PageMeta pageMeta) throws SAXException, WingException, SQLException, IOException, AuthorizeException {
        pageMeta.addTrailLink(contextPath + "/", T_dspace_home);
        pageMeta.addTrail().addContent(T_featuredcontent_trail);
        pageMeta.addMetadata("title").addContent(T_featuredcontent_title);
    }

    @Override
    public void addBody(Body body) throws SAXException, WingException, SQLException, IOException, AuthorizeException {

        String img_location = "";
        String link_target = "";
        String caption = "";

        // load http-parameters to restore the input data for 'cancel' button
        String img_location_param = parameters.getParameter("img_location",null);
        String link_target_param = parameters.getParameter("link_target",null);
        String caption_param = parameters.getParameter("caption",null);

//        String errorString = parameters.getParameter("errors",null);
//        ArrayList<String> errors = new ArrayList<>();
//        if (StringUtils.isNotEmpty(errorString)) {
//            Collections.addAll(errors, errorString.split(","));
//        }

        Division div = body.addDivision("admin-featured-content", "primary administrative featured-content-admin");
        div.setHead(T_head1);


        HashMap<String, String> currentItemMap = FeaturedContentController.getFeaturedItem();
        if( currentItemMap != null) {
            Division rowDiv = div.addDivision("featured-content-container", "secondary row featured-content-container");
            rowDiv.setHead(T_head_current);
            // DIVISION: current item preview
            Division current = rowDiv.addDivision("featured-content-home");
              img_location = currentItemMap.get("img_location");
              link_target = currentItemMap.get("link_target");
              caption = currentItemMap.get("caption");

            Para currentPara = current.addPara();
            currentPara.addFigure(img_location, link_target, caption, List.TYPE_SIMPLE);
        }   else {
            div.addPara(T_no_current);
        }

        // DIVISION: new item input form
        Division contentDiv = div.addInteractiveDivision("featured-content-div",
                contextPath + "/admin/featured-content",
                Division.METHOD_POST,
                "secondary");
        contentDiv.setHead(T_head_find);

        List form = contentDiv.addList("featured-content-form", List.TYPE_FORM);

        Item imgLocItem = form.addItem();
        Text imgLocText = imgLocItem.addText("img_location");
        imgLocText.setLabel(T_img_location);
        imgLocText.setRequired(true);
        if (StringUtils.isNotBlank(img_location_param)) {
            imgLocText.setValue(img_location_param);
        } else if (StringUtils.isNotBlank(img_location)) {
            imgLocText.setValue(img_location);
        }

        form.addItem(T_img_location_desc);

        Item linkTargetItem = form.addItem();
        Text linkTargetText = linkTargetItem.addText("link_target");
        linkTargetText.setLabel(T_link_target);
        linkTargetText.setRequired(true);
        if (StringUtils.isNotBlank(link_target_param)) {
            linkTargetText.setValue(link_target_param);
        } else if (StringUtils.isNotBlank(link_target)) {
            linkTargetText.setValue(link_target);
        }

        form.addItem(T_link_target_desc);

        Item captionItem = form.addItem();
        Text captionText = captionItem.addText("caption");
        captionText.setLabel(T_caption);
        captionText.setRequired(true);
        if (StringUtils.isNotBlank(caption_param)) {
            captionText.setValue(caption_param);
        } else if (StringUtils.isNotBlank(caption)) {
            captionText.setValue(caption);
        }

        Item submit_btn = form.addItem();
        submit_btn.addButton("submit_find").setValue(T_set_featured_content);

        // Do not remove! required for 'cocoon flow'.
        contentDiv.addHidden("administrative-continue").setValue(knot.getId());

    }
}
