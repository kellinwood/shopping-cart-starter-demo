package com.csipon.demo.fn.model;

import com.insyde.flink.statefun.api.DataType;
import lombok.Data;
import org.apache.flink.statefun.sdk.java.TypeName;
import org.apache.flink.statefun.sdk.java.types.SimpleType;
import org.apache.flink.statefun.sdk.java.types.Type;

import static com.csipon.demo.ObjectMapperUtil.MAPPER;

@Data
public class UserActionEvent {
    @DataType
    public static final Type<UserActionEvent> TYPE = SimpleType.simpleImmutableTypeFrom(
            TypeName.typeNameFromString("com.demo/UserActionEvent"),
            MAPPER::writeValueAsBytes,
            bytes -> MAPPER.readValue(bytes, UserActionEvent.class));

    private Action action;
    private String userId;
    private Integer quantity;
    private String itemId;
    private Status status;

    public enum Action {
        ADD_TO_CARD,
        CHECKOUT,
        CLEAR_CARD,
        ITEM_AVAILABILITY;
    }

    public enum Status {
        INSTOCK,
        OUTOFSTOCK
    }
}
