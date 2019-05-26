package com.bj58.risk.netty.messagepack;

import org.msgpack.MessagePack;
import org.msgpack.template.Templates;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MessagePackTest {

    public static void main(String[] args) {
        List<String> list = new ArrayList<>();
        list.add("test1");
        list.add("test2");
        list.add("test3");
        MessagePack messagePack = new MessagePack();
        try {
            byte[] bytes = messagePack.write(list);
            List<String> read = messagePack.read(bytes, Templates.tList(Templates.TString));
            read.forEach(string -> System.out.println(string));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
