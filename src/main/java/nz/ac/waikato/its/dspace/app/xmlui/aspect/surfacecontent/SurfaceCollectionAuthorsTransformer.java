package nz.ac.waikato.its.dspace.app.xmlui.aspect.surfacecontent;

import org.apache.avalon.framework.parameters.Parameters;
import org.apache.cocoon.ProcessingException;
import org.apache.cocoon.environment.SourceResolver;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.dspace.app.xmlui.cocoon.AbstractDSpaceTransformer;
import org.dspace.app.xmlui.wing.WingException;
import org.dspace.app.xmlui.wing.element.Body;
import org.dspace.app.xmlui.wing.element.Division;
import org.dspace.authorize.AuthorizeException;
import org.dspace.content.Collection;
import org.dspace.core.ConfigurationManager;
import org.dspace.core.Constants;
import org.dspace.discovery.*;
import org.dspace.discovery.configuration.DiscoveryConfigurationParameters;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.*;

/**
 * @author Andrea Schweer schweer@waikato.ac.nz
 *         for the University of Waikato's Institutional Research Repositories
 */
@SurfaceContentDataProvider(key = "top-collection-authors")
public class SurfaceCollectionAuthorsTransformer extends AbstractDSpaceTransformer {
    public static final String CONFIG_KEY_DATAFILE = "surface-content.collection-top-authors.datafile";
    public static final String CONFIG_KEY_NUM_AUTHORS = "surface-content.collection-top-authors.num-authors";

    public static final String MODULE = "uow-aspects";

    protected List<String> parentHandles;
    protected int collectionId;
    protected List<Pair<String, Integer>> authorItemCounts;

    protected String linkTargetPattern = "%s/handle/%s/discover?filtertype=author&amp;filter_relational_operator=equals&amp;filter=%s";
    protected String linkTextPattern = "%s (%d)";

    @Override
    public void addBody(Body body) throws SAXException, WingException, SQLException, IOException, AuthorizeException, ProcessingException {
        Collection collection = Collection.find(context, collectionId);
        Division div = body.addDivision("surface-content");
        if (collection == null) {
            getLogger().warn("No collection found for selected id (" + collectionId + "), not adding top authors to DRI");
            return;
        }
        if (authorItemCounts == null || authorItemCounts.isEmpty()) {
            getLogger().warn("No author item counts available, not adding top authors to DRI");
            return;
        }

        org.dspace.app.xmlui.wing.element.List itemCountList = div.addList("author-item-counts", "simple", "surface-content-count-list");
        itemCountList.setHead("Top authors, " + collection.getName());

        for (Pair<String, Integer> authorItemCount : authorItemCounts) {
            String linkTarget = String.format(linkTargetPattern, contextPath, collection.getHandle(), encodeForURL(authorItemCount.getKey()));
            String linkText = String.format(linkTextPattern, authorItemCount.getKey(), authorItemCount.getValue());

            itemCountList.addItemXref(linkTarget, linkText);
        }
    }

    @Override
    public void setup(SourceResolver resolver, Map objectModel, String src, Parameters parameters) throws ProcessingException, SAXException, IOException {
        super.setup(resolver, objectModel, src, parameters);

        try {
            collectionId = -1;
            String fileName = ConfigurationManager.getProperty(MODULE, CONFIG_KEY_DATAFILE);
            if (StringUtils.isBlank(fileName)) {
                fileName = ConfigurationManager.getProperty("dspace.dir") + "/var/surface-content/collection-top-authors";
                getLogger().info("No data file configured for " + MODULE + " module, property " + CONFIG_KEY_DATAFILE + "; " +
                        "falling back to default (" + fileName + ")");
            }

            File file = new File(fileName);
            if (file.exists() && file.canRead()) {
                int candidate;
                try (Scanner scanner = new Scanner(file)) {
                    candidate = scanner.nextInt();
                }

                if (Collection.find(context, candidate) != null) {
                    collectionId = candidate;
                }
            }

            if (collectionId != -1) {
                int numAuthors = ConfigurationManager.getIntProperty(MODULE, CONFIG_KEY_NUM_AUTHORS, 5);
                authorItemCounts = populateAuthorItemCounts(collectionId, numAuthors);
            } else {
                getLogger().warn("Could not find or read file, or could not read valid collection id, for collection top authors data: "
                        + fileName);
            }
        } catch (IOException | SQLException e) {
            getLogger().error("Caught exception trying to surface top authors by collection: " + e.getMessage(), e);
        }
    }

    protected List<Pair<String, Integer>> populateAuthorItemCounts(int collectionId, int numAuthors) {
        List<Pair<String, Integer>> result = new ArrayList<>();

        if (collectionId == -1) {
            getLogger().error("No valid collection id specified, cannot populate author item counts");
            return result;
        }

        // construct + perform solr query
        DiscoverQuery query = new DiscoverQuery();
        query.addFilterQueries("read:g0", "location.coll:" + collectionId, "search.resourcetype:" + Constants.ITEM);
        query.addFacetField(new DiscoverFacetField(
                "author_keyword",
                DiscoveryConfigurationParameters.TYPE_STANDARD,
                numAuthors,
                DiscoveryConfigurationParameters.SORT.COUNT));
        query.setMaxResults(0);
        query.setFacetMinCount(1);

        SearchService searcher = SearchUtils.getSearchService();
        try {
            DiscoverResult searchResults = searcher.search(context, query, false);
            List<DiscoverResult.FacetResult> facetResults = searchResults.getFacetResult("author_keyword");

            for (DiscoverResult.FacetResult facetResult : facetResults) {
                String authorName = facetResult.getDisplayedValue();
                int count = (int) facetResult.getCount();
                result.add(Pair.of(authorName, count));
            }
        } catch (SearchServiceException e) {
            getLogger().error("Could not search top authors in collection id=" + collectionId + ": " + e.getMessage(), e);
        }

        return result;
    }

    public SurfaceCollectionAuthorsTransformer() {
        super();
    }

    @Override
    public void recycle() {
        parentHandles = null;
        authorItemCounts = null;
        collectionId = -1;
        super.recycle();
    }
}
