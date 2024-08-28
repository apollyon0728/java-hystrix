package com.pingan.test.springbootdemo.hystrix;

import java.util.concurrent.TimeUnit;

import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixCommandKey;
import com.netflix.hystrix.HystrixCommandProperties;
import com.netflix.hystrix.HystrixObservableCommand;
import com.netflix.hystrix.HystrixThreadPoolKey;

import rx.Observable;
import rx.Subscriber;

/**
 * HelloWorldHystrixObservableCommand类继承自HystrixObservableCommand<String>，用于演示如何使用Hystrix的Observable命令
 * 这个类实现了构造函数和construct方法，用于异步地执行字符串的处理操作
 */
public class HelloWorldHystrixObservableCommand extends HystrixObservableCommand<String> {

    // 声明一个名称变量，用于标识每个命令实例
    private final String name;

    /**
     * 构造函数，初始化命令实例
     * @param name 用于标识命令的名称
     */
    public HelloWorldHystrixObservableCommand(String name) {
        // 调用父类的构造函数，设置命令所属的命令组
        super(HystrixCommandGroupKey.Factory.asKey("ExampleGroup"));
        this.name = name;
    }

    /**
     * construct方法是HystrixObservableCommand的核心方法，它返回一个Observable<String>对象
     * 这个方法在命令执行线程池中的一个线程上运行，并生成字符串序列
     * @return 返回一个Observable<String>对象，该对象可以被订阅以接收字符串序列
     */
    @Override
    protected Observable<String> construct() {
        // 打印构造函数运行的线程信息
        System.out.println("in construct! thread:" + Thread.currentThread().getName());
        // 创建并返回一个Observable对象，开始异步生成字符串序列
        return Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(Subscriber<? super String> observer) {
                // 尝试生成字符串序列
                try {
                    // 检查观察者是否已取消订阅，如果没有，则继续发送数据
                    if (!observer.isUnsubscribed()) {
                        observer.onNext("Hello1" + " thread:" + Thread.currentThread().getName());
                        observer.onNext("Hello2" + " thread:" + Thread.currentThread().getName());
                        observer.onNext(name + " thread:" + Thread.currentThread().getName());
                        // 完成前的打印信息
                        System.out.println("complete before------" + " thread:" + Thread.currentThread().getName());
                        // 标记序列完成
                        observer.onCompleted();
                        // 这些代码不会被执行，因为onCompleted后不会继续执行observer的方法
                        System.out.println("complete after------" + " thread:" + Thread.currentThread().getName());
                        observer.onCompleted();
                        observer.onNext("abc");
                    }
                } catch (Exception e) {
                    // 发生异常时，调用观察者的onError方法
                    observer.onError(e);
                }
            }
        });
    }
}

