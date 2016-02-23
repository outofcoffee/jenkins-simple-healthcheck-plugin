package com.gatehillsoftware.tools.simplehealthcheck.service.check;

import com.gatehillsoftware.tools.simplehealthcheck.model.input.CheckType;
import com.google.common.collect.Maps;

import java.util.Map;

/**
 * @author pete
 */
public class CheckStrategyFactoryImpl implements CheckStrategyFactory {
    private final Map<CheckType, CheckStrategy> strategies;

    public CheckStrategyFactoryImpl() {
        strategies = Maps.newHashMap();
        strategies.put(CheckType.SOCKET, new SocketStrategyImpl());
        strategies.put(CheckType.HTTP_200, new Http200StrategyImpl());
    }

    @Override
    public CheckStrategy get(CheckType check) {
        return strategies.get(check);
    }
}
