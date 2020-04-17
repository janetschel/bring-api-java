## Unoffical Bring! Api
### Prequesit
Bring! is an pretty popular grocery shopping app developed in Switzerland and mainly used in Germany, Austria and Switzerland.

Sadly Bring! does not offer a public API to interact with and build own applications which make requests to their servers.

Since I needed an API to connect to Bring! servers and perform requests for another project of mine, I build this little **unofficial**
app which lets you interact with the Bring! servers.

### Usage
First you need to add the .jar to your classpath.

After that you can use it like that:

```Java
public class Example(){

    @SneakyThrows
    public static void main(String[] args){
        
        // Basic login
        BringApi bringApi = new BringApi("email", "password");
        bringApi.loadLists(); // loads all lists with the user as a member
        BringList myList = bringApi.getList("listName");
        
        // Get all products from a list
        List<Product> myProducts = myList.getProducts();
        
        // Add a product to a list
        Product myNewProduct = new Product("productName", "productSpecification");
        myList.addProduct(myNewProduct);
        
        // Delete a product from a list
        myList.removeProduct(myNewProduct);
    }
}
```

Note that you can also log in by using the method
```Java
public class Example(){

    @SneakyThrows
    public static void main(String[] args){
       
        // Log in using the login method
        BringApi bringApi = new BringApi();    
        bringApi.login("email", "password");
        
        // ...        
    }
}
``` 
Please be aware of the exceptions thrown by the methods.
In this example I used the `@SneakyThrows` annotation from lombok so I don't have a massive overhead using try-catch etc.

You have to catch and handle those exceptions by yourself (or annotate your method with at `@SneakyThrows` if your application allows that without it being to dirty)

### Headsup
Please note that this is an **unofficial** API, and I am in no way associated with this company.

It also is not complete so there may be some features I did not implement yet. You can always contribute to this project by simply opening an issue or PR.

Any new ideas? Let me know!

### Status
It works with the latest version of Bring!. I will test it with every new version