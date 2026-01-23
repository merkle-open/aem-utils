```java

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
        querySearch.addAdditionalPredicates(querySearch.createFullTextPredicate(titleSearchQuery, PredicateProperties.JCR_TITLE));
        querySearch.addAdditionalPredicates(querySearch.createFullTextPredicate(descriptionSearchQuery, PredicateProperties.JCR_DESCRIPTION));
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
