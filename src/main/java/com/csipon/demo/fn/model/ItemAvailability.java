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
public class ItemAvailability {
    @MessageType
    public static final Type<ItemAvailability> TYPE = SimpleType.simpleImmutableTypeFrom(
            TypeName.typeNameFromString("com.demo/ItemAvailability"),
            MAPPER::writeValueAsBytes,
            bytes -> MAPPER.readValue(bytes, ItemAvailability.class));

    public enum Status {
        INSTOCK,
        OUTOFSTOCK
    }

    private Status status;
    private int quantity;
}
