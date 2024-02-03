package com.xyazh.kanake.api;


public interface IManaStorage {
    //是否存满
    boolean isFull();

    //将容器存满
    void setFull();

    boolean isManaEmpty();

    //将容器清空
    void clearMana();

    //返回还有多少空余
    double remain();

    //能否工作
    boolean canWork(Object object);

    //能否提取
    boolean canExtract(Object object);

    //接收Mana,传入需要接收的的，返回剩下没接收的
    double receiveMana(double n);

    //提取Mana,传入需要提取的，返回实际提取的
    double extractMana(double n);

    //设置mana存储
    void setManaStored(double n);

    //获取mana存储
    double getManaStored();

    //获取最大Mana存储
    double getMaxManaStored();
}
