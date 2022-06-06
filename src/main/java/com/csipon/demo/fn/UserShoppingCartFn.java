package com.csipon.demo.fn;

import com.csipon.demo.fn.model.*;
import com.spring.flink.statefun.MessageBuilder;
import com.spring.flink.statefun.api.DispatchableFunction;
import com.spring.flink.statefun.api.Handler;
import com.spring.flink.statefun.api.StatefulFunction;
import lombok.extern.slf4j.Slf4j;
import org.apache.flink.statefun.sdk.java.Address;
import org.apache.flink.statefun.sdk.java.AddressScopedStorage;
import org.apache.flink.statefun.sdk.java.Context;
import org.apache.flink.statefun.sdk.java.ValueSpec;
import org.apache.flink.statefun.sdk.java.io.KafkaEgressMessage;
import org.apache.flink.statefun.sdk.java.message.EgressMessage;
import org.apache.flink.statefun.sdk.java.message.Message;

import org.springframework.beans.factory.annotation.Value;

import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Slf4j
@StatefulFunction(namespace = "com.demo", name = "user-shopping-cart")
public class UserShoppingCartFn implements DispatchableFunction {

    public static final ValueSpec<Basket> BASKET = ValueSpec.named("basket").withCustomType(Basket.TYPE);

    @Value("${kafka.producer.topics.receipt}")
    private String receiptTopic;

    @Handler
    public CompletableFuture<Void> onAddToCard(Context context, AddToCard event) {
        log.info("{}", event);
        log.info("Scope: {}", context.self());
        log.info("Caller: {}", context.caller());

        final RequestItem requestItem = new RequestItem(event.getQuantity());
        final Message request =
                MessageBuilder.forAddress(StockFn.class, event.getItemId())
                        .withCustomType(RequestItem.TYPE, requestItem)
                        .build();
        context.send(request);
        log.info("---");
        return context.done();
    }

    @Handler
    public CompletableFuture<Void> onItemAvailability(Context context, ItemAvailability event) {
        log.info("{}", event);
        log.info("Scope: {}", context.self());
        log.info("Caller: {}", context.caller());
        if (ItemAvailability.Status.INSTOCK.equals(event.getStatus())) {
            final AddressScopedStorage storage = context.storage();
            final Basket basket = storage.get(BASKET).orElse(Basket.initEmpty());

            // ItemAvailability event comes from the Stock function and contains the itemId as the
            // caller id
            final Optional<Address> caller = context.caller();
            if (caller.isPresent()) {
                basket.add(caller.get().id(), event.getQuantity());
            } else {
                throw new IllegalStateException("There should always be a caller in this example");
            }

            storage.set(BASKET, basket);
            log.info("Basket: {}", basket);
            log.info("---");
        }
        return context.done();
    }

    @Handler
    public CompletableFuture<Void> onCheckout(Context context, Checkout event) {
        final AddressScopedStorage storage = context.storage();

        log.info("{}", event);
        log.info("Scope: {}", context.self());
        log.info("Caller: {}", context.caller());
        log.info("Basket: {}", storage.get(BASKET));

        final Optional<String> itemsOption =
                storage
                        .get(BASKET)
                        .map(
                                basket ->
                                        basket.getEntries().stream()
                                                .map(entry -> entry.getKey() + ": " + entry.getValue())
                                                .collect(Collectors.joining("\n")));

        itemsOption.ifPresent(
                items -> {
                    log.info("Receipt items: ");
                    log.info("{}", items);

                    final Receipt receipt = new Receipt(context.self().id(), items);
                    final EgressMessage egressMessage =
                            KafkaEgressMessage.forEgress(Identifiers.RECEIPT_EGRESS)
                                    .withKey(context.self().id().getBytes(StandardCharsets.UTF_8))
                                    .withTopic(receiptTopic)
                                    .withValue(Receipt.TYPE, receipt)
                                    .build();
                    context.send(egressMessage);
                });
        log.info("---");
        return context.done();
    }

    @Handler
    public CompletableFuture<Void> onClearCard(Context context, ClearCard event) {
        final AddressScopedStorage storage = context.storage();

        log.info("{}", event);
        log.info("Scope: {}", context.self());
        log.info("Caller: {}", context.caller());
        log.info("Basket: {}", storage.get(BASKET));

        storage
                .get(BASKET)
                .ifPresent(
                        basket -> {
                            for (Map.Entry<String, Integer> entry : basket.getEntries()) {
                                RestockItem restockItem =
                                        new RestockItem(entry.getKey(), entry.getValue());
                                Message restockCommand =
                                        MessageBuilder.forAddress(StockFn.class, entry.getKey())
                                                .withCustomType(RestockItem.TYPE, restockItem)
                                                .build();

                                context.send(restockCommand);
                            }
                            basket.clear();
                        });
        log.info("---");
        return context.done();
    }
}
