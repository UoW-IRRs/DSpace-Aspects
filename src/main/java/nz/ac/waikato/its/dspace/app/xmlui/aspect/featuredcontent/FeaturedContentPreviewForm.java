package nz.ac.waikato.its.dspace.app.xmlui.aspect.featuredcontent;

import org.dspace.app.xmlui.cocoon.AbstractDSpaceTransformer;
import org.dspace.app.xmlui.wing.Message;
import org.dspace.app.xmlui.wing.WingException;
import org.dspace.app.xmlui.wing.element.*;
import org.dspace.authorize.AuthorizeException;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.sql.SQLException;

/**
 * Created by jjung on 9/02/16.
 */
public class FeaturedContentPreviewForm extends AbstractDSpaceTransformer {
    /** Language strings */
    private static final Message T_dspace_home = message("xmlui.general.dspace_home");
    private static final Message T_featuredcontent_preview_trail = message("xmlui.aspect.FeaturedContent.trail");
    private static final Message T_featuredcontent_preview_title = message("xmlui.aspect.FeaturedContent.title");
    private static final Message T_preview_head1 = message("xmlui.aspect.FeaturedContent.head1");
    private static final Message T_img_location = message("xmlui.aspect.FeaturedContent.form_img_location");
    private static final Message T_link_target = message("xmlui.aspect.FeaturedContent.form_link_target");
    private static final Message T_caption = message("xmlui.aspect.FeaturedContent.form_caption");
    private static final Message T_apply_change = message("xmlui.aspect.FeaturedContent.FeaturedContentPreviewForm.apply");
    private static final Message T_cancel = message("xmlui.general.cancel");
    private static final Message T_preview_new = message("xmlui.aspect.FeaturedContent.FeaturedContentPreviewForm.head_preview_new");

    @Override
    public void addPageMeta(PageMeta pageMeta) throws SAXException, WingException, SQLException, IOException, AuthorizeException {
        pageMeta.addTrailLink(contextPath + "/", T_dspace_home);
        pageMeta.addTrail().addContent(T_featuredcontent_preview_trail);
        pageMeta.addMetadata("title").addContent(T_featuredcontent_preview_title);
    }

    @Override
    public void addBody(Body body) throws SAXException, WingException, SQLException, IOException, AuthorizeException {

        // Get our parameters and state;
        String img_location = parameters.getParameter("img_location", null);
        String link_target = parameters.getParameter("link_target", null);
        String caption = parameters.getParameter("caption", null);

        Division div = body.addDivision("admin-featured-content", "primary administrative featured-content-admin");
        div.setHead(T_preview_head1);

        Division rowDiv = div.addDivision("featured-content-container", "secondary row featured-content-container");
        rowDiv.setHead(T_preview_new);

        Division previewDiv = rowDiv.addDivision("featured-content-home");
        previewDiv.addPara().addFigure(img_location, link_target, caption, List.TYPE_SIMPLE);

        Division contentDiv = div.addInteractiveDivision("featured-content-configuring",
                contextPath + "/admin/featured-content",
                Division.METHOD_POST,
                "secondary");

        List form = contentDiv.addList("featured-content-submit-form", List.TYPE_FORM);
        Item imgLocPreviewItem = form.addItem();
        Text imgLocPreview = imgLocPreviewItem.addText("img_location_preview");
        imgLocPreview.setDisabled(true);
        imgLocPreview.setValue(img_location);
        imgLocPreview.setLabel(T_img_location);


        Item linkTargetPreviewItem = form.addItem();
        Text linkTargetPreview = linkTargetPreviewItem.addText("link_target_preview");
        linkTargetPreview.setDisabled(true);
        linkTargetPreview.setValue(link_target);
        linkTargetPreview.setLabel(T_link_target);

        Item captionPreviewItem = form.addItem();
        Text captionPreview = captionPreviewItem.addText("caption_preview", Text.A_DISABLED);
        captionPreview.setDisabled(true);
        captionPreview.setValue(caption);
        captionPreview.setLabel(T_caption);

        org.dspace.app.xmlui.wing.element.Item actions = form.addItem();
        Button confirmButton = actions.addButton("submit_confirm");
        confirmButton.setValue(T_apply_change);

        actions.addButton("submit_cancel").setValue(T_cancel);

        form.addItem().addHidden("img_location").setValue(img_location);
        form.addItem().addHidden("link_target").setValue(link_target);
        form.addItem().addHidden("caption").setValue(caption);

        // Do not remove! required for 'cocoon flow'.
        contentDiv.addHidden("administrative-continue").setValue(knot.getId());
    }
}
