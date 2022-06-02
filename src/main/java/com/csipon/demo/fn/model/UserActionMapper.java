package com.csipon.demo.fn.model;

import com.csipon.demo.fn.UserShoppingCartFn;
import com.spring.flinksf.MessageBuilder;
import org.apache.flink.statefun.sdk.java.message.Message;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
public class UserActionMapper {
    private Function<UserActionEvent, Message> addToCard = u ->
            MessageBuilder.forAddress(UserShoppingCartFn.class, u.getUserId())
                    .withCustomType(AddToCard.TYPE, new AddToCard(u.getUserId(), u.getQuantity(), u.getItemId()))
                    .build();

    private Function<UserActionEvent, Message> checkout = u ->
            MessageBuilder.forAddress(UserShoppingCartFn.class, u.getUserId())
                    .withCustomType(Checkout.TYPE, new Checkout(u.getUserId()))
                    .build();

    private Function<UserActionEvent, Message> clearCard = u ->
            MessageBuilder.forAddress(UserShoppingCartFn.class, u.getUserId())
                    .withCustomType(ClearCard.TYPE, new ClearCard(u.getUserId()))
                    .build();

    private Function<UserActionEvent, Message> itemAvailability = u ->
            MessageBuilder.forAddress(UserShoppingCartFn.class, u.getUserId())
                    .withCustomType(ItemAvailability.TYPE, mapToItemAvailability(u))
                    .build();
    private Map<UserActionEvent.Action, Function<UserActionEvent, Message>> map = new HashMap<>();

    {
        map.put(UserActionEvent.Action.ADD_TO_CARD, addToCard);
        map.put(UserActionEvent.Action.CHECKOUT, checkout);
        map.put(UserActionEvent.Action.CLEAR_CARD, clearCard);
        map.put(UserActionEvent.Action.ITEM_AVAILABILITY, itemAvailability);
    }

    public Message map(UserActionEvent action) {
        return map.get(action.getAction()).apply(action);
    }

    private ItemAvailability mapToItemAvailability(UserActionEvent action) {
        ItemAvailability.Status status = ItemAvailability.Status.valueOf(action.getStatus().name());
        return new ItemAvailability(status, action.getQuantity());
    }

}
