## Example usage

### QuerySearch

```java

import com.day.cq.search.Query;
import com.day.cq.search.QueryBuilder;
import com.day.cq.tagging.TagManager;
import com.merkle.oss.aem.utils.query.PredicateProperties;
import com.merkle.oss.aem.utils.query.QueryResultHelper;
import com.merkle.oss.aem.utils.query.QuerySearch;
import com.merkle.oss.aem.utils.query.QuerySearchUtil;
import org.apache.sling.api.resource.Resource;

import static com.merkle.oss.aem.utils.sling.SlingUtil.to;
//other imports...

@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class QueryExampleComponent {

    @OSGiService
    private QueryBuilder queryBuilder;

    @Self
    private Resource resource;

    @AdaptTo
    private TagManager tagManager;

    @PageProperty(name = "cq:tags")
    private List<String> tags = Collections.emptyList();

    @ValueMapValue
    private String searchPath;

    @ValueMapValue
    private String titleSearchQuery;

    @ValueMapValue
    private String descriptionSearchQuery;

    private List<ResultItem> searchResultItem;

    @PostConstruct
    void init() {
        //Query configuration
        final QuerySearch querySearch = new QuerySearch(NameConstants.NT_PAGE);
        querySearch.addPath(searchPath);
        querySearch.setHitsPerPage(10);
        querySearch.addOrderByPredicate("@jcr:content/" + JcrConstants.JCR_LASTMODIFIED, false);
        //Query search predicates
        querySearch.addAdditionalPredicates(QuerySearchUtil.createTemplatesPredicate(querySearch, "/apps/mySite/template/standard"));
        querySearch.addAdditionalPredicates(QuerySearchUtil.createFullTextPredicate(titleSearchQuery, PredicateProperties.JCR_TITLE));
        querySearch.addAdditionalPredicates(QuerySearchUtil.createFullTextPredicate(descriptionSearchQuery, PredicateProperties.JCR_DESCRIPTION));
        querySearch.addAdditionalPredicates(QuerySearchUtil.createTagListPredicateGroup(tags, PredicateProperties.CQ_TAGS, false, tagManager));
        //Query execution
        final QueryResultHelper queryResultHelper = QueryResultHelper.create(resource.getResourceResolver());
        final Query query = querySearch.toQuery(queryBuilder, resource.getResourceResolver());
        searchResultItem = query.getResult().getHits().stream()
                .map(queryResultHelper::getResource)
                .filter(Objects::nonNull)
                .map(to(ResultItem.class))
                .collect(Collectors.toList());
    }
}


```

> [!IMPORTANT]
> Always follow the Query execution pattern for proper resource retrieval due to closing resourceResolver.

```java

//...
//Query execution
final QueryResultHelper queryResultHelper = QueryResultHelper.create(resource.getResourceResolver());
final Query query = querySearch.toQuery(queryBuilder, resource.getResourceResolver());
final List<Object> searchResultItem = query.getResult().getHits().stream()
        .map(queryResultHelper::getResource)
        .filter(Objects::nonNull)
        //...


```
