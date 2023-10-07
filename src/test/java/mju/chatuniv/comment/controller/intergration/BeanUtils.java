package mju.chatuniv.comment.controller.intergration;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import mju.chatuniv.comment.service.CommentReadService;
import mju.chatuniv.comment.service.CommentWriteService;
import org.springframework.context.ApplicationContext;

public class BeanUtils {

    private static final ApplicationContext applicationContext = ApplicationContextProvider.getApplicationContext();

    public static List<CommentWriteService> getBeansOfCommentWriteServiceType() {
        Map<String, CommentWriteService> beansOfType = new TreeMap<>(
                applicationContext.getBeansOfType(CommentWriteService.class));
        return new ArrayList<>(beansOfType.values());
    }

    public static List<CommentReadService> getBeansOfCommentReadServiceType() {
        Map<String, CommentReadService> beansOfType = new TreeMap<>(
                applicationContext.getBeansOfType(CommentReadService.class));
        return new ArrayList<>(beansOfType.values());
    }
}
