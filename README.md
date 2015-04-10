# kie-server-client-jms
Example connecting to kie-server through standalone remote jms client

Based on the helloworld-brms quickstart available at https://github.com/jboss-developer/jboss-brms-quickstarts/

I've rewritten HelloWorldBRMSTest.java to connect over JMS to the kie-server running in an instance of BPM Suite 6.1

Some prerequisites:

* Install EAP 6.4
* Install BPM Suite
* Import the quickstarts into the workbench
* Build and deploy helloworld-brms-kmodule project
* Configure helloworld-brms-kmodule as a container in the kie-server
* Update BPMS_USER and BPMS_PASSWORD to credentials configured for accessing the server
* Update BPMS_SERVER to the correct address
