package com.gatehillsoftware.tools.simplehealthcheck.service.check;

import com.gatehillsoftware.tools.simplehealthcheck.model.input.CheckType;

/**
 * @author pete
 */
public interface CheckStrategyFactory {
    CheckStrategy get(CheckType check);
}
