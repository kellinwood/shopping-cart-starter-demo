package com.csipon.demo.fn.model;

import com.spring.flink.statefun.api.DataType;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.apache.flink.statefun.sdk.java.TypeName;
import org.apache.flink.statefun.sdk.java.types.SimpleType;
import org.apache.flink.statefun.sdk.java.types.Type;

import static com.csipon.demo.ObjectMapperUtil.MAPPER;

@AllArgsConstructor
@NoArgsConstructor
public class Checkout {
    @DataType
    public static final Type<Checkout> TYPE = SimpleType.simpleImmutableTypeFrom(
            TypeName.typeNameFromString("com.demo/Checkout"),
            MAPPER::writeValueAsBytes,
            bytes -> MAPPER.readValue(bytes, Checkout.class));
    private String userId;
}
