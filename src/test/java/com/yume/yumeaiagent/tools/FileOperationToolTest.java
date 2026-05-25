package com.yume.yumeaiagent.tools;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

public class FileOperationToolTest {

    @Test
    public void testReadFile() {
        FileOperationTool tool = new FileOperationTool();
        String fileName = "yumestk.txt";
        String result = tool.readFile(fileName);
        Assertions.assertNotNull(result);
    }

    @Test
    public void testWriteFile() {
        FileOperationTool tool = new FileOperationTool();
        String fileName = "yumestk.txt";
        String content = "https://github.com/yumestk";
        String result = tool.writeFile(fileName, content);
        Assertions.assertNotNull(result);
    }
}
