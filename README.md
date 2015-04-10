# kie-server-client-jms
Example connecting to kie-server through standalone remote jms client

Based on the helloworld-brms quickstart available at https://github.com/jboss-developer/jboss-brms-quickstarts/

I've rewritten HelloWorldBRMSTest.java to connect over JMS to the kie-server running in an instance of WildFly 8.1

Some prerequisites:
Install WildFly
Install kie-server and kie-workbench into WildFly
Import the quickstarts into the workbench
Build and deploy helloworld-brms-kmodule project
Configure helloworld-brms-kmodule as a container in the kie-server
Update BPMS_USER and BPMS_PASSWORD to credentials configured for accessing the server
Update BPMS_SERVER to the correct address
