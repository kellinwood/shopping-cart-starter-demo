package com.csipon.demo.fn.model;

import com.insyde.flink.statefun.api.DataType;
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
public class RequestItem {
    @DataType
    public static final Type<RequestItem> TYPE = SimpleType.simpleImmutableTypeFrom(
            TypeName.typeNameFromString("com.demo/RequestItem"),
            MAPPER::writeValueAsBytes,
            bytes -> MAPPER.readValue(bytes, RequestItem.class));

    private Integer quantity;
}
