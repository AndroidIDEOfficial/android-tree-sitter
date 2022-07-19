package com.itsaky.androidide.treesitter;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public enum TSInputEncoding {
    TSInputEncodingUTF8(0, StandardCharsets.UTF_8),
    TSInputEncodingUTF16(1, StandardCharsets.UTF_16LE);

    private final int flag;
    private final Charset charset;

    private TSInputEncoding(int flag, Charset charset) {
        this.flag = flag;
        this.charset = charset;
    }

    public int getFlag() {
        return flag;
    }

    public Charset getCharset() {
        return charset;
    }
}
