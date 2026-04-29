package com.yume.yumeaiagent.agent.model;


/**
 * 代理执行状态的枚举类
 */
public enum AgentState {

    /**
     * 空闲
     */
    IDLE,

    /**
     * 运行中
     */
    Running,

    /**
     * 已完成
     */
    Finished,

    /**
     * 错误
     */
    Error;

}
