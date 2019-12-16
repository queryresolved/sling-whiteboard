/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with this
 * work for additional information regarding copyright ownership. The ASF
 * licenses this file to You under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.osgi.feature.impl;

import org.osgi.feature.ArtifactID;
import org.osgi.feature.Bundle;
import org.osgi.feature.Feature;
import org.osgi.feature.Feature.Builder;
import org.osgi.feature.FeatureService;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonNumber;
import javax.json.JsonObject;
import javax.json.JsonString;
import javax.json.JsonValue;

public class FeatureServiceImpl implements FeatureService {

    @Override
    public Feature readFeature(Reader jsonReader) throws IOException {
        JsonObject json = Json.createReader(jsonReader).readObject();

        String id = json.getString("id");
        Builder builder = new Feature.Builder(ArtifactID.fromMavenID(id));

        builder.setTitle(json.getString("title", null));
        builder.setDescription(json.getString("description", null));
        builder.setVendor(json.getString("vendor", null));
        builder.setLicense(json.getString("license", null));
        builder.setLocation(json.getString("location", null));

        builder.setComplete(json.getBoolean("complete", false));
        builder.setFinal(json.getBoolean("final", false));

        builder.addBundles(getBundles(json));

        return builder.build();
    }

    private Bundle[] getBundles(JsonObject json) {
        List<Bundle> bundles = new ArrayList<>();

        JsonArray ja = json.getJsonArray("bundles");
        for (JsonValue val : ja) {
            if (val.getValueType() == JsonValue.ValueType.OBJECT) {
                JsonObject jo = val.asJsonObject();
                String bid = jo.getString("id");
                Bundle.Builder bbuilder = new Bundle.Builder(ArtifactID.fromMavenID(bid));

                for (Map.Entry<String, JsonValue> entry : jo.entrySet()) {
                    if (entry.getKey().equals("id"))
                        continue;

                    JsonValue value = entry.getValue();

                    Object v;
                    switch (value.getValueType()) {
                    case NUMBER:
                        v = ((JsonNumber) value).longValueExact();
                        break;
                    case STRING:
                        v = ((JsonString) value).getString();
                        break;
                    default:
                        v = value.toString();
                    }
                    bbuilder.addMetadata(entry.getKey(), v);
                }
                bundles.add(bbuilder.build());
            }
        }

        return bundles.toArray(new Bundle[0]);
    }

    @Override
    public void writeFeature(Feature feature, Writer jsonWriter) throws IOException {
        // TODO Auto-generated method stub

    }

    @Override
    public Feature mergeFeatures(Feature f1, Feature f2) {
        // TODO Auto-generated method stub
        return null;
    }

}