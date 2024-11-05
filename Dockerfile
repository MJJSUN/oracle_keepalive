# 使用 OpenJDK 作为基础镜像
FROM openjdk:11-jdk

# 设置工作目录
WORKDIR /usr/src/myapp

# 将当前目录的内容复制到容器的工作目录
COPY . .

# 编译 Java 程序
RUN javac FixedMemoryHog.java

# 使用 /bin/sh 启动，以便在 CMD 中解析环境变量
CMD ["sh", "-c", "java -Xmx${JVM_XMX:-512m} FixedMemoryHog"]
