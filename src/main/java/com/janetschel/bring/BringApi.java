package com.janetschel.bring;

import com.janetschel.bring.data.Product;
import com.janetschel.bring.exception.AlreadyLoggedInException;
import com.janetschel.bring.exception.AmbigiousNameException;
import com.janetschel.bring.exception.NoMatchingListExistsException;
import com.janetschel.bring.exception.NotLoggedInException;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

@NoArgsConstructor
public class BringApi {
    private String userUuid = null;

    @Getter
    private final List<BringList> lists = new ArrayList<>();

    /**
     * Logs the user in and returns an object of this class
     * @param email E-Mail of the user account
     * @param password Password of the user account
     */
    public BringApi(@NotNull String email, @NotNull String password) {
        login(email, password);
    }

    /**
     * Logs the user in if provided with an e-mail and a password
     * @param email E-Mail of the user account
     * @param password Password of the user account
     */
    @SneakyThrows(AlreadyLoggedInException.class)
    public void login(@NotNull String email, @NotNull String password) {
        if (userUuid != null) {
            throw new AlreadyLoggedInException("Uuid already exists. Did you already log in?");
        }

        String parameters = String.format("email=%s&password=%s", email, password);
        JSONObject response = new JSONObject(performRequest(RequestMethods.GET, "bringlists", parameters));
        userUuid = response.getString("uuid");
    }

    /**
     * Loads all lists associated with the logged in user
     * @throws NotLoggedInException if no user has provided their credentials yet
     */
    public void loadLists() throws NotLoggedInException {
        if (userUuid == null) {
            throw new NotLoggedInException("No valid Uuid found. Did you log in?");
        }

        String urlPart = String.format("bringusers/%s/lists", userUuid);
        JSONObject response = new JSONObject(performRequest(RequestMethods.GET, urlPart, true));

        JSONArray lists = response.getJSONArray("lists");

        for (var currentList : lists) {
            JSONObject list = new JSONObject(currentList.toString());
            String name = list.getString("name");
            String listUuid = list.getString("listUuid");

            this.lists.add(new BringList(listUuid, name, this));
        }
    }

    /**
     * Returns a BringList-Object based on the name of the list
     * @param nameOfList Name of the list to be found
     * @return Returns the list - if found
     * @throws AmbigiousNameException if more than one list has the same name. Use getLists() instead
     * @throws NoMatchingListExistsException if no list with this name is found
     */
    public BringList getList(String nameOfList) throws AmbigiousNameException, NoMatchingListExistsException {
        BringList bringList = new BringList();
        int count = 0;

        for (BringList list : lists) {
            if (nameOfList.equals(list.getListName())) {
                bringList = list;
                count++;
            }
        }

        if (count > 1) {
            throw new AmbigiousNameException("Multiple lists with the same name exist.");
        } else if (count == 0) {
            throw new NoMatchingListExistsException("No list matching provided name found. Please check for capilaization.");
        }

        return bringList;
    }

    /**
     * Returns a list of all products in a list
     * @param bringList Searching for products in this list
     * @return List of all products. If none exist the list is empty
     */
    List<Product> getProductsFromList(@NotNull BringList bringList) {
        List<Product> products = new ArrayList<>();

        String urlPart = String.format("bringlists/%s", bringList.getListUuid());
        JSONObject response = new JSONObject(performRequest(RequestMethods.GET, urlPart, true));

        JSONArray allProducts = response.getJSONArray("purchase");

        for (var currentProduct : allProducts) {
            JSONObject list = new JSONObject(currentProduct.toString());
            String name = list.getString("name");
            String specification = list.getString("specification");

            products.add(new Product(name, specification));
        }

        return products;
    }

    /**
     * Adds a product to the list
     * @param product Product with name and specification
     * @param bringList A list to add the prodcut to
     */
    void addProductToList(Product product, BringList bringList) {
        String urlPart = String.format("bringlists/%s", bringList.getListUuid());
        String parameters = String.format("uuid=%s&purchase=%s&specification=%s",
                bringList.getListUuid(), product.getName(), product.getSpecification());

        performRequest(RequestMethods.PUT, urlPart, parameters, true);
    }

    /**
     * Removes a product from a list
     * @param product Product to delete
     * @param bringList A list to delete the product from
     */
    void removeProductFromList(Product product, BringList bringList) {
        String urlPart = String.format("bringlists/%s", bringList.getListUuid());
        String parameters = String.format("purchase=&recently=&specification=&remove=%s", product.getName());

        performRequest(RequestMethods.PUT, urlPart, parameters, true);
    }

    /**
     * Returns all users in a (shared) list
     * @param bringList List to return users from
     * @return Users in the list
     */
    List<User> getAllUsersFromList(BringList bringList) {
        List<User> users = new ArrayList<>();

        String urlPart = String.format("bringlists/%s/users", bringList.getListUuid());
        JSONObject response = new JSONObject(performRequest(RequestMethods.GET, urlPart, true));

        JSONArray allUsers = response.getJSONArray("users");

        for (var currentUser : allUsers) {
            JSONObject user = new JSONObject(currentUser.toString());
            String name = user.getString("name");
            String email = user.getString("email");

            users.add(new User(name, email));
        }

        return users;
    }

    private String performRequest(@NotNull RequestMethods requestMethod, @NotNull String requestUrl, @NotNull Boolean customHeadersNeeded) {
        return performRequest(requestMethod, requestUrl, "", customHeadersNeeded);
    }

    private String performRequest(@NotNull RequestMethods requestMethod, @NotNull String requestUrl, @NotNull String parameters) {
        return performRequest(requestMethod, requestUrl, parameters, false);
    }

    @SneakyThrows({ MalformedURLException.class, IOException.class })
    private String performRequest(@NotNull RequestMethods requestMethod, @NotNull String requestUrl, @NotNull String parameters, @NotNull Boolean customHeadersNeeded) {
        String bringUrl = "https://api.getbring.com/rest/";
        URL url = new URL(String.format("%s%s?%s", bringUrl, requestUrl, parameters));

        System.out.println(url.toString());

        HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
        httpURLConnection.setRequestMethod(requestMethod.getValue());

        if (customHeadersNeeded) {
            httpURLConnection.setRequestProperty("X-BRING-API-KEY", "cof4Nc6D8saplXjE3h3HXqHH8m7VU2i1Gs0g85Sp");
            httpURLConnection.setRequestProperty("X-BRING-CLIENT", "webApp");
            httpURLConnection.setRequestProperty("X-BRING-CLIENT-SOURCE", "webApp");
            httpURLConnection.setRequestProperty("X-BRING-CLIENT-INSTANCE-ID", "Web-knWFp69zgN1vzhVLW9im5ZgowcBlIH2C");
            httpURLConnection.setRequestProperty("X-BRING-COUNTRYY", "de");
            httpURLConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            httpURLConnection.setRequestProperty("charset", "UTF-8");
            httpURLConnection.setRequestProperty("X-BRING-USER-UUID", userUuid);
        }

        if (requestMethod == RequestMethods.POST || requestMethod == RequestMethods.PUT) {
            httpURLConnection.setDoOutput(true);
        }

        InputStream response = httpURLConnection.getInputStream();

        if (requestMethod == RequestMethods.GET) {
            Scanner scanner = new Scanner(response);
            return scanner.useDelimiter("\\A").next();
        }

        return null;
    }
}
