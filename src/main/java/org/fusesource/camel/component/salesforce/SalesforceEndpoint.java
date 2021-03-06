/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.fusesource.camel.component.salesforce;

import org.apache.camel.Consumer;
import org.apache.camel.Processor;
import org.apache.camel.Producer;
import org.apache.camel.impl.DefaultEndpoint;
import org.apache.camel.impl.SynchronousDelegateProducer;
import org.fusesource.camel.component.salesforce.internal.OperationName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Represents a Salesforce endpoint.
 */
public class SalesforceEndpoint extends DefaultEndpoint {

    private static final Logger LOG = LoggerFactory.getLogger(SalesforceEndpoint.class);

    private final SalesforceEndpointConfig config;
    private final OperationName operationName;
    private final String topicName;

    public SalesforceEndpoint(String uri, SalesforceComponent salesforceComponent,
                              SalesforceEndpointConfig config, OperationName operationName, String topicName) {
        super(uri, salesforceComponent);

        this.config = config;
        this.operationName = operationName;
        this.topicName = topicName;
    }

    public Producer createProducer() throws Exception {
        // producer requires an operation, topicName must be the invalid operation name
        if (operationName == null) {
            throw new IllegalArgumentException(String.format("Invalid Operation %s", topicName));
        }

        SalesforceProducer producer = new SalesforceProducer(this);
        if (isSynchronous()) {
            return new SynchronousDelegateProducer(producer);
        } else {
            return producer;
        }
    }

    public Consumer createConsumer(Processor processor) throws Exception {
        // consumer requires a topicName, operation name must be the invalid topic name
        if (topicName == null) {
            throw new IllegalArgumentException(String.format("Invalid topic name %s, matches a producer operation name",
                operationName.value()));
        }

        return new SalesforceConsumer(this, processor,
            getComponent().getSubscriptionHelper());
    }

    @Override
    public SalesforceComponent getComponent() {
        return (SalesforceComponent) super.getComponent();
    }

    public boolean isSingleton() {
        // re-use endpoint instance across multiple threads
        // the description of this method is a little confusing
        return true;
    }

    public SalesforceEndpointConfig getConfiguration() {
        return config;
    }

    public OperationName getOperationName() {
        return operationName;
    }

    public String getTopicName() {
        return topicName;
    }

}
