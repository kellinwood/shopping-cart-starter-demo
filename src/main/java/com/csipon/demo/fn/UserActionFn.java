package com.csipon.demo.fn;

import com.csipon.demo.fn.model.UserActionEvent;
import com.csipon.demo.fn.model.UserActionMapper;
import com.insyde.flink.statefun.api.DispatchableFunction;
import com.insyde.flink.statefun.api.Handler;
import com.insyde.flink.statefun.api.StatefulFunction;
import lombok.RequiredArgsConstructor;
import org.apache.flink.statefun.sdk.java.Context;
import org.apache.flink.statefun.sdk.java.message.Message;

import java.util.concurrent.CompletableFuture;

@RequiredArgsConstructor
@StatefulFunction(namespace = "com.demo", name = "user-action")
public class UserActionFn implements DispatchableFunction {

    private final UserActionMapper mapper;

    @Handler
    public CompletableFuture<Void> onUserAction(Context context, UserActionEvent event) {
//        map generic user action event into specific and send to UserShoppingCartFn
        Message message = mapper.map(event);
        context.send(message);
        return context.done();
    }
}
