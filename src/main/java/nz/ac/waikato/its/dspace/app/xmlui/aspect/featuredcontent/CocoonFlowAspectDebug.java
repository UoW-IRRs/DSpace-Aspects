package nz.ac.waikato.its.dspace.app.xmlui.aspect.featuredcontent;

/**
 * Created by jjung on 11/02/16.
 *
 * Utility for cocoon flow debugging
 *
 * usage.
 *   1. import in '${cocoonflow}.js'
 *      importClass(Packages.nz.ac.waikato.its.dspace.app.xmlui.aspect.featuredcontent.CocoonFlowAspectDebug);
 *   2. invoke with debugging message in '${cocoonflow}.js'
 *      CocoonFlowAspectDebug.printDebugMsg(" 1. Entering the method step (1) ... ");
 *   3. Check messages from standard-out
 *
 */
public class CocoonFlowAspectDebug {
    public static void printDebugMsg(String msg){
        String output = "CocoonFlowAspectDebug] " + msg;
        System.out.println(output);
    }

}
