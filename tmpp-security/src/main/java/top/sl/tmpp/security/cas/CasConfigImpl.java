package top.sl.tmpp.security.cas;

import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import top.itning.cas.config.ICheckIsLoginConfig;
import top.itning.cas.config.INeedSetMap2SessionConfig;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Cas配置实现
 *
 * @author itning
 */
@Component
public class CasConfigImpl implements ICheckIsLoginConfig, INeedSetMap2SessionConfig {

    @Override
    public boolean isLogin(HttpServletResponse resp, HttpServletRequest req) {
        return StringUtils.isNotBlank(req.getHeader(HttpHeaders.AUTHORIZATION));
    }

    @Override
    public boolean needSetMapSession() {
        return false;
    }
}
