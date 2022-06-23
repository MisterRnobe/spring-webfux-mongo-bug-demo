## spring-webfux-mongo-bug-demo

This project reproduces bug described in [issue]() and [stackoverflow](). 

In a nutshell, spring data's method returning `Flux` and passed to a controller stops producing data after some period of time.

### How to reproduce
1. Launch mongodb, you can use provided `docker-compose.yaml` file
```shell
docker-compose up
```
2. Launch the application using your IDE. Ensure `application.yaml` file contains appropriate mongodb uri
3. Wait for the application fill data, it should print `Finished data inserting` to log
4. The application should fetch data from HTTP endpoint `/data` from itself by 8 workers. They must print the following message
```
#{NUM} Fetched {COUNT} records
```
5. After a while one of fetcher may print nothing, you log may look like this one
```
2022-06-23 16:16:21.845  INFO 41517 --- [or-http-epoll-2] ru.nmedvedev.demo.service.Demo           : #8 Fetched 90000 records
2022-06-23 16:16:21.880  INFO 41517 --- [or-http-epoll-6] ru.nmedvedev.demo.service.Demo           : #4 Fetched 90000 records
2022-06-23 16:16:21.948  INFO 41517 --- [or-http-epoll-7] ru.nmedvedev.demo.service.Demo           : #5 Fetched 90000 records
2022-06-23 16:16:21.952  INFO 41517 --- [or-http-epoll-5] ru.nmedvedev.demo.service.Demo           : #3 Fetched 90000 records
2022-06-23 16:16:22.057  INFO 41517 --- [or-http-epoll-4] ru.nmedvedev.demo.service.Demo           : #2 Fetched 90000 records
2022-06-23 16:16:22.150  INFO 41517 --- [or-http-epoll-8] ru.nmedvedev.demo.service.Demo           : #6 Fetched 90000 records
2022-06-23 16:16:22.379  INFO 41517 --- [or-http-epoll-1] ru.nmedvedev.demo.service.Demo           : #7 Fetched 90000 records
2022-06-23 16:16:22.515  INFO 41517 --- [or-http-epoll-3] ru.nmedvedev.demo.service.Demo           : #1 Fetched 90000 records
2022-06-23 16:16:24.353  INFO 41517 --- [or-http-epoll-6] ru.nmedvedev.demo.service.Demo           : #4 Fetched 100000 records
2022-06-23 16:16:24.362  INFO 41517 --- [or-http-epoll-2] ru.nmedvedev.demo.service.Demo           : #8 Fetched 100000 records
2022-06-23 16:16:24.402  INFO 41517 --- [or-http-epoll-5] ru.nmedvedev.demo.service.Demo           : #3 Fetched 100000 records
2022-06-23 16:16:24.530  INFO 41517 --- [or-http-epoll-7] ru.nmedvedev.demo.service.Demo           : #5 Fetched 100000 records
2022-06-23 16:16:24.534  INFO 41517 --- [or-http-epoll-4] ru.nmedvedev.demo.service.Demo           : #2 Fetched 100000 records
2022-06-23 16:16:24.546  INFO 41517 --- [or-http-epoll-8] ru.nmedvedev.demo.service.Demo           : #6 Fetched 100000 records
2022-06-23 16:16:24.793  INFO 41517 --- [or-http-epoll-1] ru.nmedvedev.demo.service.Demo           : #7 Fetched 100000 records
2022-06-23 16:16:26.668  INFO 41517 --- [or-http-epoll-6] ru.nmedvedev.demo.service.Demo           : #4 Fetched 110000 records
2022-06-23 16:16:26.732  INFO 41517 --- [or-http-epoll-2] ru.nmedvedev.demo.service.Demo           : #8 Fetched 110000 records
2022-06-23 16:16:26.782  INFO 41517 --- [or-http-epoll-5] ru.nmedvedev.demo.service.Demo           : #3 Fetched 110000 records
2022-06-23 16:16:26.858  INFO 41517 --- [or-http-epoll-8] ru.nmedvedev.demo.service.Demo           : #6 Fetched 110000 records
2022-06-23 16:16:26.979  INFO 41517 --- [or-http-epoll-4] ru.nmedvedev.demo.service.Demo           : #2 Fetched 110000 records
2022-06-23 16:16:27.005  INFO 41517 --- [or-http-epoll-1] ru.nmedvedev.demo.service.Demo           : #7 Fetched 110000 records
2022-06-23 16:16:27.069  INFO 41517 --- [or-http-epoll-7] ru.nmedvedev.demo.service.Demo           : #5 Fetched 110000 records
2022-06-23 16:16:29.026  INFO 41517 --- [or-http-epoll-6] ru.nmedvedev.demo.service.Demo           : #4 Fetched 120000 records
2022-06-23 16:16:29.052  INFO 41517 --- [or-http-epoll-2] ru.nmedvedev.demo.service.Demo           : #8 Fetched 120000 records
2022-06-23 16:16:29.060  INFO 41517 --- [or-http-epoll-5] ru.nmedvedev.demo.service.Demo           : #3 Fetched 120000 records
2022-06-23 16:16:29.151  INFO 41517 --- [or-http-epoll-8] ru.nmedvedev.demo.service.Demo           : #6 Fetched 120000 records
2022-06-23 16:16:29.268  INFO 41517 --- [or-http-epoll-4] ru.nmedvedev.demo.service.Demo           : #2 Fetched 120000 records
2022-06-23 16:16:29.281  INFO 41517 --- [or-http-epoll-1] ru.nmedvedev.demo.service.Demo           : #7 Fetched 120000 records
```
Here fetcher #1 stops fetching since it print nothing after `#1 Fetched 90000 records` although it should print `#1 Finished data fetching` on successful finish or error 
