package ac.scri.com.huananroot;

import java.util.List;

/**
 * 文件描述：.
 * <p>
 * 作者：Created by linfeidie on 2019/8/20
 * <p>
 * 版本号：HuananRoot
 */
public class SiteNode {
    public String nodeName;

    public int nodeNum;

    public DodeDirection  dodeDirection = DodeDirection.UP;

    public String noteDir;

    public boolean isWork = false;

    public enum DodeDirection{
        LEFT, UP, RIGHT, DOWN
    }

    public List<String> dirs;

    // 0 已完成状态  1正在处理状态  -1待处理状态
    public int nodeStatus = -1;

    @Override
    public String toString() {
        return "SiteNode{" +
                "nodeName='" + nodeName + '\'' +
                ", nodeNum=" + nodeNum +
                ", dodeDirection=" + dodeDirection +
                ", noteDir='" + noteDir + '\'' +
                ", isWork=" + isWork +
                ", dirs=" + dirs +
                '}';
    }

    public void setDirs(List<String> dirs) {
        this.dirs = dirs;
    }
}
