package com.bj58.risk.nio;

import java.nio.channels.Selector;

public class ReactorTask implements Runnable {

    private Selector selector;

    public ReactorTask(Selector selector) {
        this.selector = selector;
    }

    @Override
    public void run() {
        while (true) {
            
        }
    }
}
