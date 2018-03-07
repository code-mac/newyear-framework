# New-Year Framework

The New Year framework is an event-based framework.

Java 内部对 lambda 表达式 泛型 反射 ParameterizedType 判断错误

领域事件只能被同应用同领域的订阅者进行订阅，应用事件只能被同应用的订阅者进行订阅，
超出应用范围的事件应该使用消息机制进行发布

应用事件 发生格式 application:newyear
领域事件 发生格式 application:newyear@domain:core

以 DomainEvent 为 key 进行注册，注册的 Subscriber 要求能够 handle 所有的 DomainEvent 
事件

<pre>
void subscribe(Class<? extend T> aClass, EventSubscriber<? super T> subscriber);
</pre>

为非延迟加载的注入操作，进行字节码修改