package org.jboss.resteasy.test.spring.inmodule;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.logging.Logger;
import org.jboss.resteasy.category.NotForWildFly9;
import org.jboss.resteasy.test.spring.inmodule.resource.ContextRefreshResource;
import org.jboss.resteasy.test.spring.inmodule.resource.ContextRefreshTrigger;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.StringAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.springframework.web.context.WebApplicationContext;
import java.util.Enumeration;


/**
 * @tpSubChapter Spring
 * @tpChapter Integration tests
 * @tpTestCaseDetails Spring context refresh, RESTEASY-632
 * @tpSince RESTEasy 3.0.16
 */
@RunWith(Arquillian.class)
@Category(NotForWildFly9.class) //requires WildFly 10+
public class ContextRefreshTest {


    private static Logger logger = Logger.getLogger(ContextRefreshTest.class);

    @Deployment
    private static Archive<?> deploy() {
        WebArchive archive = ShrinkWrap.create(WebArchive.class, ContextRefreshTest.class.getSimpleName() + ".war")
                .addClass(ContextRefreshResource.class)
                .addClass(ContextRefreshTrigger.class)
                .addClass(ContextRefreshTest.class)
                .addClass(NotForWildFly9.class) //required as this test is not @RunAsClient annotated
                .addAsWebInfResource(ContextRefreshTest.class.getPackage(), "web.xml", "web.xml")
                .addAsWebInfResource(ContextRefreshTest.class.getPackage(), "contextRefresh/applicationContext.xml", "applicationContext.xml");
        archive.addAsManifestResource(new StringAsset("Dependencies: org.springframework.spring meta-inf\n"), "MANIFEST.MF");
        return archive;
    }

    /**
     * @tpTestDetails Refresh the persistent representation of the spring configuration twice
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testContextRefresh() throws Exception {
        Assert.assertTrue(ContextRefreshTrigger.isOK());
        Enumeration<?> en = ContextRefreshTrigger.getApplicationContext().getServletContext().getAttributeNames();
        while (en.hasMoreElements()) {
            logger.info(en.nextElement());
        }
        Object o = ContextRefreshTrigger.getApplicationContext().getServletContext().getAttribute(WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE);
        logger.info(o);
        Assert.assertFalse(o instanceof Exception);
    }
}
