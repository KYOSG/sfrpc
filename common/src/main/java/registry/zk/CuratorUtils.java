package registry.zk;

import lombok.extern.slf4j.Slf4j;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;

import java.util.List;

/**
 * @Projectname: sfrpc
 * @Filename: CuratorFramwork
 * @Author: SpringForest
 * @Data:2023/2/7 22:57
 * @Description:
 */
@Slf4j
public class CuratorUtils {
    //重试等待时间
    private static final int BASE_SLEEP_TIME = 1000;
    //最大重试次数
    private static final int MAX_RETRIES = 3;

    /*
     * 创建并获取客户端对象
     * */
    public static CuratorFramework getClient() {
        //设置重试策略
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(BASE_SLEEP_TIME, MAX_RETRIES);

        CuratorFramework zkClient = CuratorFrameworkFactory.builder()
                //要连接的服务器列表
                .connectString("127.0.0.1:2181")
                //重试策略
                .retryPolicy(retryPolicy)
                .build();
        zkClient.start();
        return zkClient;
    }

    /*
     * 创建节点
     * */
    public static void createNode(CuratorFramework zkClient, String path) {
        try {
            //创建持久（PERSISTENT）节点，如果父节点不存在则先创建父节点
            zkClient.create().creatingParentsIfNeeded().withMode(CreateMode.PERSISTENT).forPath(path);
            log.info("节点创建成功[{}]", path);
        } catch (Exception e) {
            log.error("zookeeper节点创建失败[{}]", path);
        }

    }

    /*
     * 删除节点
     * */
    public static void deleteNoe(CuratorFramework zkClient, String path) {
        try {
            zkClient.delete().forPath(path);
            log.info("节点删除成功[{}]", path);
        } catch (Exception e) {
            log.error("zookeeper节点删除失败[{}]", path);
        }
    }

    /*
     * 更新节点内容
     * */
    public static void updateNode(CuratorFramework zkClient, String path, String content) {
        try {
            zkClient.setData().forPath(path, content.getBytes());
            log.info("zookeeper节点更新成功[{}{}]", path, content);
        } catch (Exception e) {
            log.error("zookeeper节点更新失败[{}{}]", path, content);
        }
    }

    /*
     * 获取所有子节点
     * */
    public static List<String> getData(CuratorFramework zkClient, String path) {
        List<String> res = null;
        try {
            res = zkClient.getChildren().forPath(path);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return res;
    }

    /*
     * 监听器
     * 当被监听的节点发生变化时可以返回设置好的值
     * */
    public static void registry(CuratorFramework zkClient, String path) throws Exception {
        PathChildrenCache pathChildrenCache = new PathChildrenCache(zkClient, path, true);
        PathChildrenCacheListener pathChildrenCacheListener = ((curatorFramework, pathChildrenCacheEvent1) -> {
            //这里写返回的值
        });
        pathChildrenCache.getListenable().addListener(pathChildrenCacheListener);
        pathChildrenCache.start();
    }
}
