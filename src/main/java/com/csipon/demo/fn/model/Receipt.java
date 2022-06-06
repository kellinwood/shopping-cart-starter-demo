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
public class Receipt {
    @DataType
    public static final Type<Receipt> TYPE = SimpleType.simpleImmutableTypeFrom(
            TypeName.typeNameFromString("com.demo/Receipt"),
            MAPPER::writeValueAsBytes,
            bytes -> MAPPER.readValue(bytes, Receipt.class));

    private String userId;
    private String details;
}
