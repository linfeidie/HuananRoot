package ac.scri.com.huananroot;

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


}
