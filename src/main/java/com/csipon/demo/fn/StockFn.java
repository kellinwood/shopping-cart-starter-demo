package com.csipon.demo.fn;

import com.csipon.demo.fn.model.ItemAvailability;
import com.csipon.demo.fn.model.RequestItem;
import com.csipon.demo.fn.model.RestockItem;
import com.spring.flink.statefun.MessageBuilder;
import com.spring.flink.statefun.api.DispatchableFunction;
import com.spring.flink.statefun.api.Handler;
import com.spring.flink.statefun.api.StatefulFunction;
import lombok.extern.slf4j.Slf4j;
import org.apache.flink.statefun.sdk.java.Address;
import org.apache.flink.statefun.sdk.java.AddressScopedStorage;
import org.apache.flink.statefun.sdk.java.Context;
import org.apache.flink.statefun.sdk.java.ValueSpec;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Slf4j
@StatefulFunction(namespace = "com.demo", name = "stock")
public class StockFn implements DispatchableFunction {

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
