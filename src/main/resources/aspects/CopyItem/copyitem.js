
importClass(Packages.org.dspace.authorize.AuthorizeManager);

importClass(Packages.org.dspace.app.xmlui.utils.FlowscriptUtils);
importClass(Packages.org.dspace.app.xmlui.utils.ContextUtil);
importClass(Packages.org.dspace.app.xmlui.aspect.administrative.FlowItemUtils);
importClass(Packages.nz.ac.lconz.irr.dspace.app.xmlui.aspect.copyitem.FlowCopyItem);
importClass(Packages.nz.ac.lconz.irr.dspace.app.xmlui.aspect.copyitem.CopyItemUtils);
importClass(Packages.org.dspace.content.Item);

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
 * Assert that the currently authenticated eperson can deposit an item.
 * If they are not then an error page is returned and this function
 * will NEVER return.
 */
function assertCanDepositCopy(itemID) {
    if (!CopyItemUtils.canDepositCopy(getDSContext(), itemID)) {
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
function startDepositCopy()
{
    assertCanDepositCopy(cocoon.request.get("itemID"));

    doDepositCopy();

    // This should never return, but just in case it does then point
    // the user to the home page.
    cocoon.redirectTo(cocoon.request.getContextPath());
    getDSContext().complete();
    cocoon.exit();
}

/**************************
 * Edit Read of Week flows
 **************************/

/** TODO allow user to pick item id? */

/**
 * Deposit copy of an item.
 */
function doDepositCopy()
{
    var identifier;
    var result;

    if (cocoon.request.get("itemID"))
    {
        // Search for the identifier
        identifier = cocoon.request.get("itemID");
        result = FlowItemUtils.resolveItemIdentifier(getDSContext(),identifier);

        // If an item was found then allow the user to deposit a copy of the item.
        if (result != null && result.getParameter("itemID"))
        {
            var itemID = result.getParameter("itemID");
            result = makeCopy(itemID);
        }
    } else {
        var contextPath = cocoon.request.getContextPath();
        cocoon.redirectTo(contextPath + "/submissions");
        coocon.exit();
    }
}

function makeCopy(itemID)
{
    var result = null;
    do {
        assertCanDepositCopy(itemID);

        sendPageAndWait("admin/copy-item/preview",{"itemID":itemID},result);
        result = null;

        if (cocoon.request.get("submit_cancel"))
        {
            var contextPath = cocoon.request.getContextPath();
            var item = Item.find(getDSContext(), itemID);
            if (item != null) {
                cocoon.redirectTo(contextPath + "/handle/" + item.getHandle(), true);
            } else {
                cocoon.redirectTo(contextPath + "/", true);
            }
            cocoon.exit();
        }
        else if (cocoon.request.get("submit_confirm"))
        {
            result = FlowCopyItem.processCopyItem(getDSContext(), itemID);

            if (result.getContinue) {
                var workspaceID = result.getParameter("workspaceID");
                contextPath = cocoon.request.getContextPath();
                cocoon.redirectTo(contextPath + "/submit?workspaceID=" + workspaceID, true);
                getDSContext().complete();
                cocoon.exit();
            }
        }
    } while (true)
}

