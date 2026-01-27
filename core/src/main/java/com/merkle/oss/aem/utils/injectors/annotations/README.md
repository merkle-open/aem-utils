## Example usage

### AdaptTo

```java

import com.merkle.oss.aem.utils.injectors.annotations.AdaptTo;
//... other imports

@Model(adaptables = SlingHttpServletRequest.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class ClassName {
    @AdaptTo
    private PageManager pageManager;
    @AdaptTo
    private TagManager tagManager;
    @AdaptTo
    private ComponentManager componentManager;
    //If CustomModel is implemented via adaptables = SlingHttpServletRequest.class
    @AdaptTo
    private CustomModel customModel;
    //If OtherCustomModel is implemented via adaptables = Resource.class
    @AdaptTo(via = "resource")
    private OtherCustomModel otherCustomModel;
    //...
}


```

```java

import com.merkle.oss.aem.utils.injectors.annotations.AdaptTo;
//... other imports

@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class ClassName {
    @AdaptTo
    private PageManager pageManager;
    @AdaptTo
    private TagManager tagManager;
    @AdaptTo
    private ComponentManager componentManager;
    //If OtherCustomModel is implemented via adaptables = Resource.class
    @AdaptTo
    private OtherCustomModel otherCustomModel;
    //...
}


```

### PageProperty

```java

import com.merkle.oss.aem.utils.injectors.annotations.PageProperty;
//... other imports

@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class ClassName {
    @PageProperty(name = JcrConstants.JCR_TITLE)
    private String title;
    @PageProperty(inherited = true)
    private boolean disableSearch;
    @PageProperty
    private int priority;
    @PageProperty(name = JcrConstants.JCR_CREATED)
    private Date jcrCreated;
    @PageProperty(name = "cq:tags")
    private List<String> tags;
    //...
}

```

```java

import com.merkle.oss.aem.utils.injectors.annotations.PageProperty;
//... other imports

@Model(adaptables = Resource.class)
public class ClassName {
    @PageProperty(name = JcrConstants.JCR_TITLE, defaultInjectionStrategy = DefaultInjectionStrategy.REQUIRED)
    private String title;
    @PageProperty(inherited = true, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
    private boolean disableSearch;
    @PageProperty(defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
    private int priority;
    @PageProperty(name = JcrConstants.JCR_CREATED, defaultInjectionStrategy = DefaultInjectionStrategy.REQUIRED)
    private Date jcrCreated;
    @PageProperty(name = "cq:tags", DefaultInjectionStrategy.OPTIONAL)
    private List<String> tags;
    //...
}

```
