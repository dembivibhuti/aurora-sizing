Type | # of Clients | Throughput | Server Latency  | Client Latency  |
| ----------- | ----------- |-----------  | ----------- |----------- |
| EHCache + Redis | 7K | 1.3 Mil/s | 90 µs | 5 ms | 
| Redis | 7K | 1.07 Mil/s | 1.3 ms | 7 ms | 
|No Cache | 7K | 800 K/s | 4 ms | 9 ms | 
| EHCache + Redis | 1 | 1.5 K/s | 70 µs | 630 µs | 
| Redis | 1 | 1 K/s | 450 µs | 960 µs | 
|No Cache | 1 | 970 /s | 400 µs | 923 µs | 