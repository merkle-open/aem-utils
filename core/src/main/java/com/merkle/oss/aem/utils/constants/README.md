## Example usage

### CookieNameBuilder

```java

import com.day.cq.wcm.api.Page;
import com.merkle.oss.aem.utils.constants.CookieNameBuilder;
import com.merkle.oss.aem.utils.wcm.PageManagerUtil;
import org.apache.sling.api.resource.Resource;
//other imports...

@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class ExampleComponent {

    @Self
    private Resource resource;

    public String getTenantSensitiveCookieName() {
        final Page currentPage = PageManagerUtil.containingPage(resource);
        //for current page with path /content/mysite/us/en/home/pagename
        //returns language tenant specific cookie name
        //-> "mySite.exampleComponent.us.en"
        return new CookieNameBuilder("mySite", "exampleComponent").appendPathSegmentNames(currentPage, 3, 4).toString();
    }

    public String getComponentSpecificCookieName() {
        //returns component specific cookie name
        //-> "mySite.exampleComponent"
        return new CookieNameBuilder("mySite", "exampleComponent").toString();
    }

    public String getComponentFeatureSpecificCookieName() {
        //returns component feature specific cookie name
        //-> "mySite.exampleComponent.feature"
        return new CookieNameBuilder("mySite", "exampleComponent.feature").toString();
    }

}


```

### FileType

#### mimeType

```java

import com.merkle.oss.aem.utils.constants.FileType;
//other imports...

@Override
protected void doGet(@NonNull final SlingHttpServletRequest request, 
                     @NonNull final SlingHttpServletResponse response) {
    
    //handle servlet logic...
    
    response.setContentType(FileType.JSON.getMimeType());
    
    //handle response...
}


```

#### extension

```java

import com.merkle.oss.aem.utils.constants.FileType;
import com.merkle.oss.aem.utils.link.LinkExternalizerUtil;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
//other imports

@Model(adaptables = SlingHttpServletRequest.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class ExampleComponent {

    @Self
    private SlingHttpServletRequest request;

    @ScriptVariable
    private Resource resource;

    private String getJSONLink() {
        final String link = LinkExternalizerUtil.externalize(resource.getPath(), request);
        /* <--- EXAMPLE ---> */
        return link + FileType.JSON.toDotExtension();
    }

}


```
