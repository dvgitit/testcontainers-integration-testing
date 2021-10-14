package com.bmuschko.testcontainers.repository.warehouse;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;

import java.io.IOException;

public class ProductIndexRepositoryImpl implements ProductIndexRepository {
    public static final String PRODUCTS_COLLECTION = "products";
    private final String serverUrl;

    public ProductIndexRepositoryImpl(String serverUrl) {
        this.serverUrl = serverUrl;
    }

    @Override
    public void insertProduct(Product product) {
        SolrClient client = null;

        try {
            client = createSolrClient();
            client.addBean(PRODUCTS_COLLECTION, product);
            client.commit(PRODUCTS_COLLECTION);
        } catch (IOException | SolrServerException e) {
            throw new ProductIndexException("Failed to insert product", e);
        } finally {
            closeCloseable(client);
        }
    }

    private SolrClient createSolrClient() {
        return new HttpSolrClient.Builder(serverUrl)
                .withConnectionTimeout(10000)
                .withSocketTimeout(60000)
                .build();
    }

    private void closeCloseable(SolrClient client) {
        try {
            if (client != null) {
                client.close();
            }
        } catch (IOException e) {
            // ignore
        }
    }
}
