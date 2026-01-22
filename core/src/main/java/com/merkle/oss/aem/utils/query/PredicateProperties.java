package com.merkle.oss.aem.utils.query;

import com.day.cq.commons.jcr.JcrConstants;
import com.day.cq.wcm.api.constants.NameConstants;
import com.merkle.oss.aem.utils.java.ClassUtil;

/**
 * Convenience interface to provide predicate property paths,
 * used for AEM queries.
 * <p>
 * Path abstraction is always originated from the page node.
 */
public class PredicateProperties {

    private PredicateProperties() {
        ClassUtil.assertNoInstance(this.getClass());
    }

    public static final String CQ_TEMPLATE = JcrConstants.JCR_CONTENT + "/@" + NameConstants.NN_TEMPLATE;

    public static final String ON_TIME = JcrConstants.JCR_CONTENT + "/@" + NameConstants.PN_ON_TIME;

    public static final String OFF_TIME = JcrConstants.JCR_CONTENT + "/@" + NameConstants.PN_OFF_TIME;

    public static final String CQ_TAGS = JcrConstants.JCR_CONTENT + "/" + NameConstants.PN_TAGS;

}
