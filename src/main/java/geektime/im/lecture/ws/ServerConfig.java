package geektime.im.lecture.ws;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.stereotype.Component;

/**
 * config for Netty Server
 */
@Component
@ConfigurationProperties(prefix = "websocket.connector.server")
public class ServerConfig {

    public int port;
    public int portForDr;
    public boolean useEpoll;
    public boolean useMemPool;
    public boolean useDirectBuffer;
    public int bossThreads;
    public int workerThreads;
    public int userThreads;
    public int connTimeoutMills;
    public int soLinger;
    public int backlog;
    public boolean reuseAddr;
    public int sendBuff;
    public int recvBuff;
    public int readIdleSecond;
    public int writeIdleSecond;
    public int allIdleSecond;
    public int idleTimes;

    public int getIdleTimes() {
        return idleTimes;
    }

    public void setIdleTimes(int idleTimes) {
        this.idleTimes = idleTimes;
    }

    public int getBacklog() {
        return backlog;
    }

    public void setBacklog(int backlog) {
        this.backlog = backlog;
    }

    public int getBossThreads() {
        return bossThreads;
    }

    public void setBossThreads(int bossThreads) {
        this.bossThreads = bossThreads;
    }

    public int getConnTimeoutMills() {
        return connTimeoutMills;
    }

    public void setConnTimeoutMills(int connTimeoutMills) {
        this.connTimeoutMills = connTimeoutMills;
    }

    public int getPortForDr() {
        return portForDr;
    }

    public void setPortForDr(int portForDr) {
        this.portForDr = portForDr;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public int getReadIdleSecond() {
        return readIdleSecond;
    }

    public void setReadIdleSecond(int readIdleSecond) {
        this.readIdleSecond = readIdleSecond;
    }

    public int getRecvBuff() {
        return recvBuff;
    }

    public void setRecvBuff(int recvBuff) {
        this.recvBuff = recvBuff;
    }

    public boolean isReuseAddr() {
        return reuseAddr;
    }

    public void setReuseAddr(boolean reuseAddr) {
        this.reuseAddr = reuseAddr;
    }

    public int getSendBuff() {
        return sendBuff;
    }

    public void setSendBuff(int sendBuff) {
        this.sendBuff = sendBuff;
    }

    public int getSoLinger() {
        return soLinger;
    }

    public void setSoLinger(int soLinger) {
        this.soLinger = soLinger;
    }

    public boolean isUseDirectBuffer() {
        return useDirectBuffer;
    }

    public void setUseDirectBuffer(boolean useDirectBuffer) {
        this.useDirectBuffer = useDirectBuffer;
    }

    public boolean isUseEpoll() {
        return useEpoll;
    }

    public void setUseEpoll(boolean useEpoll) {
        this.useEpoll = useEpoll;
    }

    public boolean isUseMemPool() {
        return useMemPool;
    }

    public void setUseMemPool(boolean useMemPool) {
        this.useMemPool = useMemPool;
    }

    public int getUserThreads() {
        return userThreads;
    }

    public void setUserThreads(int userThreads) {
        this.userThreads = userThreads;
    }

    public int getWorkerThreads() {
        return workerThreads;
    }

    public void setWorkerThreads(int workerThreads) {
        this.workerThreads = workerThreads;
    }

    public int getWriteIdleSecond() {
        return writeIdleSecond;
    }

    public void setWriteIdleSecond(int writeIdleSecond) {
        this.writeIdleSecond = writeIdleSecond;
    }

    @Bean
    public static PropertySourcesPlaceholderConfigurer propertyConfigOfServer() {
        PropertySourcesPlaceholderConfigurer placeholderConfigurer = new PropertySourcesPlaceholderConfigurer();
        return placeholderConfigurer;
    }

    public int getAllIdleSecond() {
        return allIdleSecond;
    }

    public void setAllIdleSecond(int allIdleSecond) {
        this.allIdleSecond = allIdleSecond;
    }
}
