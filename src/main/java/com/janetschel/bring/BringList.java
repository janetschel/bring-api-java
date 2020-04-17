package com.janetschel.bring;

import com.janetschel.bring.data.Product;
import lombok.Getter;

import java.util.List;

public class BringList {
    @Getter private String listUuid;
    @Getter private String listName;

    private BringApi bringApi;

    BringList(String listUuid, String listName, BringApi bringApi) {
        this.listUuid = listUuid;
        this.listName = listName;
        this.bringApi = bringApi;
    }

    BringList() {}

    /**
     * @return All products currently in the list
     */
    public List<Product> getProducts() {
        return bringApi.getProductsFromList(this);
    }

    /**
     * @param product Adds a specified product to the list
     */
    public void addProduct(Product product) {
        bringApi.addProductToList(product, this);
    }

    /**
     * @param product Removes a specified product from the list
     */
    public void removeProduct(Product product) {
        bringApi.removeProductFromList(product, this);
    }

    /**
     * @return Returns all users which are members of the list
     */
    public List<User> getAllUser() {
        return bringApi.getAllUsersFromList(this);
    }
}
