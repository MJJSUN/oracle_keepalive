![image](https://img.pub/p/ff2fc92a1799fecc992c.jpg)

### 推荐使用docker部署，[仓库地址](https://hub.docker.com/r/sunssr/fixed-memory-hog)

```
docker run -d \
  --name mm \
  -e JVM_XMX=6G \
  -e CPU_LOAD=0.1 \
  --restart always \
  sunssr/fixed-memory-hog:1.1.1
```
JVM_XMX：你想使用的内存大小。例如：6G。

CPU_LOAD：每个CPU核心的使用率[0,1]。例如：使用率设置为0.1，则每个核心都将使用它10%的CPU资源。

### 自行构建
```
git clone -b master https://github.com/MJJSUN/oracle_keepalive.git
cd oracle_keepalive
docker buildx build --platform linux/amd64,linux/arm64 .
```
