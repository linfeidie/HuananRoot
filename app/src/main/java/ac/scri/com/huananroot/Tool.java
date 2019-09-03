package ac.scri.com.huananroot;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * 文件描述：.
 * <p>
 * 作者：Created by 林飞堞 on 2019/9/1
 * <p>
 * 版本号：HuananRoot
 */
public class Tool {
    public interface Where<D> {
        boolean where(D obj) ;
    }
    /**
     * 通过接口函数选择对象集合的属性值
     * @param colls
     * @param gb
     * @return
     * <T extends Comparable<T> ,D>
     */
    public static final <D> List<D> where(Collection<D> colls , Where<D> gb){
        Iterator<D> iter = colls.iterator() ;
        List<D> set=new ArrayList<D>();
        while(iter.hasNext()) {
            D d = iter.next() ;
            if(gb.where(d)){
                set.add(d);
            }
        }
        return set;
    }

    public static <T> List<T> deepCopy(List<T> src) throws IOException, ClassNotFoundException {
        ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream(byteOut);
        out.writeObject(src);

        ByteArrayInputStream byteIn = new ByteArrayInputStream(byteOut.toByteArray());
        ObjectInputStream in = new ObjectInputStream(byteIn);
        @SuppressWarnings("unchecked")
        List<T> dest = (List<T>) in.readObject();
        return dest;
    }
}
