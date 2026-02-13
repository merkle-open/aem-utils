package com.merkle.oss.aem.utils.query;

import com.day.cq.commons.jcr.JcrConstants;
import com.day.cq.dam.api.DamConstants;
import com.day.cq.wcm.api.constants.NameConstants;
import com.merkle.oss.aem.utils.annotations.tooling.Generated;
import com.merkle.oss.aem.utils.java.ClassUtil;

/**
 * Convenience interface to provide predicate property paths,used for AEM queries.
 * <p>
 * Path abstraction is always originated from the page node.
 */
public final class PredicateProperties {

    @Generated("Bypass coverage for static utility constructor")
    private PredicateProperties() {
        ClassUtil.assertNoInstance(this.getClass());
    }

    public static final String JCR_TITLE = JcrConstants.JCR_CONTENT + "/@" + JcrConstants.JCR_TITLE;

    public static final String JCR_DESCRIPTION = JcrConstants.JCR_CONTENT + "/@" + JcrConstants.JCR_DESCRIPTION;

    public static final String CQ_TEMPLATE = JcrConstants.JCR_CONTENT + "/@" + NameConstants.NN_TEMPLATE;

    public static final String ON_TIME = JcrConstants.JCR_CONTENT + "/@" + NameConstants.PN_ON_TIME;

    public static final String OFF_TIME = JcrConstants.JCR_CONTENT + "/@" + NameConstants.PN_OFF_TIME;

    public static final String CQ_TAGS = JcrConstants.JCR_CONTENT + "/" + NameConstants.PN_TAGS;

    public static final String PAGE_LAST_MODIFIED = JcrConstants.JCR_CONTENT + "/@" + NameConstants.PN_PAGE_LAST_MOD;

    public static final String METADATA_DC_TITLE = JcrConstants.JCR_CONTENT + "/" + DamConstants.METADATA_FOLDER + "/@" + DamConstants.DC_TITLE;

    public static final String METADATA_DC_DESCRIPTION = JcrConstants.JCR_CONTENT + "/" + DamConstants.METADATA_FOLDER + "/@" + DamConstants.DC_DESCRIPTION;

    public static final String METADATA_JCR_LAST_MODIFIED = JcrConstants.JCR_CONTENT + "/" + DamConstants.METADATA_FOLDER + "/@" + JcrConstants.JCR_LASTMODIFIED;

}
