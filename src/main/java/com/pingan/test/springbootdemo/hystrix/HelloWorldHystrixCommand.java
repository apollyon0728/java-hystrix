package com.pingan.test.springbootdemo.hystrix;

import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixCommandKey;
import com.netflix.hystrix.HystrixCommandProperties;
import com.netflix.hystrix.HystrixThreadPoolKey;

/**
 * HelloWorldHystrixCommand类继承自HystrixCommand<String>，用于演示如何使用Hystrix命令.
 * 它实现了HystrixCommand接口的run方法，并定义了构造函数和必要的属性.
 * 该类展示了如何配置Hystrix命令的线程池、命令组和一些默认属性.
 */
public class HelloWorldHystrixCommand extends HystrixCommand<String> {

    private final String name;

    /**
     * 构造函数，初始化HelloWorldHystrixCommand实例.
     *
     * @param name 被问候者的名称.
     */
    public HelloWorldHystrixCommand(String name) {
        // 使用HystrixCommandGroupKey工厂定义命令组名称
        super(Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey("testCommandGroupKey"))
                .andCommandKey(HystrixCommandKey.Factory.asKey("testCommandKey"))
                // 使用HystrixThreadPoolKey工厂定义线程池名称
                .andThreadPoolKey(HystrixThreadPoolKey.Factory.asKey("testThreadPool"))
                // 设置命令的默认属性，如执行超时时间
                .andCommandPropertiesDefaults(HystrixCommandProperties.Setter()
                        // .withExecutionIsolationStrategy(ExecutionIsolationStrategy.SEMAPHORE) // 信号量隔离
                        .withExecutionTimeoutInMilliseconds(5000)));
        // HystrixCommandProperties.Setter().withCircuitBreakerEnabled(true);
        // HystrixCollapserProperties.Setter()
        // HystrixThreadPoolProperties.Setter().withCoreSize(1);
        this.name = name;
    }

    // 覆盖getFallback方法以提供降级逻辑
    // @Override
    // protected String getFallback() {
    //     System.out.println("触发了降级!");
    //     return "exeucute fallback";
    // }

    /**
     * run方法是HystrixCommand接口的一部分，包含了实际的业务逻辑.
     * 在这个例子中，它返回一个问候语字符串.
     *
     * @return 包含问候语的字符串.
     * @throws InterruptedException 如果线程被中断.
     */
    @Override
    protected String run() throws InterruptedException {
        // for (int i = 0; i < 10; i++) {
        //     System.out.println("runing HelloWorldHystrixCommand..." + i);
        // }
        //
        // TimeUnit.MILLISECONDS.sleep(2000);
        return "Hello " + name + "! thread:" + Thread.currentThread().getName();
    }
}

