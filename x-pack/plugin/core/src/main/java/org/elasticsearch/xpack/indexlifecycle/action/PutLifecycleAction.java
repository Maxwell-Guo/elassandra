/*
 * Copyright Elasticsearch B.V. and/or licensed to Elasticsearch B.V. under one
 * or more contributor license agreements. Licensed under the Elastic License;
 * you may not use this file except in compliance with the Elastic License.
 */
package org.elasticsearch.xpack.indexlifecycle.action;

import org.elasticsearch.action.Action;
import org.elasticsearch.action.ActionRequestBuilder;
import org.elasticsearch.action.ActionRequestValidationException;
import org.elasticsearch.action.support.master.AcknowledgedRequest;
import org.elasticsearch.action.support.master.AcknowledgedResponse;
import org.elasticsearch.client.ElasticsearchClient;
import org.elasticsearch.common.ParseField;
import org.elasticsearch.common.Strings;
import org.elasticsearch.common.collect.Tuple;
import org.elasticsearch.common.io.stream.StreamInput;
import org.elasticsearch.common.io.stream.StreamOutput;
import org.elasticsearch.common.xcontent.ConstructingObjectParser;
import org.elasticsearch.common.xcontent.NamedXContentRegistry;
import org.elasticsearch.common.xcontent.ToXContentObject;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentParser;
import org.elasticsearch.xpack.indexlifecycle.LifecyclePolicy;

import java.io.IOException;
import java.util.Objects;

public class PutLifecycleAction extends Action<PutLifecycleAction.Request, PutLifecycleAction.Response, PutLifecycleAction.RequestBuilder> {
    public static final PutLifecycleAction INSTANCE = new PutLifecycleAction();
    public static final String NAME = "cluster:admin/xpack/index_lifecycle/put";

    protected PutLifecycleAction() {
        super(NAME);
    }

    @Override
    public RequestBuilder newRequestBuilder(ElasticsearchClient client) {
        return new RequestBuilder(client, this);
    }

    @Override
    public Response newResponse() {
        return new Response();
    }

    public static class RequestBuilder extends ActionRequestBuilder<Request, Response, RequestBuilder> {

        protected RequestBuilder(ElasticsearchClient client, Action<Request, Response, RequestBuilder> action) {
            super(client, action, new Request());
        }

    }

    public static class Response extends AcknowledgedResponse implements ToXContentObject {

        public Response() {
        }

        public Response(boolean acknowledged) {
            super(acknowledged);
        }

        @Override
        public XContentBuilder toXContent(XContentBuilder builder, Params params) throws IOException {
            builder.startObject();
            addAcknowledgedField(builder);
            builder.endObject();
            return builder;
        }

        @Override
        public void readFrom(StreamInput in) throws IOException {
            readAcknowledged(in);
        }

        @Override
        public void writeTo(StreamOutput out) throws IOException {
            writeAcknowledged(out);
        }

        @Override
        public int hashCode() {
            return Objects.hash(isAcknowledged());
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null) {
                return false;
            }
            if (obj.getClass() != getClass()) {
                return false;
            }
            Response other = (Response) obj;
            return Objects.equals(isAcknowledged(), other.isAcknowledged());
        }

        @Override
        public String toString() {
            return Strings.toString(this, true, true);
        }

    }

    public static class Request extends AcknowledgedRequest<Request> implements ToXContentObject {

        public static final ParseField POLICY_FIELD = new ParseField("policy");
        private static final ConstructingObjectParser<Request, Tuple<String, NamedXContentRegistry>> PARSER =
            new ConstructingObjectParser<>("put_lifecycle_request", a -> new Request((LifecyclePolicy) a[0]));
        static {
            PARSER.declareObject(ConstructingObjectParser.constructorArg(), (p, c) -> LifecyclePolicy.parse(p, c), POLICY_FIELD);
        }
        
        private LifecyclePolicy policy;
        
        public Request(LifecyclePolicy policy) {
            this.policy = policy;
        }
        
        Request() {
        }

        public LifecyclePolicy getPolicy() {
            return policy;
        }

        @Override
        public ActionRequestValidationException validate() {
            return null;
        }

        public static Request parseRequest(String name, XContentParser parser, NamedXContentRegistry namedXContentRegistry) {
            return PARSER.apply(parser, new Tuple<>(name, namedXContentRegistry));
        }

        @Override
        public XContentBuilder toXContent(XContentBuilder builder, Params params) throws IOException {
            builder.startObject();
            builder.field(POLICY_FIELD.getPreferredName(), policy);
            builder.endObject();
            return builder;
        }

        @Override
        public void readFrom(StreamInput in) throws IOException {
            super.readFrom(in);
            policy = new LifecyclePolicy(in);
        }

        @Override
        public void writeTo(StreamOutput out) throws IOException {
            super.writeTo(out);
            policy.writeTo(out);
        }

        @Override
        public int hashCode() {
            return Objects.hash(policy);
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null) {
                return false;
            }
            if (obj.getClass() != getClass()) {
                return false;
            }
            Request other = (Request) obj;
            return Objects.equals(policy, other.policy);
        }

        @Override
        public String toString() {
            return Strings.toString(this, true, true);
        }

    }

}