package com.modeln.build.common.file;

import java.util.HashMap;
import java.util.Map;


public class CMnMapTokenResolver implements IMnTokenResolver {

    protected Map<String, String> tokenMap = new HashMap<String, String>();

    public CMnMapTokenResolver(Map<String, String> tokenMap) {
        this.tokenMap = tokenMap;
    }

    public String resolveToken(String tokenName) {
        return this.tokenMap.get(tokenName);
    }

}
