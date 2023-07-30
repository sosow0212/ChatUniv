package mju.chatuniv.comment.controller.intergration;

import mju.chatuniv.comment.application.service.CommentService;
import org.springframework.context.ApplicationContext;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class BeanUtils {

    private static final ApplicationContext applicationContext = ApplicationContextProvider.getApplicationContext();

    public static List<CommentService> getBeansOfCommentServiceType() {
        Map<String, CommentService> beansOfType = new TreeMap<>(applicationContext.getBeansOfType(CommentService.class));
        return new ArrayList<>(beansOfType.values());
    }
}
