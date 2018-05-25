/*
 * Copyright 2017 Sixhours.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.sixhours.memcached.cache;

import net.spy.memcached.ClientMode;
import net.spy.memcached.ConnectionFactoryBuilder;
import net.spy.memcached.MemcachedClient;
import net.spy.memcached.auth.AuthDescriptor;
import net.spy.memcached.auth.PlainCallbackHandler;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.List;

/**
 * Factory for the {@link MemcachedCacheManager} instances.
 *
 * @author Igor Bolic
 */
public class MemcachedCacheManagerFactory {

    private final MemcachedCacheProperties properties;

    public MemcachedCacheManagerFactory(MemcachedCacheProperties properties) {
        this.properties = properties;
    }

    public MemcachedCacheManager create() throws IOException {
        final DisposableMemcachedCacheManager cacheManager = new DisposableMemcachedCacheManager(memcachedClient());

        cacheManager.setExpiration(properties.getExpiration());
        cacheManager.setExpirations(properties.getExpirations());
        cacheManager.setPrefix(properties.getPrefix());
        cacheManager.setNamespace(properties.getNamespace());

        return cacheManager;
    }

    private MemcachedClient memcachedClient() throws IOException {
        final List<InetSocketAddress> servers = properties.getServers();
        final ClientMode mode = properties.getMode();
        final MemcachedCacheProperties.Protocol protocol = properties.getProtocol();

        final ConnectionFactoryBuilder connectionFactoryBuilder = new ConnectionFactoryBuilder()
                .setClientMode(mode)
                .setProtocol(protocol.value());

        if (properties.getUsername() != null) {
            connectionFactoryBuilder.setAuthDescriptor(new AuthDescriptor(
                new String[] { "PLAIN" },
                new PlainCallbackHandler(properties.getUsername(), properties.getPassword())));
        }

        return new MemcachedClient(connectionFactoryBuilder.build(), servers);
    }
}
