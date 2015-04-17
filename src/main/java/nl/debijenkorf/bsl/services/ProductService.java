package nl.debijenkorf.bsl.services;

import nl.debijenkorf.bsl.models.ProductModel;


public interface ProductService {
    public ProductModel getProduct(String productSku) throws Exception;

    //public CatalogueResponse getCatalogue(String query, int size, int start, String sort) throws Exception;
}
