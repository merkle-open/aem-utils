## Example usage

* [Link Handling](#link-handling)
* [Links](#links)
    * [Target](#target)
* [LinkExternalizerUtil](#linkexternalizerutil)
    * [externalizeRichTextLinks()](#externalizerichtextlinks)

### Link Handling

Examples on various link handling scenarios

```java

import com.day.cq.wcm.api.Page;
import com.merkle.oss.aem.utils.link.LinkExternalizerUtil;
import com.merkle.oss.aem.utils.link.LinkMappingUtil;
import com.merkle.oss.aem.utils.link.LinkUtil;
import com.merkle.oss.aem.utils.wcm.PageManagerUtil;
import org.apache.sling.api.SlingHttpServletRequest;
//other imports...

@Model(adaptables = SlingHttpServletRequest.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class ExampleComponent {

    @Self
    private SlingHttpServletRequest request;

    //-> /content/mysite/us/en/home/pagename
    @ValueMapValue
    private String targetPath;

    private Page targetPage;

    @PostConstruct
    void init() {
        if (StringUtils.isNotBlank(targetPath)) {
            targetPage = PageManagerUtil.containingPage(targetPath, request.getResourceResolver());
        }
    }

    public String getExternalizedPathLink() {
        if (StringUtils.isBlank(targetPath)) {
            return StringUtils.EMPTY;
        }
        /* <--- EXAMPLE ---> */
        //-> "https://www.mySite.com/en/home/pagename.html"
        return LinkUtil.appendHtml(LinkExternalizerUtil.externalize(targetPath, request));
    }

    public String getExternalizedPageLink() {
        if (targetPage == null) {
            return StringUtils.EMPTY;
        }
        /* <--- EXAMPLE ---> */
        //-> "https://www.mySite.com/en/home/pagename.html"
        return LinkUtil.appendHtml(LinkExternalizerUtil.externalize(targetPage, request));
    }

    public String getMappedPathLink() {
        if (StringUtils.isBlank(targetPath)) {
            return StringUtils.EMPTY;
        }
        /* <--- EXAMPLE ---> */
        //-> "/en/home/pagename.html"
        return LinkUtil.appendHtml(LinkMappingUtil.map(targetPath, request));
    }

    public String getMappedPageLink() {
        if (targetPage == null) {
            return StringUtils.EMPTY;
        }
        /* <--- EXAMPLE ---> */
        //-> "/en/home/pagename.html"
        return LinkUtil.appendHtml(LinkMappingUtil.map(targetPage.getPath(), request));
    }

    public String getPathLink() {
        if (StringUtils.isBlank(targetPath)) {
            return StringUtils.EMPTY;
        }
        /* <--- EXAMPLE ---> */
        //-> "/content/mysite/us/en/home/pagename.html"
        return LinkUtil.createLink(targetPath, request.getResourceResolver());
    }

    public String getPageLink() {
        if (targetPage == null) {
            return StringUtils.EMPTY;
        }
        /* <--- EXAMPLE ---> */
        //-> "/content/mysite/us/en/home/pagename.html"
        return LinkUtil.createLink(targetPage);
    }

}


```

### Links

#### Target

```java

import com.merkle.oss.aem.utils.link.LinkUtil;
import com.merkle.oss.aem.utils.link.constants.Links;
//other imports...

@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class ExampleComponent {

    @ValueMapValue
    private String linkPath;

    @ValueMapValue
    private String linkLabel;

    @ValueMapValue
    private boolean isOpenNewWindowLink;

    public boolean isOpenNewWindowLink() {
        return isOpenNewWindowLink;
    }

    public String getTarget() {
        /* <--- EXAMPLE ---> */
        return LinkUtil.getTarget(isOpenNewWindowLink());
    }

    public String getRel() {
        /* <--- EXAMPLE ---> */
        return Links.Target.of(getTarget()).getRel();
    }

}


```

### LinkExternalizerUtil

#### externalizeRichTextLinks()

```java

import com.merkle.oss.aem.utils.link.LinkExternalizerUtil;
import org.apache.sling.api.SlingHttpServletRequest;
//other imports...

@Model(adaptables = SlingHttpServletRequest.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class ExampleComponent {

    @Self
    private SlingHttpServletRequest request;

    //-> <p>follow <a href="/content/mysite/us/en/home/pagename">here</a> for more</p>
    @ValueMapValue
    private String richText;

    public String getTextWithExternalizedLinks() {
        if (StringUtils.isBlank(richText)) {
            return StringUtils.EMPTY;
        }

        /* <--- EXAMPLE ---> */
        //-> <p>follow <a href="https://www.mySite.com/en/home/pagename.html">here</a> for more</p>
        return LinkExternalizerUtil.externalizeRichTextLinks(richText, request);
    }

}


```
