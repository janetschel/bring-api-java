package com.janetschel.bring.data;

import lombok.Getter;
import org.jetbrains.annotations.NotNull;

@Getter
public class Product {
    private final String name;
    private final String specification;

    public Product(@NotNull String name, @NotNull String specification) {
        name = name
                .replace("ä", "ae")
                .replace("ö", "oe")
                .replace("ü", "ue")
                .replace(" ", "%20");
        specification = specification
                .replace("ä", "ae")
                .replace("ö", "oe")
                .replace("ü", "ue")
                .replace(" ", "%20");

        this.name = name.trim();
        this.specification = specification;
    }
}
