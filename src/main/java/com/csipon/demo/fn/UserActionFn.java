package com.csipon.demo.fn;

import com.csipon.demo.fn.model.UserActionEvent;
import com.csipon.demo.fn.model.UserActionMapper;
import com.spring.flinksf.api.DispatchableFunction;
import com.spring.flinksf.api.Handler;
import lombok.RequiredArgsConstructor;
import org.apache.flink.statefun.sdk.java.Context;
import org.apache.flink.statefun.sdk.java.TypeName;
import org.apache.flink.statefun.sdk.java.message.Message;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

@RequiredArgsConstructor
@Component
public class UserActionFn implements DispatchableFunction {

    public static final TypeName TYPE = TypeName.typeNameFromString("com.demo/user-action");

    private final UserActionMapper mapper;

    @Handler
    public CompletableFuture<Void> onUserAction(Context context, UserActionEvent event) {
//        map generic user action event into specific and send to UserShoppingCartFn
        Message message = mapper.map(event);
        context.send(message);
        return context.done();
    }


}
