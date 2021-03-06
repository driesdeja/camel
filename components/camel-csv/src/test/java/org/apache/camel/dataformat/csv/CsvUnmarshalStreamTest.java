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
package org.apache.camel.dataformat.csv;

import java.util.List;

import org.apache.camel.EndpointInject;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;

/**
 * Spring based integration test for the <code>CsvDataFormat</code>
 * @version 
 */
public class CsvUnmarshalStreamTest extends CamelTestSupport {

    public static final int EXPECTED_COUNT = 3;

    @EndpointInject(uri = "mock:result")
    private MockEndpoint result;

    @SuppressWarnings("unchecked")
    @Test
    public void testCsvUnMarshal() throws Exception {
        result.expectedMessageCount(EXPECTED_COUNT);

        String message = "";
        for (int i = 0; i < EXPECTED_COUNT; ++i) {
            message += i + "|\"" + i + "\n" + i + "\"\n";
        }

        template.sendBody("direct:start", message);

        assertMockEndpointsSatisfied();

        for (int i = 0; i < EXPECTED_COUNT; ++i) {
            List<String> body = result.getReceivedExchanges().get(i)
                    .getIn().getBody(List.class);
            assertEquals(2, body.size());
            assertEquals(String.valueOf(i), body.get(0));
            assertEquals(String.format("%d\n%d", i, i), body.get(1));
        }
    }
    
    @Override
    protected RouteBuilder createRouteBuilder() throws Exception {
        return new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                CsvDataFormat csv = new CsvDataFormat();
                csv.setLazyLoad(true);
                csv.setDelimiter("|");

                from("direct:start")
                        .unmarshal(csv)
                        .split(body())
                        .to("mock:result");
            }
        };
    }
}