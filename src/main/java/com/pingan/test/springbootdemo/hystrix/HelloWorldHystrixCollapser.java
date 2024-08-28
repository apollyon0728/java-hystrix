package com.pingan.test.springbootdemo.hystrix;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.netflix.hystrix.HystrixCollapser;
import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixCommandKey;
import com.netflix.hystrix.HystrixCommandProperties;
import com.netflix.hystrix.HystrixThreadPoolKey;
import com.netflix.hystrix.HystrixCommand.Setter;

/**
 * HelloWorldHystrixCollapser类用于演示如何使用Hystrix进行请求合并
 * 它扩展了HystrixCollapser，将多个单独的请求合并为一个批处理请求，以减少网络调用次数
 * <p>
 * Sample {@link HystrixCollapser} that automatically batches multiple requests to execute()/queue()
 * into a single {@link HystrixCommand} execution for all requests within the defined batch (time or size).
 */
public class HelloWorldHystrixCollapser extends HystrixCollapser<List<String>, String, Integer> {

    private final Integer key; // 请求的键，用于标识每个请求

    // 构造函数，初始化请求的键
    public HelloWorldHystrixCollapser(Integer key) {
        this.key = key;
    }

    // 返回请求的参数，用于识别每个单独的请求
    @Override
    public Integer getRequestArgument() {
        return key;
    }

    // 创建一个批量请求命令
    @Override
    protected HystrixCommand<List<String>> createCommand(final Collection<CollapsedRequest<String, Integer>> requests) {
        return new BatchCommand(requests);	// 把批量请求传给command类
    }

    // 把批量请求的结果和对应的请求一一对应起来
    @Override
    protected void mapResponseToRequests(List<String> batchResponse, Collection<CollapsedRequest<String, Integer>> requests) {
        int count = 0;
        for (CollapsedRequest<String, Integer> request : requests) {
            request.setResponse(batchResponse.get(count++));
        }
    }

    // command类，用于处理批量请求
    private static final class BatchCommand extends HystrixCommand<List<String>> {
        private final Collection<CollapsedRequest<String, Integer>> requests; // 批量请求集合

        // 构造函数，初始化批量请求集合
        private BatchCommand(Collection<CollapsedRequest<String, Integer>> requests) {
            super(Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey("CollepsingGroup"))
                    .andCommandKey(HystrixCommandKey.Factory.asKey("CollepsingKey")));
            this.requests = requests;
        }

        // 执行批量请求，处理每个请求并返回结果
        @Override
        protected List<String> run() {
            ArrayList<String> response = new ArrayList<String>(); // 用于存储所有请求的响应
            // 处理每个请求，返回结果
            for (CollapsedRequest<String, Integer> request : requests) {
                // artificial response for each argument received in the batch
                response.add("ValueForKey: " + request.getArgument() + " thread:" + Thread.currentThread().getName());
            }
            return response;
        }
    }
}

