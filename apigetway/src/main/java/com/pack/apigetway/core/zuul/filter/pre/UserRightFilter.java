package com.pacific.apigetway.core.zuul.filter.pre;

import com.google.common.base.Charsets;
import com.google.common.base.Strings;
import com.google.common.hash.Hashing;
import com.pacific.apigetway.common.Constant;
import com.pacific.apigetway.core.zuul.FilterOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import java.util.UUID;

/**
 * @author yekai
 * @date 20181210
 **/
public class UserRightFilter extends AbstractPreZuulFilter {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserRightFilter.class);

    @Value("${login.enable}")
    private String login;

    @Override
    public int filterOrder() {
        return FilterOrder.USER_RIGHT_ORDER;
    }

    @Override
    public Object doRun() {
        String batchNo = Hashing.md5().newHasher()
                .putString( UUID.randomUUID().toString(), Charsets.UTF_8)
                .hash().toString();
        context.set(Constant.REQUEST_BATCH_NO, batchNo);

        if (!Strings.isNullOrEmpty(login) && Boolean.TRUE.toString().equals(login)) {
            LOGGER.info("没权限访问");
            return fail(401, "have no permission .");
        }

        return success();
    }
}
