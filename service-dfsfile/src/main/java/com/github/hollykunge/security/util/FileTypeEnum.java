package com.github.hollykunge.security.util;

public enum FileTypeEnum {
    IMG("img","img"),
    PNG("img","png"),
    JGP("img","jpg"),
    JGEP("img","jgeg"),
    GIF("img","gif"),

    TXT("text","txt"),
    DOC("doc","doc"),
    DOCX("doc","docx"),

    ZIP("zip","zip"),
    RAR("zip","rar"),
    JAR("zip","jar"),

    OTHER("other","other")
    ;
    //防止字段值被修改，增加的字段也统一final表示常量
    private final String type;
    private final String value;
    
    private FileTypeEnum(String type, String value){
        this.type = type;
        this.value = value;
    }
    //根据key获取枚举
    public static FileTypeEnum getEnumByValue(String value){
        if(null == value){
            return OTHER;
        }
        for(FileTypeEnum temp: FileTypeEnum.values()){
            if(temp.getValue().equals(value)){
                return temp;
            }
        }
        return OTHER;
    }
    public String getType() {
        return type;
    }
    public String getValue() {
        return value;
    }
}