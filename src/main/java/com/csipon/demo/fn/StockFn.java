package com.csipon.demo.fn;

import com.csipon.demo.fn.model.ItemAvailability;
import com.csipon.demo.fn.model.RequestItem;
import com.csipon.demo.fn.model.RestockItem;
import com.spring.flinksf.api.DispatchableFunction;
import com.spring.flinksf.api.Handler;
import lombok.extern.slf4j.Slf4j;
import org.apache.flink.statefun.sdk.java.*;
import org.apache.flink.statefun.sdk.java.message.MessageBuilder;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Component
public class StockFn implements DispatchableFunction {

    public static final TypeName TYPE = TypeName.typeNameFromString("com.demo/stock");
    public static final ValueSpec<Integer> STOCK = ValueSpec.named("stock").withIntType();

    @Handler
    public CompletableFuture<Void> onRestockItem(Context context, RestockItem event) {
        AddressScopedStorage storage = context.storage();
        final int quantity = storage.get(STOCK).orElse(0);
        log.info("{}", event);
        log.info("Scope: {}", context.self());
        log.info("Caller: {}", context.caller());
        final int newQuantity = quantity + event.getQuantity();
        storage.set(STOCK, newQuantity);
        log.info("---");
        return context.done();
    }

    @Handler
    public CompletableFuture<Void> onRequestItem(Context context, RequestItem event) {
        AddressScopedStorage storage = context.storage();
        final int quantity = storage.get(STOCK).orElse(0);
        log.info("{}", event);
        log.info("Scope: {}", context.self());
        log.info("Caller: {}", context.caller());

        final int requestQuantity = event.getQuantity();

        final ItemAvailability itemAvailability;
        log.info("Available quantity: {}", quantity);
        log.info("Requested quantity: {}", requestQuantity);
        if (quantity >= requestQuantity) {
            storage.set(STOCK, quantity - requestQuantity);
            itemAvailability = new ItemAvailability(ItemAvailability.Status.INSTOCK, requestQuantity);
        } else {
            itemAvailability = new ItemAvailability(ItemAvailability.Status.OUTOFSTOCK, requestQuantity);
        }

        final Optional<Address> caller = context.caller();
        if (caller.isPresent()) {
            context.send(
                    MessageBuilder.forAddress(caller.get())
                            .withCustomType(ItemAvailability.TYPE, itemAvailability)
                            .build());
        } else {
            throw new IllegalStateException("There should always be a caller in this example");
        }
        log.info("---");
        return context.done();
    }
}
