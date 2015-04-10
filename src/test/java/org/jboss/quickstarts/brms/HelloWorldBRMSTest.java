/*
 * JBoss, Home of Professional Open Source
 * Copyright 2014, Red Hat, Inc. and/or its affiliates, and individual
 * contributors by the @authors tag. See the copyright.txt in the 
 * distribution for a full listing of individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,  
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jboss.quickstarts.brms;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.drools.core.runtime.impl.ExecutionResultImpl;
import org.junit.BeforeClass;
import org.junit.Test;
import org.kie.api.command.BatchExecutionCommand;
import org.kie.api.command.Command;
import org.kie.internal.command.CommandFactory;
import org.kie.internal.runtime.helper.BatchExecutionHelper;
import org.kie.server.api.commands.CallContainerCommand;
import org.kie.server.api.commands.CommandScript;
import org.kie.server.api.model.KieServerCommand;
import org.kie.server.api.model.ServiceResponsesList;
import org.kie.server.client.KieServicesFactory;

/**
 * @author rafaelbenevides
 * @author David Murphy
 * 
 */
public class HelloWorldBRMSTest {

	private static final String BPMS_SERVER = "http://172.17.0.2:8080/kie-server";
	private static final String BPMS_USER = "bpmsAdmin";
	private static final String BPMS_PASSWORD = "jbossadmin1!";

	// A sale for a VIP customer
	private static Sale vipSale;
	// A sale for a regular customer
	private static Sale regularSale;
	// A sale for a Bad customer
	private static Sale badSale;

	private static InitialContext ctx;

	@BeforeClass
	public static void setup() {
		try {
			ctx = getRemoteJbossInitialContext(new URL(BPMS_SERVER), BPMS_USER,
					BPMS_PASSWORD);
		} catch (MalformedURLException e) {
			throw new IllegalStateException(
					"Could not connect to server, check BPMS_SERVER url", e);

		}

		CustomerType regular = new CustomerType();
		regular.setType("regular");

		CustomerType vip = new CustomerType();
		vip.setType("VIP");

		CustomerType bad = new CustomerType();
		bad.setType("BAD");

		Customer vipCustomer = new Customer();
		vipCustomer.setCustomerType(vip);
		vipSale = new Sale();
		vipSale.setCustomer(vipCustomer);

		Customer regularCustomer = new Customer();
		regularCustomer.setCustomerType(regular);
		regularSale = new Sale();
		regularSale.setCustomer(regularCustomer);

		Customer badCustomer = new Customer();
		badCustomer.setCustomerType(bad);
		badSale = new Sale();
		badSale.setCustomer(badCustomer);
	}

	@Test
	public void testGoodCustomer() {
		System.out.println("** Testing VIP customer **");
		vipSale = execute(vipSale);
		// Sale approved
		assertTrue(vipSale.getApproved().booleanValue());
		// Discount of 0.5
		assertEquals(vipSale.getDiscount(), 0.50F, 0.0);
	}

	@Test
	public void testRegularCustomer() {
		System.out.println("** Testing regular customer **");
		regularSale = execute(regularSale);
		// Sale approved
		assertTrue(regularSale.getApproved().booleanValue());
		// No Discount
		assertNull(regularSale.getDiscount());
	}

	@Test
	public void testBadCustomer() {
		System.out.println("** Testing BAD customer **");
		badSale = execute(badSale);
		// Sale denied
		assertFalse(badSale.getApproved().booleanValue());
	}

	private Sale execute(Sale sale) {
		CommandScript command = getCommand(sale);
		ServiceResponsesList response = getResponse(command);
		return parseResponse(response);
	}

	private CommandScript getCommand(Sale sale) {
		Command<?> insert = CommandFactory.newInsert(sale, "sale");
		Command<?> fire = CommandFactory.newFireAllRules();
		Command<?> get = CommandFactory.newGetObjects("sale");
		BatchExecutionCommand batch = CommandFactory.newBatchExecution(
				Arrays.asList(insert, fire, get), "helloworldKSession");

		String payload = BatchExecutionHelper.newXStreamMarshaller().toXML(
				batch);

		// Kie server commands
		List<KieServerCommand> commands = new ArrayList<>();
		commands.add(new CallContainerCommand("helloworld", payload));

		return new CommandScript(commands);
	}

	private ServiceResponsesList getResponse(CommandScript script) {
		return KieServicesFactory.newKieServicesJMSClient(ctx, BPMS_USER,
				BPMS_PASSWORD).executeScript(script);
	}
	
	private Sale parseResponse(ServiceResponsesList response){
		String responseXml = response.getResponses().get(0).getResult().toString();
		ExecutionResultImpl executionResults = (ExecutionResultImpl)BatchExecutionHelper.newXStreamMarshaller().fromXML(responseXml);
		List<Sale> sales = (List<Sale>)executionResults.getValue("sale");
		return sales.get(0);
	}

	private static InitialContext getRemoteJbossInitialContext(URL url,
			String user, String password) {
		Properties initialProps = new Properties();
		initialProps.setProperty(InitialContext.INITIAL_CONTEXT_FACTORY,
				"org.jboss.naming.remote.client.InitialContextFactory");
		String jbossServerHostName = url.getHost();
		initialProps.setProperty(InitialContext.PROVIDER_URL,
				"remote://" + jbossServerHostName + ":4447");
		initialProps.setProperty(InitialContext.SECURITY_PRINCIPAL, user);
		initialProps.setProperty(InitialContext.SECURITY_CREDENTIALS, password);

		for (Object keyObj : initialProps.keySet()) {
			String key = (String) keyObj;
			System.setProperty(key, (String) initialProps.get(key));
		}
		try {
			return new InitialContext(initialProps);
		} catch (NamingException e) {
			throw new IllegalStateException(
					"Could not construct initial context for JMS", e);
		}
	}

}
