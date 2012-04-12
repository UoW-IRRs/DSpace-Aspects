
importClass(Packages.org.dspace.authorize.AuthorizeManager);

importClass(Packages.org.dspace.app.xmlui.utils.FlowscriptUtils);
importClass(Packages.org.dspace.app.xmlui.utils.ContextUtil);
importClass(Packages.org.dspace.app.xmlui.aspect.administrative.FlowItemUtils);
importClass(Packages.nz.ac.lconz.irr.dspace.app.xmlui.aspect.readofweek.FlowReadOfWeek);

/**
 * Simple access method to access the current cocoon object model.
 */
function getObjectModel()
{
    return FlowscriptUtils.getObjectModel(cocoon);
}

/**
 * Return the DSpace context for this request since each HTTP request generates
 * a new context this object should never be stored and instead allways accessed
 * through this method so you are ensured that it is the correct one.
 */
function getDSContext()
{
    return ContextUtil.obtainContext(getObjectModel());
}

/**
 * Send the current page and wait for the flow to be continued. This method will
 * preform two usefull actions: set the flow parameter & add result information.
 *
 * The flow parameter is used by the sitemap to seperate requests comming from a
 * flow script from just normal urls.
 *
 * The result object could potentialy contain a notice message and a list of
 * errors. If either of these are present then they are added to the sitemap's
 * parameters.
 */
function sendPageAndWait(uri,bizData,result)
{
    if (bizData == null)
        bizData = {};


    if (result != null)
    {
        var outcome = result.getOutcome();
        var header = result.getHeader();
        var message = result.getMessage();
        var characters = result.getCharacters();


        if (message != null || characters != null)
        {
            bizData["notice"]     = "true";
            bizData["outcome"]    = outcome;
            bizData["header"]     = header;
            bizData["message"]    = message;
            bizData["characters"] = characters;
        }

        var errors = result.getErrorString();
        if (errors != null)
        {
            bizData["errors"] = errors;
        }
    }

    // just to remember where we came from.
    bizData["flow"] = "true";
    cocoon.sendPageAndWait(uri,bizData);
}

/**
 * Send the given page and DO NOT wait for the flow to be continued. Execution will
 * proceed as normal. This method will perform two useful actions: set the flow
 * parameter & add result information.
 *
 * The flow parameter is used by the sitemap to separate requests coming from a
 * flow script from just normal urls.
 *
 * The result object could potentially contain a notice message and a list of
 * errors. If either of these are present then they are added to the sitemap's
 * parameters.
 */
function sendPage(uri,bizData,result)
{
    if (bizData == null)
        bizData = {};

    if (result != null)
    {
        var outcome = result.getOutcome();
        var header = result.getHeader();
        var message = result.getMessage();
        var characters = result.getCharacters();

        if (message != null || characters != null)
        {
            bizData["notice"]     = "true";
            bizData["outcome"]    = outcome;
            bizData["header"]     = header;
            bizData["message"]    = message;
            bizData["characters"] = characters;
        }

        var errors = result.getErrorString();
        if (errors != null)
        {
            bizData["errors"] = errors;
        }
    }

    // just to remember where we came from.
    bizData["flow"] = "true";
    cocoon.sendPage(uri,bizData);
}

/**
 * Return whether the currently authenticated eperson is an
 * administrator.
 */
function isAdministrator() {
    return AuthorizeManager.isAdmin(getDSContext());
}

/**
 * Assert that the currently authenticated eperson is an administrator.
 * If they are not then an error page is returned and this function
 * will NEVER return.
 */
function assertAdministrator() {

    if ( ! isAdministrator()) {
        sendPage("admin/not-authorized");
        cocoon.exit();
    }
}


/*********************
 * Entry Point flows
 *********************/

/**
 * Start managing read of week
 */
function startManageReadOfTheWeek()
{
    assertAdministrator();

    doManageReadOfTheWeek();

    // This should never return, but just in case it does then point
    // the user to the home page.
    cocoon.redirectTo(cocoon.request.getContextPath());
    getDSContext().complete();
    cocoon.exit();
}

/**************************
 * Edit Read of Week flows
 **************************/

/** TODO allow user to say that there should be no read of the week */

/**
 * Manage read of week, this is a flow entry point allowing the user to search for items by
 * their internal id or handle.
 */
function doManageReadOfTheWeek()
{
    assertAdministrator();

    var identifier;
    var result;
    do {
        sendPageAndWait("admin/read-of-the-week/status",{"identifier":identifier},result);
        assertAdministrator();
        result = null;

        if (cocoon.request.get("submit_find") && cocoon.request.get("identifier"))
        {
            // Search for the identifier
            identifier = cocoon.request.get("identifier");
            result = FlowItemUtils.resolveItemIdentifier(getDSContext(),identifier);

            // If an item was found then allow the user to pick the item as read of the week.
            if (result != null && result.getParameter("itemID"))
            {
                var itemID = result.getParameter("itemID");
                result = doPreviewReadOfTheWeek(itemID);
            }
        }
    } while (true)
}

function doPreviewReadOfTheWeek(itemID)
{
    assertAdministrator();

    var result;
    do {
        assertAdministrator();

        sendPageAndWait("admin/read-of-the-week/preview",{"itemID":itemID},result);
        result = null;

        if (cocoon.request.get("submit_cancel"))
        {
            // go back to where ever we came from.
            return null;
        }
        else if (cocoon.request.get("submit_confirm"))
        {
            var result = FlowReadOfWeek.processPickReadOfTheWeek(getDSContext(), itemID);

            if (result.getContinue)

            // If the user actually deleted the item the return back
            // to the manage items page.
                if (result != null)
                    return result;
        }
    } while (true)
}

