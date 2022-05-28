package com.csipon.demo.fn.model;

import com.spring.flinksf.api.MessageType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.flink.statefun.sdk.java.TypeName;
import org.apache.flink.statefun.sdk.java.types.SimpleType;
import org.apache.flink.statefun.sdk.java.types.Type;

import static com.csipon.demo.ObjectMapperUtil.MAPPER;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RestockItem {
    @MessageType
    public static final Type<RestockItem> TYPE = SimpleType.simpleImmutableTypeFrom(
            TypeName.typeNameFromString("com.demo/RestockItem"),
            MAPPER::writeValueAsBytes,
            bytes -> MAPPER.readValue(bytes, RestockItem.class));

    private String itemId;
    private Integer quantity;
}
