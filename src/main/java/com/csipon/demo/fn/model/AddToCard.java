package com.csipon.demo.fn.model;

import com.spring.flink.statefun.api.DataType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.flink.statefun.sdk.java.TypeName;
import org.apache.flink.statefun.sdk.java.types.SimpleType;
import org.apache.flink.statefun.sdk.java.types.Type;

import static com.csipon.demo.ObjectMapperUtil.MAPPER;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AddToCard {
    @DataType
    public static final Type<AddToCard> TYPE = SimpleType.simpleImmutableTypeFrom(
            TypeName.typeNameFromString("com.demo/AddToCard"),
            MAPPER::writeValueAsBytes,
            bytes -> MAPPER.readValue(bytes, AddToCard.class));

    private String userId;
    private Integer quantity;
    private String itemId;
}
