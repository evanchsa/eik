/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.karaf.eik.ui.features;

public class MvnUrlConverter {

	public String getPath(String url) {
		if (url != null) {
			if (url.startsWith("mvn:")) {
				url = url.substring(4);
                String[] repositorySplit = url.split("!");
                String urlWithoutRepository = repositorySplit[repositorySplit.length - 1];

                String[] segments = urlWithoutRepository.split("/");
                if (segments.length >= 3) {
                    String groupId = segments[0];
                    String artifactId = segments[1];
                    String version = segments[2];

                    return groupId.replace(".", "/") + "/" + artifactId + "/" + version + "/" + artifactId + "-" + version + ".jar";
                }
			}
		}
		
		return url;
	}
	
}
