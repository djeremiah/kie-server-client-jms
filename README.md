# kie-server-client-jms
Example connecting to kie-server through standalone remote jms client

Based on the helloworld-brms quickstart available at https://github.com/jboss-developer/jboss-brms-quickstarts/

I've rewritten HelloWorldBRMSTest.java to connect over JMS to the kie-server running in an instance of WildFly 8.1

Some prerequisites:
* Install WildFly [8.1.0.Final](http://download.jboss.org/wildfly/8.1.0.Final/wildfly-8.1.0.Final.zip)
* Install [kie-server](http://download.jboss.org/drools/release/6.2.0.Final/kie-server-distribution-6.2.0.Final.zip) and [kie-workbench](http://download.jboss.org/drools/release/6.2.0.Final/kie-drools-wb-distribution-6.2.0.Final.zip) into WildFly
* [Import](https://github.com/jboss-developer/jboss-brms-quickstarts/tree/6.2.x#import-the-brms-repository) the quickstarts into the workbench
* From within the workbench, build and deploy helloworld-brms-kmodule project 
* [Configure and start](http://docs.jboss.org/drools/release/6.2.0.Final/drools-docs/html/ch19.html#d0e19266) helloworld-brms-kmodule as a container in the kie-server
* In this project, in HelloWorldBRMSTest.java, Update BPMS_USER and BPMS_PASSWORD to credentials configured for accessing the server
* Similarly, update BPMS_SERVER to the correct address

Then, you should be able to fire up the JUnit test and see the results
