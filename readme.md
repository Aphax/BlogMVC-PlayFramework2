# A MVC blog with Play Java 2.5

### Requirements

  - JDK8
  - Mysql5

### Launch local server

```sh
$ cd BlogMVC-PlayFramework2
$ ./bin/activator run
```
Server will compile files and then open Netty server on port 9000.
Open http://localhost:9000

### Database configuration

Create mysql database named "blogmvc" from dump.sql root file.
You can edit configuration in conf/application.conf file, on the "db" namespace (at the bottom).
If you want to try another database engine, follow details here [https://www.playframework.com/documentation/2.5.x/JavaDatabase](https://www.playframework.com/documentation/2.5.x/JavaDatabase)

### Improvements TODO

- Comments feature
- Better data validation in admin panel
- Small REST API sample for comments feature
- CSRF token security sample
- Tests samples (Unit , Functionnal and Integration)
- Improve this documentation

### Resources / Going further
Why is Play Framework so fast ? by Lightbend (Play's distributor)
[https://www.lightbend.com/blog/why-is-play-framework-so-fast](https://www.lightbend.com/blog/why-is-play-framework-so-fast)

The ultimate guide by YEVGENIY BRIKMAN (Play developer at LinkedIn)
[http://www.ybrikman.com/writing/2014/03/10/the-ultimate-guide-to-getting-started/](http://www.ybrikman.com/writing/2014/03/10/the-ultimate-guide-to-getting-started/)

**Enjoy!**
