package com.csipon.demo.fn.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.spring.flinksf.api.MessageType;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.flink.statefun.sdk.java.TypeName;
import org.apache.flink.statefun.sdk.java.types.SimpleType;
import org.apache.flink.statefun.sdk.java.types.Type;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static com.csipon.demo.ObjectMapperUtil.MAPPER;

@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Basket {
    @MessageType
    public static final Type<Basket> TYPE = SimpleType.simpleImmutableTypeFrom(
            TypeName.typeNameFromString("com.demo/Basket"),
            MAPPER::writeValueAsBytes,
            bytes -> MAPPER.readValue(bytes, Basket.class));

    private Map<String, Integer> basket;

    public static Basket initEmpty() {
        return new Basket(new HashMap<>());
    }

    public void add(String itemId, int quantity) {
        basket.put(itemId, basket.getOrDefault(itemId, 0) + quantity);
    }

    public void remove(String itemId, int quantity) {
        int remainder = basket.getOrDefault(itemId, 0) - quantity;
        if (remainder > 0) {
            basket.put(itemId, remainder);
        } else {
            basket.remove(itemId);
        }
    }

    @JsonIgnore
    public Set<Map.Entry<String, Integer>> getEntries() {
        return basket.entrySet();
    }

    @JsonProperty("basket")
    public Map<String, Integer> getBasketContent() {
        return basket;
    }

    public void clear() {
        basket.clear();
    }
}
