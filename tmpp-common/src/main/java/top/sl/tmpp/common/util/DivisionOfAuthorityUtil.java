package top.sl.tmpp.common.util;

import top.sl.tmpp.common.entity.LoginUser;

/**
 * @author ShuLu
 * @date 2019/11/27 13:30
 */
public class DivisionOfAuthorityUtil {
    public static String DivisionOfAuthority(LoginUser loginUser) {
        switch (loginUser.getUserType()) {
            case "O1":
                return "软件学院";
            case "O2":
                return "电子工程学院";
            case "O3":
                return "艺术设计学院";
            case "O4":
                return "商学院";
            case "O5":
                return "基础部";
            case "O6":
                return "素质教育中心";
            case "O7":
                return "体育教研室";
            default:
                return null;
        }
    }
}
