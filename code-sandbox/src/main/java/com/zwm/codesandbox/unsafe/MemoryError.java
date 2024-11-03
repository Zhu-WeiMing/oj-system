package com.zwm.codesandbox.unsafe;

import java.util.ArrayList;
import java.util.List;

public class MemoryError {
    public static void main(String[] args) {
        List<byte[]> list = new ArrayList<byte[]>();
        while (true){
            list.add(new byte[1024*1024]);
        }
    }
}
