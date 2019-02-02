# 事务

   事务具有四个特征：原子性（Atomicity）、一致性（Consistency）、隔离性（Lsolation）、持续性（Durability）
   + 原子性：一个事务中的所有操作，或者全部完成，或者全部不完成，不会结束在中间某个环节。事务在执行过程中发生错误，会被恢复（Rollback）到事务开始前的状态，就像这个事务从来没有执行过一样，即，事务不可分割，不可简约
   + 一致性：在事务开始之前和事务结束以后，数据库的完整性没有被破坏，这表示写入的资料必须完全符合所有的预设约束、触发器、级联回滚等
   + 隔离型：数据库允许多个并发事务同时对其数据进行读写和修改的能力，隔离性可以防止多个事务并发执行时由于交叉执行而导致数据的不一致，事务隔离分为不同级别，包括读未提交（read uncommittend)
   、读提交（read committed）、可重复读（repeated read）和串行化（Serializable）
   + 持久性：事务处理结束后，对数据的修改就是永久的，即便系统故障也不会丢失
   
## 隔离性
   + Read Uncommitted(读取未提交内容)：允许脏读，也就是可能读取到其他会话中未提交事务修改的数据
   + Read Committed(提交读)：只能读取到已经提交的数据,Oracle等多数数据库默认都是该级别（不重复读）
   + Repeated Read(可重复读)：在同一个事务内的查询都是事务开始时刻一致的，InnoDB默认级别，在 SQL 标准中，该隔离级别消除了不可重复读，但是还存在幻读
   + Serializable（串行读）：完全串行化的读，每次读都需要获得表级共享锁，读写都会相互阻塞
    
    
   + 脏读：一个事务在访问数据，并且对数据进行了修改，而这种修改还没有提交到数据库中，这时，另外一个事务也访问这个数据，然后使用了这个数据。
   + 不可重复读：一个事务内，多次读同一数据，在这个事务还没结束时，另外一个事务也访问该同一数据，在第一个数据中的两次读数据之间，由于第二个事务的修改，那么第一个事务两次读到的数据可能是不一样的
   ，这样就发生了在一个事务内读到的数据是不一样的）
   + 幻读：第一个事务对一个表中的数据进行了修改，这种修改涉及到表中的全部数据行，同时，第二个事务也修改这个表中的数据，这种修改是向表中插入一行新数据，那么，以后就会发生操作第一个事务的用户发现表中还有没有修改
   的数据行，就好像发生了幻觉一样
   
### mysql 防止幻读方法   
   当隔离级别是可重复读，且禁用 innodb_locks_unsafe_for_binlog 的情况下，在搜索和扫描 index 的时候使用的 next-key lokcs 可以避免幻读
   
   
### spring中的隔离级别
   
   + ISOLATION_DEFAULT：PlatfromTransactionManager 默认的隔离级别，使用数据库默认事务隔离级别，另外四个与 JDBC 的隔离级别相对应
   + ISOLATION_READ_UNCOMMITTED：最低的事务隔离级别，允许另外一个事务可以看到这个事务未提交的数据，这种隔离级别会产生脏读，不可重复读和幻读
   + ISOLATION_READ_COMMITTED：保证了一个事务修改的数据提交后才能被另外一个事务读取，另外一个事务不能读取该事务未提交的数据
   + ISOLATION_REPEATABLE_READ：这种事务隔离级别可以防止脏读、不可重复读，但是可能出现幻读
   + ISOLATION_SERIALIZABLE：花费最高代价但最可靠的事务隔离级别，事务被处理为顺序执行
   
## 事务传播

   + PROPAGATION_REQUIRED：支持当前事务，如果当前没有事务，就新建一个事务（默认）
   + PROPAGATION_REQUIRES_NEW：新建事务，如果当前存在事务，把当前事务挂起，新建的事务和被挂启的事务没有任何关系，是两个独立的事务，外层事务失败回归之后，不能回滚内层事务
   执行的结果，内层事务失败抛出异常后，外层事务捕获，也可以不处理回滚操作
   + PROPAGATION_SUPPORTS： 支持当前事务，如果当前没有事务，就以非事务方式执行
   + PROPAGATION_MANDATORY：支持当前事务，如果当前没有事务，就抛出异常
   + PROPAGATION_NOT_SUPPORTED：以非事务方式操作，如果当前存在事务，就把事务挂起
   + PROPAGATION_NEVER：以非事务方式执行，如果当前存在事务，则抛出异常
   + PROPAGATION_NESTED：如果一个活动的事物存在，则运行在一个嵌套的事务中，如果没有活动事务，则按REQUIRED属性执行，它使用了一个单独的事务，这个事务拥有多个可以回滚的保存点，
   内部事务的回滚不会对外部事务造成影响，它只对 DataSourceTransactionManager 事务管理器起效

## 事务不生效的几种情况
   + mysql数据库表引擎是 MyLSAM 不支持事务
   + 如果使用了 spring+mvc,则 context:component-scan重复扫描问题可能引起事务失败（父子容器）
   + @Transactional 注解开启配置，必须放到 listener 里加载，如果放到 DispatcherServlet 的配置里，事务不起作用
   + @Transactional 注解只能应用到 public 可见度的方法上。 如果你在 protected、private 或者 package-visible 的方法上使用 @Transactional 注解，它也不会报错，事务也会失效
   + 同一个类内 A 方法调用 B 方法，B方法的事务无效（此处 B 方法无法代理）
   + 事务的传播机制，嵌套方法不使用事务