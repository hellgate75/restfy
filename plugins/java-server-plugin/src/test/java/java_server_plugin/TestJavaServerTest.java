package java_server_plugin;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;

import java_server_plugin.TestJavaServer;

import org.apache.maven.plugin.testing.MojoRule;
import org.junit.Rule;
import org.junit.Test;


public class TestJavaServerTest {
	@Rule
    public MojoRule rule = new MojoRule()
    {
      @Override
      protected void before() throws Throwable 
      {
      }

      @Override
      protected void after()
      {
      }
    };
	@Test
	public void test0PluginMustBeCreated() throws Exception {
		File pom = new File( "src/test/resources/classes-pom.xml" );
		assertNotNull( pom );
		assertTrue( pom.exists() );
        TestJavaServer testJavaServer = (TestJavaServer) rule.lookupMojo("connect", pom );
        assertNotNull( testJavaServer );
        testJavaServer.execute();
    }
}
